package RatGame;

import javafx.scene.image.Image;

public class Sterilisation extends Item{

    private float posX;
    private float posY;
    Image texture;

    public Sterilisation() {
        texture = new Image("Assets/Sterilisation.png");
    }

    @Override
    public void use() {

    }

    @Override
    public void steppedOn() {

    }
}
