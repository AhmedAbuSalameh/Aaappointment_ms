package appointmentschedulingsystem.appointmentschedulingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for authentication and session behavior.
 *
 * @author Ahmed
 * @version 1.0
 */
public class AuthServiceTest {

    /** Authentication service. */
    private AuthService authService;

    /**
     * Creates an isolated users file for each test.
     *
     * @throws Exception when file setup fails
     */
    @BeforeEach
    void setUp() throws Exception {
        Path tempFile = Files.createTempFile("users", ".txt");
        System.setProperty("users.file", tempFile.toString());
        UserStorage.addUser(new User("Admin", "admin@test.com", "admin", "admin"));
        authService = new AuthService();
    }

    /**
     * Verifies successful login.
     */
    @Test
    void shouldLoginWithValidCredentials() {
        User user = authService.login("admin@test.com", "admin");
        assertEquals("admin@test.com", user.getEmail());
    }

    /**
     * Verifies invalid login.
     */
    @Test
    void shouldRejectWrongPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.login("admin@test.com", "wrong"));
    }

    /**
     * Verifies logout behavior.
     */
    @Test
    void shouldLogoutAndClearSession() {
        authService.login("admin@test.com", "admin");
        authService.logout();
        assertNull(authService.getCurrentUser());
        assertThrows(IllegalStateException.class, authService::requireLogin);
    }
}
