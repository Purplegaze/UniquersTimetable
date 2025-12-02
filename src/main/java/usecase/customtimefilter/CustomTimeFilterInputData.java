package usecase.customtimefilter;

public class CustomTimeFilterInputData {

    private final String query;
    private final String dayOfWeek;
    private final String startTime;
    private final String endTime;

    public CustomTimeFilterInputData(String query,
                                     String dayOfWeek,
                                     String startTime,
                                     String endTime) {
        this.query = query;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getQuery() {
        return query;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}