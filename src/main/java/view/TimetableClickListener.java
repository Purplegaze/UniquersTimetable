package view;

public interface TimetableClickListener {
    void onEmptySlotClicked(String day, String startTime, String endTime);
    void onCourseClicked(String courseCode);
}
