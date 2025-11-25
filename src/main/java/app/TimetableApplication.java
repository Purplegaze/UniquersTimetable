package app;

import data_access.CourseDataAccessInterface;
import data_access.InMemoryCourseDataAccess;
import entity.Course;
import usecase.SearchCourse;
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

            // Data Access (placeholder - swap for API later)
            CourseDataAccessInterface courseDataAccess = new InMemoryCourseDataAccess();

            SearchCourse.OutputBoundary presenter = new SearchCourse.OutputBoundary() {
                @Override
                public void presentSearchResults(SearchCourse.OutputData outputData) {
                    List<SearchPanel.SearchResultItem> viewModels = new ArrayList<>();
                    for (SearchCourse.OutputData.ResultItem item : outputData.getResults()) {
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

            SearchCourse.InputBoundary interactor = new SearchCourse.Interactor(
                    courseDataAccess,
                    presenter
            );

            searchPanel.setListener(new SearchPanel.SearchPanelListener() {
                @Override
                public void onSearchRequested(String query) {
                    interactor.execute(new SearchCourse.InputData(query));
                }

                @Override
                public void onResultSelected(String courseCode) {
                    Course course = courseDataAccess.findByCourseCode(courseCode);
                    if (course != null) {
                        new SectionView(course);
                    }
                }
            });

            interactor.execute(new SearchCourse.InputData(""));

            mainView.display();
        });
    }
}
