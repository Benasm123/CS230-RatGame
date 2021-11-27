package RatGame;

import javafx.scene.image.Image;

public class MaleSexChange extends Item {
    public MaleSexChange()
	{
        texture = new Image("Assets/MaleSexChange.png");
	}
    @Override
    public void use() {

    }

    @Override
    public void steppedOn(Rat rat) {
        rat.changeSexMale(rat);
    }

    @Override
    public void update(float deltaTime) {

    }

}
