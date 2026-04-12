package appointmentschedulingsystem.appointmentschedulingsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based storage for users.
 *
 * @author Ahmed
 * @version 1.0
 */
public final class UserStorage {

    /** Default administrator email. */
    private static final String DEFAULT_ADMIN_EMAIL = "admin@system.com";

    /** Default administrator password. */
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    /** Prevents object creation. */
    private UserStorage() {
    }

    /**
     * Resolves the users file path.
     *
     * @return path to the users file
     */
    private static String getFilePath() {
        return System.getProperty("users.file", "users.txt");
    }

    /**
     * Loads all users from storage.
     *
     * @return list of users
     */
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(getFilePath());
        if (!file.exists()) {
            return users;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    users.add(new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load users.", exception);
        }
        return users;
    }

    /**
     * Saves all users to storage.
     *
     * @param users users to save
     */
    public static void saveUsers(List<User> users) {
        try (FileWriter writer = new FileWriter(new File(getFilePath()))) {
            for (User user : users) {
                String line = user.getFullName() + "," + user.getEmail() + ","
                        + user.getPassword() + "," + user.getRole();
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save users.", exception);
        }
    }

    /**
     * Adds a single user.
     *
     * @param user user to add
     */
    public static void addUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    /**
     * Finds a user by email.
     *
     * @param email email to search for
     * @return matching user or null
     */
    public static User findUserByEmail(String email) {
        return loadUsers().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Ensures that a default administrator account exists.
     */
    public static void ensureDefaultAdmin() {
        if (findUserByEmail(DEFAULT_ADMIN_EMAIL) == null) {
            addUser(new User("System Admin", DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD, "admin"));
        }
    }
}
