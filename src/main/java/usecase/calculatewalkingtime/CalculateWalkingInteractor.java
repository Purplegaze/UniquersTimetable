package usecase.calculatewalkingtime;

import entity.Timetable;
import entity.TimetableBlock;

import java.util.HashMap;
import java.util.Map;

/**
 * The Calculate Walking Time Interactor.
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

        Timetable timetable = inputData.getTimetable();

        // Check if timetable is empty or null
        if (timetable == null || timetable.getBlocks().isEmpty()) {
            presenter.prepareFailView("No courses found in timetable.");
            return;
        }

        Map<String, Integer> walkingTimes = new HashMap<>();

        for (TimetableBlock block : timetable.getBlocks()) {

            TimetableBlock next = block.getNextCourse();

            // only calculate if back-to-back
            if (next != null && block.getTimeSlot().immediatelyPrecedes(next.getTimeSlot())) {

                double walkingTime = dataAccess.calculateWalking(
                        block.getCourse().getLocation(),
                        next.getCourse().getLocation());

                String key = block.getTimeSlot().getDayOfWeek() + " "
                        + block.getCourse().getCourseCode()
                        + " → "
                        + next.getCourse().getCourseCode();

                walkingTimes.put(key, (int) walkingTime);
            }
        }

        if (walkingTimes.isEmpty()) {
            presenter.prepareFailView("No back-to-back classes found.");
        } else {
            presenter.prepareSuccessView(new CalculateWalkingOutputData(walkingTimes));
        }
    }
}
