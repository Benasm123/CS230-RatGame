/**
 * This class creates a player profile.
 * The player profile is also responsible for saving, loading and deleting player profile files.
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

// In new file you have a .txt at the end of the save, but you don't need to save it as a text document
// Can remove that part and it will work just as well i believe, and makes searching for files easier.
// Youve used casting and the lectures state we need to put a space between cast and variable eg- "(int) variable"
// Not "(int)variable". Not sure why this makes it a lot more ugly and less readable but thats whats asked

// TODO: When a profile is saved, set the MainMenu playerProfile variable to the profile.
public class PlayerProfile implements Comparable<PlayerProfile> {
	private String name;
	private int highScore;
    // I would rename this to highestLevel or something similar to be more clear.
	private int highestLevel;

	/**
	 * creates an instance of a profile with a name using setName method
	 * @param name
	 */
	public PlayerProfile(String name) {
		setName(name);
	}

	/**
	 *
	 * @return the name of a profile
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of a profile
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return the score related to a profile
	 */
	public int getHighScore() {
		return highScore;
	}

	/**
	 * ??
	 * @param highScore
	 */
	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	/**
	 *
	 * @return the level obtained by a profile
	 */
	public int getHighestLevel() {
		return highestLevel;
	}

	/**
	 * ??
	 * @param highestLevel
	 */
	public void setHighestLevel(int highestLevel) {
		this.highestLevel = highestLevel;
	}

	/**
	 * attempts to create a file as any name that the player would want
	 * @param file
	 */
	private void newFile(String file) {
		try {
			new File("src/Profiles").mkdir();
			File myObj = new File("src//Profiles//" + file);
			if (myObj.exists())
			{
				deleteSave(file);
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
	 * attempts to save/write the profile progress to the file
	 */
	public void save() {
		newFile(name);
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
	public void deleteSave(String save) {
		File myObj = new File("src//Profiles//" + save);
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
	 * @return the difference between the score
	 */
	@Override
    public int compareTo(PlayerProfile profile) {
        return (int)(profile.getHighScore() - this.highScore);
    }

	/**
	 *
	 * @return player's name, score and level obtained as a string
	 */
	@Override
	public String toString() {
		return name + " -  Score: " + highScore + " - LV: " + highestLevel;
	}

    public void levelComplete(int level, int score) {
        this.highestLevel = Math.max(this.highestLevel, level);
        this.highScore = Math.max(this.highScore, score);
        save();
    }
}
