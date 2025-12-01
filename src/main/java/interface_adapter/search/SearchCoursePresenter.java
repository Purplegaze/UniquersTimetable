package interface_adapter.search;

import entity.Course;
import entity.Section;
import interface_adapter.search.SearchViewModel;
import interface_adapter.search.SearchViewModel.SearchResult;
import usecase.search.SearchCourseOutputBoundary;
import usecase.search.SearchCourseOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for the Search Course use case.
 */
public class SearchCoursePresenter implements SearchCourseOutputBoundary {

    private final SearchViewModel viewModel;

    public SearchCoursePresenter(SearchViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSearchResults(SearchCourseOutputData outputData) {
        if (outputData == null) {
            presentError("No output data provided");
            return;
        }

        // Get Course entities from output data
        List<Course> courses = outputData.getCourses();

        // Convert entities to view models
        List<SearchResult> results = new ArrayList<>();
        for (Course course : courses) {
            results.add(new SearchResult(
                    course.getCourseCode(),
                    course.getCourseName()
            ));
        }

        viewModel.setResults(results);
    }

    @Override
    public void presentNoResults() {
        viewModel.setNoResults();
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setError(errorMessage);
    }
}