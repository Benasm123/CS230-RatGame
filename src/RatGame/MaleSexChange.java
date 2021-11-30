/**
 * This class allows for the creation of Male sex change items.
 * If they are of the opposite sex, this sex change item changes the rat's sex to male as soon as it's stepped on.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;

import javafx.scene.image.Image;

public class MaleSexChange extends Item {
    public MaleSexChange()
	{
        texture = new Image("Assets/MaleSexChange.png");
	}

    /**
     *
     */
    @Override
    public void use() {

    }

    /**
     * method inherited from the parent class Item.
     * For this particular item, it changes a rat's sex to male as soon as it is stepped on.
     * It only does this after it has confirmed that the rat is of the opposite sex
     * The item then becomes expired and disappears from the map
     * @param rat the rat that steps on the sex change item.
     */
    @Override
    public void steppedOn(Rat rat) {
        // Same comments as in female sex change here, you can even move everytthing into the changeSex method
        if(rat.type == Rat.ratType.DEATHRAT){
            expired =true;
        }else{
            rat.changeSexMale(rat);
            expired =true;
        }
    }

    /**
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {

    }

}
