package RatGame;

import javafx.scene.image.Image;

public class Poison extends Item {

    //private boolean isOnPath;

    public Poison() {
        texture =  new Image("Assets/Poison.png");
    }

    public void kill() {

    }


    @Override
    public void use() {

    }

    @Override
    public void steppedOn(Rat rat) {

    }

}
