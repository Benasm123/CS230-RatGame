package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class LevelSelect {

    @FXML
    VBox LevelButtons;
    @FXML
    VBox LeaderboardButtons;
    @FXML
	private Text top10;
    
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
        
        String[] allLeaderboards = new File("src/Leaderboards/").list();

        if (allLeaderboards == null)
        {
        	new Leaderboard(0);
        	new Leaderboard(1);
        	new Leaderboard(2);
        	new Leaderboard(3);
        	new Leaderboard(4);
        	new Leaderboard(5);
        	new Leaderboard(6);
        	new Leaderboard(7);
        	new Leaderboard(8);
        	new Leaderboard(9);
        	allLeaderboards = new File("src/Leaderboards/").list();
        }

        for (String j : allLeaderboards)
        {

            Button leaderboardSelectButton = new Button(j);
            leaderboardSelectButton.setOnAction(event -> 
            {
                try {
                    displayPressed(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
//                }
            LeaderboardButtons.getChildren().add(leaderboardSelectButton);
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
    public void displayPressed(ActionEvent event)throws IOException {
        Leaderboard board = new Leaderboard();
        int lvl = Integer.parseInt(String.valueOf(((Button)event.getSource()).getText().charAt(0)));
        board.load(lvl);
    	top10.setText(board.toString());
    }

}
