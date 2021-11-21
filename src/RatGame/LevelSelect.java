package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class LevelSelect {

    @FXML
    VBox LevelButtons;
    public void initialize(){
        String[] allLevels = new File("src/Levels/").list();

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
            LevelButtons.getChildren().add(levelSelectButton);
        }
    }

    public void playPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/level.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(loader.load());

        Level controller = loader.getController();
        controller.createLevel(((Button)event.getSource()).getText());
    }

}
