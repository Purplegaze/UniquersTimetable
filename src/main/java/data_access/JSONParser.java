package data_access;
import entity.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;

/**
 * Parser for the 2017 timetable data.
 * This is currently in an EXTREMELY SPAGHETTI state as we needed it working ASAP to test the other use cases.
 * It will be cleaned up later.
 *
 * CURRENTLY UNIMPLEMENTED: Rating, credits
 *
 * Known things that cause exceptions (currently band-aided with try-catch):
 * - Assigned room is "" instead of null
 * - instructors or schedule is an empty array instead of a JSONObject
 * - TODO: Ask on Piazza what the cleanest way of handling unpredictable JSON types is.
 * - enrolled/capacity value can be null??
 */
public class JSONParser {
    private final ArrayList<Course> courses;

    public JSONParser() {
        this("2017_ttb_archive.json");
    }

    public JSONParser(String filename) {
        this.courses = new ArrayList<>();

        String[] dayArray = {"MO", "TU", "WE", "TH", "FR", "SA", "SU"};
        final List<String> days = Arrays.asList(dayArray);

        try {
            List<Building> buildings = BuildingLoader.loadBuildings("src/main/resources/buildings_geocoded.json");

            Map<String, Building> buildingMap = new HashMap<String, Building>();
            for (Building building : buildings) {
                buildingMap.put(building.getBuildingCode(), building);
            }


            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONObject jsonObject = new JSONObject(jsonString);

            Set keys = jsonObject.keySet();
            for (Object key : keys) {
                String courseString = key.toString();
                JSONObject courseData = jsonObject.getJSONObject(courseString);

                String courseCode = courseData.getString("code");

                float courseCredits = 0.0F;

                int breadthCategory = 0;
                String breadthString = courseData.getString("breadthCategories");
                for (int i = 1; i <= 5; i++) {
                    if (breadthString.contains("("+i+")")) {
                        breadthCategory = i;
                        break;
                    }
                }


                String term = courseData.getString("section");
                String courseName = courseData.getString("courseTitle");
                String courseDescription = courseData.getString("courseDescription");

                List<Section> courseSections = new ArrayList<Section>();

                Course course = new Course(courseCode, courseName, courseDescription, courseCredits, term, courseSections, null, breadthCategory);

                JSONObject sectionsData = courseData.getJSONObject("meetings");
                for (Object sectionKey : sectionsData.keySet()) {
                    JSONObject sectionData = sectionsData.getJSONObject((String) sectionKey);

                    List<String> instructors = new ArrayList<>();
                    try {
                        JSONObject instructorData = sectionData.getJSONObject("instructors");
                        for (Object instructorKey : instructorData.keySet()) {
                            instructors.add(instructorData.getJSONObject(instructorKey.toString()).getString("lastName"));
                        }
                    } catch (JSONException e) {
                        // Happens when the result is a blank array. Make this nicer later
                    }
                    List<TimeSlot> times = new ArrayList<>();

                    JSONObject sectionSchedule = null;
                    try {
                        sectionSchedule = sectionData.getJSONObject("schedule");
                    } catch (JSONException e) {
                        // Happens when it's a blank array (section is cancelled).
                        continue;
                    }
                    for (Object timeKey : sectionSchedule.keySet()) {
                        JSONObject timeData = sectionSchedule.getJSONObject(timeKey.toString());
                        if (timeData.isNull("meetingDay")) {
                            continue;
                        }
                        String meetingDay = timeData.getString("meetingDay");
                        String startString = timeData.getString("meetingStartTime"); // 14:00
                        String endString = timeData.getString("meetingEndTime"); // 14:00

                        // TODO: Multi-semester classes that change rooms midway through the year aren't accounted for.
                        // Currently: it checks if fall is null, then returns winter if so and fall if not
                        // Also, this code is gross.
                        Building building;
                        try {
                            if (timeData.isNull("assignedRoom1")) {
                                if (timeData.isNull("assignedRoom2")) {
                                    building = null;
                                }
                                else {
                                    String room2 = timeData.getString("assignedRoom2");
                                    building = buildingMap.get(room2.substring(0, 2));
                                }
                            } else {
                                String room1 = timeData.getString("assignedRoom1");
                                building = buildingMap.get(room1.substring(0, 2));
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            building = null;
                        }


                        int dayValue = days.indexOf(meetingDay) + 1;
                        LocalTime startTime = LocalTime.parse(startString);
                        LocalTime endTime = LocalTime.parse(endString);
                        TimeSlot timeSlot = new TimeSlot(dayValue, startTime, endTime, building);
                        times.add(timeSlot);

                    }

                    int enrolled = 0;
                    int capacity = 0;
                    try {
                        enrolled = Integer.parseInt(sectionData.getString("actualEnrolment"));
                        capacity = Integer.parseInt(sectionData.getString("enrollmentCapacity"));
                    } catch (Exception e) {
                    }

                    Section section = new Section(sectionKey.toString(), times, enrolled, instructors, capacity, course);
                    courseSections.add(section);
                }


                courses.add(course);

            }

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> getCourses() {
        return courses;
    }

}