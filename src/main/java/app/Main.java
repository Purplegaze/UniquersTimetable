package app;

import data_access.CourseDataAccessInterface;
import data_access.CourseEvalDataReader;
import data_access.InMemoryTimetableDataAccess;
import data_access.JSONCourseDataAccess;
import data_access.TimetableDataAccessInterface;
import entity.Course;
import interface_adapter.calculatewalkingtime.CalculateWalkingController;
import interface_adapter.calculatewalkingtime.CalculateWalkingInterface;
import interface_adapter.calculatewalkingtime.CalculateWalkingPresenter;

import interface_adapter.addcourse.AddCourseController;
import interface_adapter.search.SearchCourseController;
import interface_adapter.deletesection.DeleteSectionController;
import interface_adapter.viewcourse.ViewCourseController;

import interface_adapter.addcourse.AddCoursePresenter;
import interface_adapter.search.SearchCoursePresenter;
import interface_adapter.deletesection.DeleteSectionPresenter;
import interface_adapter.viewcourse.ViewCoursePresenter;

import interface_adapter.viewcourse.ViewCourseViewModel;
import interface_adapter.search.SearchViewModel;
import interface_adapter.addcourse.AddCourseViewModel;
import interface_adapter.deletesection.DeleteSectionViewModel;

import interface_adapter.controller.AddCourseController;
import interface_adapter.controller.SearchCourseController;
import interface_adapter.controller.DeleteSectionController;
import interface_adapter.controller.ViewCourseController;
import interface_adapter.presenter.AddCoursePresenter;
import interface_adapter.presenter.SearchCoursePresenter;
import interface_adapter.presenter.DeleteSectionPresenter;
import interface_adapter.customtimefilter.CustomTimeFilterViewModel;
import view.SearchPanelAdapter;
import usecase.calculatewalkingtime.CalculateWalkingDataAccessInterface;
import usecase.calculatewalkingtime.CalculateWalkingInputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingInteractor;
import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;

import view.*;
import interface_adapter.presenter.SearchPanelInterface;
import view.TimetableViewAdapter;
import interface_adapter.presenter.TimetableViewInterface;
import interface_adapter.presenter.ViewCoursePresenter;
import interface_adapter.viewmodel.ViewCourseViewModel;
import interface_adapter.viewmodel.SearchResultViewModel;
import java.util.List;
import usecase.addcourse.AddCourseInputBoundary;
import usecase.addcourse.AddCourseInteractor;
import usecase.addcourse.AddCourseOutputBoundary;

import usecase.search.SearchCourseInputBoundary;
import usecase.search.SearchCourseInteractor;
import usecase.search.SearchCourseOutputBoundary;

import usecase.deletesection.DeleteSectionInputBoundary;
import usecase.deletesection.DeleteSectionInteractor;
import usecase.deletesection.DeleteSectionOutputBoundary;

import usecase.viewcourse.ViewCourseInputBoundary;
import usecase.viewcourse.ViewCourseInteractor;

import view.MainView;
import view.SearchPanel;
import view.SectionView;
import view.TimetableView;
import usecase.viewcourse.ViewCourseInputBoundary;
import usecase.viewcourse.ViewCourseInteractor;
import view.*;
import view.TimetableClickListener;
import interface_adapter.customtimefilter.CustomTimeFilterController;
import interface_adapter.customtimefilter.CustomTimeFilterPresenter;
import usecase.customtimefilter.CustomTimeFilterInteractor;

import javax.swing.*;

import view.WalkingTimeView;
import view.WalkingTimeViewAdapter;
import data_access.WalkingTimeDataAccessObject;

