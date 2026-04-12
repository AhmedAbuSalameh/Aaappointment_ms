package appointmentschedulingsystem.appointmentschedulingsystem;

/**
 * Represents a system user.
 *
 * @author Ahmed
 * @version 1.0
 */
public class User {

    /** User full name. */
    private final String fullName;

    /** Unique email. */
    private final String email;

    /** Plain-text password for this academic project only. */
    private final String password;

    /** Role name such as admin or user. */
    private final String role;

    /**
     * Creates a new user.
     *
     * @param fullName full name
     * @param email email address
     * @param password password
     * @param role user role
     */
    public User(String fullName, String email, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Returns the user full name.
     *
     * @return full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the email address.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user role.
     *
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * Checks whether the user is an administrator.
     *
     * @return true when role is admin
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
}
