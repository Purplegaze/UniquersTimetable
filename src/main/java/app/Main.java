package app;

import data_access.CourseDataAccessInterface;
import data_access.CourseEvalDataReader;
import data_access.InMemoryTimetableDataAccess;
import data_access.JSONCourseDataAccess;
import data_access.TimetableDataAccessInterface;
import entity.Course;
import interface_adapter.calculatewalkingtime.CalculateWalkingController;
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
import view.TimetableClickListener;

import usecase.calculatewalkingtime.CalculateWalkingDataAccessInterface;
import usecase.calculatewalkingtime.CalculateWalkingInputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingInteractor;
import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;

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

import javax.swing.*;

import view.WalkingTimeView;
//import view.WalkingTimeViewAdapter;
import data_access.WalkingTimeDataAccessObject;
import interface_adapter.customtimefilter.CustomTimeFilterController;
import interface_adapter.customtimefilter.CustomTimeFilterPresenter;
import usecase.customtimefilter.CustomTimeFilterInputBoundary;
import usecase.customtimefilter.CustomTimeFilterInteractor;
import usecase.customtimefilter.CustomTimeFilterOutputBoundary;

// Use Case 6: Return to Search
import interface_adapter.returntosearch.ReturnToSearchController;
import interface_adapter.returntosearch.ReturnToSearchPresenter;
import usecase.returntosearch.ReturnToSearchInputBoundary;
import usecase.returntosearch.ReturnToSearchInteractor;
import usecase.returntosearch.ReturnToSearchOutputBoundary;
import interface_adapter.filter_courses.FilterCoursesController;
import interface_adapter.filter_courses.FilterCoursesPresenter;
import interface_adapter.filter_courses.FilterCoursesViewModel;

import usecase.filter_courses.FilterCoursesInputBoundary;
import usecase.filter_courses.FilterCoursesInteractor;
import usecase.filter_courses.FilterCoursesOutputBoundary;

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

                // Reader for ratings
                CourseEvalDataReader ratingReader = new CourseEvalDataReader("src/main/resources/course_eval_data.csv");

                // ViewModels
                SearchViewModel searchViewModel = new SearchViewModel();
                AddCourseViewModel addCourseViewModel = new AddCourseViewModel();
                DeleteSectionViewModel deleteSectionViewModel = new DeleteSectionViewModel();

                FilterCoursesViewModel filterCoursesViewModel = new FilterCoursesViewModel();
                FilterCoursesOutputBoundary filterCoursesPresenter =
                        new FilterCoursesPresenter(searchViewModel);
                FilterCoursesInputBoundary filterCoursesInteractor =
                        new FilterCoursesInteractor(courseDataAccess.getAllCourses(), filterCoursesPresenter);
                FilterCoursesController filterCoursesController =
                        new FilterCoursesController(filterCoursesInteractor);

                // Create UI views
                MainView mainView = new MainView();
                TimetableView timetableView = mainView.getTimetableView();
                timetableView.setAddCourseViewModel(addCourseViewModel);
                timetableView.setDeleteSectionViewModel(deleteSectionViewModel);

                SearchPanel searchPanel = mainView.getSearchPanel();
                WalkingTimeView walkingTimeView = mainView.getWalkingTimeView();

                // Create presenters

                AddCourseOutputBoundary addCoursePresenter = new AddCoursePresenter(addCourseViewModel);
                SearchCourseOutputBoundary searchCoursePresenter = new SearchCoursePresenter(searchViewModel);
                DeleteSectionOutputBoundary deleteSectionPresenter = new DeleteSectionPresenter(deleteSectionViewModel);

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
           
                // Custom Time Filter use case (Use Case #3)
                CustomTimeFilterOutputBoundary customTimeFilterPresenter =
                        new CustomTimeFilterPresenter(searchViewModel);

                CustomTimeFilterInputBoundary customTimeFilterInteractor =
                        new CustomTimeFilterInteractor(courseDataAccess, customTimeFilterPresenter);

                CustomTimeFilterController customTimeFilterController =
                        new CustomTimeFilterController(customTimeFilterInteractor);

                // Use Case 6: Return to Search Components
                ReturnToSearchOutputBoundary returnToSearchPresenter = new ReturnToSearchPresenter(searchPanel);
                ReturnToSearchInputBoundary returnToSearchInteractor = new ReturnToSearchInteractor(returnToSearchPresenter);
                ReturnToSearchController returnToSearchController = new ReturnToSearchController(returnToSearchInteractor);

                timetableView.setClickListener(new TimetableClickListener() {
                    @Override
                    public void onEmptySlotClicked(String day, String startTime, String endTime) {
                        // For now, we don’t combine with a text query – just use an empty query string.
                        String query = "";
                        customTimeFilterController.execute(query, day, startTime, endTime);
                    }

                    @Override
                    public void onCourseClicked(String courseCode) {
                        returnToSearchController.execute(courseCode);
                    }
                });


                // Interactor for ViewCourse, injecting the rating reader
                ViewCourseInputBoundary viewCourseInteractor =
                        new ViewCourseInteractor(courseDataAccess, ratingReader, viewCoursePresenter);

                // Create controllers
                AddCourseController addCourseController = new AddCourseController(addCourseInteractor);
                SearchCourseController searchCourseController = new SearchCourseController(searchCourseInteractor);
                DeleteSectionController deleteSectionController = new DeleteSectionController(deleteCourseInteractor);
                ViewCourseController viewCourseController = new ViewCourseController(viewCourseInteractor);
                timetableView.setDeleteController(deleteSectionController);

                // Wiring for Calculate Walking Use Case
                CalculateWalkingDataAccessInterface walkingDataAccess = new WalkingTimeDataAccessObject();

                CalculateWalkingOutputBoundary walkingPresenter =
                        new CalculateWalkingPresenter(walkingTimeView.getViewModel());

                CalculateWalkingInputBoundary walkingInteractor =
                        new CalculateWalkingInteractor(walkingDataAccess, timetableDataAccess, walkingPresenter);

                CalculateWalkingController walkingController =
                        new CalculateWalkingController(walkingInteractor);

                searchPanel.setController(searchCourseController);
                searchPanel.setViewModel(searchViewModel);
                searchPanel.setFilterController(filterCoursesController);
                searchPanel.setCustomTimeFilterController(customTimeFilterController);
                timetableView.setDeleteController(deleteSectionController);
                walkingTimeView.setWalkingController(walkingController);

                // Observe ViewModel to display SectionView when a course is loaded with ratings
                viewCourseViewModel.addPropertyChangeListener(evt -> {
                    if ("course".equals(evt.getPropertyName())) {
                        Course course = (Course) evt.getNewValue();
                        if (course != null) {
                            // Display the SectionView using the course (now with ratings) and the add controller
                            new SectionView(viewCourseViewModel, addCourseController).display();
                        }
                    } else if ("error".equals(evt.getPropertyName())) {
                        JOptionPane.showMessageDialog(mainView,
                                viewCourseViewModel.getError(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });

                // Wire UI events to controllers
                searchViewModel.addPropertyChangeListener(evt -> {
                    System.out.println("Property changed: " + evt.getPropertyName());

                    if ("selectedCourse".equals(evt.getPropertyName())) {
                        String courseCode = searchViewModel.getSelectedCourseCode();
                        System.out.println("Selected course: " + courseCode);

                        if (courseCode != null) {
                            viewCourseController.execute(courseCode);
                            
                        }
                    }
                });

                searchCourseController.execute("");
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