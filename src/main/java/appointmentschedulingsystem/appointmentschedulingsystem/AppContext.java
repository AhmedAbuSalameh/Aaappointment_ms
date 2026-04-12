package appointmentschedulingsystem.appointmentschedulingsystem;

/**
 * Simple application context that wires repositories, services, and seed data.
 *
 * @author Ahmed
 * @version 1.0
 */
public final class AppContext {

    /** Shared authentication service. */
    private static AuthService authService;

    /** Shared appointment service. */
    private static AppointmentService appointmentService;

    /** Prevents object creation. */
    private AppContext() {
    }

    /**
     * Initializes the application once.
     */
    public static void initialize() {
        if (authService != null && appointmentService != null) {
            return;
        }
        UserStorage.ensureDefaultAdmin();
        TimeProvider timeProvider = new SystemTimeProvider();
        InMemoryAppointmentRepository repository = new InMemoryAppointmentRepository();
        appointmentService = new AppointmentService(repository, timeProvider);
        appointmentService.addObserver(new ConsoleNotificationObserver());
        appointmentService.addObserver(new EmailNotificationObserver());
        appointmentService.seedDefaultSlots();
        authService = new AuthService();
    }

    /**
     * Returns the shared authentication service.
     *
     * @return authentication service
     */
    public static AuthService getAuthService() {
        return authService;
    }

    /**
     * Returns the shared appointment service.
     *
     * @return appointment service
     */
    public static AppointmentService getAppointmentService() {
        return appointmentService;
    }
}
