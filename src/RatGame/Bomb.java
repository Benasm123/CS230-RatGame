package RatGame;

import javafx.scene.image.Image;
import javafx.util.Pair;
import java.util.ArrayList;

/**
 * This class allows for the creation of Bomb items.
 * The bomb item makes rats and items in its path disappear.
 * The impact of the bomb's explosion continues until it hits a grass tile vertically and horizontally.
 *
 * @author Ephraim Okonji
 * @version 1.0
 *
 */
public class Bomb extends Item {
    private static final int COUNTDOWN = 4;

    private float timeSincePlaced;
    private boolean isExploding;

    /**
     * Creates an instance of an item of type bomb
     */
    public Bomb() {
        texture = new Image("Assets/Bomb.png");
        isExploding = false;
        type = ItemType.BOMB;
    }

    /**
     * @param levelGrid
     * @return the list of tiles that can be affected by the bomb
     */
    public ArrayList<Pair<Integer, Integer>> getBombTiles(Tile[][] levelGrid) {
        isExploding = false;
        ArrayList<Pair<Integer, Integer>> bombTiles = new ArrayList<>();
        int lastCheckedX = xPos;
        int lastCheckedY = yPos;

        while (canExplodeHere(levelGrid,lastCheckedX,yPos)) {
            bombTiles.add(new Pair<>(lastCheckedX,yPos));
            lastCheckedX--;
        }
        lastCheckedY = yPos;
        while (canExplodeHere(levelGrid,xPos,lastCheckedY)) {
            bombTiles.add(new Pair<>(xPos,lastCheckedY));
            lastCheckedY++;
        }
        lastCheckedX = xPos;
        while (canExplodeHere(levelGrid,lastCheckedX,yPos)) {
            bombTiles.add(new Pair<>(lastCheckedX,yPos));
            lastCheckedX++;
        }
        lastCheckedY = yPos;
        while (canExplodeHere(levelGrid,xPos,lastCheckedY)) {
            bombTiles.add(new Pair<>(xPos,lastCheckedY));
            lastCheckedY--;
        }
        return bombTiles;
    }

    /**
     *
     * @return isExploding
     */
    public boolean isExploding() {
        return isExploding;
    }

    /**
     * method inherited from the parent class, called when the item is used
     */
    @Override
    public void use() {
        timeSincePlaced = 0;
    }

    /**
     *
     * @param rat
     */
    @Override
    public void steppedOn(Rat rat) {

    }

    /**
     * method which updates the bomb since last time frame in seconds
     * @param deltaTime The time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        timeSincePlaced += deltaTime;
        if (timeSincePlaced >= COUNTDOWN) {
            isExploding = true;
            expired = true;
        }
    }

    /**
     *
     * @param levelGrid
     * @param xPos
     * @param yPos
     * @return
     */
    private boolean canExplodeHere(Tile[][] levelGrid, int xPos, int yPos) {
        return levelGrid[xPos][yPos].getType().isTraversable;
    }
}
