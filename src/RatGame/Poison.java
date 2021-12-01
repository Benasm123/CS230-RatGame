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

    /**
     * Creates an instance of an item of type poison
     */
    public Poison() {
        texture =  new Image("Assets/Poison.png");
        type = ItemType.POISON;
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
        rat.die();
        this.expired = true;
    }

    /**
     *
     * @param deltaTime The time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {

    }

}
