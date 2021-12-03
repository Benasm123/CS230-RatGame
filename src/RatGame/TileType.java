package RatGame;

import javafx.scene.image.Image;

/**
 * This enum class allows for the creation of various tile types.
 *
 * @author Ephraim Okonji
 * @version 1.0
 *
 */
public enum TileType {
    Grass (new Image("Assets/Grass.png"), false),
    Path(new Image("Assets/Path.png"),true),
    Tunnel(new Image("Assets/Tunnel.png"),true),
    VerticalTunnel (new Image("Assets/TunnelVertical.png"),true);

    // Variables
    Image textureName;
    boolean isTraversable;

    /**
     * creates an instance of a tile type
     * @param textureName the graphic/texture given to each tile from our Assets bank.
     * @param isTraversable checks if the tile is traversable by rats and items.
     */
    TileType (Image textureName, boolean isTraversable) {
        this.textureName = textureName;
        this.isTraversable = isTraversable;
    }
}
