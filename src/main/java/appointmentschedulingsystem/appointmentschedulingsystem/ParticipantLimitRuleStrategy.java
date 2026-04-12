package appointmentschedulingsystem.appointmentschedulingsystem;

/**
 * Enforces a participant limit.
 *
 * @author Ahmed
 * @version 1.0
 */
public class ParticipantLimitRuleStrategy implements BookingRuleStrategy {

    /** Maximum number of participants. */
    private final int maximumParticipants;

    /**
     * Creates a participant limit rule.
     *
     * @param maximumParticipants maximum participants
     */
    public ParticipantLimitRuleStrategy(int maximumParticipants) {
        this.maximumParticipants = maximumParticipants;
    }

    /**
     * Validates the participant count.
     *
     * @param appointment appointment to validate
     * @return true when participant count is valid
     */
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getParticipantsCountValue() > 0
                && appointment.getParticipantsCountValue() <= maximumParticipants;
    }

    /**
     * Returns the rule error message.
     *
     * @return error message
     */
    @Override
    public String getErrorMessage() {
        return "Maximum participants are " + maximumParticipants + ".";
    }
}
