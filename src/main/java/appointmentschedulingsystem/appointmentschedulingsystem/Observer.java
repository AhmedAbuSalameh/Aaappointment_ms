package appointmentschedulingsystem.appointmentschedulingsystem;

/**
 * Observer interface for notifications.
 *
 * @author Ahmed
 * @version 1.0
 */
public interface Observer {

    /**
     * Sends a message to a user.
     *
     * @param user destination user
     * @param message notification message
     */
    void notify(User user, String message);
}
