package RatGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {
    // The dimensions of the window
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    Rectangle2D screenSize = Screen.getPrimary().getBounds();

    //test
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FXML/mainMenu.fxml"));
        primaryStage.setTitle("Rat Game");

        primaryStage.setScene(new Scene(root, Math.min(WINDOW_WIDTH, screenSize.getWidth()), Math.min(WINDOW_HEIGHT, screenSize.getHeight())));
        primaryStage.show();
    }

    public void exitProgram(){
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
