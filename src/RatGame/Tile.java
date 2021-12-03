package RatGame;

import javafx.scene.image.Image;

/**
 * This class creates instances of tiles that are used in the map of the game.
 * It is implemented in the level class which spawns tiles according to the level format.
 *
 * @author Ephraim Okonji
 * @version 1.0
 *
 */
public class Tile {
    // Constants
    public static final int TILE_HEIGHT = 50;
    public static final int TILE_WIDTH = 50;

    // Variables
    private float xPos;
    private float yPos;
    private TileType type;
    private Image texture;

    /**
     * Creates a Tile object
     * @param xPos x-position of the tile
     * @param yPos y-position of the tile
     * @param type the type of the tile
     */
    public Tile(float xPos, float yPos, TileType type) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
        this.texture = type.textureName;
    }

    /**
     * @return the type of the Tile
     */
    public TileType getType() {
        return type;
    }

    /**
     * @return the texture of the Tile
     */
    public Image getTexture() {
        return texture;
    }

    /**
     * @return the tile type as a string according to the level format
     */
    @Override
    public String toString() {
        if (TileType.Grass == type) {
            return "G";
        }
        else if (TileType.Path == type) {
            return "P";
        }
        else if (TileType.Tunnel == type) {
            return "T";
        }
        else if (TileType.VerticalTunnel == type) {
            return "V";
        }
        else return "not a type of tile in the level";
    }
}
