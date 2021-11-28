package RatGame;

import javafx.scene.image.Image;

public class NoEntrySign extends Item 
{
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
