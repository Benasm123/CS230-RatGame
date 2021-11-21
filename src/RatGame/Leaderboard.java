package RatGame;

import java.util.ArrayList;
import java.util.Collections;

public class Leaderboard
{
	public static ArrayList <PlayerProfile> leaderboard = new ArrayList<>();
	

	public static void addProfile(PlayerProfile profile)
	{
		leaderboard.add(profile);
		Collections.sort(leaderboard);
	}
	
	public static void print()
	{
		System.out.println("Leaderboard");
		for (int i = 0; i < 10; i++)
		{
			System.out.println(leaderboard.get(i));
		}
	}
	 
}
