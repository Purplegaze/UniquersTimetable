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
        Timetable timetable = inputData.getTimetable();

        if (timetable == null || timetable.getBlocks().isEmpty()) {
            presenter.prepareFailView("No courses found in timetable.");
            return;
        }

        Map<String, Integer> walkingTimes = new HashMap<>();

        for (TimetableBlock current : timetable.getBlocks()) {

            TimetableBlock next = current.getNextCourse();

            if (next != null && current.getTimeSlot().immediatelyPrecedes(next.getTimeSlot())) {

                Building from = current.getTimeSlot().getBuilding();
                Building to = next.getTimeSlot().getBuilding();

                String key = current.getTimeSlot().getDayName() + ": " +
                        current.getCourse().getCourseCode()
                        + " → "
                        + next.getCourse().getCourseCode();

                if (from.getBuildingCode() == null || to.getBuildingCode() == null ||
                        from.getBuildingCode().equalsIgnoreCase("TBD") ||
                        to.getBuildingCode().equalsIgnoreCase("TBD")) {
                    walkingTimes.put(key, -1);
                    continue;
                }

                double rawTime = dataAccess.calculateWalking(from, to);
                int roundedTime = (int) Math.round(rawTime);

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
