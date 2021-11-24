package RatGame;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public abstract class Item {

    private Image texture;
    private ImageView imageView;
    private int xPos;
    private int yPos;

    protected Item(){
        this.imageView = new ImageView();
        this.imageView.setImage(this.texture);
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
        return item;
    }

    public abstract void use();
    public abstract void steppedOn(Rat rat);

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
}
