package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * This class is the changeProfile class allows player to switch profiles.
 * @author Ahmed Almahari.
 */
public class ChangeProfile {

    @FXML
    private VBox profiles;

    /**
     * Initializes the change profile, getting all profiles and shows them to player.
     */
    public void initialize(){
        String[] allProfiles = new File("src/Profiles").list();

        assert allProfiles != null;

        for (String i : allProfiles){
            Button changeProfileButton = new Button(i);
            changeProfileButton.setPrefWidth(200);
            changeProfileButton.setPrefHeight(30);
            changeProfileButton.setOnAction(event -> {
                try {
                    playPressed(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            profiles.getChildren().add(changeProfileButton);
        }
    }

    /**
     * Loads the main menu as newly chosen profile and switches to it.
     * @param event The event which triggered this action.
     * @throws IOException If the FXML file is not found will throw an error.
     */
    public void playPressed(ActionEvent event) throws IOException {
        loadProfile(((Button) event.getTarget()).getText());

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);

    }

    /**
     * Loads the main menu and switches to it.
     * @param event The event which triggered this action.
     * @throws IOException If the FXML file is not found will throw an error.
     */
    public void switchToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    /**
     * Loads the player profile.
     * @param name player profile
     */
    private void loadProfile(String name){
        PlayerProfile profile = new PlayerProfile(name);
        profile.load(name);
    }

}
