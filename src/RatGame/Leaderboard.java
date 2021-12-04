package RatGame;

import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Leaderboard class which manages top 10 scores for each level.
 * @author José Mendes, Benas Montrimas.
 */
public class Leaderboard {

    // Variables
    private final String levelName;
    public ArrayList<Pair<String, Integer>> leaderboardStandings;

    /**
     * Constructor creating and loading the leaderboard for the selected level.
     * @param levelName The name of the level that the leaderboard is for.
     */
    public Leaderboard(String levelName){
        this.levelName = levelName;
        leaderboardStandings = new ArrayList<>();
        loadLeaderboard();
    }

    /**
     * Updates the leaderboard with a new score, adding it to the leaderboard and saves it to the board.
     * @param profileName The name of the profile that achieved the score.
     * @param score The score to add to the leaderboard.
     */
    public void updateLeaderboard(String profileName, int score) {
        addToLeaderboard(profileName, score);

        // Limit leaderboard to 10 entries.
        while (leaderboardStandings.size() > 10) {
            leaderboardStandings.remove(10);
        }

        saveLeaderboard();
    }

    /**
     * Gets the leaderboard standings.
     * @return The leaderboard standings.
     */
    public ArrayList<Pair<String, Integer>> getLeaderboardStandings() {
        return leaderboardStandings;
    }

    /**
     * Adds an entry to the leaderboard.
     * @param profileName The name of the profile which achieved the score.
     * @param score The score to add to the leaderboard.
     */
    private void addToLeaderboard(String profileName, int score) {
        for (int i = 0; i < leaderboardStandings.size() ; i++) {
            int scoreToCheckAgainst = leaderboardStandings.get(i).getValue();
            if (scoreToCheckAgainst < score) {
                leaderboardStandings.add(i, new Pair<>(profileName, score));
                return;
            }
        }
        leaderboardStandings.add(new Pair<>(profileName, score));
    }

    /**
     * Loads the leaderboard from the leaderboard save.
     */
    private void loadLeaderboard() {
        File leaderboardFile = new File("src/Leaderboards/" + levelName);

        try {
            if (!leaderboardFile.exists()) {
                if (leaderboardFile.createNewFile()) {
                    System.out.println("Created Leaderboard File");
                }
            }

            Scanner fileReader = new Scanner(leaderboardFile);
            while (fileReader.hasNextLine()) {
                String[] entry = fileReader.nextLine().split(" ");
                leaderboardStandings.add(new Pair<>(entry[0], Integer.parseInt(entry[1])));
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the leaderboard to a file.
     */
    private void saveLeaderboard() {
        File leaderboardFile = new File("src/Leaderboards/" + levelName);

        try {
            FileWriter fileWriter = new FileWriter(leaderboardFile);
            for (Pair<String, Integer> entry : leaderboardStandings) {
                fileWriter.write(entry.getKey() + " " + entry.getValue() + "\n");
            }

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
