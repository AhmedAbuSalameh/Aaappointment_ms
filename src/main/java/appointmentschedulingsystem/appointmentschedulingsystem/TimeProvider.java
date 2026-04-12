package appointmentschedulingsystem.appointmentschedulingsystem;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Abstraction for current time access.
 *
 * @author Ahmed
 * @version 1.0
 */
public interface TimeProvider {

    /**
     * Returns the current date.
     *
     * @return current date
     */
    LocalDate today();

    /**
     * Returns the current date and time.
     *
     * @return current date and time
     */
    LocalDateTime now();
}
