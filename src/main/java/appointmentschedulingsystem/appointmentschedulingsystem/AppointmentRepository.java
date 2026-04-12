package appointmentschedulingsystem.appointmentschedulingsystem;

import java.util.List;

/**
 * Repository abstraction for appointments.
 *
 * @author Ahmed
 * @version 1.0
 */
public interface AppointmentRepository {

    /**
     * Saves an appointment.
     *
     * @param appointment appointment to save
     */
    void save(Appointment appointment);

    /**
     * Returns all appointments.
     *
     * @return all appointments
     */
    List<Appointment> findAll();

    /**
     * Finds an appointment by id.
     *
     * @param id appointment id
     * @return appointment or null
     */
    Appointment findById(String id);
}
