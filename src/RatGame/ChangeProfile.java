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
    VBox profiles;
    public void initialize(){
        String[] allProfiles = new File("src/Profiles").list();
        // Instead of an assert, you can create the profiles folder here, as ithink thats the only time this would evaluate false


        for (String i : allProfiles){
            Button changeProfileButton = new Button(i);
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

    private void loadProfile(String name){
        PlayerProfile profile = new PlayerProfile(name);
        profile.load(name);
    }

    // Make sure to include a single line between methods and also if the fxml allows it these can be private but im not
    // to sure if they need to be public for javaFX.
    // Tested it seems like it needs to be public if accessing through scene builder, if accessing from java can make private
    public void playPressed(ActionEvent event) throws IOException {
        loadProfile(((Button) event.getTarget()).getText());

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);

    }

    public void switchToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

}
