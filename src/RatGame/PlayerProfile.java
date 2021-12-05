package RatGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class creates a player profile.
 * The player profile is also responsible for saving, loading and deleting player profile files.
 * @author José
 * @version 1.0
 *
 */
public class PlayerProfile implements Comparable<PlayerProfile> {

	// Variables
	private String name;
	private int highScore;
	private int highestLevel;

	/**
	 * creates an instance of the profile with a name.
	 * @param name The name of the profile.
	 */
	public PlayerProfile(String name) {
		setName(name);
	}

	/**
     * Gets the name of the profile.
	 * @return the name of a profile
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of the profile
	 * @param name The name to set the profile to.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the high score of the profile.
	 * @return the score related to the profile
	 */
	public int getHighScore() {
		return highScore;
	}

	/**
	 * Get the highest level that the profile has achieved.
	 * @return the highest level achieved by a profile.
	 */
	public int getHighestLevel() {
		return highestLevel;
	}

	/**
	 * attempts to save the profile progress to the file
	 */
	public void save() {
		newFile();
		try {
			FileWriter writer = new FileWriter("src//Profiles//" + name);
			writer.write(name + "\n" + highScore + "\n" + highestLevel);
			writer.close();
			System.out.println("Saved");
            load(name);
		}
		catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * attempts to delete the file saved by the player
	 */
	public void deleteSave() {
		File myObj = new File("src//Profiles//" + name);
		if (myObj.delete()) {
			System.out.println("Save deleted");
		}
		else {
			System.out.println("Failed to delete the file.");
		}
	}

	/**
	 * attempts to load the file saved the player
     * @param save The name of the profile to save.
     */
	public void load(String save) {
		try {
		      File myObj = new File("src//Profiles//" + save);
              if (!myObj.exists()) {
                  return;
              }
		      Scanner in = new Scanner(myObj);
		      
		      name = in.nextLine();
		      highScore = Integer.parseInt(in.nextLine());
		      highestLevel = Integer.parseInt(in.nextLine());
		      
		      in.close();
		}
		catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		}
        MainMenu.setCurrentProfile(this);
    }

	/**
	 * This method allows player profiles to be compared by score
	 * uses the difference to decide if the profile goes up or down on the leaderboard
	 * @param profile The profile to compare to.
	 * @return the difference between the scores.
	 */
	@Override
    public int compareTo(PlayerProfile profile) {
        return profile.getHighScore() - this.highScore;
    }

	/**
	 * Prints the name and high score of the profile.
	 * @return player's name and score obtained as a string
	 */
	@Override
	public String toString() {
		return name + " -  Score: " + highScore;
	}

    /**
     * Updates the highest level and high score of the profile and saves it.
     * @param level The level number that has been cleared.
     * @param score The score achieved from the level.
     */
    public void levelComplete(int level, int score) {
        this.highestLevel = Math.max(this.highestLevel, level);
        this.highScore = Math.max(this.highScore, score);
        save();
    }

    /**
     * attempts to create a file
     */
    private void newFile() {
        try {
            if (new File("src/Profiles").mkdir()) {
                System.out.println("Profiles folder created");
            }

            File myObj = new File("src//Profiles//" + name);
            if (myObj.exists()) {
                deleteSave();
            }
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            }
            else {
                System.out.println("File already exists.");
            }
            System.out.println("Absolute path: " + myObj.getAbsolutePath());
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
