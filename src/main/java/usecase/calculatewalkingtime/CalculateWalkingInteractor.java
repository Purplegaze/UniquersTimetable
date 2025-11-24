package usecase.calculatewalkingtime;
import entity.Building;
import entity.Timetable;
import entity.TimetableBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Calculate Walking Interactor.
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
    public void execute(CalculateWalkingInputData calculateWalkingInputData) {
        Timetable timetable = calculateWalkingInputData.getTimetable();
        Map<String, Integer> walkingTimes = new HashMap<>();

        for (TimetableBlock block : timetable.getBlocks()) {
            TimetableBlock next = block.getNextCourse();
            if  (next != null) {
                if (block.getTimeSlot().immediatelyPrecedes(block.getNextCourse().getTimeSlot())) {
                    double walkingTime = dataAccess.calculateWalking(block.getCourse().getLocation(),
                            block.getNextCourse().getCourse().getLocation());

                    walkingTimes.put(block.getNextCourse().getCourse().getCourseName(), (int) walkingTime);
                }
            }

        }

        presenter.prepareSuccessView(new CalculateWalkingOutputData(walkingTimes));
    }
}
