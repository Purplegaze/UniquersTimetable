package app;

import data_access.CourseDataAccessInterface;
import data_access.InMemoryCourseDataAccess;
import entity.Course;
import usecase.search.SearchCourseInputBoundary;
import usecase.search.SearchCourseInputData;
import usecase.search.SearchCourseOutputBoundary;
import usecase.search.SearchCourseOutputData;
import usecase.search.SearchCourseInteractor;
import view.MainView;
import view.SearchPanel;
import view.SectionView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Timetable Application
 */
public class TimetableApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            MainView mainView = new MainView();
            SearchPanel searchPanel = mainView.getSearchPanel();

            // Data Access
            CourseDataAccessInterface courseDataAccess = new InMemoryCourseDataAccess();

            SearchCourseInputBoundary interactor = createSearchCourseInteractor(searchPanel, courseDataAccess);

            connectSearchPanel(searchPanel, interactor, courseDataAccess);

            interactor.execute(new SearchCourseInputData(""));

            mainView.display();
        });
    }

    private static void connectSearchPanel(SearchPanel searchPanel,
                                           SearchCourseInputBoundary interactor,
                                           CourseDataAccessInterface courseDataAccess) {

        searchPanel.setListener(new SearchPanel.SearchPanelListener() {
            @Override
            public void onSearchRequested(String query) {
                interactor.execute(new SearchCourseInputData(query));
            }

            @Override
            public void onResultSelected(String courseCode) {
                Course course = courseDataAccess.findByCourseCode(courseCode);
                if (course != null) {
                    new SectionView(course);
                }
            }
        });
    }

    private static SearchCourseInputBoundary createSearchCourseInteractor(SearchPanel searchPanel, CourseDataAccessInterface courseDataAccess) {
        SearchCourseOutputBoundary presenter = new SearchCourseOutputBoundary() {
            @Override
            public void presentSearchResults(SearchCourseOutputData outputData) {
                List<SearchPanel.SearchResultItem> viewModels = new ArrayList<>();
                for (SearchCourseOutputData.ResultItem item : outputData.getResults()) {
                    String displayText = item.getCourseCode() + " - " + item.getCourseName();
                    viewModels.add(new SearchPanel.SearchResultItem(
                            item.getCourseCode(),
                            displayText
                    ));
                }
                searchPanel.displayResults(viewModels);
            }

            @Override
            public void presentNoResults() {
                searchPanel.displayNoResults();
            }

            @Override
            public void presentError(String errorMessage) {
                searchPanel.displayError(errorMessage);
            }
        };
        return new SearchCourseInteractor(courseDataAccess, presenter);
    }
}