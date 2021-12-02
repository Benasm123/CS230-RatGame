package RatGame;

import javafx.scene.image.Image;


public class DeathRat extends Item {
    private float timeLeft;
    private boolean isSpawning;

    private static final int TIMER = 2;

    public DeathRat() {
        timeLeft = TIMER;
        texture = new Image("Assets/Death.png");
        type = ItemType.DEATH_RAT;
    }

    public void setSpawning(boolean spawning) {
        isSpawning = spawning;
    }

    public boolean getSpawning() {
        return isSpawning;
    }

    public void setType(ItemType type) {
    }

    @Override
    public void use() {

    }

    @Override
    public void steppedOn(Rat rat) {

    }

    @Override
    public void update(float deltaTime) {
        timeLeft -= deltaTime;
        if (timeLeft <= 0) {
            isSpawning = true;
            expired = true;
        }
    }
}
