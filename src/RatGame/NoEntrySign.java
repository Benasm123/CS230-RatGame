package RatGame;

import javafx.scene.image.Image;

// Curly brackets need to be on same level as the statement, and you use both ways here.
public class NoEntrySign extends Item 
{
    // Maybe make this a bit more meaningful, like STARTING_HEALTH or something
	private static final int HP = 5;
	private int hpLeft;
	
	public NoEntrySign()
	{
		hpLeft = HP;
        texture = new Image("Assets/Stop.png");
	}
    
    @Override
    public void use() {
        System.out.println("Stop sign used?");
    }

    @Override
    public void steppedOn(Rat rat) {
        hpLeft--;
        rat.setxVel(rat.getxVel()*-1);
        rat.setyVel(rat.getyVel()*-1);
    	if (hpLeft == 0)
    	{
    		expired = true;
    	}
    }

    @Override
    public void update(float deltaTime) {

    }

}
