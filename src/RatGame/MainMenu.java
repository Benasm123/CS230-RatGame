package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class MainMenu {
    public void initialize(){
        updateMessage();
    }

    @FXML protected void quitProgram() {
        System.exit(0);
    }

    public void switchToSettings(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/settings.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    @FXML
    private Text messageDay;

    String[] words = {"Hello", "Hi", "Bye", "Woah"};

    /**
     * gives a message everytime the game started
     */
    public void updateMessage() {
        Random rand = new Random();

        messageDay.setText(words[rand.nextInt(words.length)]);
    }

    public void switchToLevelSelect(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/levelSelect.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }
}
