package RatGame;

import javafx.scene.image.Image;

// try to space the methods out with one line. mostly ok but use and death rat are touching.
public class DeathRat extends Item {

    // Static final variables need to be ALL CAPS, so TIMER
    private static final int timer = 2;
    private float timeLeft;
    private boolean isSpawning;

    public void setSpawning(boolean spawning){
        isSpawning = spawning;
    }

    public boolean getSpawning(){
        return isSpawning;
    }

    public void setType(ItemType type){
    }

    // Constructor needs to go before all other modules and after the variables.
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
            isSpawning = true;
            expired = true;
        }

    }
}
