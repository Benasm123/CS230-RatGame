package RatGame;

public enum TileType {
    Grass ("Assets/Grass.png", false),
    Path("Assets/Path.png",true),
    Tunnel("Assets/Tunnel.png",false),
    VerticalTunnel ("Assets/TunnelVertical.png",false);

    String tileName;
    boolean isTraversable;

    TileType (String tileName, boolean isTraversable) {
        this.tileName = tileName;
        this.isTraversable = isTraversable;
    }
}
