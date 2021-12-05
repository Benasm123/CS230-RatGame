package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;

/**
 * The main menu scene controller. Controls all main menu functionality.
 * @author Benas Montrimas and Ahmed Almahari.
 */
public class MainMenu {

    // Constants
    private static final String MESSAGE_URL = "http://cswebcat.swansea.ac.uk/puzzle";
    private static final String SOLUTION_URL = "http://cswebcat.swansea.ac.uk/message?solution=";

    // Holds which profile is currently selected.
    private static PlayerProfile currentProfile;
    private static boolean showFPS;
    private static double volume;

    // FXML variables.
    @FXML private Text messageDay;
    @FXML private Text selectedProfile;
    @FXML private Button playButton;

    /**
     * Set up the main menu when loaded.
     */
    public void initialize(){
        updateMessage();
        loadConfigFile();
        updateSelectedProfile();
        checkIfPlayerHasSave();

        if (currentProfile == null) {
            playButton.setDisable(true);
        }
    }

    /**
     * Continues the game from a previous save.
     * @param event The event which triggered the action.
     * @throws IOException Throws an error if the level file cannot be found.
     */
    public void continueGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/level.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(loader.load());

        Level controller = loader.getController();

        String[] allSaves = new File("src/Saves/").list();

        String profileName = currentProfile.getName();
        if (allSaves != null) {
            for (String save : allSaves) {
                if (save.substring(1).startsWith(profileName)) {
                    controller.createLevel(save, true);
                }
            }
        }
    }

    /**
     * Get if the fps is set to showing.
     * @return True if fps is set to show, else false.
     */
    public static boolean isShowFPS() {
        return showFPS;
    }

    /**
     * Sets whether the fps is to be shown or hidden.
     * @param showFPS Whether the fps is to be shown.
     */
    public static void setShowFPS(boolean showFPS) {
        MainMenu.showFPS = showFPS;
        updateConfig();
    }

    /**
     * Used to exit the game from the main menu when pressing the exit button.
     */
    public void quitProgram() {
        System.exit(0);
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

    /**
     * Loads the new game scene and switches to it.
     * @param event The event which triggered this action.
     * @throws IOException Throws an error if FXML file cannot be found.
     */
    public void onNewGamePressed(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/newGame.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    /**
     * Get the current profile loaded.
     * @return The current profile which is currently loaded.
     */
    public static PlayerProfile getCurrentProfile() {
        return currentProfile;
    }

    /**
     * Gets the volume of the music.
     * @return The volume of the music.
     */
    public static double getVolume() {
        return volume;
    }

    /**
     * Sets the volume of the music.
     * @param volume The value you want to set the volume to.
     */
    public static void setVolume(double volume) {
        MainMenu.volume = volume;
        Main.mediaPlayer.setVolume(volume);
        updateConfig();
    }

    /**
     * Sets the current profile to a new one.
     * @param profile The profile which you want to set as the current profile.
     */
    public static void setCurrentProfile(PlayerProfile profile) {
        currentProfile = profile;
        updateConfig();
    }

    /**
     * Loads the config file and loads setting and profiles last used.
     */
    private void loadConfigFile() {
        try {
            File configFile = new File("src/Config/ConfigFile");

            if (!configFile.exists()) {
                return;
            }
            Scanner reader = new Scanner(configFile);

            if (reader.hasNextLine()) {
                String profileName = reader.nextLine();
                loadProfile(profileName);
            } else {
                String[] allProfiles = new File("src/Profiles").list();
                if (allProfiles == null || allProfiles.length == 0) {
                    return;
                }
                loadProfile(allProfiles[0]);
            }

            if (reader.hasNextLine()) {
                String stringShowFPS = reader.nextLine();
                setShowFPS(stringShowFPS.charAt(0) == 't');
            } else {
                setShowFPS(false);
            }

            if (reader.hasNextLine()) {
                String stringVolume = reader.nextLine();
                setVolume(Double.parseDouble(stringVolume));
            } else {
                setVolume(1);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check whether there is a save to load.
     */
    private void checkIfPlayerHasSave() {
        String[] allSaves = new File("src/Saves/").list();

        if (allSaves == null) {
            return;
        }

        for (String save : allSaves) {
            if (save.substring(1).startsWith(MainMenu.currentProfile.getName())) {
                updatePlayButton();
            }
        }
    }

    /**
     * Updates the play button to show that there is a save to load.
     */
    private void updatePlayButton() {
        playButton.setText("Continue!");
        playButton.setOnAction(event -> {
            try {
                continueGame(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Will load the last used profile.
     */
    private void loadProfile(String profileName) {
        PlayerProfile playerProfile = new PlayerProfile(profileName);
        playerProfile.load(profileName);
        setCurrentProfile(playerProfile);
    }

    /**
     * Switched the scene to the credits page.
     * @param event The event that triggered the action.
     * @throws IOException Throws an error if the credits page file is not found.
     */
    public void goToCredits(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/CreditsPage.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    /**
     * Updates text to show the current profile selected.
     */
    private void updateSelectedProfile() {
    	if (currentProfile == null) {
    		selectedProfile.setText("No profile selected");
    	} else {
    		selectedProfile.setText("Profile: " + currentProfile.getName());
    	}
    }

    /**
     * Updates the message of the day.
     */
    private void updateMessage() {
        try {
            InputStream requestReturn = sendRequestToURL(MESSAGE_URL);
            String puzzle = getStringFromBytes(requestReturn);
            String answer = solvePuzzle(puzzle);
            requestReturn.close();

            InputStream solutionReturn = sendRequestToURL(SOLUTION_URL + answer);
            String message = getStringFromBytes(solutionReturn);
            solutionReturn.close();

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
     * Updates config file to save settings.
     */
    private static void updateConfig() {
        File configFile = new File("src/Config/ConfigFile");
        try {
            FileWriter writer = new FileWriter(configFile, false);
            writer.write(currentProfile.getName() + "\n");
            writer.write(showFPS + "\n");
            writer.write(String.valueOf(volume));
            writer.close();

        } catch (IOException e) {
            System.out.println("Error: Cant write to file.");
            e.printStackTrace();
        }
    }
}
