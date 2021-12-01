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
    private static final int TIMER = 4;

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
        bombTimerImageView(timeSincePlaced);
        if (timeSincePlaced >= TIMER) {
            isExploding = true;
            expired = true;
        }
    }

    /**
     * method which determines if a position is in the bomb's range
     * @param levelGrid
     * @param xPos
     * @param yPos
     * @return
     */
    private boolean canExplodeHere(Tile[][] levelGrid, int xPos, int yPos) {
        return levelGrid[xPos][yPos].getType().isTraversable;
    }

    /**
     * works with the delta time in update method to give a visual countdown of the bomb
     * @param timeSincePlaced
     */
    private void bombTimerImageView(float timeSincePlaced) {
        Image bombAt4 = new Image("Assets/Bombcountdown/Bomb4.png");
        Image bombAt3 = new Image("Assets/Bombcountdown/Bomb3.png");
        Image bombAt2 = new Image("Assets/Bombcountdown/Bomb2.png");
        Image bombAt1 = new Image("Assets/Bombcountdown/Bomb1.png");

        if (timeSincePlaced > TIMER - 4 && timeSincePlaced <= TIMER - 3) {
            getImageView().setImage(bombAt4);
        }
        else if (timeSincePlaced > TIMER - 3 && timeSincePlaced <= TIMER - 2) {
            getImageView().setImage(bombAt3);
        }
        else if (timeSincePlaced > TIMER - 2 && timeSincePlaced <= TIMER - 1) {
            getImageView().setImage(bombAt2);
        }
        else if (timeSincePlaced > TIMER - 1 && timeSincePlaced <= TIMER) {
            getImageView().setImage(bombAt1);
        }
    }
}
