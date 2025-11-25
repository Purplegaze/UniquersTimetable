package entity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeSlot {
    private final int dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Building building;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public TimeSlot(int dayOfWeek, LocalTime startTime, LocalTime endTime, Building building) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("dayOfWeek must be between 1 (Monday) and 7 (Sunday)");
        }
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.building = building;
    }

    /**
     * Gets the day of the week for this time slot.
     *
     * @return the day of week (1 = Monday, 7 = Sunday)
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Gets the start time of this time slot.
     *
     * @return the start time
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of this time slot.
     *
     * @return the end time
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Gets the day of week as a string name.
     *
     * @return the day name (e.g., "Monday", "Tuesday")
     */
    public String getDayName() {
        switch (dayOfWeek) {
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            case 7: return "Sunday";
            default: return "Unknown";
        }
    }

    /**
     * Gets the day of week as a short abbreviation.
     *
     * @return the abbreviated day name (e.g., "Mon", "Tue")
     */
    public String getDayAbbreviation() {
        switch (dayOfWeek) {
            case 1: return "Mon";
            case 2: return "Tue";
            case 3: return "Wed";
            case 4: return "Thu";
            case 5: return "Fri";
            case 6: return "Sat";
            case 7: return "Sun";
            default: return "???";
        }
    }

    public Building getBuilding() {
        return building;
    }

    /**
     * Checks if this time slot overlaps with another time slot.
     * Two time slots overlap if they occur on the same day and their times intersect.
     *
     * @param other the other time slot to check against
     * @return true if the time slots overlap
     */
    public boolean overlapsWith(TimeSlot other) {
        if (this.dayOfWeek != other.dayOfWeek) {
            return false;
        }

        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }

    /**
     * Checks if this time slot is immediately consecutive with another time slot after it.
     * (same day, this ends when other starts, with no gap).
     *
     * @param other the other time slot to check against
     * @return true if the time slots are back-to-back
     */
    public boolean immediatelyPrecedes(TimeSlot other) {
        return this.dayOfWeek == other.dayOfWeek && this.endTime.equals(other.startTime);
    }

    /**
     * Checks if this time slot is immediately consecutive with another time slot before it.
     * (same day, this ends when other starts, with no gap).
     *
     * @param other the other time slot to check against
     * @return true if the time slots are back-to-back
     */
    public boolean immediatelyFollows(TimeSlot other) {
        return this.dayOfWeek == other.dayOfWeek && this.startTime.equals(other.endTime);
    }

    /**
     * Calculates the duration of this time slot in minutes.
     *
     * @return the duration in minutes
     */
    public long getDurationMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * Checks if this time slot falls within a specified time range on a given day.
     *
     * @param day the day to check (1 = Monday, 7 = Sunday)
     * @param rangeStart the start of the time range
     * @param rangeEnd the end of the time range
     * @return true if this time slot is within the specified range
     */
    public boolean isWithinRange(int day, LocalTime rangeStart, LocalTime rangeEnd) {
        if (this.dayOfWeek != day) {
            return false;
        }
        return !this.startTime.isBefore(rangeStart) && !this.endTime.isAfter(rangeEnd);
    }

    /**
     * Formats the time slot as a readable string.
     *
     * @return formatted string like "Mon 09:00-10:00"
     */
    public String toDisplayString() {
        return String.format("%s %s-%s",
                getDayAbbreviation(),
                startTime.format(TIME_FORMATTER),
                endTime.format(TIME_FORMATTER));
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "day=" + getDayName() +
                ", start=" + startTime +
                ", end=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return dayOfWeek == timeSlot.dayOfWeek &&
                startTime.equals(timeSlot.startTime) &&
                endTime.equals(timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        int result = dayOfWeek;
        result = 31 * result + startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }
}