package RatGame;

import javafx.scene.image.Image;

public enum TileType {
    Grass (new Image("Assets/Grass.png"), false),
    Path(new Image("Assets/Path.png"),true),
    Tunnel(new Image("Assets/Tunnel.png"),false),
    VerticalTunnel (new Image("Assets/TunnelVertical.png"),false);

    Image textureName;
    boolean isTraversable;

    TileType (Image textureName, boolean isTraversable) {
        this.textureName = textureName;
        this.isTraversable = isTraversable;
    }
}
