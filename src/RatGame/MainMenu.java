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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

// TODO: Add functionality to settings
// TODO: Add player profile selection and changing
public class MainMenu {
    public void initialize(){
        updateMessage();
    }

    @FXML protected void quitProgram() {
        System.exit(0);
    }

    public void switchToSettings(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/settings.fxml")));
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
        String messageURL = "http://cswebcat.swansea.ac.uk/puzzle";
        String solutionURL = "http://cswebcat.swansea.ac.uk/message?solution=";
        URL url = null;
        URLConnection connection = null;
        try {
            InputStream requestReturn = sendRequestToURL(messageURL);
            String puzzle = getStringFromBytes(requestReturn);
            String answer = solvePuzzle(puzzle);

            InputStream solutionReturn = sendRequestToURL(solutionURL + answer);
            String message = getStringFromBytes(solutionReturn);

            messageDay.setText(message);
        } catch (IOException e) {
            System.out.println("Couldn't access the message of the day url.");
            Random rand = new Random();
            messageDay.setText(words[rand.nextInt(words.length)]);
            e.printStackTrace();
        }

    }

    private InputStream sendRequestToURL(String urlToAccess) throws IOException {
        URL url = new URL(urlToAccess);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    private String getStringFromBytes(InputStream is) throws IOException {
        byte[] bytes = is.readAllBytes();
        return new String(bytes);
    }

    private String solvePuzzle(String puzzle){
        String possibleLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int directionToGo = -1;
        StringBuilder answer = new StringBuilder();
        for (int i = 0 ; i < puzzle.length() ; i++){
            int posOfLetterInAlphabet = possibleLetters.indexOf(puzzle.charAt(i));
            answer.append(possibleLetters.charAt(Math.floorMod(posOfLetterInAlphabet + ((i + 1) * directionToGo), 26)));
            directionToGo *= -1;
        }
        answer.append("CS-230");
        String length = String.valueOf(answer.length());
        answer = new StringBuilder(length + answer);
        return String.valueOf(answer);
    }

    public void switchToLevelSelect(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/levelSelect.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    public void switchToChangeProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/changeProfile.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }
    public void switchToNewGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/newGame.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }
}
