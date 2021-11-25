package RatGame;

import javafx.scene.image.Image;

public class Poison extends Item {

    private float posX;
    private float posY;
    Image texture;


    public Poison() {
        texture =  new Image("Assets/Poison.png");
    }

    public void kill() {

    }

    @Override
    public void use() {

    }

    @Override
    public void steppedOn(Rat rat) {

    }

    @Override
    public void update(float deltaTime) {

    }

}
