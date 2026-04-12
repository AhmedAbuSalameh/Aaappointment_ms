package appointmentschedulingsystem.appointmentschedulingsystem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Controller for administrator operations.
 *
 * @author Ahmed
 * @version 1.0
 */
public class AdminDashboardController {

    /** Appointments table. */
    @FXML
    private TableView<Appointment> appointmentsTable;

    /** Date column. */
    @FXML
    private TableColumn<Appointment, String> dateColumn;

    /** Time column. */
    @FXML
    private TableColumn<Appointment, String> timeColumn;

    /** Duration column. */
    @FXML
    private TableColumn<Appointment, String> durationColumn;

    /** Participants column. */
    @FXML
    private TableColumn<Appointment, String> participantsColumn;

    /** Type column. */
    @FXML
    private TableColumn<Appointment, String> typeColumn;

    /** Status column. */
    @FXML
    private TableColumn<Appointment, String> statusColumn;

    /** User column. */
    @FXML
    private TableColumn<Appointment, String> userColumn;

    /**
     * Initializes the dashboard table.
     */
    @FXML
    private void initialize() {
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime()));
        durationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDuration()));
        participantsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getParticipantsCount()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        userColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserEmail()));
        handleRefresh();
    }

    /**
     * Refreshes the appointments table.
     */
    @FXML
    private void handleRefresh() {
        appointmentsTable.setItems(FXCollections.observableArrayList(
                AppContext.getAppointmentService().getAllAppointments()));
    }

    /**
     * Adds a reservation on behalf of a user.
     */
    @FXML
    private void handleAddAppointment() {
        AdminBookingInput input = showAdminDialog(null);
        if (input == null) {
            return;
        }
        User user = UserStorage.findUserByEmail(input.userEmail);
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "User Error", "User email not found.");
            return;
        }
        try {
            AppContext.getAppointmentService().bookAppointment(user, input.date, input.time,
                    input.durationHours, input.participantsCount, input.type);
            handleRefresh();
            showAlert(Alert.AlertType.INFORMATION, "Added", "Reservation created successfully.");
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Add Error", exception.getMessage());
        }
    }

    /**
     * Modifies the selected reservation.
     */
    @FXML
    private void handleModifyAppointment() {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a reservation.");
            return;
        }
        AdminBookingInput input = showAdminDialog(appointment);
        if (input == null) {
            return;
        }
        try {
            AppContext.getAppointmentService().adminModifyAppointment(appointment.getId(), input.date,
                    input.time, input.durationHours, input.participantsCount, input.type);
            handleRefresh();
            showAlert(Alert.AlertType.INFORMATION, "Updated", "Reservation updated successfully.");
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Update Error", exception.getMessage());
        }
    }

    /**
     * Cancels the selected reservation.
     */
    @FXML
    private void handleCancelAppointment() {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a reservation.");
            return;
        }
        try {
            AppContext.getAppointmentService().adminCancelAppointment(appointment.getId());
            handleRefresh();
            showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Reservation cancelled successfully.");
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Cancellation Error", exception.getMessage());
        }
    }

    /**
     * Sends reminders for upcoming appointments.
     */
    @FXML
    private void handleSendReminders() {
        int remindersSent = AppContext.getAppointmentService().sendUpcomingReminders();
        showAlert(Alert.AlertType.INFORMATION, "Reminders", remindersSent + " reminder(s) sent.");
    }

    /**
     * Logs out the administrator.
     */
    @FXML
    private void handleLogout() {
        AppContext.getAuthService().logout();
        try {
            App.setRoot("login");
        } catch (IOException exception) {
            showAlert(Alert.AlertType.ERROR, "Logout Error", exception.getMessage());
        }
    }

    /**
     * Opens the admin dialog.
     *
     * @param appointment existing appointment or null
     * @return dialog input or null when cancelled
     */
    private AdminBookingInput showAdminDialog(Appointment appointment) {
        Dialog<AdminBookingInput> dialog = new Dialog<>();
        dialog.setTitle(appointment == null ? "Add Reservation" : "Modify Reservation");
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField userEmailField = new TextField(appointment == null ? "" : appointment.getUserEmail());
        userEmailField.setDisable(appointment != null);
        DatePicker datePicker = new DatePicker(appointment == null ? LocalDate.now().plusDays(1) : appointment.getDateValue());
        TextField timeField = new TextField(appointment == null ? "09:00" : appointment.getTimeValue().toString());
        TextField durationField = new TextField(appointment == null ? "1" : String.valueOf(appointment.getDurationHours()));
        TextField participantsField = new TextField(appointment == null ? "1" : String.valueOf(appointment.getParticipantsCountValue()));
        ComboBox<AppointmentType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(AppointmentType.values());
        typeCombo.setValue(appointment == null ? AppointmentType.INDIVIDUAL : appointment.getTypeValue());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("User Email:"), userEmailField);
        grid.addRow(1, new Label("Date:"), datePicker);
        grid.addRow(2, new Label("Time (HH:MM):"), timeField);
        grid.addRow(3, new Label("Duration (hours):"), durationField);
        grid.addRow(4, new Label("Participants:"), participantsField);
        grid.addRow(5, new Label("Type:"), typeCombo);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButton) {
                return new AdminBookingInput(
                        userEmailField.getText().trim(),
                        datePicker.getValue(),
                        LocalTime.parse(timeField.getText().trim()),
                        Integer.parseInt(durationField.getText().trim()),
                        Integer.parseInt(participantsField.getText().trim()),
                        typeCombo.getValue());
            }
            return null;
        });

        Optional<AdminBookingInput> result = dialog.showAndWait();
        return result.orElse(null);
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

    /**
     * Dialog data holder for admin operations.
     *
     * @author Ahmed
     * @version 1.0
     */
    private static class AdminBookingInput {

        /** User email. */
        private final String userEmail;

        /** Reservation date. */
        private final LocalDate date;

        /** Reservation time. */
        private final LocalTime time;

        /** Duration in hours. */
        private final int durationHours;

        /** Participants count. */
        private final int participantsCount;

        /** Appointment type. */
        private final AppointmentType type;

        /**
         * Creates admin dialog input.
         *
         * @param userEmail user email
         * @param date reservation date
         * @param time reservation time
         * @param durationHours duration in hours
         * @param participantsCount participants count
         * @param type appointment type
         */
        private AdminBookingInput(String userEmail, LocalDate date, LocalTime time,
                int durationHours, int participantsCount, AppointmentType type) {
            this.userEmail = userEmail;
            this.date = date;
            this.time = time;
            this.durationHours = durationHours;
            this.participantsCount = participantsCount;
            this.type = type;
        }
    }
}
