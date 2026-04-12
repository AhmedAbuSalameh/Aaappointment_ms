package appointmentschedulingsystem.appointmentschedulingsystem;

/**
 * Strategy interface for appointment validation rules.
 *
 * @author Ahmed
 * @version 1.0
 */
public interface BookingRuleStrategy {

    /**
     * Validates an appointment.
     *
     * @param appointment appointment to validate
     * @return true when valid
     */
    boolean isValid(Appointment appointment);

    /**
     * Returns the validation message.
     *
     * @return validation error message
     */
    String getErrorMessage();
}
