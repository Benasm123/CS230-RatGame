package RatGame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import java.util.ArrayList;

/**
 * This class allows for the creation of Sterilisation items.
 * The sterilisation item makes rats within a small radius sterile.
 *
 * @author Ephraim Okonji, Benas Montrimas
 * @version 1.0
 *
 */
public class Sterilisation extends Item {

    // Constants
    private static final int LIFE_DURATION = 4;
    private static final int SPREAD_RADIUS = 1;
    private static final Image STERILE_TEXTURE_PATH = new Image("Assets/Sterilisation.png");
    private static final Image STERILE_SPREAD_TEXTURE_PATH = new Image("Assets/Sterelisationspread.png");

    // Variables
    private float timeSincePlaced;
    private boolean sterileTilesGot;

    // Collections
    private ArrayList<ImageView> sterileTilesImages;
    private ArrayList<Pair<Integer, Integer>> sterilizedTiles;


    /**
     * Creates an instance of a Sterilisation item.
     */
    public Sterilisation() {
        texture = STERILE_TEXTURE_PATH;
        type = ItemType.STERILISATION;
        sterilizedTiles = new ArrayList<>();
        sterileTilesGot = false;
        sterileTilesImages = new ArrayList<>();
    }

    public Sterilisation(int x, int y, boolean expired, float timeSincePlaced, boolean sterileTilesGot, ArrayList<Pair<Integer, Integer>> sterilizedTiles) {
        this.type = ItemType.STERILISATION;
        this.texture = STERILE_TEXTURE_PATH;

        this.xPos = x;
        this.yPos = y;
        this.expired = expired;
        this.timeSincePlaced = timeSincePlaced;
        this.sterileTilesGot = sterileTilesGot;
        this.sterilizedTiles = sterilizedTiles;

        sterileTilesImages = new ArrayList<>();
        for (Pair<Integer, Integer> position : this.sterilizedTiles) {
            sterileTilesImageViews(position.getKey(), position.getValue());
        }
    }

    /**
     * method which adds to the list of tiles that are sterilized and sets the check for sterile tiles to true
     * @param levelGrid
     */
    public void addToSterilizedTiles(Tile[][] levelGrid) {

      for (int i = -SPREAD_RADIUS; i <= SPREAD_RADIUS; i++) {
            for (int j = -SPREAD_RADIUS; j <= SPREAD_RADIUS; j++) {
                int x = this.xPos + i;
                int y = this.yPos + j;
                if ((x >= 0 && x < levelGrid.length) && (y >= 0 && y < levelGrid[0].length)) {
                    sterilizedTiles.add(new Pair<>(x,y));
                    sterileTilesImageViews(x,y);
                }
            }
        }
        sterileTilesGot = true;
    }

    /**
     * @return whether the sterilized tiles have been gotten or not
     */
    public boolean sterileTilesGot() {
        return sterileTilesGot;
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
     * Not used by this item
     * @param rat
     */
    @Override
    public void steppedOn(Rat rat) {

    }

    /**
     * method which checks if a rat has been on a sterile tile
     * @param rats
     */
    public void checkIfRatOnSterileTile(ArrayList<Rat> rats) {
        for (Rat rat : rats) {
            int ratX = (int) rat.getXPos();
            int ratY = (int) rat.getYPos();

            for (Pair<Integer, Integer> sterilizedTile : sterilizedTiles) {
                int sterileX = sterilizedTile.getKey();
                int sterileY = sterilizedTile.getValue();

                if (sterileX == ratX && sterileY == ratY) {
                    rat.setIsSterile();
                }
            }
        }
    }

    /**
     * method which updates the item since last time frame in seconds
     * @param deltaTime The time since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        timeSincePlaced += deltaTime;
        if (timeSincePlaced >= LIFE_DURATION) {
            expired = true;
        }
    }

    /**
     *
     * @return the list of tiles to spread image to
     */
    public ArrayList<ImageView> getSterileTilesImages() {
        return sterileTilesImages;
    }

    /**
     * method which spreads the sterilisation image to all tiles in radius
     * @param xPos
     * @param yPos
     */
    private void sterileTilesImageViews(int xPos, int yPos) {
        ImageView sterileTileSpreadImg = new ImageView();
        sterileTileSpreadImg.setImage(STERILE_SPREAD_TEXTURE_PATH);
        sterileTileSpreadImg.setTranslateX(xPos * Tile.TILE_WIDTH);
        sterileTileSpreadImg.setTranslateY(yPos * Tile.TILE_HEIGHT);
        sterileTilesImages.add(sterileTileSpreadImg);
    }

    /**
     * To string which returns the string used for saving this item.
     * @return The string required for saving this item.
     */
    @Override
    public String toString() {
        StringBuilder positions = new StringBuilder();
        for (Pair<Integer, Integer> position : sterilizedTiles) {
            positions.append(position.getKey()).append(" ").append(position.getValue()).append(" ");
        }
        positions.deleteCharAt(positions.length()-1);

        return "STR " +
                xPos + " " +
                yPos + " " +
                expired + " " +
                timeSincePlaced + " " +
                sterileTilesGot + " " +
                positions;
    }
}