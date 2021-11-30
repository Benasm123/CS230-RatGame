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

// For the @author tag in your java docs i think you put your own name rather than just the group
// Should also go here after imports and before class declaration.
// make sure everyone else includes this too, i forgot about the class javadocs.

// Order for methods and variables needs to be:
// Public
// Protected
// package level
// Then private
// so need to reorder the methods
public class Bomb extends Item {

    // Id seperate the static variables from the others by a line, just to make teh separation clearer
    private static final int COUNTDOWN = 4;
    // This can be initialised In use.
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

    // I would suggest refactoring this to something a little more meaningful.
    private boolean canExplodeHere(Tile[][] levelGrid, int xPos, int yPos) {
        return levelGrid[xPos][yPos].getType().isTraversable;
    }

    // Convention specifies One single line between methods, this comment was written in a second one.
    public ArrayList<Pair<Integer, Integer>> getBombTiles(Tile[][] levelGrid) {
        isExploding = false;
        ArrayList<Pair<Integer, Integer>> bombTiles = new ArrayList<>();
        int lastCheckedX = xPos;
        int lastCheckedY = yPos;

        while (canExplodeHere(levelGrid,lastCheckedX,yPos)) {
            bombTiles.add(new Pair<>(lastCheckedX,yPos));
            lastCheckedX--;
        }
        // This last checked is changed but not used until you change it again in the next while loop,
        // can tidy up and remove the redundant variable sets.
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
