package usecase.customtimefilter;

import data_access.CourseDataAccessInterface;
import entity.Course;
import entity.Section;
import entity.TimeSlot;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Use Case 3: Custom Day/Range Filter Search.
 * This interactor filters courses based on:
 *  - text query (optional)
 *  - a specific day of the week
 *  - a start–end time window
 *
 * It returns all courses that contain at least one section meeting
 * entirely within the given time range on the selected day.
 */
public class CustomTimeFilterInteractor implements CustomTimeFilterInputBoundary {

    private final CourseDataAccessInterface courseDataAccess;
    private final CustomTimeFilterOutputBoundary presenter;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    public CustomTimeFilterInteractor(CourseDataAccessInterface courseDataAccess,
                                      CustomTimeFilterOutputBoundary presenter) {
        this.courseDataAccess = courseDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(CustomTimeFilterInputData inputData) {
        try {
            String query = inputData.getQuery();
            String day   = inputData.getDayOfWeek();
            String start = inputData.getStartTime();
            String end   = inputData.getEndTime();

            // Step 1: Retrieve all courses from the data source
            List<Course> allCourses = courseDataAccess.getAllCourses();

            // Step 2: Apply text-based filtering (query may be empty)
            List<Course> textMatched = filterByQuery(allCourses, query);

            // Step 3: Filter based on day + time range
            List<Course> matchingCourses = filterByTimeWindow(
                    textMatched, day, start, end);

            // Step 4: Present results
            if (matchingCourses.isEmpty()) {
                presenter.presentNoResults();
            } else {
                List<CustomTimeFilterOutputData.ResultItem> resultItems = new ArrayList<>();

                for (Course course : matchingCourses) {
                    // Placeholder section time; can later be updated to specific slot info
                    resultItems.add(new CustomTimeFilterOutputData.ResultItem(
                            course.getCourseCode(),
                            course.getCourseName(),
                            "Time TBD"
                    ));
                }

                presenter.presentResults(
                        new CustomTimeFilterOutputData(resultItems)
                );
            }

        } catch (Exception e) {
            presenter.presentError(
                    "Failed to search courses with custom time filter: " + e.getMessage()
            );
        }
    }

    /**
     * Filters a list of courses based on a text query.
     * The query matches against course code or course name.
     * If the query is empty, all courses are returned.
     */
    private List<Course> filterByQuery(List<Course> courses, String query) {
        if (query == null) {
            query = "";
        }
        query = query.toLowerCase().trim();

        if (query.isEmpty()) {
            return new ArrayList<>(courses);
        }

        List<Course> matches = new ArrayList<>();
        for (Course course : courses) {
            String code = course.getCourseCode() != null
                    ? course.getCourseCode().toLowerCase() : "";
            String name = course.getCourseName() != null
                    ? course.getCourseName().toLowerCase() : "";

            if (code.contains(query) || name.contains(query)) {
                matches.add(course);
            }
        }
        return matches;
    }

    /**
     * Filters courses based on whether they have at least one section
     * whose timeslot falls fully within the given time window
     * on the specified day of the week.
     */
    private List<Course> filterByTimeWindow(List<Course> courses,
                                            String day, String start, String end) {

        List<Course> matches = new ArrayList<>();

        int dayInt = mapDayToInt(day);
        if (dayInt == -1) {
            return matches; // invalid day input
        }

        LocalTime rangeStart = LocalTime.parse(start, TIME_FORMATTER);
        LocalTime rangeEnd   = LocalTime.parse(end, TIME_FORMATTER);

        for (Course course : courses) {
            boolean courseMatches = false;

            if (course.getSections() != null) {
                for (Section section : course.getSections()) {
                    for (TimeSlot slot : section.getTimes()) {

                        // Use TimeSlot's built-in time range check
                        if (slot.isWithinRange(dayInt, rangeStart, rangeEnd)) {
                            courseMatches = true;
                            break;
                        }
                    }
                    if (courseMatches) break;
                }
            }

            if (courseMatches) {
                matches.add(course);
            }
        }

        return matches;
    }

    /**
     * Converts a day name (e.g., "Mon", "Monday") into the TimeSlot integer format:
     * 1 = Monday, ... 7 = Sunday.
     */
    private int mapDayToInt(String day) {
        if (day == null) return -1;

        String d = day.trim().toLowerCase();

        switch (d) {
            case "mon":
            case "monday":
                return 1;
            case "tue":
            case "tues":
            case "tuesday":
                return 2;
            case "wed":
            case "weds":
            case "wednesday":
                return 3;
            case "thu":
            case "thur":
            case "thurs":
            case "thursday":
                return 4;
            case "fri":
            case "friday":
                return 5;
            case "sat":
            case "saturday":
                return 6;
            case "sun":
            case "sunday":
                return 7;
            default:
                return -1;
        }
    }
}