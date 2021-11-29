/**
 * This class allows for the creation of Sterilisation items.
 * The sterilisation item makes rats within a small radius sterile.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */


package RatGame;

import javafx.scene.image.Image;

public class Sterilisation extends Item{

    private static final int lifeDuration = 4;
    private static final int spreadRadius = 5;
    private float remainingTime;

    public Sterilisation() {
        texture = new Image("Assets/Sterilisation.png");
    }

    public void makeSterile(){

    }
    
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

        remainingTime -= deltaTime;
        makeSterile();
        if (remainingTime == 0) {
            expired = true;
        }
    }
}
