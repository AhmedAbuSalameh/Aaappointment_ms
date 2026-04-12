package appointmentschedulingsystem.appointmentschedulingsystem;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the login screen.
 *
 * @author Ahmed
 * @version 1.0
 */
public class LoginController {

    /** Email input field. */
    @FXML
    private TextField emailField;

    /** Password input field. */
    @FXML
    private PasswordField passwordField;

    /**
     * Handles user login.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both email and password.");
            return;
        }
        try {
            User user = AppContext.getAuthService().login(email, password);
            showAlert(Alert.AlertType.INFORMATION, "Login Successful",
                    "Welcome, " + user.getFullName() + "!");
            if (user.isAdmin()) {
                App.setRoot("admin_dashboard");
            } else {
                App.setRoot("user_dashboard");
            }
        } catch (IllegalArgumentException | IOException exception) {
            showAlert(Alert.AlertType.ERROR, "Login Error", exception.getMessage());
        }
    }

    /**
     * Switches to the sign-up screen.
     *
     * @throws IOException when screen loading fails
     */
    @FXML
    private void switchToSignUp() throws IOException {
        App.setRoot("signup");
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
