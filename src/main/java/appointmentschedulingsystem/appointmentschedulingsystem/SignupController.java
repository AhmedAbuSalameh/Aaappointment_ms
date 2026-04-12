package appointmentschedulingsystem.appointmentschedulingsystem;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the sign-up screen.
 *
 * @author Ahmed
 * @version 1.0
 */
public class SignupController {

    /** Gmail format validation pattern. */
    private static final String GMAIL_REGEX = "^[A-Za-z0-9._%+-]+@gmail\\.com$";

    /** Full name input field. */
    @FXML
    private TextField nameField;

    /** Email input field. */
    @FXML
    private TextField emailField;

    /** Password input field. */
    @FXML
    private PasswordField passwordField;

    /**
     * Switches back to the login screen.
     *
     * @throws IOException when the screen cannot be loaded
     */
    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }

    /**
     * Creates a new user account.
     */
    @FXML
    private void handleSignUp() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Please fill in all fields.");
            return;
        }
        if (!email.matches(GMAIL_REGEX)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email",
                    "Please sign up using a real Gmail address ending with @gmail.com.");
            return;
        }
        if (UserStorage.findUserByEmail(email) != null) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Email", "This email is already registered.");
            return;
        }
        UserStorage.addUser(new User(name, email, password, "user"));
        showAlert(Alert.AlertType.INFORMATION, "Signup Successful",
                "Account created successfully. Booking confirmations will be sent to this Gmail when email sending is configured.");
        try {
            switchToLogin();
        } catch (IOException exception) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", exception.getMessage());
        }
    }

    /**
     * Shows an alert message.
     *
     * @param type alert type
     * @param title alert title
     * @param message alert message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
