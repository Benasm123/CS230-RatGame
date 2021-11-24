package RatGame;

import javafx.scene.image.Image;

public class Gas extends Item{

    private static final int lifeDuration = 5;
    private static int lifeRemaining;
    private static final int speed = 5;
    private float posX;
    private float posY;
    Image texture;

    public Gas() {

        texture = new Image("Assets/Gas.png");
    }

    public void spreadGas(){

    }


    @Override
    public void use() {

    }

    @Override
    public void steppedOn(Rat rat) {

    }
}
