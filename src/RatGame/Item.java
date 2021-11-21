package RatGame;

import javafx.scene.image.Image;

public abstract class Item {

    Image texture;
    int xPos;
    int yPos;

    public abstract void use();
    public abstract void steppedOn();
    
}
