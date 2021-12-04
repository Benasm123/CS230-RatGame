package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * This class is the changeProfile class allows player to switch profiles.
 * @author José Mendes & Ahmed Almahari.
 */
public class NewProfile {
    private static final String[] ILLEGAL_CHARACTERS_IN_FILE_NAME = {"<", ">", ":", "\"", "/", "\\", "|", "?", "*", " ", ".", ","};

    @FXML
    public TextField username;

    @FXML
    public Button cancelButton;

    @FXML
    public Button createButton;

    @FXML
    public Text invalidNameText;

    public void initialize() {
        File profileFolder = new File("src/Profiles");
        if (profileFolder.exists()) {
            if (Objects.requireNonNull(profileFolder.list()).length == 0) {
                cancelButton.setDisable(true);
            }
        } else {
            cancelButton.setDisable(true);
        }
    }

    /**
     * Called when the Create button is pressed in New Game screen, and creates a new profile.
     * @param event The action event that triggered this method.
     * @throws IOException If the file that holds the information to the screen we want to go to doesn't exist will throw an error.
     */
    public void newGame(ActionEvent event) throws IOException {
        if (username.getText().isEmpty() || username.getText().length() > 20) {
            invalidNameText.setVisible(true);
            return;
        }

        for (String i : ILLEGAL_CHARACTERS_IN_FILE_NAME){
            if (username.getText().contains(i)){
                invalidNameText.setVisible(true);
                return;
            }
        }

        PlayerProfile player = new PlayerProfile(username.getText());
        player.save();

        Parent root = null;
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    /**
     * Called when the cancel button is pressed in New Game screen, and swaps to the main menu screen.
     * @param event The action event that triggered this method.
     * @throws IOException If the file that holds the information to the screen we want to go to doesn't exist will throw an error.
     */
    public void switchToMainMenu(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }
}
