package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * The settings screen controller and handles all its functionality.
 * @author Benas Montrimas.
 */
public class Settings {

    @FXML private Button fpsButton;

    public void initialize() {
        updateFPSText();
    }

    public void updateFPSText() {
        if (MainMenu.isShowFPS()) {
            fpsButton.setText("Showing");
        } else {
            fpsButton.setText("Hidden");
        }
    }

    public void switchToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    public void switchShowFPS(ActionEvent event) {
        MainMenu.setShowFPS(!MainMenu.isShowFPS());
        updateFPSText();
    }
}
