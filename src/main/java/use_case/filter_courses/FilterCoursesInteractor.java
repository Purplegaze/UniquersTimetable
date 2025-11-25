package use_case.filter_courses;

import entity.Course;

import java.util.List;

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
        String query = inputData.getQuery();
        Integer breadth = inputData.getBreadth();

        List<Course> source = allCourses;
        if (breadth != null) {
            source = allCourses.stream()
                    .filter(c -> c.getBreadthCategory() != null
                            && c.getBreadthCategory() == breadth)
                    .collect(java.util.stream.Collectors.toList());
        }


        String q = query == null ? "" : query.toLowerCase().trim();

        List<Course> result = source.stream()
                .filter(c -> {
                    String code = c.getCourseCode() == null ? "" : c.getCourseCode();
                    String name = c.getCourseName() == null ? "" : c.getCourseName();
                    return q.isEmpty()
                            || code.toLowerCase().contains(q)
                            || name.toLowerCase().contains(q);
                })
                .collect(java.util.stream.Collectors.toList());

        presenter.present(new FilterCoursesOutputData(result));
    }
}
