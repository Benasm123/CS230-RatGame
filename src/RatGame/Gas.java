/**
 * This class allows for the creation of Gas items.
 * The gas item kills rats that are in the gas particles for too long.
 *
 * @author CS-230 Group13 (21/22)
 * @version 1.0
 *
 */

package RatGame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;

// Space after Item and before bracket open
public class Gas extends Item{

    // Static final needs to be ALL CAPS and at the top of the file (for speed)
    private static final int LIFE_DURATION = 10;
    private static final int SPREAD_RANGE = 5;
    private static final int SPREAD_INTERVAL = 1;

    private boolean isSpreadingGas;
    private float timeTillSpread;
    private float lifeRemaining;
    private int currentRange;

    private ArrayList<Pair<Integer, Integer>> gasSpreadPositions;
    private ArrayList<ImageView> gasImageViews;

    public Gas() {
        texture = new Image("Assets/Gas.png");
        lifeRemaining = LIFE_DURATION;
        type = ItemType.GAS;

        timeTillSpread = SPREAD_INTERVAL;
        currentRange = 0;
        gasSpreadPositions = new ArrayList<>();
        gasImageViews = new ArrayList<>();
    }

    public void checkTiles(Tile[][] levelGrid){
        ArrayList<Pair<Integer, Integer>> toAddToSpreadPositions = new ArrayList<>();
        for (Pair<Integer, Integer> position : gasSpreadPositions){
            int x = position.getKey();
            int y = position.getValue();
            // Check left, then up, then right, then down
            if (levelGrid[x - 1][y].getType().isTraversable &&
                    gasNotOnTile(x - 1, y)) {
                toAddToSpreadPositions.add(new Pair<>(x - 1, y));
                createGasSpreadImageView(x - 1, y);
            }
            if (levelGrid[x][y + 1].getType().isTraversable &&
                    gasNotOnTile(x, y + 1)) {
                toAddToSpreadPositions.add(new Pair<>(x, y + 1));
                createGasSpreadImageView(x, y + 1);
            }
            if (levelGrid[x + 1][y].getType().isTraversable &&
                    gasNotOnTile(x + 1, y)) {
                toAddToSpreadPositions.add(new Pair<>(x + 1, y));
                createGasSpreadImageView(x + 1, y);
            }
            if (levelGrid[x][y - 1].getType().isTraversable &&
                    gasNotOnTile(x, y - 1)) {
                toAddToSpreadPositions.add(new Pair<>(x, y - 1));
                createGasSpreadImageView(x, y - 1);
            }
        }
        gasSpreadPositions.addAll(toAddToSpreadPositions);
    }

    private boolean gasNotOnTile(int x, int y) {
        for (Pair<Integer, Integer> positionsToCompare : gasSpreadPositions){
            if (positionsToCompare.equals(new Pair<>(x, y))){
                return false;
            }
        }
        return true;
    }

    public void spreadGas(Tile[][] levelGrid){
        currentRange++;
        if (gasSpreadPositions.isEmpty()){
            gasSpreadPositions.add(new Pair<>(xPos, yPos));
            createGasSpreadImageView(xPos, yPos);
        } else {
            checkTiles(levelGrid);
        }
    }

    private void createGasSpreadImageView(int xPos, int yPos) {
        ImageView gasSpreadImageView = new ImageView();
        Image gasSpreadImage = new Image("Assets/GasSpreadingTest.png");
        gasSpreadImageView.setImage(gasSpreadImage);
        gasSpreadImageView.setTranslateX(xPos * Tile.TILE_WIDTH);
        gasSpreadImageView.setTranslateY(yPos * Tile.TILE_HEIGHT);
        gasImageViews.add(gasSpreadImageView);
    }

    @Override
    public void use() {

    }

    @Override
    public void steppedOn(Rat rat) {

    }

    public void checkIfRatsInGas(ArrayList<Rat> rats) {
        for (Rat rat : rats) {
            int ratX = (int) rat.getxPos();
            int ratY = (int) rat.getyPos();
            for (Pair<Integer, Integer> position : gasSpreadPositions) {
                int gasX = position.getKey();
                int gasY = position.getValue();
                if (gasX == ratX && gasY == ratY) {
//                rat.isPoisoned = true;
                } else {
//                rat.isPoisoned = false;
                }
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        lifeRemaining -= deltaTime;
        timeTillSpread -= deltaTime;

        if (timeTillSpread <= 0 && currentRange < SPREAD_RANGE){
            isSpreadingGas = true;
            timeTillSpread = SPREAD_INTERVAL;
        }

        if (lifeRemaining <= 0) {
            expired = true;
        }
    }

    public ArrayList<ImageView> getGasImageViews() {
        return gasImageViews;
    }

    public ArrayList<Pair<Integer, Integer>> getGasSpreadPositions() {
        return gasSpreadPositions;
    }

    public boolean isSpreadingGas() {
        return isSpreadingGas;
    }

    public void setIsSpreadingGas(boolean isSpreadingGas){
        this.isSpreadingGas = isSpreadingGas;
    }
}
