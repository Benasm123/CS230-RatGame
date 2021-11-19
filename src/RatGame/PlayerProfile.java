package RatGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PlayerProfile
{
	private String name;
	private int score;
	private int level;
	
	public PlayerProfile(String name)
	{
		setName(name);
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public int getScore()
	{
		return score;
	}
	public void setScore(int score)
	{
		this.score = score;
	}
	public int getLevel()
	{
		return level;
	}
	public void setLevel(int level)
	{
		this.level = level;
	}
	public void newFile(String file)
	{
		try
		{
			File myObj = new File(file + ".txt");
			if (myObj.createNewFile())
			{
				System.out.println("File created: " + myObj.getName());
				System.out.println("Absolute path: " + myObj.getAbsolutePath());
			} else
			{
				System.out.println("File already exists.");
				System.out.println("Absolute path: " + myObj.getAbsolutePath());
			}
		} catch (IOException e)
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	public void save()
	{
		newFile(name);
		try
		{
			FileWriter writer = new FileWriter(name + ".txt");
			writer.write(name + "\n" + score + "\n" + level);
			writer.close();
			System.out.println("Saved");
		} catch (IOException e)
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	public void deleteSave(String save)
	{
		File myObj = new File(save + ".txt");
		if (myObj.delete())
		{
			System.out.println("Save deleted");
		} else
		{
			System.out.println("Failed to delete the file.");
		}
	}
	public void load(String save)
	{
		try {
		      File myObj = new File(save + ".txt");
		      Scanner in = new Scanner(myObj);
		      
		      name = in.nextLine();
		      score = Integer.parseInt(in.nextLine());
		      level = Integer.parseInt(in.nextLine());
		      
		      in.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
}
