package interface_adapter.customtimefilter;

import usecase.customtimefilter.CustomTimeFilterInputBoundary;
import usecase.customtimefilter.CustomTimeFilterInputData;

public class CustomTimeFilterController {

    private final CustomTimeFilterInputBoundary interactor;

    public CustomTimeFilterController(CustomTimeFilterInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String query, String dayOfWeek, String startTime, String endTime) {

        CustomTimeFilterInputData inputData =
                new CustomTimeFilterInputData(query, dayOfWeek, startTime, endTime);

        interactor.execute(inputData);
    }
}