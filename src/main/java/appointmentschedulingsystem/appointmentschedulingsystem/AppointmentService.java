package appointmentschedulingsystem.appointmentschedulingsystem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Coordinates booking, modification, cancellation, and reminders.
 *
 * @author Ahmed
 * @version 1.0
 */
public class AppointmentService {

    /** Appointment repository. */
    private final AppointmentRepository repository;

    /** Current time provider. */
    private final TimeProvider timeProvider;

    /** Available time slots. */
    private final List<TimeSlot> availableSlots = new ArrayList<>();

    /** Validation strategies. */
    private final List<BookingRuleStrategy> bookingRules = new ArrayList<>();

    /** Registered observers. */
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Creates an appointment service.
     *
     * @param repository appointment repository
     * @param timeProvider time provider
     */
    public AppointmentService(AppointmentRepository repository, TimeProvider timeProvider) {
        this.repository = repository;
        this.timeProvider = timeProvider;
        bookingRules.add(new DurationBookingRuleStrategy(2));
        bookingRules.add(new ParticipantLimitRuleStrategy(5));
        bookingRules.add(new TypeSpecificRuleStrategy());
    }

    /**
     * Seeds default slots for demonstration.
     */
    public void seedDefaultSlots() {
        LocalDate start = timeProvider.today().plusDays(1);
        for (int day = 0; day < 7; day++) {
            LocalDate date = start.plusDays(day);
            availableSlots.add(new TimeSlot(date, LocalTime.of(9, 0), 2));
            availableSlots.add(new TimeSlot(date, LocalTime.of(11, 0), 2));
            availableSlots.add(new TimeSlot(date, LocalTime.of(13, 0), 2));
            availableSlots.add(new TimeSlot(date, LocalTime.of(15, 0), 2));
        }
    }

    /**
     * Registers an observer.
     *
     * @param observer observer to register
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Returns all available slots.
     *
     * @return list of slots not fully booked
     */
    public List<TimeSlot> getAvailableSlots() {
        return availableSlots.stream()
                .filter(this::isSlotAvailable)
                .sorted(Comparator.comparing(TimeSlot::getDate).thenComparing(TimeSlot::getTime))
                .collect(Collectors.toList());
    }

