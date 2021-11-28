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
