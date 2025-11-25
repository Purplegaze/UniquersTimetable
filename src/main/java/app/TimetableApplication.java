package app;

import data_access.CourseDataAccessInterface;
import data_access.JSONCourseDataAccess;
import data_access.InMemoryTimetableDataAccess;
import data_access.TimetableDataAccessInterface;
import entity.Course;
import usecase.search.SearchCourseInputBoundary;
import usecase.search.SearchCourseInputData;
import usecase.search.SearchCourseOutputBoundary;
import usecase.search.SearchCourseOutputData;
import usecase.search.SearchCourseInteractor;
import usecase.addcourse.AddCourseInputBoundary;
import usecase.addcourse.AddCourseOutputBoundary;
import usecase.addcourse.AddCourseOutputData;
import usecase.addcourse.AddCourseInteractor;
import view.MainView;
import view.SearchPanel;
import view.SectionView;
import view.TimetableView;

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
            TimetableView timetableView = mainView.getTimetableView();

            // Data Access
            CourseDataAccessInterface courseDataAccess = new JSONCourseDataAccess();
            TimetableDataAccessInterface timetableDataAccess = new InMemoryTimetableDataAccess();

            CourseColorHelper colorHelper = new CourseColorHelper();

            AddCourseInputBoundary addCourseInteractor = createAddCourseInteractor(
                    timetableDataAccess, timetableView, colorHelper);

            SearchCourseInputBoundary searchInteractor = createSearchInteractor(
                    courseDataAccess, searchPanel);

            connectSearchPanel(searchPanel, searchInteractor, courseDataAccess, addCourseInteractor,
                    timetableDataAccess);

            searchInteractor.execute(new SearchCourseInputData(""));

            mainView.display();
        });
    }

    private static AddCourseInputBoundary createAddCourseInteractor(
            TimetableDataAccessInterface timetableDataAccess,
            TimetableView timetableView,
            CourseColorHelper colorHelper) {

        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) {
                displayCourseOnTimetable(timetableView, outputData, colorHelper, false);
            }

            @Override
            public void presentConflict(AddCourseOutputData outputData) {
                String conflictMsg = "Conflict with: " + String.join(", ", outputData.getConflictingCourses());
                timetableView.showConflictWarning(conflictMsg);
                displayCourseOnTimetable(timetableView, outputData, colorHelper, true);
            }

            @Override
            public void presentError(String errorMessage) {
                timetableView.showErrorMessage(errorMessage);
            }
        };

        return new AddCourseInteractor(timetableDataAccess, presenter);
    }



    /**
     * Display a course on the timetable.
     */
    private static void displayCourseOnTimetable(
            TimetableView timetableView,
            AddCourseOutputData outputData,
            CourseColorHelper colorHelper,
            boolean hasConflict) {

        TimetableView.TimetableSlotItem item = new TimetableView.TimetableSlotItem(
                outputData.getCourseCode(),
                outputData.getSectionCode(),
                outputData.getLocation(),
                colorHelper.getNextColor(),
                hasConflict
        );

        timetableView.displayCourse(
                outputData.getDay(),
                outputData.getStartHour(),
                outputData.getEndHour(),
                item
        );
    }

    private static void connectSearchPanel(SearchPanel searchPanel,
                                           SearchCourseInputBoundary interactor,
                                           CourseDataAccessInterface courseDataAccess,
                                           AddCourseInputBoundary addCourseInteractor,
                                           TimetableDataAccessInterface timetableDataAccess) {

        searchPanel.setListener(new SearchPanel.SearchPanelListener() {
            @Override
            public void onSearchRequested(String query) {
                interactor.execute(new SearchCourseInputData(query));
            }

            @Override
            public void onResultSelected(String courseCode) {
                Course course = courseDataAccess.findByCourseCode(courseCode);
                if (course != null) {
                    new SectionView(course, addCourseInteractor, timetableDataAccess);
                }
            }
        });
    }

    private static SearchCourseInputBoundary createSearchInteractor(
            CourseDataAccessInterface courseDataAccess,
            SearchPanel searchPanel) {

        SearchCourseOutputBoundary presenter = new SearchCourseOutputBoundary() {
            @Override
            public void presentSearchResults(SearchCourseOutputData outputData) {
                List<SearchPanel.SearchResultItem> viewModels = new ArrayList<>();
                for (SearchCourseOutputData.ResultItem item : outputData.getResults()) {
                    String displayText = item.getCourseCode() + " - " + item.getCourseName();
                    viewModels.add(new SearchPanel.SearchResultItem(item.getCourseCode(), displayText));
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