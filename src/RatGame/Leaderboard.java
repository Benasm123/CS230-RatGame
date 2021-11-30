/**
 * This class ranks the top 10 scores in a leaderboard and prints them as a string.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;

import java.util.ArrayList;
import java.util.Collections;

// TODO: Needs to be made so one leaderboard for each level
// I dont think this keeps track of scores right now
// TODO: Cap the leaderboard length to 10 - can do by after sorting, check size and if more than 10 just copy the first
// 10 elements over the top of itself.
// Right now you store all, but only print top 10, but dont want to be storing all players that ever play as we will
// need to save this leaderboard.
public class Leaderboard {
	// creates a list of player proles called leaderboard
	public static ArrayList <PlayerProfile> leaderboard = new ArrayList<>();

	/**
	 * adds a player profile to the leaderboard and sorts it automatically
	 * @param profile
	 */
	public static void addProfile(PlayerProfile profile) {
		leaderboard.add(profile);
		Collections.sort(leaderboard);
	}

	/**
	 * prints out the leaderboard (top 10 scores) as a string.
	 */
	public static void print() {
		System.out.println("Leaderboard");
		for (int i = 0; i < 10; i++) {
			System.out.println(leaderboard.get(i));
		}
	}
}
