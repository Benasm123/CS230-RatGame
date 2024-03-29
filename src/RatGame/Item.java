package RatGame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Abstract Parent class for all items.
 * @author Jack Lewis, Benas Montrimas.
 */
public abstract class Item {

    // Constants
    private static final float HITBOX_OFFSET = 0.1f;
    private static final float HITBOX_WIDTH_HEIGHT = 1.0f - HITBOX_OFFSET * 2;

    // Variables all items have.
    protected ItemType type;
    protected Image texture;
    protected ImageView imageView;
    protected int xPos;
    protected int yPos;
    protected boolean expired;
    protected HitBox hitBox;

    /**
     * Constructor initializing things all items need.
     */
    protected Item(){
        imageView = new ImageView();
        expired = false;
    }

    /**
     * Creates a hitbox for the item.
     */
    public void createHitBox() {
        hitBox = new HitBox(xPos + HITBOX_OFFSET, yPos + HITBOX_OFFSET, HITBOX_WIDTH_HEIGHT, HITBOX_WIDTH_HEIGHT);
    }

    /**
     * Used to create individual items without needing constructor.
     * @param type The type of item that you want to create.
     * @return Returns the item created.
     */
    public static Item create(ItemType type){
        Item item = null;
        switch (type) {
            case BOMB:
                item = new Bomb();
                break;
            case GAS:
                item = new Gas();
                break;
            case STERILISATION:
                item = new Sterilisation();
                break;
            case POISON:
                item = new Poison();
                break;
            case MALE_SEX_CHANGE:
                item = new MaleSexChange();
                break;
            case FEMALE_SEX_CHANGE:
                item = new FemaleSexChange();
                break;
            case NO_ENTRY_SIGN:
                item = new NoEntrySign();
                break;
            case DEATH_RAT:
                item = new DeathRat();
                break;
            default:
                System.out.println("Attempting to spawn an item not available!");
                break;
        }
        item.getImageView().setImage(item.getTexture());
        return item;
    }

    /**
     * Called when an item is placed.
     */
    public abstract void use();

    /**
     * Called when a rat steps on the item.
     * @param rat The rat which has stepped on the item.
     */
    public abstract void steppedOn(Rat rat);

    /**
     * Called every frame to update item.
     * @param deltaTime The time since last frame.
     */
    public abstract void update(float deltaTime);

    /**
     * Gets the texture.
     * @return The items texture.
     */
    public Image getTexture(){
        return texture;
    }

    /**
     * Sets the texture.
     * @param texture The texture that you want to set to.
     */
    public void setTexture(Image texture) {
        this.texture = texture;
    }

    /**
     * Gets the image view.
     * @return The items image view.
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Gets the items hitbox.
     * @return The hitbox of the item.
     */
    public HitBox getHitBox() {
        return hitBox;
    }

    /**
     * Gets the x position.
     * @return The items x position.
     */
    public int getXPos() {
        return xPos;
    }

    /**
     * Sets the x position
     * @param xPos The x position you want to set it to.
     */
    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    /**
     * Gets the y positions.
     * @return The items y position.
     */
    public int getYPos() {
        return yPos;
    }

    /**
     * Sets the y position.
     * @param yPos The y position you want to set it to.
     */
    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    /**
     * Sets whether the item is expired.
     * @param expired The value to set if the item is expired to.
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     * Get the type of the item.
     * @return The items type.
     */
    public ItemType getType() {
        return type;
    }

    /**
     * Set the items type.
     * @param type The type you want to set the item to.
     */
    public void setType(ItemType type) {
        this.type = type;
    }
}
