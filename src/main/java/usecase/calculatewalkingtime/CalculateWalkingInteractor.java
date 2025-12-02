package usecase.calculatewalkingtime;

import entity.Building;
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

        // Use the timetable passed in from the controller
        Timetable timetable = inputData.getTimetable();

        if (timetable == null || timetable.getBlocks().isEmpty()) {
            presenter.prepareFailView("No courses found in timetable.");
            return;
        }

        Map<String, Integer> walkingTimes = new HashMap<>();

        boolean hasLongWalk = false;


        for (TimetableBlock current : timetable.getBlocks()) {

            TimetableBlock next = current.getNextCourse();
            if (next == null) {
                continue;
            }

            if (!current.getTimeSlot().immediatelyPrecedes(next.getTimeSlot())) {
                continue;
            }

                Building from = current.getTimeSlot().getBuilding();
                Building to = next.getTimeSlot().getBuilding();

            String key = current.getTimeSlot().getDayName() + ": " +
                    current.getTimeSlot().getStartTime().getHour() + "-" +
                    current.getCourse().getCourseCode()
                    + " → "
                    + next.getCourse().getCourseCode();

            if (from.getBuildingCode() == null || to.getBuildingCode() == null ||
                    from.getBuildingCode().equalsIgnoreCase("TBD") ||
                    to.getBuildingCode().equalsIgnoreCase("TBD")) {

                walkingTimes.put(key, -1);
                continue;
            }

            int rounded = (int) Math.round(dataAccess.calculateWalking(from, to));
            walkingTimes.put(key, rounded);

            if (rounded > 10) {
                hasLongWalk = true;
            }
        }

        if (walkingTimes.isEmpty()) {
            presenter.prepareFailView("No back-to-back classes found.");
            return;
        }

        presenter.prepareSuccessView(
                new CalculateWalkingOutputData(walkingTimes, hasLongWalk)
        );
    }
}
