package appointmentschedulingsystem.appointmentschedulingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for booking, validation, and reminders.
 *
 * @author Ahmed
 * @version 1.0
 */
public class AppointmentServiceTest {

    /** Service under test. */
    private AppointmentService service;

    /** Fixed-time provider for predictable tests. */
    private TimeProvider timeProvider;

    /** Sample user. */
    private User user;

    /**
     * Prepares test objects.
     */
    @BeforeEach
    void setUp() {
        // Slots start at today()+1 (see seedDefaultSlots). Use "now" so the first day's
        // morning slot falls inside the 24h reminder window (not 25h after "now").
        timeProvider = new TimeProvider() {
            @Override
            public LocalDate today() {
                return LocalDate.of(2026, 3, 23);
            }

            @Override
            public LocalDateTime now() {
                return LocalDateTime.of(2026, 3, 23, 10, 0);
            }
        };
        service = new AppointmentService(new InMemoryAppointmentRepository(), timeProvider);
        service.seedDefaultSlots();
        user = new User("Test User", "user@test.com", "1234", "user");
    }

    /**
     * Verifies successful booking.
     */
    @Test
    void shouldBookAppointmentSuccessfully() {
        Appointment appointment = service.bookAppointment(user, LocalDate.of(2026, 3, 24),
                LocalTime.of(9, 0), 1, 1, AppointmentType.INDIVIDUAL);

        assertEquals("CONFIRMED", appointment.getStatus());
        assertEquals(1, service.getAppointmentsForUser(user).size());
        assertFalse(service.isSlotAvailable(LocalDate.of(2026, 3, 24), LocalTime.of(9, 0)));
    }

    /**
     * Verifies duration validation.
     */
    @Test
    void shouldRejectDurationAboveMaximum() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.bookAppointment(user, LocalDate.of(2026, 3, 24),
                        LocalTime.of(11, 0), 3, 1, AppointmentType.INDIVIDUAL));

        assertTrue(exception.getMessage().contains("Maximum duration"));
    }

    /**
     * Verifies participant validation.
     */
    @Test
    void shouldRejectParticipantLimitExceeded() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.bookAppointment(user, LocalDate.of(2026, 3, 24),
                        LocalTime.of(11, 0), 1, 6, AppointmentType.GROUP));

        assertTrue(exception.getMessage().contains("Maximum participants"));
    }

    /**
     * Verifies type-specific rules.
     */
    @Test
    void shouldApplyTypeSpecificRules() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.bookAppointment(user, LocalDate.of(2026, 3, 24),
                        LocalTime.of(13, 0), 1, 1, AppointmentType.GROUP));

        assertTrue(exception.getMessage().contains("Group appointments"));
    }

    /**
     * Verifies user cancellation behavior.
     */
    @Test
    void shouldCancelAppointmentAndFreeTheSlot() {
        Appointment appointment = service.bookAppointment(user, LocalDate.of(2026, 3, 24),
                LocalTime.of(15, 0), 1, 1, AppointmentType.INDIVIDUAL);

        service.cancelAppointmentForUser(user, appointment.getId());

        assertEquals("CANCELLED", appointment.getStatus());
        assertTrue(service.isSlotAvailable(LocalDate.of(2026, 3, 24), LocalTime.of(15, 0)));
    }

    /**
     * Verifies reminder generation using a mocked observer.
     */
    @Test
    void shouldSendReminderUsingObserverMock() {
        Observer observer = mock(Observer.class);
        service.addObserver(observer);
        service.bookAppointment(user, LocalDate.of(2026, 3, 24), LocalTime.of(9, 0),
                1, 1, AppointmentType.INDIVIDUAL);

        int count = service.sendUpcomingReminders();

        assertEquals(1, count);
        verify(observer, atLeastOnce()).notify(eq(user), contains("Reminder"));
    }
}
