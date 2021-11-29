package RatGame;

import javafx.scene.image.Image;

public class DeathRat extends Item {

    private static final int timer = 2;
    private float timeLeft;

    public void setSpawning(boolean spawning){
        boolean isSpawning = false;
    }

    public DeathRat()
	{
        timeLeft = timer;
        texture = new Image("Assets/Death.png");
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
        if (timeLeft <= 0)
        {
            setSpawning(true);
        }
    }
}
