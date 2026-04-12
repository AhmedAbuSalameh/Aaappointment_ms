package appointmentschedulingsystem.appointmentschedulingsystem;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents an available appointment time slot.
 *
 * @author Ahmed
 * @version 1.0
 */
public class TimeSlot {

    /** Slot date. */
    private final LocalDate date;

    /** Slot start time. */
    private final LocalTime time;

    /** Maximum duration in hours. */
    private final int maxDurationHours;

    /**
     * Creates a time slot.
     *
     * @param date slot date
     * @param time slot time
     * @param maxDurationHours maximum allowed duration in hours
     */
    public TimeSlot(LocalDate date, LocalTime time, int maxDurationHours) {
        this.date = date;
        this.time = time;
        this.maxDurationHours = maxDurationHours;
    }

    /**
     * Returns the date.
     *
     * @return slot date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the time.
     *
     * @return slot time
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Returns the maximum allowed duration.
     *
     * @return maximum duration in hours
     */
    public int getMaxDurationHours() {
        return maxDurationHours;
    }
}
