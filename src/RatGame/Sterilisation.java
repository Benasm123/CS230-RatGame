package RatGame;

import javafx.scene.image.Image;

public class Sterilisation extends Item{

    private static final int spreadRadius = 5;
    private float remainingTime;
    private float posX;
    private float posY;
    Image texture;

    public Sterilisation() {
        texture = new Image("Assets/Sterilisation.png");
    }

    public void makeSterile(){

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
