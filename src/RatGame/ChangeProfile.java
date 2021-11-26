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

public class ChangeProfile {
    @FXML
    VBox profiles;
    public void initialize(){
        String[] allLevels = new File("src/Profiles").list();

        assert allLevels != null;

        for (String i : allLevels){
            Button levelSelectButton = new Button(i);
            levelSelectButton.setOnAction(event -> {
                try {
                    playPressed(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            profiles.getChildren().add(levelSelectButton);
        }
    }
    public void playPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/changeProfile.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(loader.load());

        Level controller = loader.getController();
        controller.createLevel(((Button)event.getSource()).getText());
    }

    public void switchToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/mainMenu.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

}