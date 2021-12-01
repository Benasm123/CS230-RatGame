package RatGame;

/**
 * Enum that contains all the item types.
 * @author Benas Montrimas.
 */
enum ItemType {
    // All item types.
    BOMB(0, "Assets/Bomb.png"),
    GAS(1, "Assets/Gas.png"),
    STERILISATION(2, "Assets/Sterilisation.png"),
    POISON(3, "Assets/Poison.png"),
    MALE_SEX_CHANGE(4, "Assets/MaleSexChange.png"),
    FEMALE_SEX_CHANGE(5, "Assets/FemaleSexChange.png"),
    NO_ENTRY_SIGN(6, "Assets/Stop.png"),
    DEATH_RAT(7, "Assets/Death.png");

    // Variables each item type needs.
    private final int index;
    private final String texture;

    /**
     * Constructor for the item type.
     * @param index The index of the item in the inventory.
     * @param texture The texture of the item type.
     */
    ItemType(int index, String texture){
        this.index = index;
        this.texture = texture;
    }

    /**
     * Gets the index of the item in the inventory.
     * @return The index of the item type.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the texture of the item type.
     * @return The item types texture.
     */
    public String getTexture(){
        return texture;
    }
}