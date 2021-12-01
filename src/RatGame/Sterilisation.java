package RatGame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import java.util.ArrayList;

/**
 * This class allows for the creation of Sterilisation items.
 * The sterilisation item makes rats within a small radius sterile.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */
public class Sterilisation extends Item {

    private static final int LIFE_DURATION = 4;
    private static final int SPREAD_RADIUS = 2;

    private float timeSincePlaced;
    private ArrayList<Pair<Integer, Integer>> sterilizedTiles;
    private boolean sterileTilesGot;
    private ArrayList<ImageView> sterileTilesImages;

    /**
     *
     */
    public Sterilisation() {
        texture = new Image("Assets/Sterilisation.png");
        type = ItemType.STERILISATION;
        sterilizedTiles = new ArrayList<>();
        sterileTilesGot = false;
        sterileTilesImages = new ArrayList<>();
    }

    /**
     *
     * @param levelGrid
     * @return
     */
    public ArrayList<Pair<Integer, Integer>> getSterilizedTiles(Tile[][] levelGrid) {

      for (int i = -SPREAD_RADIUS; i <= SPREAD_RADIUS; i++) {
            for (int j = -SPREAD_RADIUS; j <= SPREAD_RADIUS; j++) {
                int x = this.xPos + i;
                int y = this.yPos + j;
                if ((x >= 0 && x < levelGrid.length) && (y >= 0 && y < levelGrid.length)) {
                    sterilizedTiles.add(new Pair<>(x,y));
                    sterileTilesImageViews(x,y);
                }
            }
        }
        sterileTilesGot = true;
        return sterilizedTiles;
    }

    /**
     *
     * @return
     */
    public boolean sterileTilesGot() {
        return sterileTilesGot;
    }

    /**
     *
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
        int ratX = (int) rat.getxPos();
        int ratY = (int) rat.getyPos();

        for (int i = 0; i < sterilizedTiles.size(); i++) {
            int sterileX = sterilizedTiles.get(i).getKey();
            int sterileY = sterilizedTiles.get(i).getValue();

            if (sterileX == ratX && sterileY == ratY) {
                rat.setIsSterile();
            }
        }
    }

    /**
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        timeSincePlaced += deltaTime;
        if (timeSincePlaced >= LIFE_DURATION) {
            expired = true;
        }
    }

    public ArrayList<ImageView> getSterileTilesImages() {
        return sterileTilesImages;
    }

    private void sterileTilesImageViews(int xPos, int yPos) {
        ImageView sterileTileSpreadImg = new ImageView();
        sterileTileSpreadImg.setImage(new Image("Assets/Sterelisationspread.png"));
        sterileTileSpreadImg.setTranslateX(xPos * Tile.TILE_WIDTH);
        sterileTileSpreadImg.setTranslateY(yPos * Tile.TILE_HEIGHT);
        sterileTilesImages.add(sterileTileSpreadImg);
    }
}
