package RatGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

/**
 * The Main application class. Sets up the window and chooses which screen to show.
 * @author Benas Montrimas.
 */
public class Main extends Application {
    // The dimensions of the window
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    /**
     * Called on application start.
     * @param primaryStage The main stage.
     * @throws Exception Will throw an exception if FXML files are not found.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root;

        File profile = new File("src/Profiles");

        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));

        if (profile.exists()) {
            if (Objects.requireNonNull(profile.list()).length == 0) {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/newGame.fxml")));
            }
        }

        primaryStage.setTitle("Rat Game");

        Rectangle2D screenSize = Screen.getPrimary().getBounds();
        primaryStage.setScene(new Scene(root, Math.min(WINDOW_WIDTH, screenSize.getWidth() - 100), Math.min(WINDOW_HEIGHT, screenSize.getHeight() - 100)));
        primaryStage.show();
    }

    /**
     * Main method.
     * @param args Arguments for application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
