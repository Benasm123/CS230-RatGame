package RatGame;

import javafx.scene.image.Image;

/**
 * This class allows for the creation of Male sex change items.
 * If they are of the opposite sex, this sex change item changes the rat's sex to male as soon as it's stepped on.
 *
 * @author Oluwatimilehin Abajingin
 * @version 1.0
 *
 */
public class MaleSexChange extends Item {

    // Image paths
    private static final String MALE_CHANGE_TEXTURE_PATH = "Assets/MaleSexChange.png";

    public MaleSexChange()
	{
        type = ItemType.MALE_SEX_CHANGE;
        texture = new Image(MALE_CHANGE_TEXTURE_PATH);
	}

    /**
     *  Constructor only used for loading the item state from a save file.
     *  @param x The items x position.
     *  @param y The items y position.
     *  @param expired If the item have expired.
     */
    public MaleSexChange(int x, int y, boolean expired) {
        this.type = ItemType.MALE_SEX_CHANGE;
        this.texture = new Image(MALE_CHANGE_TEXTURE_PATH);

        this.xPos = x;
        this.yPos = y;
        this.expired = expired;
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
        // Same comments as in female sex change here, you can even move everything into the changeSex method
        if (rat.getType() != RatType.DEATH_RAT) {
            rat.changeSexMale();
        }
        expired =true;
    }

    /**
     *
     * @param deltaTime
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
        return "MSX " +
                xPos + " " +
                yPos + " " +
                expired;
    }

}
