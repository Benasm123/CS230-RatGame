/**
 * This class allows for the creation of Bomb items.
 * The bomb item makes rats and items in its path disappear.
 * The impact of the bomb's explosion continues until it hits a grass tile vertically and horizontally.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */
package RatGame;

import javafx.scene.image.Image;
import javafx.util.Pair;
import java.util.ArrayList;

public class Bomb extends Item {

    private static final int COUNTDOWN = 4;
    private float timeSincePlaced = 0;
    private boolean isExploding;

    public Bomb() {
        texture = new Image("Assets/Bomb.png");
        isExploding = false;
        type = ItemType.BOMB;
    }

    /**
     * explodes vertically and horizontally till it reaches Grass tiles in both directions,
     * deletes everything in path (rat, item)
     */

    private boolean canExplodeHere(Tile[][] levelGrid, int xPos, int yPos) {
        return levelGrid[xPos][yPos].getType().isTraversable;
    }


    public ArrayList<Pair<Integer, Integer>> getBombTiles(Tile[][] levelGrid) {
        isExploding = false;
        ArrayList<Pair<Integer, Integer>> bombTiles = new ArrayList<>();
        int lastCheckedX = xPos;
        int lastCheckedY = yPos;

        while (canExplodeHere(levelGrid,lastCheckedX,yPos)) {
            bombTiles.add(new Pair<>(lastCheckedX,yPos));
            lastCheckedX--;
        }
        lastCheckedX = xPos;
        lastCheckedY = yPos;
        while (canExplodeHere(levelGrid,xPos,lastCheckedY)) {
            bombTiles.add(new Pair<>(xPos,lastCheckedY));
            lastCheckedY++;
        }
        lastCheckedX = xPos;
        lastCheckedY = yPos;
        while (canExplodeHere(levelGrid,lastCheckedX,yPos)) {
            bombTiles.add(new Pair<>(lastCheckedX,yPos));
            lastCheckedX++;
        }
        lastCheckedX = xPos;
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
     *
     */
    @Override
    public void use() {

    }

    /**
     *
     * @param rat
     */
    @Override
    public void steppedOn(Rat rat) {

    }

    /**
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        timeSincePlaced += deltaTime;
        if (timeSincePlaced >= COUNTDOWN) {
            isExploding = true;
            expired = true;
        }
    }
}
