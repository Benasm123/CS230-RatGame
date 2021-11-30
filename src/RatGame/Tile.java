/**
 * This class creates instances of tiles that are used in the map of the game.
 * It is implemented in the level class which spawns tiles according to the level format.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;
import javafx.scene.image.Image;

// your toString returns "" if the tile type isnt one of the ones in the enum and this isnt ever possible,
// and if it does happen better to throw an error than do that as this could break otehr things otherwise.
public class Tile {
    // Ive made this public so we can use it throughout the project instead of all needing to set this.
    public static final int TILE_HEIGHT = 50;
    public static final int TILE_WIDTH = 50;

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
        else return "";
    }
}
