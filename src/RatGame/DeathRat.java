package RatGame;

import javafx.scene.image.Image;

public class DeathRat extends Item {

    private static final int timer = 2;
    private float timeLeft;
    private boolean isSpawning;

    public void setSpawning(boolean spawning){
        isSpawning = false;
    }

    public boolean getSpawning(){
        return isSpawning;
    }

    public void setType(ItemType type){
    }

    public DeathRat()
	{
        timeLeft = timer;
        texture = new Image("Assets/Death.png");
        type = ItemType.DEATH_RAT;
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
