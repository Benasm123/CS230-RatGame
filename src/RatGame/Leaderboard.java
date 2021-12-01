/**
 * This class ranks the top 10 scores in a leaderboard and prints them as a string.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

// TODO: Needs to be made so one leaderboard for each level
// I dont think this keeps track of scores right now
// TODO: Cap the leaderboard length to 10 - can do by after sorting, check size and if more than 10 just copy the first
// 10 elements over the top of itself.
// Right now you store all, but only print top 10, but dont want to be storing all players that ever play as we will
// need to save this leaderboard.
public class Leaderboard {
	// creates a list of player proles called leaderboard
	private static int LVL;
	
	public static ArrayList <PlayerProfile> leaderboard;
	public Leaderboard(int lvl)
	{
		leaderboard = new ArrayList<>();
		LVL = lvl;
		save();
	}
	public Leaderboard()
	{
		leaderboard = new ArrayList<>();
	}
	/**
	 * adds a player profile to the leaderboard and sorts it automatically
	 * @param profile
	 */
	public void addProfile(PlayerProfile profile) {
		leaderboard.add(profile);
		Collections.sort(leaderboard);
		if(leaderboard.size() > 10)
		{
			leaderboard.remove(10);
		}
	}

	/**
	 * prints out the leaderboard (top 10 scores) as a string.
	 */
	@Override
	public String toString() {
		String board = "Leaderboard\n";
		for (int i = 0; i < leaderboard.size(); i++) {
			board += (leaderboard.get(i) + "\n");
		}
		return board;
	}
	
	private void newFile() 
	{
		try {
			new File("src/Leaderboards").mkdir();
			File myObj = new File("src/Leaderboards/" + LVL + ". Leaderboard.txt");
			if (myObj.exists())
			{
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
	public void save() 
	{
		newFile();
		try {
			FileWriter writer = new FileWriter("src/Leaderboards/" + LVL + ". Leaderboard.txt");
			for (int i = 0; i < leaderboard.size(); i++)
			{
				writer.write(leaderboard.get(i).getName() + "," +  leaderboard.get(i).getHighScore() +"\n");
			}
			writer.close();
			System.out.println("Saved");
		}
		catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	public void deleteSave() {
		File myObj = new File("src/Leaderboards/" + LVL + ". Leaderboard.txt");
		if (myObj.delete()) {
			System.out.println("Save deleted");
		}
		else {
			System.out.println("Failed to delete the file.");
		}
	}
	public void load(int lvl) 
	{
		try 
		{
		      File myObj = new File("src/Leaderboards/" + lvl + ". Leaderboard.txt");
		      Scanner in = new Scanner(myObj);
		      
		      while (in.hasNextLine())
				{
		    	  	String line = in.nextLine();
		    	  	String[] param = line.split(",");
		    	  	PlayerProfile player = new PlayerProfile(param[0]);
		    	  	player.setHighScore(Integer.parseInt(param[1]));
					leaderboard.add(player);
				}
		      in.close();
		}
		catch (FileNotFoundException e) 
		{
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		}
	}
}
