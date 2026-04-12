package appointmentschedulingsystem.appointmentschedulingsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory appointment repository.
 *
 * @author Ahmed
 * @version 1.0
 */
public class InMemoryAppointmentRepository implements AppointmentRepository {

    /** Stored appointments. */
    private final List<Appointment> appointments = new ArrayList<>();

    /**
     * Saves an appointment. The same instance is kept in memory.
     *
     * @param appointment appointment to save
     */
    @Override
    public void save(Appointment appointment) {
        if (!appointments.contains(appointment)) {
            appointments.add(appointment);
        }
    }

    /**
     * Returns all appointments.
     *
     * @return copy of appointments list
     */
    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(appointments);
    }

    /**
     * Finds an appointment by id.
     *
     * @param id appointment id
     * @return matching appointment or null
     */
    @Override
    public Appointment findById(String id) {
        return appointments.stream()
                .filter(appointment -> appointment.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
