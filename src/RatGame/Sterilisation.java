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
        texture = new Image("Assets/Sterilisation.png");
        type = ItemType.STERILISATION;
        sterilizedTiles = new ArrayList<>();
        sterileTilesGot = false;
        sterileTilesImages = new ArrayList<>();
    }

    /**
     * method which returns the list of sterilized tiles and sets check for tiles to true
     * @param levelGrid
     * @return sterilized tiles
     */
    public ArrayList<Pair<Integer, Integer>> getSterilizedTiles(Tile[][] levelGrid) {

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
        return sterilizedTiles;
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
     * Not used by the bomb
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
            int ratX = (int) rat.getxPos();
            int ratY = (int) rat.getyPos();

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
        sterileTileSpreadImg.setImage(new Image("Assets/Sterelisationspread.png"));
        sterileTileSpreadImg.setTranslateX(xPos * Tile.TILE_WIDTH);
        sterileTileSpreadImg.setTranslateY(yPos * Tile.TILE_HEIGHT);
        sterileTilesImages.add(sterileTileSpreadImg);
    }
}
