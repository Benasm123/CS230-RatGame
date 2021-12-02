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
    private float timeLeft;
    private boolean isSpawning;

    private static final int TIMER = 2;

    /**
     * Creates an instance of an item of type death rat.
     */
    public DeathRat() {
        timeLeft = TIMER;
        texture = new Image("Assets/Death.png");
        type = ItemType.DEATH_RAT;
    }

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
     *
     * @param rat
     */
    @Override
    public void steppedOn(Rat rat) {

    }

    /**
     *
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
}
