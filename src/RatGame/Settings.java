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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * The settings screen controller and handles all its functionality.
 * @author Benas Montrimas.
 */
public class Settings {

    @FXML private Slider volSlider;
    @FXML private Button playMusic;
    @FXML private Button fpsButton;


    public void initialize() {
        updateFPSText();
        volSlider.setMax(1);
        volSlider.setBlockIncrement(0.1);
        volSlider.setOnMouseReleased(this::updateVolume);
        volSlider.setValue(MainMenu.getVolume());
    }

    private void updateVolume(MouseEvent event) {
        MainMenu.setVolume(volSlider.getValue());
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

    public void play(ActionEvent event){
        playSound();
    }


    private void playSound(){
        if (Main.mediaPlayer.getVolume() == 0) {
            MainMenu.setVolume(1);
        } else {
            MainMenu.setVolume(0);
        }
        volSlider.setValue(MainMenu.getVolume());
    }


}
