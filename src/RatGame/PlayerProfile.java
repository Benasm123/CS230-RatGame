/**
 * This class creates a player profile.
 * The player profile is also responsible for saving, loading and deleting player profile files.
 * @author CS-250 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PlayerProfile implements Comparable<PlayerProfile> {
	private String name;
	private int score;
	private int level;

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
	public int getScore() {
		return score;
	}

	/**
	 * ??
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 *
	 * @return the level obtained by a profile
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * ??
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * attempts to create a file as any name that the player would want
	 * @param file
	 */
	public void newFile(String file) {
		try {
			File myObj = new File("src//Profiles//" + file + ".txt");
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
			FileWriter writer = new FileWriter("src//Profiles//" + name + ".txt");
			writer.write(name + "\n" + score + "\n" + level);
			writer.close();
			System.out.println("Saved");
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
		File myObj = new File("src//Profiles//" + save + ".txt");
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
	public void load(String save) {
		try {
		      File myObj = new File("src//Profiles//" + save + ".txt");
		      Scanner in = new Scanner(myObj);
		      
		      name = in.nextLine();
		      score = Integer.parseInt(in.nextLine());
		      level = Integer.parseInt(in.nextLine());
		      
		      in.close();
		}
		catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		}
	}

	/**
	 * This method allows player profiles to be compared by score
	 * uses the difference to decide if the profile goes up or down on the leaderboard
	 * @param profile
	 * @return the difference between the score
	 */
	@Override
    public int compareTo(PlayerProfile profile) {
        return (int)(profile.getScore() - this.score);
    }

	/**
	 *
	 * @return player's name, score and level obtained as a string
	 */
	@Override
	public String toString() {
		return name + " -  Score: " + score + " - LV: " + level;
	}
}
