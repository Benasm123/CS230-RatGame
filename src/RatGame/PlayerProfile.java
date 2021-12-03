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
	 * creates an instance of the profile with a name using setName method
	 * @param name
	 */
	public PlayerProfile(String name) {
		setName(name);
	}

	/**
	 * @return the name of a profile
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of the profile
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return the score related to the profile
	 */
	public int getHighScore() {
		return highScore;
	}

	/**
	 * set a new high score for the profile
	 * @param score
	 */
	public void setHighScore(int score) {
		highScore = score;
	}

	/**
	 *
	 * @return the highest level achieved by a profile
	 */
	public int getHighestLevel() {
		return highestLevel;
	}

	/**
	 * set a new highest level achieved by the profile
	 * @param level
	 */
	public void setHighestLevel(int level) {
		highestLevel = level;
	}

	/**
	 * attempts to create a file
	 */
	private void newFile() {
		try {
			new File("src/Profiles").mkdir();
			File myObj = new File("src//Profiles//" + name);
			if (myObj.exists()) {
				deleteSave();
			}
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
				System.out.println("Absolute path: " + myObj.getAbsolutePath());
			}
			else {
				System.out.println("File already exists.");
				System.out.println("Absolute path: " + myObj.getAbsolutePath());
			}
		}
		catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
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
	 * @param save
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
	 * @param save
	 */
	public boolean load(String save) {
		try {
		      File myObj = new File("src//Profiles//" + save);
              if (!myObj.exists()) {
                  return false;
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
        return true;
	}

	/**
	 * This method allows player profiles to be compared by score
	 * uses the difference to decide if the profile goes up or down on the leaderboard
	 * @param profile
	 * @return the difference between the scores
	 */
	@Override
    public int compareTo(PlayerProfile profile) {
        return (int) (profile.getHighScore() - this.highScore);
    }

	/**
	 *
	 * @return player's name and score obtained as a string
	 */
	@Override
	public String toString() {
		return name + " -  Score: " + highScore;
	}

    /**
     * Updates the profiles highest level and high score and saves it.
     * @param level The level number that has been cleared.
     * @param score The score achieved from the level.
     */
    public void levelComplete(int level, int score) {
        this.highestLevel = Math.max(this.highestLevel, level);
        this.highScore = Math.max(this.highScore, score);
        save();
    }
}
