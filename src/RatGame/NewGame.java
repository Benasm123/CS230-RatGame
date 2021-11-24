package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;

public class NewGame
{
    public TextField username;

    public void newGame(ActionEvent event) throws IOException
    {
        PlayerProfile player = new PlayerProfile(username.getText());
        player.save();

        Parent root = FXMLLoader.load(getClass().getResource("FXML/levelSelect.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }
    public void switchToMainMenu(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/mainMenu.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }
}
