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

        for (String i : allLevels) {

            Button levelSelectButton = new Button(i);
            // TODO: once have access to player profile i can do this.
            levelSelectButton.setOnAction(event -> {
                try {
                    playPressed(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            if (MainMenu.getCurrentProfile() != null) {
                try {
                    if (MainMenu.getCurrentProfile().getHighestLevel() + 1 < Integer.parseInt(i.substring(0, 1))) {
                        levelSelectButton.setDisable(true);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Level: " + i + " doesnt start with a digit");
                }
            }
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
