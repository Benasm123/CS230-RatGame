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

    public Bomb() {
        texture = new Image("Assets/Bomb.png");
    }

    /**
     * explodes vertically and horizontally till it reaches Grass tiles in both directions,
     * deletes everything in path (rat, item)
     */
    public void explode(Tile[][] levelGrid, Item item, Rat rat) {

        final boolean canExplodeRight = levelGrid[xPos + 1][yPos].getType().isTraversable;
        final boolean canExplodeLeft = levelGrid[xPos - 1][yPos].getType().isTraversable;
        final boolean canExplodeUp = levelGrid[xPos][yPos + 1].getType().isTraversable;
        final boolean canExplodeDown = levelGrid[xPos][yPos - 1].getType().isTraversable;
        final boolean isExploding = canExplodeDown || canExplodeLeft || canExplodeRight || canExplodeUp;

        while (isExploding) {
            if (item.xPos && item.yPos == getBombTiles()){
                item.expired = true;
            }
            if (rat.getxPos() && rat.getyPos() == getBombTiles()) {
                rat.die();
            }
        }
        expired = true;
    }

    public ArrayList<Pair<Integer, Integer>> getBombTiles(Tile[][] levelGrid) {
        ArrayList<Pair<Integer, Integer>> bombTiles = new ArrayList<>();
        int lastCheckedX = xPos;
        int lastCheckedY = yPos;
        final boolean canExplodeRight = levelGrid[xPos + 1][yPos].getType().isTraversable;
        final boolean canExplodeLeft = levelGrid[xPos - 1][yPos].getType().isTraversable;
        final boolean canExplodeUp = levelGrid[xPos][yPos + 1].getType().isTraversable;
        final boolean canExplodeDown = levelGrid[xPos][yPos - 1].getType().isTraversable;
        final boolean isExploding = canExplodeDown || canExplodeLeft || canExplodeRight || canExplodeUp;

        if (isExploding) {
            while (canExplodeLeft && xPos - 1 != lastCheckedX) {
                bombTiles.add(new Pair<>(xPos,yPos));
                lastCheckedX--;
            }
            lastCheckedX = xPos;
            lastCheckedY = yPos;
            while (canExplodeUp && yPos + 1 != lastCheckedY) {
                bombTiles.add(new Pair<>(xPos,yPos));
                lastCheckedY++;
            }
            lastCheckedX = xPos;
            lastCheckedY = yPos;
            while (canExplodeRight && xPos + 1 != lastCheckedX) {
                bombTiles.add(new Pair<>(xPos,yPos));
                lastCheckedX++;
            }
            lastCheckedX = xPos;
            lastCheckedY = yPos;
            while (canExplodeDown && yPos - 1 != lastCheckedY) {
                bombTiles.add(new Pair<>(xPos,yPos));
                lastCheckedY--;
            }
            lastCheckedX = xPos;
            lastCheckedY = yPos;
        }
        return bombTiles;
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
            explode();
        }
    }
}
