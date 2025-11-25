package data_access;
import entity.*;

import java.util.List;

/**
 * Parse the JSON Parser with resources/2017_ttb_archive.json
 * For testing :) Should be deleted later.
 */
public class ParseCourseArchiveData {
    public static void main(String[] args) {
        JSONParser jsonParser = new JSONParser();
        List<Course> courses = jsonParser.getCourses();
        System.out.println(courses.size());
    }
}
