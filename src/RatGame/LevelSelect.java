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

public class LevelSelect {

    @FXML
    VBox LevelButtons;

    public void initialize(){
        String[] allLevels = new File("src/Levels/").list();

        assert allLevels != null;

        for (String i : allLevels){

            Button levelSelectButton = new Button(i);
            // TODO: once have access to player profile i can do this.
//            if (MainMenu.getCurrentProfile().getLevel() > Integer.parseInt(i.substring(0, 1))){
            // TODO Make this set the button to do nothing.
//                  levelSelectButton.setDisable(true);
//            } else {
                levelSelectButton.setOnAction(event -> {
                    try {
                        playPressed(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
//            }
            LevelButtons.getChildren().add(levelSelectButton);

        }
    }

    private void playPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/level.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(loader.load());

        Level controller = loader.getController();
        controller.createLevel(((Button)event.getSource()).getText());
    }
    
    public void switchToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

}
