package usecase.addcourse;

import data_access.CourseDataAccessInterface;
import data_access.TimetableDataAccessInterface;
import entity.Building;
import entity.Course;
import entity.Section;
import entity.TimeSlot;
import interface_adapter.controller.AddCourseController.TimeSlotData;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interactor for the Add Course use case.
 */
public class AddCourseInteractor implements AddCourseInputBoundary {

    private final TimetableDataAccessInterface timetableDataAccess;
    private final CourseDataAccessInterface courseDataAccess;
    private final AddCourseOutputBoundary presenter;
    private final Map<String, Building> buildingCache;

    public AddCourseInteractor(TimetableDataAccessInterface timetableDataAccess,
                               CourseDataAccessInterface courseDataAccess,
                               AddCourseOutputBoundary presenter) {
        if (timetableDataAccess == null || courseDataAccess == null || presenter == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.timetableDataAccess = timetableDataAccess;
        this.courseDataAccess = courseDataAccess;
        this.presenter = presenter;
        this.buildingCache = new HashMap<>();
    }

    @Override
    public void execute(AddCourseInputData inputData) {
        if (inputData == null) {
            presenter.presentError("Invalid input: No data provided");
            return;
        }

        try {
            Course course = courseDataAccess.findByCourseCode(inputData.getCourseCode());

            if (course == null) {
                presenter.presentError("Course not found: " + inputData.getCourseCode());
                return;
            }

            Section originalSection = findSectionInCourse(course, inputData.getSectionCode());

            if (originalSection == null) {
                presenter.presentError("Section " + inputData.getSectionCode() +
                        " not found in course " + inputData.getCourseCode());
                return;
            }

            // Create Section entity with selected time slots (with real enrollment capacity)
            Section section = createSection(course, originalSection, inputData);

            // Validate section has time slots
            if (section.getTimes().isEmpty()) {
                presenter.presentError("Invalid input: Section has no time slots");
                return;
            }

            // Check term compatibility
            if (!isTermCompatible(course)) {
                String currentTerm = timetableDataAccess.getCurrentTerm();
                String termName = "F".equals(currentTerm) ? "Fall" : "Winter";
                String courseTermName = course.isFall() ? "Fall" : "Winter";
                presenter.presentError("Cannot mix terms: Your timetable has " + termName
                        + " courses, but this is a " + courseTermName + " course");
                return;
            }

            // Check for duplicate
            if (timetableDataAccess.hasSection(section)) {
                presenter.presentError("Section " + section.getSectionId()
                        + " is already in your timetable");
                return;
            }

            // Check if there are any conflicts
            boolean hasConflict = timetableDataAccess.hasConflicts(section);

            if (hasConflict) {
                presenter.presentError("Cannot add section: Time conflict with existing courses");
                return;
            }

            // Add section
            boolean added = timetableDataAccess.addSection(section);

            if (!added) {
                presenter.presentError("Failed to add section to timetable");
                return;
            }

            AddCourseOutputData outputData = new AddCourseOutputData(section, false);
            presenter.presentSuccess(outputData);

        } catch (Exception e) {
            presenter.presentError("Error adding course: " + e.getMessage());
        }
    }

    /**
     * Find section in course's section list.
     */
    private Section findSectionInCourse(Course course, String sectionCode) {
        for (Section section : course.getSections()) {
            if (section.getSectionId().equals(sectionCode)) {
                return section;
            }
        }
        return null;
    }

    /**
     * Create Section entity.
     */
    private Section createSection(Course course, Section originalSection,
                                  AddCourseInputData inputData) {
        // Create TimeSlot entities from primitive data
        List<TimeSlot> timeSlots = new ArrayList<>();
        for (TimeSlotData data : inputData.getTimeSlotDataList()) {
            TimeSlot timeSlot = createTimeSlot(data);
            timeSlots.add(timeSlot);
        }

        List<String> instructors = new ArrayList<>();
        if (inputData.getInstructor() != null && !inputData.getInstructor().trim().isEmpty()) {
            instructors.add(inputData.getInstructor());
        }

        return new Section(
                inputData.getSectionCode(),
                timeSlots,
                originalSection.getEnrolledStudents(),
                instructors,
                originalSection.getCapacity(),
                course
        );
    }

    /**
     * Create TimeSlot entity from primitive data.
     */
    private TimeSlot createTimeSlot(TimeSlotData data) {
        if (data.getStartHour() >= data.getEndHour()) {
            throw new IllegalArgumentException(
                    "Invalid time range: start hour must be before end hour"
            );
        }

        Building building = getOrCreateBuilding(data.getLocation());
        int dayValue = convertDayNameToDayValue(data.getDay());
        LocalTime startTime = LocalTime.of(data.getStartHour(), 0);
        LocalTime endTime = LocalTime.of(data.getEndHour(), 0);

        return new TimeSlot(dayValue, startTime, endTime, building);
    }

    private Building getOrCreateBuilding(String code) {
        return buildingCache.computeIfAbsent(code,
                k -> new Building(code, code, 0.0, 0.0));
    }

    private int convertDayNameToDayValue(String dayName) {
        return switch (dayName.toLowerCase()) {
            case "monday" -> 1;
            case "tuesday" -> 2;
            case "wednesday" -> 3;
            case "thursday" -> 4;
            case "friday" -> 5;
            case "saturday" -> 6;
            case "sunday" -> 7;
            default -> throw new IllegalArgumentException("Invalid day: " + dayName);
        };
    }

    /**
     * Check if course term is compatible with current timetable term.
     */
    private boolean isTermCompatible(Course course) {
        String currentTerm = timetableDataAccess.getCurrentTerm();
        String courseTerm = course.getTerm();

        if (currentTerm == null) {
            return true;
        }

        if (course.isYearLong()) {
            return true;
        }

        return currentTerm.equals(courseTerm);
    }
}