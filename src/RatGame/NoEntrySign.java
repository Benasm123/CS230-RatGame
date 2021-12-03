package RatGame;

import javafx.scene.image.Image;

// Curly brackets need to be on same level as the statement, and you use both ways here.
public class NoEntrySign extends Item {

    // Constants
	private static final int MAX_HP = 6;

    // Image paths
    private static final String NO_ENTRY_TEXTURE_PATH = "Assets/Stop.png";
    private static final String BROKEN_SIGN_TEXTURE_PATH_START = "Assets/brokenstop/brokenstop";
    private static final String IMAGE_FILE_ENDING = ".png";

	private int hpLeft;
	
	public NoEntrySign() {
		hpLeft = MAX_HP;
        setTexture(new Image(NO_ENTRY_TEXTURE_PATH));
	}

    public NoEntrySign(int x, int y, boolean expired, int hpLeft) {
        this.type = ItemType.NO_ENTRY_SIGN;
        this.texture = new Image(NO_ENTRY_TEXTURE_PATH);

        this.xPos = x;
        this.yPos = y;
        this.expired = expired;
        this.hpLeft = hpLeft;

        updateImage();
    }
    
    @Override
    public void use() {
        System.out.println("Stop sign used?");
    }

    private void updateImage() {
        if (hpLeft > 0 && hpLeft < MAX_HP) {
            getImageView().setImage(new Image(BROKEN_SIGN_TEXTURE_PATH_START + hpLeft + IMAGE_FILE_ENDING));
        }
    }

    @Override
    public void steppedOn(Rat rat) {
        hpLeft--;
        updateImage();
        rat.setxVel(rat.getxVel()*-1);
        rat.setyVel(rat.getyVel()*-1);
    	if (hpLeft == 0) {
    		expired = true;
    	}
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public String toString() {
        return "STP " +
                xPos + " " +
                yPos + " " +
                expired + " " +
                hpLeft;
    }
}
