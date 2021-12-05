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

    // Constants
    private static final int TIMER = 4;
    private static final Image BOMB_AT_4 = new Image("Assets/Bombcountdown/Bomb4.png");
    private static final Image BOMB_AT_3 = new Image("Assets/Bombcountdown/Bomb3.png");
    private static final Image BOMB_AT_2 = new Image("Assets/Bombcountdown/Bomb2.png");
    private static final Image BOMB_AT_1 = new Image("Assets/Bombcountdown/Bomb1.png");
    private static final Image BOMB_TEXTURE = new Image("Assets/Bomb.png");

    // Variables
    private float timeSincePlaced;
    private boolean isExploding;

    // Collections
    ArrayList<Pair<Integer, Integer>> bombTiles;

    /**
     * Creates an instance of an item of type bomb
     */
    public Bomb() {
        type = ItemType.BOMB;
        bombTiles = new ArrayList<>();
        texture = BOMB_TEXTURE;
        isExploding = false;
    }

    /**
     * Constructor only used for loading the item state from a save file.
     * @param x The x position of the bomb.
     * @param y The y position of the bomb.
     * @param expired If the bomb is expired or not.
     * @param timeSincePlaced The time since the bomb has been placed.
     * @param isExploding If the bomb is exploding.
     */
    public Bomb(int x, int y, boolean expired, float timeSincePlaced, boolean isExploding) {
        this.type = ItemType.BOMB;
        this.texture = BOMB_TEXTURE;
        this.bombTiles = new ArrayList<>();

        this.xPos = x;
        this.yPos = y;
        this.expired = expired;
        this.timeSincePlaced = timeSincePlaced;
        this.isExploding = isExploding;
    }

    /**
     * @param levelGrid the level layout
     * @return the list of tiles that can be affected by the bomb
     */
    public ArrayList<Pair<Integer, Integer>> getBombTiles(Tile[][] levelGrid) {
        isExploding = false;

        int lastCheckedX = xPos;
        int lastCheckedY;

        while (inBombRange(levelGrid,lastCheckedX,yPos)) {
            bombTiles.add(new Pair<>(lastCheckedX,yPos));
            lastCheckedX--;
        }
        lastCheckedY = yPos;
        while (inBombRange(levelGrid,xPos,lastCheckedY)) {
            bombTiles.add(new Pair<>(xPos,lastCheckedY));
            lastCheckedY++;
        }
        lastCheckedX = xPos;
        while (inBombRange(levelGrid,lastCheckedX,yPos)) {
            bombTiles.add(new Pair<>(lastCheckedX,yPos));
            lastCheckedX++;
        }
        lastCheckedY = yPos;
        while (inBombRange(levelGrid,xPos,lastCheckedY)) {
            bombTiles.add(new Pair<>(xPos,lastCheckedY));
            lastCheckedY--;
        }
        return bombTiles;
    }

    /**
     * method which returns if the bomb is exploding or not.
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
     * method inherited from the parent class that defines what happens when a particular rat steps on it
     * Not used by the bomb
     * @param rat The rat that has stepped on the item.
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
     * To string which returns the string used for saving this item.
     * @return The string required for saving this item.
     */
    @Override
    public String toString() {
        return "BMB " +
                xPos + " " +
                yPos + " " +
                expired + " " +
                timeSincePlaced + " " +
                isExploding;
    }

    /**
     * method which determines if a position is in the bomb's range
     * @param levelGrid the level layout
     * @param xPos x-position to check
     * @param yPos y-position to check
     * @return True if the tile at the position is in the bomb range and is valid.
     */
    private boolean inBombRange(Tile[][] levelGrid, int xPos, int yPos) {
        return levelGrid[xPos][yPos].getType().isTraversable;
    }

    /**
     * works with the delta time in update method to give a visual countdown of the bomb
     * @param timeSincePlaced The time since the bomb has been placed.
     */
    private void bombTimerImageView(float timeSincePlaced) {
        if (timeSincePlaced > TIMER - 4 && timeSincePlaced <= TIMER - 3) {
            getImageView().setImage(BOMB_AT_4);
        }
        else if (timeSincePlaced > TIMER - 3 && timeSincePlaced <= TIMER - 2) {
            getImageView().setImage(BOMB_AT_3);
        }
        else if (timeSincePlaced > TIMER - 2 && timeSincePlaced <= TIMER - 1) {
            getImageView().setImage(BOMB_AT_2);
        }
        else if (timeSincePlaced > TIMER - 1 && timeSincePlaced <= TIMER) {
            getImageView().setImage(BOMB_AT_1);
        }
    }
}
