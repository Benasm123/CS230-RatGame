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
    private boolean isSterileTilesGot;

    /**
     *
     */
    public Sterilisation() {
        texture = new Image("Assets/Sterilisation.png");
        type = ItemType.STERILISATION;
        sterilizedTiles = new ArrayList<>();
        isSterileTilesGot = false;
    }

    /**
     *
     * @param levelGrid
     * @return
     */
    public ArrayList<Pair<Integer, Integer>> getSterilizedTiles(Tile[][] levelGrid) {
      /*  int startX = xPos + SPREAD_RADIUS;
        int startY = yPos + SPREAD_RADIUS;
        boolean check = startX != xPos && startY != yPos;

        while (inSterileRadius(levelGrid,startX,startY) && check) {
            sterilizedTiles.add(new Pair<>(startX,startY));
            startX--;
            startY--;
        }
        startX = xPos - SPREAD_RADIUS;
        startY = yPos - SPREAD_RADIUS;

        while (inSterileRadius(levelGrid,startX,startY) && check) {
            sterilizedTiles.add(new Pair<>(startX, startY));
            startX++;
            startY++;
        }*/
        for (int i = -SPREAD_RADIUS; i < levelGrid.length; i++) {
            for (int j = -SPREAD_RADIUS; i < levelGrid.length; i++) {
                if ((i >= 0 || i < levelGrid.length) || (j >= 0 || j < levelGrid.length)) {
                    int x = this.xPos + i;
                    int y = this.yPos + i;
                    while (inSterileRadius(levelGrid, x, y)) {
                        sterilizedTiles.add(new Pair<>(x,y));
                    }
                }
            }
        }
        isSterileTilesGot = true;
        return sterilizedTiles;
    }

    /**
     *
     * @return
     */
    public boolean isSterileTilesGot() {
        return isSterileTilesGot;
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
                //for testing
                System.out.println("Rat is STERILE");
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

    /**
     *
     * @param levelGrid
     * @param xPos
     * @param yPos
     * @return
     */
    private boolean inSterileRadius(Tile[][] levelGrid, int xPos, int yPos) {
        return levelGrid[xPos][yPos].getType().isTraversable;
    }
}
