/**
 * This class allows for the creation of Female sex change items.
 * If they are of the opposite sex, this sex change item changes the rat's sex to female as soon as it's stepped on.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;

import javafx.scene.image.Image;

public class FemaleSexChange extends Item {
    public FemaleSexChange()
	{
        texture = new Image("Assets/FemaleSexChange.png");
	}

    /**
     *
     */
    @Override
    public void use() {

    }

    /**
     * method inherited from the parent class Item.
     * For this particular item, it changes a rat's sex to female as soon as it is stepped on.
     * It only does this after it has confirmed that the rat is of the opposite sex
     * The item then becomes expired and disappears from the map
     * @param rat the rat that steps on the sex change item.
     */
    @Override
    public void steppedOn(Rat rat) {
        // Space after if and before condition. and beteen brackets before and after else
        // You can just set expired to true after the if statement and also just have 1 if, checkiing if its not a death rat
        // tidies it up.
        if(rat.type==Rat.ratType.DEATHRAT){
            expired = true;
        }else{
            rat.changeSexFemale(rat);
            expired = true;
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
