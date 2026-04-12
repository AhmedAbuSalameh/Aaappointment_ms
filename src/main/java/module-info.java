module appointmentschedulingsystem.appointmentschedulingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.mail;
    requires java.naming;

    opens appointmentschedulingsystem.appointmentschedulingsystem to javafx.fxml;
    exports appointmentschedulingsystem.appointmentschedulingsystem;
}
