/**
 * This class allows for the creation of Poison items.
 * The poison item kills rats as soon as they step on it.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;

import javafx.scene.image.Image;

public class Poison extends Item {

    public Poison() {
        texture =  new Image("Assets/Poison.png");
    }

    /**
     * method inherited from the parent class
     *
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
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {

    }

}
