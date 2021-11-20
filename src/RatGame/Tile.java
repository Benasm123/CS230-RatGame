package RatGame;

import javafx.scene.image.Image;

public class Tile {
    private final int tileHeight = 50;
    private final int tileWidth = 50;

    private float posX;
    private float posY;
    private TileType type;
    private Image texture;


    public Tile(float posX, float posY, TileType type) {
        this.posX = posX;
        this.posY = posY;
        this.type = type;
        this.texture = new Image(this.type.tileName);
    }

    public TileType getType() {
        return type;
    }

    public Image getTexture(){
        return texture;
    }

}
