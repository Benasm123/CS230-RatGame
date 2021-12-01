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
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

// TODO: Add functionality to settings
// TODO: Add player profile selection and changing
public class MainMenu {

    private static final String MESSAGE_URL = "http://cswebcat.swansea.ac.uk/puzzle";
    private static final String SOLUTION_URL = "http://cswebcat.swansea.ac.uk/message?solution=";

    private static PlayerProfile currentProfile;

    @FXML
    private Text messageDay;
    @FXML
    private Text selectedProfile;

    /**
     * Set up the main menu when loaded.
     */
    public void initialize(){
        updateMessage();
        updateSelectedProfile();
    }
    
    
    /**
     * Used to exit the game from the main menu when pressing the exit button.
     */
    @FXML protected void quitProgram() {
        System.exit(0);
    }
    /**
     * shows current profile
     */
    private void updateSelectedProfile()
    {
    	if (currentProfile == null)
    	{
    		selectedProfile.setText("No profile selected");
    	} else
    	{
    		selectedProfile.setText("Profile: " + currentProfile.getName());
    	}
    }
    /**
     * gives a message everytime the game started
     */
    public void updateMessage() {
        try {
            InputStream requestReturn = sendRequestToURL(MESSAGE_URL);
            String puzzle = getStringFromBytes(requestReturn);
            String answer = solvePuzzle(puzzle);

            InputStream solutionReturn = sendRequestToURL(SOLUTION_URL + answer);
            String message = getStringFromBytes(solutionReturn);

            messageDay.setText(message);
        } catch (IOException e) {
            System.out.println("Couldn't access the message of the day url.");
            messageDay.setText("Welcome!");
            e.printStackTrace();
        }
    }

    /**
     * Returns the Input stream received from the HTTP GET request to the url passed in.
     * @param urlToAccess The url as a string that you want to do an HTTP GET request to.
     * @return The Input steam containing the bytes received from the HTTP GET request.
     * @throws IOException If the url cannot be accessed will throw an error.
     */
    private InputStream sendRequestToURL(String urlToAccess) throws IOException {
        URL url = new URL(urlToAccess);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    /**
     * Takes in an input stream containing bytes and converts it to a readable string.
     * @param is The input stream that you want to convert the bytes from.
     * @return Returns the string that the bytes from the input stream refer to.
     * @throws IOException If the input stream cannot be read will throw an error.
     */
    private String getStringFromBytes(InputStream is) throws IOException {
        byte[] bytes = is.readAllBytes();
        return new String(bytes);
    }

    /**
     * Solves the puzzle given from the first HTTP GET request to the message of the day api.
     * @param puzzle The String containing the puzzle.
     * @return This returns the solution to the puzzle as a string.
     */
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

    /**
     * Called when the Setting button is pressed in game, and swaps to the settings screen.
     * @param event The action event that triggered this method.
     * @throws IOException If the file that holds the information to the screen we want to go to doesn't exist will throw an error.
     */
    public void switchToSettings(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/settings.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    /**
     * Called when the Level Select button is pressed in game, and swaps to the level select screen.
     * @param event The action event that triggered this method.
     * @throws IOException If the file that holds the information to the screen we want to go to doesn't exist will throw an error.
     */
    public void switchToLevelSelect(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/levelSelect.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    /**
     * Called when the Change Profile button is pressed in game, and swaps to the change profile screen.
     * @param event The action event that triggered this method.
     * @throws IOException If the file that holds the information to the screen we want to go to doesn't exist will throw an error.
     */
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

    public static PlayerProfile getCurrentProfile() {
        return currentProfile;
    }

    public static void setCurrentProfile(PlayerProfile pp){
        currentProfile = pp;
    }
}
