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
 * Controller for the regular user dashboard.
 *
 * @author Ahmed
 * @version 1.0
 */
public class UserDashboardController {

    /** Available slots table. */
    @FXML
    private TableView<TimeSlot> availableSlotsTable;

    /** Slot date column. */
    @FXML
    private TableColumn<TimeSlot, String> slotDateColumn;

    /** Slot time column. */
    @FXML
    private TableColumn<TimeSlot, String> slotTimeColumn;

    /** Slot duration column. */
    @FXML
    private TableColumn<TimeSlot, String> slotDurationColumn;

    /** My appointments table. */
    @FXML
    private TableView<Appointment> myAppointmentsTable;

    /** Appointment date column. */
    @FXML
    private TableColumn<Appointment, String> dateColumn;

    /** Appointment time column. */
    @FXML
    private TableColumn<Appointment, String> timeColumn;

    /** Appointment duration column. */
    @FXML
    private TableColumn<Appointment, String> durationColumn;

    /** Appointment participants column. */
    @FXML
    private TableColumn<Appointment, String> participantsColumn;

    /** Appointment type column. */
    @FXML
    private TableColumn<Appointment, String> typeColumn;

    /** Appointment status column. */
    @FXML
    private TableColumn<Appointment, String> statusColumn;

    /**
     * Initializes both tables.
     */
    @FXML
    private void initialize() {
        slotDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));
        slotTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime().toString()));
        slotDurationColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getMaxDurationHours() + " hour(s)"));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime()));
        durationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDuration()));
        participantsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getParticipantsCount()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        refreshTables();
    }

    /**
     * Refreshes available slots and user appointments.
     */
    @FXML
    private void handleRefresh() {
        refreshTables();
    }

    /**
     * Books an appointment from the selected slot.
     */
    @FXML
    private void handleBookAppointment() {
        TimeSlot slot = availableSlotsTable.getSelectionModel().getSelectedItem();
        if (slot == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an available slot.");
            return;
        }
        BookingInput input = showBookingDialog(slot.getDate(), slot.getTime(), 1, 1, AppointmentType.INDIVIDUAL);
        if (input == null) {
            return;
        }
        try {
            AppContext.getAppointmentService().bookAppointment(
                    AppContext.getAuthService().getCurrentUser(),
                    input.date, input.time, input.durationHours, input.participantsCount, input.type);
            refreshTables();
            showAlert(Alert.AlertType.INFORMATION, "Booking Successful", "Appointment status = CONFIRMED");
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Booking Error", exception.getMessage());
        }
    }

    /**
     * Modifies the selected user appointment.
     */
    @FXML
    private void handleModifyAppointment() {
        Appointment appointment = myAppointmentsTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an appointment.");
            return;
        }
        BookingInput input = showBookingDialog(appointment.getDateValue(), appointment.getTimeValue(),
                appointment.getDurationHours(), appointment.getParticipantsCountValue(), appointment.getTypeValue());
        if (input == null) {
            return;
        }
        try {
            AppContext.getAppointmentService().modifyAppointmentForUser(
                    AppContext.getAuthService().getCurrentUser(), appointment.getId(),
                    input.date, input.time, input.durationHours, input.participantsCount, input.type);
            refreshTables();
            showAlert(Alert.AlertType.INFORMATION, "Updated", "Appointment updated successfully.");
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Update Error", exception.getMessage());
        }
    }

    /**
     * Cancels the selected user appointment.
     */
    @FXML
    private void handleCancelAppointment() {
        Appointment appointment = myAppointmentsTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an appointment.");
            return;
        }
        try {
            AppContext.getAppointmentService().cancelAppointmentForUser(
                    AppContext.getAuthService().getCurrentUser(), appointment.getId());
            refreshTables();
            showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Slot is available again.");
        } catch (RuntimeException exception) {
            showAlert(Alert.AlertType.ERROR, "Cancellation Error", exception.getMessage());
        }
    }

    /**
     * Logs out the current user.
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
     * Reloads data from the service layer.
     */
    private void refreshTables() {
        User currentUser = AppContext.getAuthService().getCurrentUser();
        availableSlotsTable.setItems(FXCollections.observableArrayList(
                AppContext.getAppointmentService().getAvailableSlots()));
        myAppointmentsTable.setItems(FXCollections.observableArrayList(
                AppContext.getAppointmentService().getAppointmentsForUser(currentUser)));
    }

    /**
     * Opens a dialog for booking and modification.
     *
     * @param defaultDate default date
     * @param defaultTime default time
     * @param defaultDuration default duration
     * @param defaultParticipants default participant count
     * @param defaultType default type
     * @return user input or null when cancelled
     */
    private BookingInput showBookingDialog(LocalDate defaultDate, LocalTime defaultTime,
            int defaultDuration, int defaultParticipants, AppointmentType defaultType) {
        Dialog<BookingInput> dialog = new Dialog<>();
        dialog.setTitle("Appointment Details");
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        DatePicker datePicker = new DatePicker(defaultDate);
        TextField timeField = new TextField(defaultTime.toString());
        TextField durationField = new TextField(String.valueOf(defaultDuration));
        TextField participantsField = new TextField(String.valueOf(defaultParticipants));
        ComboBox<AppointmentType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(AppointmentType.values());
        typeCombo.setValue(defaultType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Date:"), datePicker);
        grid.addRow(1, new Label("Time (HH:MM):"), timeField);
        grid.addRow(2, new Label("Duration (hours):"), durationField);
        grid.addRow(3, new Label("Participants:"), participantsField);
        grid.addRow(4, new Label("Type:"), typeCombo);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButton) {
                return new BookingInput(
                        datePicker.getValue(),
                        LocalTime.parse(timeField.getText().trim()),
                        Integer.parseInt(durationField.getText().trim()),
                        Integer.parseInt(participantsField.getText().trim()),
                        typeCombo.getValue());
            }
            return null;
        });

        Optional<BookingInput> result = dialog.showAndWait();
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
     * Small value object for dialog results.
     *
     * @author Ahmed
     * @version 1.0
     */
    private static class BookingInput {

        /** Selected date. */
        private final LocalDate date;

        /** Selected time. */
        private final LocalTime time;

        /** Selected duration. */
        private final int durationHours;

        /** Selected participants count. */
        private final int participantsCount;

        /** Selected type. */
        private final AppointmentType type;

        /**
         * Creates a booking input object.
         *
         * @param date selected date
         * @param time selected time
         * @param durationHours duration in hours
         * @param participantsCount participants count
         * @param type appointment type
         */
        private BookingInput(LocalDate date, LocalTime time, int durationHours,
                int participantsCount, AppointmentType type) {
            this.date = date;
            this.time = time;
            this.durationHours = durationHours;
            this.participantsCount = participantsCount;
            this.type = type;
        }
    }
}
