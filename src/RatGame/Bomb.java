package RatGame;

import javafx.scene.image.Image;

public class Bomb extends Item {

    private static final int countdown = 4;
    private float posX;
    private float posY;
    Image texture;

    public Bomb() {
        texture = new Image("Assets/Bomb.png");
    }

    // explodes vertically and horizontally till it reaches Grass tiles in both directions,
    // deletes everything in path (rat, item)
    public void explosion() {

    }

    @Override
    public void use() {

    }

    @Override
    public void steppedOn() {

    }
}