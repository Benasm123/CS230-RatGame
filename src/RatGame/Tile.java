package RatGame;

import javafx.scene.image.Image;

public class Tile <E> {

    private float posX, posY;
    private int tileHeight, tileWidth;
    private TileType type;
    private E e; // the element contained in the linked list
    private Tile<E> next; // the next element of the linked list

    Image grassTile = new Image("Assets/Grass.png");
    Image pathTile = new Image("Assets/Path.png");
    Image tunnelTile = new Image("Assets/Tunnel.png");
    Image tunnelVertTile = new Image("Assets/TunnelVertical.png");

    public Tile(float posX, float posY, int tileHeight, int tileWidth, TileType type) {
        this.posX = posX;
        this.posY = posY;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.type = type;
    }

    public void add() {

    }

    public void remove() {

    }

    public void find() {

    }

    public void draw() {

    }

    public TileType getType() {
        return type;
    }

}
