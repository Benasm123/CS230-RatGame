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
        if (rat.type == Rat.ratType.FEMALE) {
            rat.changeSex(rat);
            expired = true;
        }
    }

    @Override
    public void update(float deltaTime) {

    }

}
