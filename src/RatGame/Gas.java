package RatGame;

import javafx.scene.image.Image;

public class Gas extends Item{

    private float posX;
    private float posY;
    Image texture;

    public Gas() {
        texture = new Image("Assets/Gas.png");
    }

    @Override
    public void use() {

    }

    @Override
    public void steppedOn(Rat rat) {

    }
}
