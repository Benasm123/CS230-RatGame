package RatGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;


public class Main extends Application {
    // The dimensions of the window
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    //test
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        primaryStage.setTitle("Rat Game");

        Rectangle2D screenSize = Screen.getPrimary().getBounds();
        primaryStage.setScene(new Scene(root, Math.min(WINDOW_WIDTH, screenSize.getWidth() - 100), Math.min(WINDOW_HEIGHT, screenSize.getHeight() - 100)));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
