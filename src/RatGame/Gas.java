/**
 * This class allows for the creation of Gas items.
 * The gas item kills rats that are in the gas particles for too long.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */

package RatGame;

import javafx.scene.image.Image;

public class Gas extends Item{

    private static final int lifeDuration = 10;
    private static int lifeRemaining;
    private static final int speed = 5;


    public Gas() {
        texture = new Image("Assets/Gas.png");
    }

    public void spreadGas(){

    }

    /**
     * method inherited from the parent class.
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
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        lifeRemaining -= deltaTime;
        spreadGas();
        if (lifeRemaining == 0) {

            expired = true;
        }
    }
}
