package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * The settings screen controller and handles all its functionality.
 * @author Benas Montrimas and Ahmed Almahari.
 */
public class Settings {

    @FXML private Slider volSlider;
    @FXML private Button fpsButton;

    /**
     * Initializes the settings screen everytime its loaded.
     */
    public void initialize() {
        updateFPSText();
        volSlider.setMax(1);
        volSlider.setBlockIncrement(0.1);
        volSlider.setOnMouseReleased(this::updateVolume);
        volSlider.setValue(MainMenu.getVolume());
    }

    /**
     * Updates the music volume to the sliders value.
     * @param event The event that triggered this action.
     */
    private void updateVolume(MouseEvent event) {
        MainMenu.setVolume(volSlider.getValue());
    }

    /**
     * Updates the fps buttons text to show whether the fps is showing.
     */
    public void updateFPSText() {
        if (MainMenu.isShowFPS()) {
            fpsButton.setText("Showing");
        } else {
            fpsButton.setText("Hidden");
        }
    }

    /**
     * Switch screens to the main menu screen.
     * @param event the event that triggered this action.
     * @throws IOException Throws an error if FXML file cannot be found.
     */
    public void switchToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    /**
     * Toggles if the fps is showing and updates the text on the button.
     */
    public void switchShowFPS() {
        MainMenu.setShowFPS(!MainMenu.isShowFPS());
        updateFPSText();
    }

    /**
     * toggles if the music is playing on button press.
     */
    public void play(){

        if (Main.mediaPlayer.getVolume() == 0) {
            MainMenu.setVolume(1);
        } else {
            MainMenu.setVolume(0);
        }
        volSlider.setValue(MainMenu.getVolume());
    }


}
