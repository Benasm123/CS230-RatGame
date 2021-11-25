package RatGame;

import javafx.scene.image.Image;

public class DeathRat extends Item {
    public DeathRat()
	{
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

    }
}
