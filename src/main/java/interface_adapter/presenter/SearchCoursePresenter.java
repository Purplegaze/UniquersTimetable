package interface_adapter.presenter;

import entity.Course;
import entity.Section;
import interface_adapter.view.SearchPanelInterface;
import interface_adapter.viewmodel.SearchResultViewModel;
import usecase.search.SearchCourseOutputBoundary;
import usecase.search.SearchCourseOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for the Search Course use case.
 */
public class SearchCoursePresenter implements SearchCourseOutputBoundary {

    private final SearchPanelInterface view;

    public SearchCoursePresenter(SearchPanelInterface view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        this.view = view;
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
        List<SearchResultViewModel> viewModels = new ArrayList<>();
        for (Course course : courses) {
            SearchResultViewModel viewModel = createViewModel(course);
            viewModels.add(viewModel);
        }

        view.displaySearchResults(viewModels);
    }

    @Override
    public void presentNoResults() {
        view.clearResults();
        view.showNoResultsMessage();
    }

    @Override
    public void presentError(String errorMessage) {
        view.showError(errorMessage);
    }

    /**
     * Create a ViewModel from a Course entity.
     */
    private SearchResultViewModel createViewModel(Course course) {
        String courseCode = course.getCourseCode();
        String courseName = course.getCourseName();
        String term = course.getTerm();
        
        // Check if course has available sections
        boolean hasAvailableSections = hasAvailableSections(course);

        return new SearchResultViewModel(
                courseCode,
                courseName,
                term,
                hasAvailableSections
        );
    }

    /**
     * Check if a course has any sections with available spots.
     */
    private boolean hasAvailableSections(Course course) {
        List<Section> sections = course.getSections();
        if (sections == null || sections.isEmpty()) {
            return false;
        }

        for (Section section : sections) {
            if (!section.isFull()) {
                return true;
            }
        }
        return false;
    }
}
