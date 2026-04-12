package appointmentschedulingsystem.appointmentschedulingsystem;

/**
 * Applies special rules depending on the appointment type.
 *
 * @author Ahmed
 * @version 1.0
 */
public class TypeSpecificRuleStrategy implements BookingRuleStrategy {

    /** Latest generated error message. */
    private String errorMessage = "Invalid appointment type rule.";

    /**
     * Validates type-specific rules.
     *
     * @param appointment appointment to validate
     * @return true when type rules are satisfied
     */
    @Override
    public boolean isValid(Appointment appointment) {
        AppointmentType type = appointment.getTypeValue();
        int participants = appointment.getParticipantsCountValue();
        int duration = appointment.getDurationHours();
        if (type == AppointmentType.GROUP && participants < 2) {
            errorMessage = "Group appointments require at least 2 participants.";
            return false;
        }
        if (type == AppointmentType.INDIVIDUAL && participants != 1) {
            errorMessage = "Individual appointments require exactly 1 participant.";
            return false;
        }
        if (type == AppointmentType.URGENT && duration > 1) {
            errorMessage = "Urgent appointments cannot exceed 1 hour.";
            return false;
        }
        if (type == AppointmentType.VIRTUAL && participants > 3) {
            errorMessage = "Virtual appointments allow at most 3 participants.";
            return false;
        }
        errorMessage = "Valid.";
        return true;
    }

    /**
     * Returns the rule error message.
     *
     * @return error message
     */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
