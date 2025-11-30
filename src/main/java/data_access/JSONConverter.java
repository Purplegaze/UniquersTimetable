package data_access;

import entity.Section;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONConverter {

    public static JSONObject logSections(List<Section> sections) {
        JSONObject result = new JSONObject();
        HashMap<String, JSONArray> courses = new HashMap<>();
        for (Section section : sections) {
            String courseCode = section.getCourse().getCourseCode();
            if (!courses.containsKey(courseCode)) {
                courses.put(courseCode,  new JSONArray());
            }
            String sectionCode = section.getSectionId();
            courses.get(courseCode).put(sectionCode);
        }
        for (String courseCode : courses.keySet()) {
            JSONArray courseArray = courses.get(courseCode);
            result.put(courseCode, courseArray);
        }
        return result;
    }
}
