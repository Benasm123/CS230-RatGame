package RatGame;

enum ItemType {
    BOMB(0, "Assets/Bomb.png"),
    GAS(1, "Assets/Gas.png"),
    STERILISATION(2, "Assets/Sterilisation.png"),
    POISON(3, "Assets/Poison.png"),
    MALE_SEX_CHANGE(4, "Assets/MaleSexChange.png"),
    FEMALE_SEX_CHANGE(5, "Assets/FemaleSexChange.png"),
    NO_ENTRY_SIGN(6, "Assets/Stop.png"),
    DEATH_RAT(7, "Assets/Death.png");

    private final int arrayPos;
    private final String texture;

    ItemType(int arrayPos, String texture){
        this.arrayPos = arrayPos;
        this.texture = texture;
    }

    public int getArrayPos() {
        return arrayPos;
    }

    public String getTexture(){
        return texture;
    }
}