package usecase.calculatewalkingtime;

import entity.Timetable;
import entity.TimetableBlock;

import java.util.HashMap;
import java.util.Map;

/**
 * The Calculate Walking Time Interactor.
 * Uses stored walking distance data to determine walking time between back-to-back classes.
 */
public class CalculateWalkingInteractor implements CalculateWalkingInputBoundary {

    private final CalculateWalkingDataAccessInterface dataAccess;
    private final CalculateWalkingOutputBoundary presenter;

    public CalculateWalkingInteractor(CalculateWalkingDataAccessInterface dataAccess,
                                      CalculateWalkingOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(CalculateWalkingInputData inputData) {

        //get the most updated timetable info
        Timetable timetable = dataAccess.getTimetable();

        if (timetable == null || timetable.getBlocks().isEmpty()) {
            presenter.prepareFailView("No courses found in timetable.");
            return;
        }

        Map<String, Integer> walkingTimes = new HashMap<>();

        for (TimetableBlock current : timetable.getBlocks()) {

            TimetableBlock next = current.getNextCourse();

            if (next != null && current.getTimeSlot().immediatelyPrecedes(next.getTimeSlot())) {

                double rawTime = dataAccess.calculateWalking(
                        current.getTimeSlot().getBuilding(),
                        next.getTimeSlot().getBuilding()
                );

                int roundedTime = (int) Math.round(rawTime);

                String key = current.getTimeSlot().getDayName() + ": " +
                        current.getCourse().getCourseCode()
                        + " → "
                        + next.getCourse().getCourseCode();

                walkingTimes.put(key, roundedTime);
            }
        }

        if (walkingTimes.isEmpty()) {
            presenter.prepareFailView("No back-to-back classes found.");
        } else {
            presenter.prepareSuccessView(new CalculateWalkingOutputData(walkingTimes));
        }
    }
}
