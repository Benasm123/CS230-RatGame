package RatGame;

import javafx.scene.image.Image;

public class Poison extends Item {

    public Poison() {
        texture =  new Image("Assets/Poison.png");
        imageView.setImage(texture);
    }

    @Override
    public void use() {
        expired = true;
    }

    @Override
    public void steppedOn(Rat rat) {
        //rat.setDead();
        use();
    }

    @Override
    public void update(float deltaTime) {

    }

}
