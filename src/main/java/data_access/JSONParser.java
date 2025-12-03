package data_access;

import entity.Building;
import entity.Course;
import entity.Section;
import entity.TimeSlot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;

/**
 * Parser for the 2017 timetable data.
 * This is currently in a semi-refactored state.
 * Known things that caused exceptions that need to be refactored f (initially band-aided with try-catch):
 * - Assigned room is "" instead of null
 * - Types are unpredictable -- instructors or schedule is an empty array instead of a JSONObject
 * - enrolled/capacity value can be null
 */
public class JSONParser {
    private static final String BUILDING_FILE = "src/main/resources/buildings_geocoded.json";
    private final ArrayList<Course> courses;

    public JSONParser() {
        this("2017_ttb_archive.json");
    }

    public JSONParser(String filename) {
        this.courses = new ArrayList<>();

        try {
            final Map<String, Building> buildingMap = getBuildingMap(BUILDING_FILE);
            final JSONObject jsonObject = getJsonObject(filename);

            Set<String> keys = jsonObject.keySet();
            for (String courseString : keys) {
                JSONObject courseData = jsonObject.getJSONObject(courseString);

                final List<Section> courseSections = new ArrayList<Section>();

                final Course course = new Course(
                        courseData.getString("code"),
                        courseData.getString("courseTitle"),
                        courseData.getString("courseDescription"),
                        0.0f, // Currently unimplemented as this is not present in the data file.
                        courseData.getString("section"),
                        courseSections,
                        null,
                        getBreadthCategory(courseData.getString("breadthCategories")));

                // Iterate through everything in the "meetings" layer
                final JSONObject sectionsData = courseData.getJSONObject("meetings");
                for (Object sectionKey : sectionsData.keySet()) {
                    final JSONObject sectionData = sectionsData.getJSONObject((String) sectionKey);

                    // Get list of instructors of the section.
                    final List<String> instructors = new ArrayList<>();
                    try {
                        // Attempt to get JSONObject from key "instructors".
                        // If the instructor list is not blank, it will be a JSONObject,
                        // otherwise the file holds a blank JSONArray instead.
                        final JSONObject instructorData = sectionData.getJSONObject("instructors");
                        for (Object instructorKey : instructorData.keySet()) {
                            instructors.add(instructorData.getJSONObject(instructorKey.toString())
                                    .getString("lastName"));
                        }
                    }
                    catch (JSONException e) {
                        // Occurs whenever the result is a blank array.
                    }

                    // Get all timeslots in the section's schedule.
                    final List<TimeSlot> times = new ArrayList<>();

                    JSONObject sectionSchedule;
                    try {
                        sectionSchedule = sectionData.getJSONObject("schedule");
                    }
                    catch (JSONException e) {
                        // Happens when it's a blank array (section is cancelled).
                        continue;
                    }
                    for (Object timeKey : sectionSchedule.keySet()) {
                        final JSONObject timeData = sectionSchedule.getJSONObject(timeKey.toString());
                        if (timeData.isNull("meetingDay")) {
                            continue;
                        }
                        final String meetingDay = timeData.getString("meetingDay");
                        final String startString = timeData.getString("meetingStartTime");
                        final String endString = timeData.getString("meetingEndTime");

                        final int dayValue = getDayCodeValue(meetingDay);
                        final LocalTime startTime = LocalTime.parse(startString);
                        final LocalTime endTime = LocalTime.parse(endString);

                        // Get the first building that has a room listed, if there is one, or null otherwise.
                        final String roomCode = getFirstRoomCode(timeData);
                        final Building building = buildingMap.get(roomCode);

                        final TimeSlot timeSlot = new TimeSlot(dayValue, startTime, endTime, building);
                        times.add(timeSlot);

                    }

                    int enrolled = 0;
                    int capacity = 0;
                    try {
                        enrolled = Integer.parseInt(sectionData.getString("actualEnrolment"));
                        capacity = Integer.parseInt(sectionData.getString("enrollmentCapacity"));
                    } catch (Exception e) {
                        // Occurs when enrollment is null.
                    }

                    // Create section object.
                    final Section section = new Section(
                            sectionKey.toString(),
                            times,
                            enrolled,
                            instructors,
                            capacity,
                            course);
                    courseSections.add(section);
                }

                courses.add(course);

            }

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get breadth category from text string.
     * Known program issue: courses with multiple breadth categories have no accurate representation.
     * @param breadthString String containing breadth category info
     * @return the integer of the first breadth category mentioned, or 0 otherwise.
     */
    private static int getBreadthCategory(String breadthString) {
        for (int i = 1; i <= 5; i++) {
            if (breadthString.contains("(" + i + ")")) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Return the first building that a course is in, or null if it does not have a valid building course code.
     * Specifically, returns fall's building if it's not empty, otherwise returns winter's building.
     * @param timeData JSON data of time slot
     * @return the two-letter building code of the first room that the course is in, or null if invalid.
     */
    @Nullable
    private static String getFirstRoomCode(JSONObject timeData) {
        try {
            if (timeData.isNull("assignedRoom1")) {
                if (timeData.isNull("assignedRoom2")) {
                    return null;
                }
                else {
                    return timeData.getString("assignedRoom2").substring(0, 2);
                }
            }
            else {
                return timeData.getString("assignedRoom1").substring(0, 2);
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Parse the file as a JSONObject.
     * @param filename the file name
     * @return a JSONObject representing the file's contents.
     */
    @NotNull
    private JSONObject getJsonObject(String filename) throws IOException, URISyntaxException {
        final String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));
        return new JSONObject(jsonString);
    }

    /**
     * Convert a building file into a HashMap mapping two-letter building codes to files.
     * @param buildingFile a file containing building data.
     * @return a HashMap object representing the building map.
     * @throws Exception from BuildingLoader.loadBuildings
     */
    @NotNull
    private static Map<String, Building> getBuildingMap(String buildingFile) throws Exception {
        final List<Building> buildings = BuildingLoader.loadBuildings(buildingFile);

        final Map<String, Building> buildingMap = new HashMap<String, Building>();
        for (Building building : buildings) {
            buildingMap.put(building.getBuildingCode(), building);
        }
        return buildingMap;
    }

    /**
     * Convert day code to day value as needed by TimeSlots.
     * @param dayCode e.g. "MO"
     * @return integer e.g. 1
     */
    private int getDayCodeValue(String dayCode) {
        return switch (dayCode) {
            case "MO" -> 1;
            case "TU" -> 2;
            case "WE" -> 3;
            case "TH" -> 4;
            case "FR" -> 5;
            case "SA" -> 6;
            case "SU" -> 7;
            default -> 0;
        };
    }

    /**
     * Parse the JSON file as a list of courses.
     * @return a list of Course objects
     */
    public List<Course> getCourses() {
        return courses;
    }

}