package interface_adapter.filter_courses;

import entity.Course;
import interface_adapter.search.SearchViewModel;
import interface_adapter.search.SearchViewModel.SearchResult;
import usecase.filter_courses.FilterCoursesOutputBoundary;
import usecase.filter_courses.FilterCoursesOutputData;

import java.util.ArrayList;
import java.util.List;

public class FilterCoursesPresenter implements FilterCoursesOutputBoundary {

    private final SearchViewModel searchViewModel;

    public FilterCoursesPresenter(SearchViewModel searchViewModel) {
        this.searchViewModel = searchViewModel;
    }

    @Override
    public void present(FilterCoursesOutputData outputData) {
        List<Course> courses = outputData.getCourses();
        List<SearchResult> results = new ArrayList<>();

        for (Course c : courses) {
            String code = c.getCourseCode();
            String name = c.getCourseName();
            results.add(new SearchResult(code, name));
        }

        searchViewModel.setResults(results);
    }
}