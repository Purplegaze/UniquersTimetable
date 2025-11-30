package app;

import data_access.*;
import entity.Course;
import interface_adapter.controller.AddCourseController;
import interface_adapter.controller.ExportTimetableController;
import interface_adapter.controller.SearchCourseController;
import interface_adapter.presenter.*;
import usecase.export.ExportTimetableDataAccessInterface;
import usecase.export.ExportTimetableInputBoundary;
import usecase.export.ExportTimetableInteractor;
import usecase.export.ExportTimetableOutputBoundary;
import view.*;
import usecase.addcourse.AddCourseInputBoundary;
import usecase.addcourse.AddCourseInteractor;
import usecase.addcourse.AddCourseOutputBoundary;
import usecase.search.SearchCourseInputBoundary;
import usecase.search.SearchCourseInteractor;
import usecase.search.SearchCourseOutputBoundary;

import javax.swing.*;

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

                // Create UI views
                MainView mainView = new MainView();
                TimetableView timetableView = mainView.getTimetableView();
                SearchPanel searchPanel = mainView.getSearchPanel();
                ExportImportPanel exportImportPanel = mainView.getExportImportPanel();

                // Create view adapters
                TimetableViewInterface timetableViewAdapter = new TimetableViewAdapter(timetableView);
                SearchPanelInterface searchViewAdapter = new SearchPanelAdapter(searchPanel);

                // Create presenters
                AddCourseOutputBoundary addCoursePresenter = new AddCoursePresenter(timetableViewAdapter);
                SearchCourseOutputBoundary searchCoursePresenter = new SearchCoursePresenter(searchViewAdapter);
                ExportTimetableOutputBoundary exportTimetablePresenter = new ExportTimetablePresenter();

                // Create use case interactors
                AddCourseInputBoundary addCourseInteractor =
                        new AddCourseInteractor(timetableDataAccess, courseDataAccess, addCoursePresenter);
                SearchCourseInputBoundary searchCourseInteractor =
                        new SearchCourseInteractor(courseDataAccess, searchCoursePresenter);
                ExportTimetableInputBoundary exportTimetableInteractor =
                        new ExportTimetableInteractor(exportTimetablePresenter, timetableDataAccess, courseDataAccess, exportDataAccess);

                // Create controllers
                AddCourseController addCourseController = new AddCourseController(addCourseInteractor);
                SearchCourseController searchCourseController = new SearchCourseController(searchCourseInteractor);
                ExportTimetableController exportTimetableController = new ExportTimetableController(exportTimetableInteractor);

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
                        } else {
                            JOptionPane.showMessageDialog(mainView,
                                    "Course not found: " + resultId,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
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