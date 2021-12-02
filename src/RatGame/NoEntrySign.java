package RatGame;

import javafx.scene.image.Image;

// Curly brackets need to be on same level as the statement, and you use both ways here.
public class NoEntrySign extends Item {
    // Maybe make this a bit more meaningful, like STARTING_HEALTH or something
	private static final int MAX_HP = 6;
	private int hpLeft;
	
	public NoEntrySign() {
		hpLeft = MAX_HP;
        setTexture(new Image("Assets/Stop.png"));
	}
    
    @Override
    public void use() {
        System.out.println("Stop sign used?");
    }
    
    private void updateImage(int hp) {
    	getImageView().setImage(new Image("Assets/brokenstop/brokenstop" + hp + ".png"));
    }
    @Override
    public void steppedOn(Rat rat) {
        hpLeft--;
        updateImage(hpLeft);
        rat.setxVel(rat.getxVel()*-1);
        rat.setyVel(rat.getyVel()*-1);
    	if (hpLeft == 0) {
    		expired = true;
    	}
    }

    @Override
    public void update(float deltaTime) {

    }

}
