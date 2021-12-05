package RatGame;

import javafx.scene.image.Image;

// Curly brackets need to be on same level as the statement, and you use both ways here.
/**
 * This class allows for the creation of No Entry Sign items.
 * The no entry sign blocks a path. Forcing the rat to turn around. 
 * @author José.
 */
public class NoEntrySign extends Item {

    // Constants
	private static final int MAX_HP = 6;

    // Image paths
    private static final String NO_ENTRY_TEXTURE_PATH = "Assets/Stop.png";
    private static final String BROKEN_SIGN_TEXTURE_PATH_START = "Assets/brokenstop/brokenstop";
    private static final String IMAGE_FILE_ENDING = ".png";

    //Varible 
	private int hpLeft;

	/**
     * Constructor initializing all variables
     */
	public NoEntrySign() {
		hpLeft = MAX_HP;
        setTexture(new Image(NO_ENTRY_TEXTURE_PATH));
	}

    /**
     * Constructor used for loading from a save only.
     * @param x The x position of the item.
     * @param y The y position of the item.
     * @param expired If the item is expired.
     * @param hpLeft The life remaining on the item until it expires.
     */
    public NoEntrySign(int x, int y, boolean expired, int hpLeft) {
        this.type = ItemType.NO_ENTRY_SIGN;
        this.texture = new Image(NO_ENTRY_TEXTURE_PATH);

        this.xPos = x;
        this.yPos = y;
        this.expired = expired;
        this.hpLeft = hpLeft;

        if (hpLeft != MAX_HP) {
            updateImage();
        }
    }
    /**
     * Called when item is used. No entry sign doesn't do anything on activation.
     */
    @Override
    public void use() {

    }
    /**
     * updates the image of the item to show damage
     */
    private void updateImage() {
        if (hpLeft > 0 && hpLeft < MAX_HP) {
            getImageView().setImage(new Image(BROKEN_SIGN_TEXTURE_PATH_START + hpLeft + IMAGE_FILE_ENDING));
        }
    }

    /**
     * Handles what happens when a rat steps on this item.
     * No entry gign takes damage and updates the image accordingly.
     * @param rat The rat that has stepped on this item.
     */
    @Override
    public void steppedOn(Rat rat) {
        hpLeft--;
        updateImage();
        System.out.println(rat.getXVel());
        rat.turnAround();
        System.out.println(rat.getXVel());
    	if (hpLeft == 0) {
    		expired = true;
    	}
    }

    /**
     * No entry sign doesn't change with time.
     * @param deltaTime The time since last frame.
     */
    @Override
    public void update(float deltaTime) {

    }

    /**
     * To string which returns the string used for saving this item.
     * @return The string required for saving this item.
     */
    @Override
    public String toString() {
        return "STP " +
                xPos + " " +
                yPos + " " +
                expired + " " +
                hpLeft;
    }
}
