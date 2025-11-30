package app;

import data_access.CourseDataAccessInterface;
import data_access.InMemoryTimetableDataAccess;
import data_access.JSONCourseDataAccess;
import data_access.TimetableDataAccessInterface;
import entity.Course;
import entity.Timetable;
import interface_adapter.calculatewalkingtime.CalculateWalkingController;
import interface_adapter.calculatewalkingtime.CalculateWalkingInterface;
import interface_adapter.calculatewalkingtime.CalculateWalkingPresenter;
import interface_adapter.calculatewalkingtime.CalculateWalkingViewModel;
import interface_adapter.controller.AddCourseController;
import interface_adapter.controller.SearchCourseController;
import interface_adapter.presenter.AddCoursePresenter;
import interface_adapter.presenter.SearchCoursePresenter;
import view.SearchPanelAdapter;
import usecase.calculatewalkingtime.CalculateWalkingDataAccessInterface;
import usecase.calculatewalkingtime.CalculateWalkingInputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingInteractor;
import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import view.*;
import interface_adapter.presenter.SearchPanelInterface;
import view.TimetableViewAdapter;
import interface_adapter.presenter.TimetableViewInterface;
import usecase.addcourse.AddCourseInputBoundary;
import usecase.addcourse.AddCourseInteractor;
import usecase.addcourse.AddCourseOutputBoundary;
import usecase.search.SearchCourseInputBoundary;
import usecase.search.SearchCourseInteractor;
import usecase.search.SearchCourseOutputBoundary;
import view.MainView;
import view.SearchPanel;
import view.SectionView;
import view.TimetableView;

import javax.swing.*;

import view.WalkingTimeView;
import view.WalkingTimeViewAdapter;
import interface_adapter.calculatewalkingtime.CalculateWalkingInterface;
import interface_adapter.calculatewalkingtime.CalculateWalkingPresenter;
import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingDataAccessInterface;
import usecase.calculatewalkingtime.CalculateWalkingInteractor;
import usecase.calculatewalkingtime.CalculateWalkingInputBoundary;
import interface_adapter.calculatewalkingtime.CalculateWalkingController;
import data_access.WalkingTimeDataAccessObject;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Create data access
                CourseDataAccessInterface courseDataAccess = new JSONCourseDataAccess();
                TimetableDataAccessInterface timetableDataAccess = new InMemoryTimetableDataAccess();

                // Create UI views
                MainView mainView = new MainView();
                TimetableView timetableView = mainView.getTimetableView();
                SearchPanel searchPanel = mainView.getSearchPanel();
                WalkingTimeView walkingTimeView = mainView.getWalkingTimeView();

                // Create view adapters
                TimetableViewInterface timetableViewAdapter = new TimetableViewAdapter(timetableView);
                SearchPanelInterface searchViewAdapter = new SearchPanelAdapter(searchPanel);
                CalculateWalkingInterface walkingViewAdapter = new WalkingTimeViewAdapter(walkingTimeView);

                // Create presenters
                AddCourseOutputBoundary addCoursePresenter = new AddCoursePresenter(timetableViewAdapter);
                SearchCourseOutputBoundary searchCoursePresenter = new SearchCoursePresenter(searchViewAdapter);
                CalculateWalkingOutputBoundary walkingPresenter = new CalculateWalkingPresenter(walkingViewAdapter);

                AddCourseInputBoundary addCourseInteractor =
                        new AddCourseInteractor(timetableDataAccess, courseDataAccess, addCoursePresenter);
                SearchCourseInputBoundary searchCourseInteractor =
                        new SearchCourseInteractor(courseDataAccess, searchCoursePresenter);
                CalculateWalkingDataAccessInterface walkingDataAccess = new WalkingTimeDataAccessObject();
                CalculateWalkingInputBoundary walkingInteractor =
                        new CalculateWalkingInteractor(walkingDataAccess, walkingPresenter);


                // Create controllers
                AddCourseController addCourseController = new AddCourseController(addCourseInteractor);
                SearchCourseController searchCourseController = new SearchCourseController(searchCourseInteractor);

                CalculateWalkingController walkingController = new CalculateWalkingController(walkingInteractor);

                walkingTimeView.setWalkingController(walkingController);

                walkingTimeView.setTimetable(timetableDataAccess.getTimetable());

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