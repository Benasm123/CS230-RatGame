package RatGame;

import javafx.scene.image.Image;

public class FemaleSexChange extends Item {
    public FemaleSexChange()
	{
        texture = new Image("Assets/FemaleSexChange.png");
	}
    @Override
    public void use() {

    }

    @Override
    public void steppedOn(Rat rat) {
        rat.changeSex(rat);
    }

    @Override
    public void update(float deltaTime) {

    }
}
