package RatGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Leaderboard
{
	public static HashMap<String, Integer> leaderboard = new HashMap<String, Integer>();

	public static void addProfile(PlayerProfile profile)
	{
		if(leaderboard.size() < 11)
		{
			leaderboard.put(profile.getName(), profile.getScore());
			sort(leaderboard);
		}
	}
	private static void sort(HashMap<String, Integer> map) 
	{
		ArrayList<Integer> scores = new ArrayList<>(map.values());
		Collections.sort(scores);
		System.out.println(scores);
	}
	
//	public static String print()
//	{
//		String top = "Leaderboard\n";
//		for (int i = 0; i < leaderboard.length; i++)
//		{
//			if (leaderboard[i] != null)
//			{
//				top += leaderboard[i] + "\n";
//			}
//		}
//		return top;
//	}
	 
}
