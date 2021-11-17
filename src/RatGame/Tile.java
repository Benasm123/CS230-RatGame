package RatGame;

import javafx.scene.image.Image;

public class Tile {

    private float posX;
    private float posY;
    private int tileHeight;
    private int tileWidth;
    private TileType type;
    private Image texture;


    public Tile(float posX, float posY, int tileHeight, int tileWidth, TileType type) {
        this.posX = posX;
        this.posY = posY;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

}