    /**
     * Returns all appointments.
     *
     * @return appointments list
     */
    public List<Appointment> getAllAppointments() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Appointment::getDateValue).thenComparing(Appointment::getTimeValue))
                .collect(Collectors.toList());
    }

    /**
     * Returns appointments for a specific user.
     *
     * @param user owner user
     * @return appointments list
     */
    public List<Appointment> getAppointmentsForUser(User user) {
        return getAllAppointments().stream()
                .filter(appointment -> appointment.getUserEmail().equalsIgnoreCase(user.getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * Books a new appointment.
     *
     * @param user owner user
     * @param date appointment date
     * @param time appointment time
     * @param durationHours duration in hours
     * @param participantsCount participants count
     * @param type appointment type
     * @return saved appointment
     */
    public Appointment bookAppointment(User user, LocalDate date, LocalTime time,
            int durationHours, int participantsCount, AppointmentType type) {
        if (!isSlotAvailable(date, time)) {
            throw new IllegalArgumentException("Selected slot is not available.");
        }
        Appointment appointment = new Appointment(date, time, durationHours, participantsCount, type, user);
        validate(appointment);
        repository.save(appointment);
        notifyObservers(user, "Your appointment on " + appointment.getDate() + " at "
                + appointment.getTime() + " is confirmed.");
        return appointment;
    }

    /**
     * Modifies a future appointment owned by the given user.
     *
     * @param user acting user
     * @param appointmentId appointment id
     * @param date new date
     * @param time new time
     * @param durationHours new duration
     * @param participantsCount new participants count
     * @param type new appointment type
     */
    public void modifyAppointmentForUser(User user, String appointmentId, LocalDate date,
            LocalTime time, int durationHours, int participantsCount, AppointmentType type) {
        Appointment appointment = requireAppointment(appointmentId);
        if (!appointment.getUserEmail().equalsIgnoreCase(user.getEmail())) {
            throw new IllegalStateException("You can only modify your own appointments.");
        }
        requireFutureAppointment(appointment);
        if (slotChanged(appointment, date, time) && !isSlotAvailable(date, time)) {
            throw new IllegalArgumentException("Selected slot is not available.");
        }
        appointment.update(date, time, durationHours, participantsCount, type);
        validate(appointment);
        notifyObservers(user, "Your appointment has been updated to " + appointment.getDate()
                + " at " + appointment.getTime() + ".");
    }

    /**
     * Cancels a future appointment owned by the given user.
     *
     * @param user acting user
     * @param appointmentId appointment id
     */
    public void cancelAppointmentForUser(User user, String appointmentId) {
        Appointment appointment = requireAppointment(appointmentId);
        if (!appointment.getUserEmail().equalsIgnoreCase(user.getEmail())) {
            throw new IllegalStateException("You can only cancel your own appointments.");
        }
        requireFutureAppointment(appointment);
        appointment.cancel();
        notifyObservers(user, "Your appointment on " + appointment.getDate() + " at "
                + appointment.getTime() + " was cancelled.");
    }

    /**
     * Allows an administrator to modify any reservation.
     *
     * @param appointmentId appointment id
     * @param date new date
     * @param time new time
     * @param durationHours new duration
     * @param participantsCount new participants count
     * @param type new type
     */
    public void adminModifyAppointment(String appointmentId, LocalDate date, LocalTime time,
            int durationHours, int participantsCount, AppointmentType type) {
        Appointment appointment = requireAppointment(appointmentId);
        if (slotChanged(appointment, date, time) && !isSlotAvailable(date, time)) {
            throw new IllegalArgumentException("Selected slot is not available.");
        }
        appointment.update(date, time, durationHours, participantsCount, type);
        validate(appointment);
        notifyObservers(appointment.getUser(), "An administrator updated your appointment to "
                + appointment.getDate() + " at " + appointment.getTime() + ".");
    }

    /**
     * Allows an administrator to cancel any reservation.
     *
     * @param appointmentId appointment id
     */
    public void adminCancelAppointment(String appointmentId) {
        Appointment appointment = requireAppointment(appointmentId);
        appointment.cancel();
        notifyObservers(appointment.getUser(), "An administrator cancelled your appointment on "
                + appointment.getDate() + " at " + appointment.getTime() + ".");
    }

    /**
     * Sends reminders for appointments occurring within the next 24 hours.
     *
     * @return number of reminders sent
     */
    public int sendUpcomingReminders() {
        LocalDateTime now = timeProvider.now();
        LocalDateTime threshold = now.plusHours(24);
        int count = 0;
        for (Appointment appointment : repository.findAll()) {
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                    appointment.getDateValue(), appointment.getTimeValue());
            if (appointment.getStatusValue() == AppointmentStatus.CONFIRMED
                    && !appointmentDateTime.isBefore(now)
                    && !appointmentDateTime.isAfter(threshold)) {
                notifyObservers(appointment.getUser(), "Reminder: you have an appointment on "
                        + appointment.getDate() + " at " + appointment.getTime() + ".");
                count++;
            }
        }
        return count;
    }

    /**
     * Checks whether a specific slot is currently available.
     *
     * @param date slot date
     * @param time slot time
     * @return true when available
     */
    public boolean isSlotAvailable(LocalDate date, LocalTime time) {
        return getAvailableSlots().stream()
                .anyMatch(slot -> slot.getDate().equals(date) && slot.getTime().equals(time));
    }

    /**
     * Checks whether a slot object is available.
     *
     * @param slot slot to inspect
     * @return true when available
     */
    private boolean isSlotAvailable(TimeSlot slot) {
        return repository.findAll().stream()
                .noneMatch(appointment -> appointment.getStatusValue() == AppointmentStatus.CONFIRMED
                        && appointment.getDateValue().equals(slot.getDate())
                        && appointment.getTimeValue().equals(slot.getTime()));
    }

    /**
     * Validates an appointment using all configured strategies.
     *
     * @param appointment appointment to validate
     */
    private void validate(Appointment appointment) {
        for (BookingRuleStrategy rule : bookingRules) {
            if (!rule.isValid(appointment)) {
                throw new IllegalArgumentException(rule.getErrorMessage());
            }
        }
    }

    /**
     * Finds an appointment by id or throws an error.
     *
     * @param appointmentId appointment id
     * @return appointment
     */
    private Appointment requireAppointment(String appointmentId) {
        Appointment appointment = repository.findById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found.");
        }
        return appointment;
    }

    /**
     * Ensures that the appointment is in the future.
     *
     * @param appointment appointment to check
     */
    private void requireFutureAppointment(Appointment appointment) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getDateValue(), appointment.getTimeValue());
        if (!appointmentDateTime.isAfter(timeProvider.now())) {
            throw new IllegalStateException("Only future appointments can be modified or cancelled.");
        }
        if (appointment.getStatusValue() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled appointments cannot be modified.");
        }
    }

    /**
     * Checks whether the slot was changed during modification.
     *
     * @param appointment original appointment
     * @param date new date
     * @param time new time
     * @return true when slot changed
     */
    private boolean slotChanged(Appointment appointment, LocalDate date, LocalTime time) {
        return !appointment.getDateValue().equals(date) || !appointment.getTimeValue().equals(time);
    }

    /**
     * Notifies all registered observers.
     *
     * @param user target user
     * @param message message text
     */
    private void notifyObservers(User user, String message) {
        for (Observer observer : observers) {
            observer.notify(user, message);
        }
    }
}
