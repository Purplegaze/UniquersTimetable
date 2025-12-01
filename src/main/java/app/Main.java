package app;

import data_access.*;
import entity.Course;
import interface_adapter.calculatewalkingtime.CalculateWalkingController;
import interface_adapter.calculatewalkingtime.CalculateWalkingInterface;
import interface_adapter.calculatewalkingtime.CalculateWalkingPresenter;
import interface_adapter.controller.*;
import interface_adapter.presenter.*;
import usecase.export.ExportTimetableDataAccessInterface;
import usecase.export.ExportTimetableInputBoundary;
import usecase.export.ExportTimetableInteractor;
import usecase.export.ExportTimetableOutputBoundary;
import view.SearchPanelAdapter;
import usecase.calculatewalkingtime.CalculateWalkingDataAccessInterface;
import usecase.calculatewalkingtime.CalculateWalkingInputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingInteractor;
import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import view.*;
import view.TimetableViewAdapter;
import interface_adapter.viewmodel.ViewCourseViewModel;
import usecase.addcourse.AddCourseInputBoundary;
import usecase.addcourse.AddCourseInteractor;
import usecase.addcourse.AddCourseOutputBoundary;
import usecase.search.SearchCourseInputBoundary;
import usecase.search.SearchCourseInteractor;
import usecase.search.SearchCourseOutputBoundary;
import usecase.deletesection.DeleteSectionInputBoundary;
import usecase.deletesection.DeleteSectionInteractor;
import usecase.deletesection.DeleteSectionOutputBoundary;
import view.MainView;
import view.SearchPanel;
import view.SectionView;
import view.TimetableView;
import usecase.viewcourse.ViewCourseInputBoundary;
import usecase.viewcourse.ViewCourseInteractor;
import view.*;

import javax.swing.*;

import view.WalkingTimeView;
import view.WalkingTimeViewAdapter;

/**
 * Main entry point for the Timetable Application.
 *
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
                ExportTimetableDataAccessInterface exportDataAccess = new ExportDataAccess();

                // Reader for ratings
                CourseEvalDataReader ratingReader = new CourseEvalDataReader("src/main/resources/course_eval_data.csv");

                // Create UI views
                MainView mainView = new MainView();
                TimetableView timetableView = mainView.getTimetableView();
                SearchPanel searchPanel = mainView.getSearchPanel();
                WalkingTimeView walkingTimeView = mainView.getWalkingTimeView();
                ExportImportPanel exportImportPanel = mainView.getExportImportPanel();

                // Create view adapters
                TimetableViewInterface timetableViewAdapter = new TimetableViewAdapter(timetableView);
                SearchPanelInterface searchViewAdapter = new SearchPanelAdapter(searchPanel);
                CalculateWalkingInterface walkingViewAdapter = new WalkingTimeViewAdapter(walkingTimeView);

                // Create presenters
                AddCourseOutputBoundary addCoursePresenter = new AddCoursePresenter(timetableViewAdapter);
                SearchCourseOutputBoundary searchCoursePresenter = new SearchCoursePresenter(searchViewAdapter);
                DeleteSectionOutputBoundary deleteSectionPresenter = new DeleteSectionPresenter(timetableViewAdapter);
                CalculateWalkingOutputBoundary walkingPresenter = new CalculateWalkingPresenter(walkingViewAdapter);
                ExportTimetableOutputBoundary exportPresenter = new ExportTimetablePresenter();

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
                ExportTimetableInputBoundary exportTimetableInteractor =
                        new ExportTimetableInteractor(exportPresenter, timetableDataAccess, courseDataAccess, exportDataAccess);


                // Interactor for ViewCourse, injecting the rating reader
                ViewCourseInputBoundary viewCourseInteractor =
                        new ViewCourseInteractor(courseDataAccess, ratingReader, viewCoursePresenter);

                // Create controllers
                AddCourseController addCourseController = new AddCourseController(addCourseInteractor);
                SearchCourseController searchCourseController = new SearchCourseController(searchCourseInteractor);
                DeleteSectionController deleteSectionController = new DeleteSectionController(deleteCourseInteractor);
                ExportTimetableController exportTimetableController = new ExportTimetableController(exportTimetableInteractor);

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
                });

                exportImportPanel.setController(exportTimetableController);

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