/**
 * Main entry point for the Timetable Application.
 * <p>
 * This is the Composition Root: wires all layers together
 * - Creates all components
 * - Wires dependencies
 * - Connects UI events to controllers
 * - Starts the application
 */

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Create data access
                CourseDataAccessInterface courseDataAccess = new JSONCourseDataAccess();
                TimetableDataAccessInterface timetableDataAccess = new InMemoryTimetableDataAccess();

                // Reader for ratings
                CourseEvalDataReader ratingReader = new CourseEvalDataReader("src/main/resources/course_eval_data.csv");

                // ViewModels
                SearchViewModel searchViewModel = new SearchViewModel();
                AddCourseViewModel addCourseViewModel = new AddCourseViewModel();
                DeleteSectionViewModel deleteSectionViewModel = new DeleteSectionViewModel();

                // Create UI views
                MainView mainView = new MainView();
                TimetableView timetableView = mainView.getTimetableView();
                timetableView.setAddCourseViewModel(addCourseViewModel);
                timetableView.setDeleteSectionViewModel(deleteSectionViewModel);

                SearchPanel searchPanel = mainView.getSearchPanel();
                searchPanel.setViewModel(searchViewModel);

                WalkingTimeView walkingTimeView = mainView.getWalkingTimeView();

                CalculateWalkingInterface walkingViewAdapter = new WalkingTimeViewAdapter(walkingTimeView);

                // Create presenters
                AddCourseOutputBoundary addCoursePresenter = new AddCoursePresenter(addCourseViewModel);
                SearchCourseOutputBoundary searchCoursePresenter = new SearchCoursePresenter(searchViewModel);
                DeleteSectionOutputBoundary deleteSectionPresenter = new DeleteSectionPresenter(deleteSectionViewModel);
                CalculateWalkingOutputBoundary walkingPresenter = new CalculateWalkingPresenter(walkingViewAdapter);

                // View Model and Presenter for ViewCourse Use Case
                ViewCourseViewModel viewCourseViewModel = new ViewCourseViewModel();
                ViewCoursePresenter viewCoursePresenter = new ViewCoursePresenter(viewCourseViewModel);

                // Create use case interactors
                AddCourseInputBoundary addCourseInteractor =
                        new AddCourseInteractor(timetableDataAccess, courseDataAccess, addCoursePresenter);
                SearchCourseInputBoundary searchCourseInteractor =
                        new SearchCourseInteractor(courseDataAccess, searchCoursePresenter);
                DeleteSectionInputBoundary deleteCourseInteractor =
                        new DeleteSectionInteractor(timetableDataAccess, deleteSectionPresenter);
                CalculateWalkingDataAccessInterface walkingDataAccess = new WalkingTimeDataAccessObject();
                CalculateWalkingInputBoundary walkingInteractor =
                        new CalculateWalkingInteractor(walkingDataAccess, walkingPresenter);


                // Interactor for ViewCourse, injecting the rating reader
                ViewCourseInputBoundary viewCourseInteractor =
                        new ViewCourseInteractor(courseDataAccess, ratingReader, viewCoursePresenter);

                // Create controllers
                AddCourseController addCourseController = new AddCourseController(addCourseInteractor);
                SearchCourseController searchCourseController = new SearchCourseController(searchCourseInteractor);
                DeleteSectionController deleteSectionController = new DeleteSectionController(deleteCourseInteractor);

                timetableView.setDeleteController(deleteSectionController);

                CalculateWalkingController walkingController = new CalculateWalkingController(walkingInteractor);

                walkingTimeView.setWalkingController(walkingController);

                walkingTimeView.setTimetable(timetableDataAccess.getTimetable());

                // Controller for ViewCourse
                ViewCourseController viewCourseController = new ViewCourseController(viewCourseInteractor);

                // Observe ViewModel to display SectionView when a course is loaded with ratings
                viewCourseViewModel.addPropertyChangeListener(evt -> {
                    if ("course".equals(evt.getPropertyName())) {
                        Course course = (Course) evt.getNewValue();
                        if (course != null) {
                            // Display the SectionView using the course (now with ratings) and the add controller
                            new SectionView(course, addCourseController).display();
                        }
                    } else if ("error".equals(evt.getPropertyName())) {
                        JOptionPane.showMessageDialog(mainView,
                                viewCourseViewModel.getError(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
                //CustomTimeFilter
                CustomTimeFilterViewModel customTimeFilterViewModel = new CustomTimeFilterViewModel();
                // Listen for changes in the CustomTimeFilterViewModel and update the SearchPanel through its adapter
                customTimeFilterViewModel.addPropertyChangeListener(evt -> {
                    String property = evt.getPropertyName();

                    if (CustomTimeFilterViewModel.PROPERTY_RESULTS.equals(property)) {
                        // New filtered results available → update the SearchPanel with these results
                        List<SearchResultViewModel> results = customTimeFilterViewModel.getResults();
                        searchViewAdapter.displaySearchResults(results);

                    } else if (CustomTimeFilterViewModel.PROPERTY_NO_RESULTS.equals(property)) {
                        // The presenter indicates that no results were found
                        if (customTimeFilterViewModel.isNoResults()) {
                            searchViewAdapter.clearResults();
                            searchViewAdapter.showNoResultsMessage();
                        }

                    } else if (CustomTimeFilterViewModel.PROPERTY_ERROR.equals(property)) {
                        // An error occurred during the use case execution
                        String error = customTimeFilterViewModel.getErrorMessage();
                        if (error != null && !error.isEmpty()) {
                            searchViewAdapter.showError(error);
                        }
                    }
                });

                // ==========================
                // Custom Time Filter Use Case (Use Case #3)
                // ==========================

                // Create presenter
                CustomTimeFilterPresenter customTimeFilterPresenter =
                        new CustomTimeFilterPresenter(customTimeFilterViewModel);

                // Create interactor
                CustomTimeFilterInteractor customTimeFilterInteractor =
                        new CustomTimeFilterInteractor(courseDataAccess, customTimeFilterPresenter);

                // Create controller
                CustomTimeFilterController customTimeFilterController =
                        new CustomTimeFilterController(customTimeFilterInteractor);

                // Wire timetable empty-slot clicks to Custom Time Filter use case
                timetableView.setClickListener(new TimetableClickListener() {
                    @Override
                    public void onEmptySlotClicked(String day, String startTime, String endTime) {
                        // Use the current search query so it combines with existing filters
                        String query = searchPanel.getSearchQuery();
                        customTimeFilterController.execute(query, day, startTime, endTime);
                    }
                });
                // Wire UI events to controllers
                searchPanel.setListener(new SearchPanel.SearchPanelListener() {
                    @Override
                    public void onSearchRequested(String query) {
                        searchCourseController.search(query);
                    }

                    @Override
                    public void onResultSelected(String resultId) {
                        Course course = courseDataAccess.findByCourseCode(resultId);

                        if (course != null) {
                            new SectionView(course, addCourseController).display();
                            walkingTimeView.setTimetable(timetableDataAccess.getTimetable());
                        } else {
                            JOptionPane.showMessageDialog(mainView,
                                    "Course not found: " + resultId,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        viewCourseController.execute(resultId);
                    }
                    @Override
                    public void onCustomTimeFilterRequested(String query,
                                                            String dayOfWeek,
                                                            String startTime,
                                                            String endTime) {

                        customTimeFilterController.execute(query, dayOfWeek, startTime, endTime);
                    }
                });

                searchCourseController.search("");
                mainView.display();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to start application:\n" + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}