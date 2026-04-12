package appointmentschedulingsystem.appointmentschedulingsystem;

/**
 * Enforces the maximum allowed appointment duration.
 *
 * @author Ahmed
 * @version 1.0
 */
public class DurationBookingRuleStrategy implements BookingRuleStrategy {

    /** Maximum allowed duration in hours. */
    private final int maximumHours;

    /**
     * Creates a duration rule.
     *
     * @param maximumHours maximum allowed duration in hours
     */
    public DurationBookingRuleStrategy(int maximumHours) {
        this.maximumHours = maximumHours;
    }

    /**
     * Validates the duration.
     *
     * @param appointment appointment to validate
     * @return true when duration is valid
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getDurationHours() > 0 && appointment.getDurationHours() <= maximumHours;
    }

    /**
     * Returns the rule error message.
     *
     * @return error message
     */
    @Override
    public String getErrorMessage() {
        return "Maximum duration is " + maximumHours + " hour(s).";
    }
}
