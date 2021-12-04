package RatGame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import java.util.ArrayList;

/**
 * This class allows for the creation of Gas items.
 * The gas item kills rats that are in the gas particles for too long.
 * @author Benas Montrimas.
 */
public class Gas extends Item {

    // Constants
    private static final int LIFE_DURATION = 10;
    private static final int SPREAD_RANGE = 5;
    private static final int SPREAD_INTERVAL = 1;
    private static final float GAS_DAMAGE = 1.0f;

    // Image Paths
    private static final String GAS_TEXTURE_PATH = "Assets/Gas.png";
    private static final String GAS_SPREAD_TEXTURE_PATH = "Assets/GasSpreadingTest.png";

    // Variables
    private boolean isSpreadingGas;
    private float timeTillSpread;
    private float lifeRemaining;
    private int currentRange;

    // Collections
    private final ArrayList<Pair<Integer, Integer>> gasSpreadPositions;
    private final ArrayList<ImageView> gasImageViews;

    /**
     * Constructor initializing all variables
     */
    public Gas() {
        texture = new Image(GAS_TEXTURE_PATH);
        lifeRemaining = LIFE_DURATION;
        type = ItemType.GAS;

        timeTillSpread = SPREAD_INTERVAL;
        currentRange = 0;
        gasSpreadPositions = new ArrayList<>();
        gasImageViews = new ArrayList<>();
    }

    public Gas(int x, int y, boolean expired, float timeTillSpread, float lifeRemaining,
               int currentRange, boolean isSpreadingGas, ArrayList<Pair<Integer, Integer>> gasSpreadPositions) {
        this.type = ItemType.GAS;
        this.texture = new Image(GAS_TEXTURE_PATH);

        this.xPos = x;
        this.yPos = y;
        this.expired = expired;
        this.timeTillSpread = timeTillSpread;
        this.lifeRemaining = lifeRemaining;
        this.currentRange = currentRange;
        this.isSpreadingGas = isSpreadingGas;
        this.gasSpreadPositions = gasSpreadPositions;

        this.gasImageViews = new ArrayList<>();
        for (Pair<Integer, Integer> currentGasPosition : gasSpreadPositions) {
            createGasSpreadImageView(currentGasPosition.getKey(), currentGasPosition.getValue());
        }
    }

    /**
     * Checks all valid tiles for the gas to spread.
     * @param levelGrid The level layout.
     */
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

    /**
     * Checks if gas from this item is already on a tile.
     * @param x The x position to check.
     * @param y The y position to check.
     * @return True if there is no gas on the tile, otherwise true.
     */
    private boolean gasNotOnTile(int x, int y) {
        for (Pair<Integer, Integer> positionsToCompare : gasSpreadPositions){
            if (positionsToCompare.equals(new Pair<>(x, y))){
                return false;
            }
        }
        return true;
    }

    /**
     * Creates gas and spreads to all available tiles.
     * @param levelGrid The level layout.
     */
    public void spreadGas(Tile[][] levelGrid){
        isSpreadingGas = false;
        currentRange++;
        if (gasSpreadPositions.isEmpty()){
            gasSpreadPositions.add(new Pair<>(xPos, yPos));
            createGasSpreadImageView(xPos, yPos);
        } else {
            checkTiles(levelGrid);
        }
    }

    /**
     * Create ImageViews for the gas particles.
     * @param xPos The x position of the gas.
     * @param yPos The y position of the gas.
     */
    private void createGasSpreadImageView(int xPos, int yPos) {
        ImageView gasSpreadImageView = new ImageView();
        Image gasSpreadImage = new Image(GAS_SPREAD_TEXTURE_PATH);
        gasSpreadImageView.setImage(gasSpreadImage);
        gasSpreadImageView.setTranslateX(xPos * Tile.TILE_WIDTH);
        gasSpreadImageView.setTranslateY(yPos * Tile.TILE_HEIGHT);
        gasImageViews.add(gasSpreadImageView);
    }

    /**
     * Called when item is used. Gas doesn't do anything on activation.
     */
    @Override
    public void use() {

    }

    /**
     * Handles what happens when a rat steps on this item. Gas does nothing when the item is stepped on.
     * @param rat The rat that has stepped on this item.
     */
    @Override
    public void steppedOn(Rat rat) {

    }

    /**
     * Checks if a rat is being gassed and if so, gasses it.
     * @param rats The collection of rats on the level.
     */
    public void checkIfRatsInGas(float deltaTime, ArrayList<Rat> rats) {
        for (Rat rat : rats) {
            setRatsBeingGassed(deltaTime, rat);
        }
    }

    /**
     * Gasses the rat being passed in.
     * @param rat The rat to gas.
     */
    private void setRatsBeingGassed(float deltaTime, Rat rat) {
        int ratX = (int) rat.getxPos();
        int ratY = (int) rat.getyPos();
        for (Pair<Integer, Integer> position : gasSpreadPositions) {
            int gasX = position.getKey();
            int gasY = position.getValue();
            if (gasX == ratX && gasY == ratY && !expired) {
                rat.poison(GAS_DAMAGE * deltaTime);
                return;
            }
        }
    }

    /**
     * Updates all aspects of the gas item every frame.
     * @param deltaTime The time since last frame.
     */
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

    /**
     * Returns the gas particles image views.
     * @return Collection of image views for each particle.
     */
    public ArrayList<ImageView> getGasImageViews() {
        return gasImageViews;
    }

    /**
     * Get the position of the gas spread particles.
     * @return Collection of all the positions of the gas particles.
     */
    public ArrayList<Pair<Integer, Integer>> getGasSpreadPositions() {
        return gasSpreadPositions;
    }

    /**
     * Get whether the gas is spreading.
     * @return True if gas should spread.
     */
    public boolean isSpreadingGas() {
        return isSpreadingGas;
    }

    /**
     * Set whether gas is spreading.
     * @param isSpreadingGas Whether the gas is spreading or not.
     */
    public void setIsSpreadingGas(boolean isSpreadingGas){
        this.isSpreadingGas = isSpreadingGas;
    }

    @Override
    public String toString() {
        StringBuilder positions = new StringBuilder();
        for (Pair<Integer, Integer> position : gasSpreadPositions) {
            positions.append(position.getKey()).append(" ").append(position.getValue()).append(" ");
        }
        // remove space at end
        positions.deleteCharAt(positions.length()-1);

        return "GAS " +
                xPos + " " +
                yPos + " " +
                expired + " " +
                timeTillSpread + " " +
                lifeRemaining + " " +
                currentRange + " " +
                isSpreadingGas + " " +
                positions;
    }
}
