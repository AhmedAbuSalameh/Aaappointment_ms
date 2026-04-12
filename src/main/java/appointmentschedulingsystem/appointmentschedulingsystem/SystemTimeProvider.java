package appointmentschedulingsystem.appointmentschedulingsystem;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Production implementation of {@link TimeProvider}.
 *
 * @author Ahmed
 * @version 1.0
 */
public class SystemTimeProvider implements TimeProvider {

    /**
     * Returns the current system date.
     *
     * @return current date
     */
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Returns the current system date and time.
     *
     * @return current date and time
     */
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
