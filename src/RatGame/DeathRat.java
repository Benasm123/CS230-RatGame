package RatGame;

import javafx.scene.image.Image;

/**
 * This class allows for the creation of death rat item.
 * The death rat kills 5 other rats.
 *
 * @author Ahmed Almahari
 *
 */
public class DeathRat extends Item {

    // Constants
    private static final int TIMER = 2;

    // Image paths
    private static final String DEATH_TEXTURE_PATH = "Assets/Death.png";

    private float timeLeft;
    private boolean isSpawning;

    /**
     * Creates an instance of an item of type death rat.
     */
    public DeathRat() {
        timeLeft = TIMER;
        texture = new Image(DEATH_TEXTURE_PATH);
        type = ItemType.DEATH_RAT;
    }

    /**
     * Constructor only used for loading the item state from a save file.
     * @param x The Death rat items x position.
     * @param y The death rat items y position.
     * @param expired If the item has expired.
     * @param timeLeft The time left until the death rat moves.
     * @param isSpawning If the rat is spawning.
     */
    public DeathRat(int x, int y, boolean expired, float timeLeft, boolean isSpawning) {
        this.type = ItemType.DEATH_RAT;
        this.texture = new Image(DEATH_TEXTURE_PATH);

        this.xPos = x;
        this.yPos = y;
        this.expired = expired;
        this.timeLeft = timeLeft;
        this.isSpawning = isSpawning;
    }

    /**
     * Sets isSpawning
     * @param spawning the value you want to set isSpawning to.
     */
    public void setSpawning(boolean spawning) {
        isSpawning = spawning;
    }

    /**
     *
     * @return isSpawning
     */
    public boolean getSpawning() {
        return isSpawning;
    }

    /**
     * method inherited from the parent class, called when the item is used
     */
    @Override
    public void use() {

    }

    /**
     * Death rat does nothing when a rat steps on its item.
     * @param rat The rat that steps on the item
     */
    @Override
    public void steppedOn(Rat rat) {

    }

    /**
     * method which keeps rat stationary when spawning.
     * @param deltaTime The time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        timeLeft -= deltaTime;
        if (timeLeft <= 0) {
            isSpawning = true;
            expired = true;
        }
    }

    /**
     * To string which returns the string used for saving this item.
     * @return The string required for saving this item.
     */
    @Override
    public String toString() {
        return "DTH " +
                xPos + " " +
                yPos + " " +
                expired + " " +
                timeLeft + " " +
                isSpawning;
    }
}
