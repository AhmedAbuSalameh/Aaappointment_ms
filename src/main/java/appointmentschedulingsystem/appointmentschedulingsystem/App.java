package appointmentschedulingsystem.appointmentschedulingsystem;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the appointment scheduling system.
 *
 * @author Ahmed
 * @version 1.0
 */
public class App extends Application {

    /** Shared scene used for simple screen switching. */
    private static Scene scene;

    /**
     * Starts the JavaFX application.
     *
     * @param stage main application stage
     * @throws IOException when an FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        AppContext.initialize();
        scene = new Scene(loadFXML("login"), 950, 650);
        stage.setTitle("Appointment Scheduling System");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Replaces the current scene root with another FXML screen.
     *
     * @param fxml FXML file name without extension
     * @throws IOException when the resource cannot be loaded
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an FXML view.
     *
     * @param fxml FXML file name without extension
     * @return loaded root node
     * @throws IOException when loading fails
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return loader.load();
    }

    /**
     * Launches the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
