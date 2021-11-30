package RatGame;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

// Lots of unused import statements.
public abstract class Item {

    // Can make these all private and add getters and setter that are public.
    // TODO: in all items need to set variables using setters and use make this private.
    protected ItemType type;
    protected Image texture;
    protected ImageView imageView;
    protected int xPos;
    protected int yPos;
    protected boolean expired;

    protected Item(){
        imageView = new ImageView();
        expired = false;
    }

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

    public abstract void use();
    public abstract void steppedOn(Rat rat);
    public abstract void update(float deltaTime);

    public Image getTexture(){
        return texture;
    }

    public void setTexture(Image texture) {
        this.texture = texture;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public void setExpired(boolean expired) {
        this.expired = true;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
