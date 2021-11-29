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
    public void explode() {
        Tile[][] levelGrid;
        int placedX;
        int placedY;

        /*final boolean canExplodeRight = levelGrid[placedX + 1][placedY].getType().isTraversable;
        final boolean canExplodeLeft = levelGrid[placedX - 1][placedY].getType().isTraversable;
        final boolean canExplodeUp = levelGrid[placedX][placedY + 1].getType().isTraversable;
        final boolean canExplodeDown = levelGrid[placedX][placedY - 1].getType().isTraversable;
        final boolean isExploding = canExplodeDown || canExplodeLeft || canExplodeRight || canExplodeUp;
        while (isExploding) {

        }*/
        expired = true;
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
