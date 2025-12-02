package usecase.filter_courses;

import entity.Course;
import java.util.List;
import java.util.stream.Collectors;
import usecase.filter_courses.FilterCoursesInputBoundary;
import usecase.filter_courses.FilterCoursesOutputBoundary;
import usecase.filter_courses.FilterCoursesInputData;
import usecase.filter_courses.FilterCoursesOutputData;

public class FilterCoursesInteractor implements FilterCoursesInputBoundary {

    private final List<Course> allCourses;
    private final FilterCoursesOutputBoundary presenter;

    public FilterCoursesInteractor(List<Course> allCourses,
                                   FilterCoursesOutputBoundary presenter) {
        this.allCourses = allCourses;
        this.presenter = presenter;
    }

    @Override
    public void execute(FilterCoursesInputData inputData) {
        String q = inputData.getQuery() == null ? "" : inputData.getQuery().toLowerCase().trim();
        Integer breadth = inputData.getBreadth();

        List<Course> filtered = allCourses.stream()
                .filter(c -> {
                    boolean breadthMatch = (breadth == null) ||
                            (c.getBreadthCategory() == breadth);

                    boolean queryMatch = q.isEmpty()
                            || c.getCourseCode().toLowerCase().contains(q)
                            || c.getCourseName().toLowerCase().contains(q);

                    return breadthMatch && queryMatch;
                })
                .collect(Collectors.toList());

        presenter.present(new FilterCoursesOutputData(filtered));
    }
}