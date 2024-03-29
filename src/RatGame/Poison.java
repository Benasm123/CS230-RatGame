package RatGame;

import javafx.scene.image.Image;

/**
 * This class allows for the creation of Poison items.
 * The poison item kills rats as soon as they step on it.
 *
 * @author Ephraim Okonji, Benas Montrimas
 * @version 1.0
 *
 */
public class Poison extends Item {

    // Image Paths
    private static final String POISON_TEXTURE_PATH = "Assets/Poison.png";

    /**
     * Creates an instance of an item of type poison
     */
    public Poison() {
        texture =  new Image(POISON_TEXTURE_PATH);
        type = ItemType.POISON;
    }

    /**
     * Constructor used only for loading from saves.
     * @param x The items x position.
     * @param y The items y position.
     * @param expired If the item is expired.
     */
    public Poison(int x, int y, boolean expired) {
        this.type = ItemType.POISON;
        this.texture = new Image(POISON_TEXTURE_PATH);

        this.xPos = x;
        this.yPos = y;
        this.expired = expired;
    }

    /**
     * method inherited from the parent class, called when the item is used
     */
    @Override
    public void use() {

    }

    /**
     * method inherited from the parent class Item.
     * For this particular item, it kills a rat as soon as it is stepped on.
     * The item then becomes expired and disappears from the map
     * @param rat the rat that steps on the poison item.
     */
    @Override
    public void steppedOn(Rat rat) {
        if (this.expired){
            return;
        }
        rat.die();
        this.expired = true;
    }

    /**
     * Poison does nothing on update.
     * @param deltaTime The time since the last frame in seconds.
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
        return "PSN " +
                xPos + " " +
                yPos + " " +
                expired;
    }

}
