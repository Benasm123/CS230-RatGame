package RatGame;

import javafx.scene.image.Image;

public class Bomb extends Item {

    private static final int COUNTDOWN = 4;
    private int timeSincePlaced = 0;

    public Bomb() {
        texture = new Image("Assets/Bomb.png");
        imageView.setImage(texture);
    }

    // explodes vertically and horizontally till it reaches Grass tiles in both directions,
    // deletes everything in path (rat, item)
    public void explode() {

    }

    @Override
    public void use() {

    }

    @Override
    public void steppedOn(Rat rat) {

    }

    @Override
    public void update(float deltaTime) {
        timeSincePlaced += deltaTime;
        if (timeSincePlaced >= COUNTDOWN) {
            explode();
            expired = true;
        }
    }
}
