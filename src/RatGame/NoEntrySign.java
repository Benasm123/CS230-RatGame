package RatGame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NoEntrySign extends Item 
{
	private static final int HP = 5;
	private int hpLeft;
	
	public NoEntrySign()
	{
        super();
		hpLeft = HP;
        texture = new Image("Assets/Stop.png");
        
	}
    
    @Override
    public void use() {
        System.out.println("Stop sign used?");
    }

    @Override
    public void steppedOn(Rat rat) {

    }

}
