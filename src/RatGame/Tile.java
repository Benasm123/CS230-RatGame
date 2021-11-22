/**
 * This class creates a Tile.
 *
 * @author CS-250 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;
import javafx.scene.image.Image;

public class Tile {
    private static final int tileHeight = 50;
    private static final int tileWidth = 50;

    private float posX;
    private float posY;
    private TileType type;
    private Image texture;

    /**
     * Creates a Tile object
     * @param posX x-position of the tile
     * @param posY y-position of the tile
     * @param type the type of the tile
     */

    public Tile(float posX, float posY, TileType type) {
        this.posX = posX;
        this.posY = posY;
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
        else return "";
    }
}
