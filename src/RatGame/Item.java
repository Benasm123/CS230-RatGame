package RatGame;

import javafx.scene.image.Image;

public abstract class Item {

    Image texture;
    int xPos;
    int yPos;

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
    public abstract void steppedOn();
    
}
