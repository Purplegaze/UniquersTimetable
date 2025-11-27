package interface_adapter.controller;

import entity.Building;
import entity.Course;
import entity.Section;
import entity.TimeSlot;
import usecase.addcourse.AddCourseInputBoundary;
import usecase.addcourse.AddCourseInputData;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Add Course use case.
 */
public class AddCourseController {

    private final AddCourseInputBoundary addCourseInteractor;
    private final Map<String, Building> buildingCache;

    public AddCourseController(AddCourseInputBoundary addCourseInteractor) {
        if (addCourseInteractor == null) {
            throw new IllegalArgumentException("Interactor cannot be null");
        }
        this.addCourseInteractor = addCourseInteractor;
        this.buildingCache = new HashMap<>();
    }

    /**
     * Handle add course request from the view.
     */
    public void addCourse(String courseCode, String courseName, String term,
                          String sectionCode, String instructor,
                          List<TimeSlotData> timeDatas) {
        try {
            validateInput(courseCode, courseName, term, sectionCode, timeDatas);

            // Create TimeSlot entities from primitive data
            List<TimeSlot> timeSlots = new ArrayList<>();
            for (TimeSlotData timeData : timeDatas) {
                TimeSlot timeSlot = createTimeSlot(timeData);
                timeSlots.add(timeSlot);
            }

            // Create Course entity
            Course course = new Course(
                    courseCode,           // courseCode
                    courseName,           // courseName
                    "",                   // description (empty for now)
                    0.5f,                 // credits (default)
                    null,                 // courseRating (null for now)
                    term,                 // term
                    new ArrayList<>(),    // sections (empty list)
                    null,                 // location (null for now)
                    0                     // breadthCategory (default)
            );

            // Create instructor list
            List<String> instructors = new ArrayList<>();
            if (instructor != null && !instructor.trim().isEmpty()) {
                instructors.add(instructor);
            }

            // Create Section entity with all required fields
            Section section = new Section(
                    sectionCode,          // section_id
                    timeSlots,            // times
                    0,                    // enrolled_students (default 0)
                    instructors,          // instructors
                    100,                  // capacity (default 100)
                    course                // course
            );

            // Execute use case with entity
            AddCourseInputData inputData = new AddCourseInputData(section);
            addCourseInteractor.execute(inputData);

        } catch (IllegalArgumentException e) {
            // Entity validation failed
            throw e;
        } catch (Exception e) {
            // Unexpected error
            System.err.println("Error in AddCourseController: " + e.getMessage());
            throw new RuntimeException("Failed to add course", e);
        }
    }

    /**
     * Create a TimeSlot entity from primitive data.
     */
    private TimeSlot createTimeSlot(TimeSlotData timeData) {
        if (timeData.getStartHour() >= timeData.getEndHour()) {
            throw new IllegalArgumentException(
                    "Invalid time range: start hour (" + timeData.getStartHour() +
                            ") must be before end hour (" + timeData.getEndHour() + ")"
            );
        }
        
        Building building = getOrCreateBuilding(timeData.getLocation());
        
        int dayValue = convertDayNameToDayValue(timeData.getDay());
        LocalTime startTime = LocalTime.of(timeData.getStartHour(), 0);
        LocalTime endTime = LocalTime.of(timeData.getEndHour(), 0);
        
        return new TimeSlot(dayValue, startTime, endTime, building);
    }

    /**
     * Get or create a building entity.
     */
    private Building getOrCreateBuilding(String code) {
        return buildingCache.computeIfAbsent(code, k -> new Building(code, code, 0.0, 0.0));
    }

    /**
     * Convert day name to day value (1-7).
     */
    private int convertDayNameToDayValue(String dayName) {
        if (dayName == null) {
            throw new IllegalArgumentException("Day name cannot be null");
        }

        return switch (dayName.toLowerCase()) {
            case "monday" -> 1;
            case "tuesday" -> 2;
            case "wednesday" -> 3;
            case "thursday" -> 4;
            case "friday" -> 5;
            case "saturday" -> 6;
            case "sunday" -> 7;
            default -> throw new IllegalArgumentException("Invalid day name: " + dayName);
        };
    }

    /**
     * Validate primitive input before conversion.
     */
    private void validateInput(String courseCode, String courseName, String term,
                               String sectionCode, List<TimeSlotData> timeDatas) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        if (term == null || !term.matches("[FSY]")) {
            throw new IllegalArgumentException("Term must be F, S, or Y");
        }
        if (sectionCode == null || sectionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Section code cannot be empty");
        }
        if (timeDatas == null || timeDatas.isEmpty()) {
            throw new IllegalArgumentException("Time slots cannot be empty");
        }
    }

    /**
     * Data class for time slot information from the view.
     */
    public static class TimeSlotData {
        private final String day;
        private final int startHour;
        private final int endHour;
        private final String location;

        public TimeSlotData(String day, int startHour, int endHour, String location) {
            this.day = day;
            this.startHour = startHour;
            this.endHour = endHour;
            this.location = location;
        }

        public String getDay() {
            return day;
        }

        public int getStartHour() {
            return startHour;
        }

        public int getEndHour() {
            return endHour;
        }

        public String getLocation() {
            return location;
        }
    }
}
