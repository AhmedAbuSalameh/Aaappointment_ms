package appointmentschedulingsystem.appointmentschedulingsystem;

/**
 * Simple observer that prints notifications to the console.
 *
 * @author Ahmed
 * @version 1.0
 */
public class ConsoleNotificationObserver implements Observer {

    /**
     * Prints the notification message.
     *
     * @param user destination user
     * @param message notification message
     */
    @Override
    public void notify(User user, String message) {
        System.out.println("Notification to " + user.getEmail() + ": " + message);
    }
}
