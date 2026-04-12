package appointmentschedulingsystem.appointmentschedulingsystem;

/**
 * Handles authentication and session state.
 *
 * @author Ahmed
 * @version 1.0
 */
public class AuthService {

    /** Currently logged-in user. */
    private User currentUser;

    /**
     * Attempts to log in with the given credentials.
     *
     * @param email user email
     * @param password user password
     * @return logged-in user
     */
    public User login(String email, String password) {
        User user = UserStorage.findUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Incorrect password.");
        }
        currentUser = user;
        return user;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Returns the current user.
     *
     * @return current user or null
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Ensures that a user is logged in.
     */
    public void requireLogin() {
        if (currentUser == null) {
            throw new IllegalStateException("Please log in first.");
        }
    }

    /**
     * Ensures that the current user is an administrator.
     */
    public void requireAdmin() {
        requireLogin();
        if (!currentUser.isAdmin()) {
            throw new IllegalStateException("Administrator access required.");
        }
    }
}
