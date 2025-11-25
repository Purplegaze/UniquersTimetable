package app;

import entity.TimeSlot;

/**
 * Helper to convert TimeSlot entities to use case format.
 */
public class TimeSlotHelper {
    
    private static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };
    
    /**
     * Convert day value (1-7) to day name.
     * @param dayValue 1=Monday, 2=Tuesday, ..., 7=Sunday
     * @return day name
     */
    public static String getDayName(int dayValue) {
        if (dayValue < 1 || dayValue > 7) {
            return "Monday"; // default
        }
        return DAYS[dayValue - 1];
    }
    
    /**
     * Get hour from TimeSlot.
     * @param timeSlot the time slot
     * @return start hour (e.g., 14 for 14:00)
     */
    public static int getStartHour(TimeSlot timeSlot) {
        return timeSlot.getStartTime().getHour();
    }
    
    /**
     * Get end hour from TimeSlot.
     * @param timeSlot the time slot
     * @return end hour (e.g., 16 for 16:00)
     */
    public static int getEndHour(TimeSlot timeSlot) {
        return timeSlot.getEndTime().getHour();
    }
}
