package appointmentschedulingsystem.appointmentschedulingsystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Loads SMTP settings for real Gmail notifications.
 *
 * @author Ahmed
 * @version 1.1
 */
public final class EmailConfiguration {

    /** Configuration file name. */
    private static final String CONFIG_FILE = "email-config.properties";

    /** Prevents object creation. */
    private EmailConfiguration() {
    }

    /**
     * Loads email settings from the project root.
     *
     * @return configuration properties
     */
    public static Properties load() {
        Properties properties = new Properties();
        Path path = Path.of(CONFIG_FILE);
        if (!Files.exists(path)) {
            properties.setProperty("mail.enabled", "false");
            return properties;
        }
        try (InputStream inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load email configuration.", exception);
        }
        return properties;
    }
}
