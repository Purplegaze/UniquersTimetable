package use_case.filter_courses;

import java.util.List;
import entity.Course;

public interface FilterCoursesOutputBoundary {
    void present(FilterCoursesOutputData outputData);
}
