package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * The level select screen class. Manages all levels available to the player and shows all levels.
 * @author Benas Montrimas.
 */
public class LevelSelect {

    // Constants
    private static final String CSS_SELECTED_CLASS = "selected";
    private static final String CSS_LEVEL_CLASS = "levelSelectButton";

    // Variables
    Button selectedButton;

    // FXML variables.
    @FXML VBox LevelButtons;
    @FXML VBox leaderboardScreen;

    /**
     * Initializes the level select, getting all levels and showing them to the player.
     */
    public void initialize(){
        selectedButton = null;

        String[] allLevels = new File("src/Levels/").list();

        assert allLevels != null;

        for (String i : allLevels) {

            Button levelSelectButton = new Button(i);
            levelSelectButton.getStyleClass().add(CSS_LEVEL_CLASS);
            levelSelectButton.setOnAction(this::levelSelected);

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

    /**
     * Loads the level and switches to it.
     * @param event The event which triggered this action.
     * @throws IOException If the FXML file is not found will throw an error.
     */
    public void playPressed(ActionEvent event) throws IOException {
        if (selectedButton == null) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/level.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(loader.load());

        Level controller = loader.getController();
        controller.createLevel(selectedButton.getText(), false);
    }

    /**
     * Highlights the selected level and shows the leaderboard for it.
     * @param event The event that triggered the action.
     */
    private void levelSelected(ActionEvent event) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove(CSS_SELECTED_CLASS);
            leaderboardScreen.getChildren().removeIf(node -> node.getClass() == Label.class);
        }

        selectedButton = (Button) event.getTarget();

        selectedButton.getStyleClass().add(CSS_SELECTED_CLASS);

        Leaderboard lb = new Leaderboard(selectedButton.getText());

        for (int i = 0; i < lb.getLeaderboardStandings().size() ; i++) {
            Label entryLabel = new Label(i + 1 + ". " + lb.getLeaderboardStandings().get(i).getKey() + " " + lb.getLeaderboardStandings().get(i).getValue());
            Font font = new Font(entryLabel.getFont().getName(), 20);
            entryLabel.setFont(font);
            leaderboardScreen.getChildren().add(entryLabel);
        }
    }

    /**
     * Loads the main menu when and switches to it.
     * @param event The event which triggered this action.
     * @throws IOException If the FXML file is not found will throw an error.
     */
    public void switchToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

}
