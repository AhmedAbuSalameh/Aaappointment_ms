package appointmentschedulingsystem.appointmentschedulingsystem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Domain entity for a booked appointment.
 *
 * @author Ahmed
 * @version 1.0
 */
public class Appointment {

    /** Formatter used by the UI table. */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /** Unique appointment identifier. */
    private final String id;

    /** Appointment date. */
    private LocalDate date;

    /** Appointment time. */
    private LocalTime time;

    /** Duration in hours. */
    private int durationHours;

    /** Number of participants. */
    private int participantsCount;

    /** Appointment status. */
    private AppointmentStatus status;

    /** Appointment type. */
    private AppointmentType type;

    /** Appointment owner. */
    private final User user;

    /**
     * Creates a new appointment.
     *
     * @param date appointment date
     * @param time appointment time
     * @param durationHours duration in hours
     * @param participantsCount participants count
     * @param type appointment type
     * @param user appointment owner
     */
    public Appointment(LocalDate date, LocalTime time, int durationHours,
            int participantsCount, AppointmentType type, User user) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.time = time;
        this.durationHours = durationHours;
        this.participantsCount = participantsCount;
        this.type = type;
        this.user = user;
        this.status = AppointmentStatus.CONFIRMED;
    }

    /**
     * Returns the appointment identifier.
     *
     * @return appointment id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the date object.
     *
     * @return appointment date
     */
    public LocalDate getDateValue() {
        return date;
    }

    /**
     * Returns the date formatted for the UI.
     *
     * @return appointment date as string
     */
    public String getDate() {
        return date.toString();
    }

    /**
     * Returns the time object.
     *
     * @return appointment time
     */
    public LocalTime getTimeValue() {
        return time;
    }

    /**
     * Returns the time formatted for the UI.
     *
     * @return appointment time as string
     */
    public String getTime() {
        return time.format(TIME_FORMATTER);
    }

    /**
     * Returns the duration text.
     *
     * @return duration text
     */
    public String getDuration() {
        return durationHours + " hour(s)";
    }

    /**
     * Returns the duration as a number.
     *
     * @return duration in hours
     */
    public int getDurationHours() {
        return durationHours;
    }

    /**
     * Returns the participants count for the UI.
     *
     * @return participants count text
     */
    public String getParticipantsCount() {
        return String.valueOf(participantsCount);
    }

    /**
     * Returns the participants count.
     *
     * @return participants count
     */
    public int getParticipantsCountValue() {
        return participantsCount;
    }

    /**
     * Returns the status for the UI.
     *
     * @return status text
     */
    public String getStatus() {
        return status.name();
    }

    /**
     * Returns the status enum.
     *
     * @return status
     */
    public AppointmentStatus getStatusValue() {
        return status;
    }

    /**
     * Returns the type for the UI.
     *
     * @return type text
     */
    public String getType() {
        return type.name();
    }

    /**
     * Returns the type enum.
     *
     * @return appointment type
     */
    public AppointmentType getTypeValue() {
        return type;
    }

    /**
     * Returns the owner full name for the UI.
     *
     * @return owner name
     */
    public String getUserName() {
        return user.getFullName();
    }

    /**
     * Returns the owner email.
     *
     * @return owner email
     */
    public String getUserEmail() {
        return user.getEmail();
    }

    /**
     * Returns the owner object.
     *
     * @return owner
     */
    public User getUser() {
        return user;
    }

    /**
     * Updates appointment details.
     *
     * @param date new date
     * @param time new time
     * @param durationHours new duration
     * @param participantsCount new participants count
     * @param type new appointment type
     */
    public void update(LocalDate date, LocalTime time, int durationHours,
            int participantsCount, AppointmentType type) {
        this.date = date;
        this.time = time;
        this.durationHours = durationHours;
        this.participantsCount = participantsCount;
        this.type = type;
    }

    /**
     * Cancels the appointment.
     */
    public void cancel() {
        this.status = AppointmentStatus.CANCELLED;
    }
}
