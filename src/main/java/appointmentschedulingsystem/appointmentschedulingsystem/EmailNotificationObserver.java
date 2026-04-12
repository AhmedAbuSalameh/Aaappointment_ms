package appointmentschedulingsystem.appointmentschedulingsystem;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Observer that sends real Gmail notifications when SMTP settings are configured.
 *
 * @author Ahmed
 * @version 1.1
 */
public class EmailNotificationObserver implements Observer {

    /** SMTP settings. */
    private final Properties configuration;

    /**
     * Creates an email observer.
     */
    public EmailNotificationObserver() {
        this.configuration = EmailConfiguration.load();
    }

    /**
     * Sends a real email notification when enabled.
     *
     * @param user destination user
     * @param message notification message
     */
    @Override
    public void notify(User user, String message) {
        if (!isEnabled()) {
            System.out.println("Email notifications are disabled. Create email-config.properties to enable Gmail sending.");
            return;
        }

        String username = configuration.getProperty("mail.username", "").trim();
        String appPassword = configuration.getProperty("mail.appPassword", "").trim();
        String host = configuration.getProperty("mail.host", "smtp.gmail.com").trim();
        String port = configuration.getProperty("mail.port", "587").trim();
        String fromName = configuration.getProperty("mail.fromName", "Appointment Scheduling System").trim();

        Properties sessionProperties = new Properties();
        sessionProperties.put("mail.smtp.auth", "true");
        sessionProperties.put("mail.smtp.starttls.enable", "true");
        sessionProperties.put("mail.smtp.host", host);
        sessionProperties.put("mail.smtp.port", port);

        Session session = Session.getInstance(sessionProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(username, fromName));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            email.setSubject("Appointment Scheduling System Notification");
            email.setText("Hello " + user.getFullName() + ",\n\n" + message
                    + "\n\nRegards,\nAppointment Scheduling System");
            Transport.send(email);
            System.out.println("Email sent successfully to " + user.getEmail());
        } catch (MessagingException | java.io.UnsupportedEncodingException exception) {
            System.err.println("Failed to send email to " + user.getEmail()
                    + ". Check email-config.properties and Gmail App Password settings.");
            exception.printStackTrace();
        }
    }

    /**
     * Checks whether email sending is enabled.
     *
     * @return true when enabled and credentials are present
     */
    private boolean isEnabled() {
        return Boolean.parseBoolean(configuration.getProperty("mail.enabled", "false"))
                && !configuration.getProperty("mail.username", "").trim().isEmpty()
                && !configuration.getProperty("mail.appPassword", "").trim().isEmpty();
    }
}
