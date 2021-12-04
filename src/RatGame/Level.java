package RatGame;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * This class is the level that the game is played on and controls all elements of the game whilst it runs.
 * @author Benas Montrimas.
 */
public class Level {
    // Constant Variables
    private static final int NUMBER_OF_ITEM_TYPES = 8;
    private static final float FPS_REFRESH_INTERVAL = 0.1f;
    private static final double NANO_TO_SECOND = 1e9;
    private static final String MAIN_MENU_PATH = "FXML/mainMenu.fxml";
    private static final String LEVEL_FOLDER_PATH = "src/Levels/";
    private static final String SAVE_FOLDER_PATH = "src/Saves/";
    private static final char GRASS_CHAR = 'G';
    private static final char PATH_CHAR = 'P';
    private static final char TUNNEL_CHAR = 'T';
    private static final char VERTICAL_TUNNEL_CHAR = 'V';
    private static final String FILE_DELIMITER = " ";
    private static final int INVENTORY_GRID_OFFSET = 1;
    private static final int ITEM_MAX_STACK = 4;
    private static final String TIME_LEFT_TEXT = "Time left: ";

    // Fps related variables
    private ArrayDeque<Float> pastDeltaTimes;
    private long lastFrameTime;
    private float timeSinceFPSRefreshed;
    private boolean isGameFinished;

    // Game Loop variables
    private boolean isPaused;
    private boolean firstLoop;

    // Level setup variables
    private boolean isSave;
    private AnimationTimer gameLoop;
    private String levelName;
    private int levelHeight;
    private int levelWidth;
    private Tile[][] levelGrid;
    private ArrayList<ImageView> tunnels;
    private ArrayList<Rat> rats;
    private ArrayList<ImageView> deadRats;
    private int numberOfRatsToLose;
    private int numberOfRatsToWin;
    private ArrayList<Stack<Item>> itemsInInventory;
    private ArrayList<Item> itemsInPlay;
    private float[] timeSinceItemSpawn;
    private int[] itemSpawnTime;
    private int score;
    private int expectedTime;
    private float totalTimeOnLevel;
    private int lastTimeStamp;

    // Mouse variables
    private double lastMouseX;
    private double lastMouseY;
    private ImageView itemBeingDragged;

    // All FXML variables.
    @FXML private AnchorPane gameScreen;
    @FXML private Canvas levelGridCanvas;
    @FXML private StackPane levelGridStackPane;
    @FXML private GridPane inventoryGrid;
    @FXML private StackPane mainStackPane;
    @FXML private StackPane gameOverScreen;
    @FXML private Rectangle gameOverBackground;
    @FXML private Text fpsCountText;
    @FXML private Text ratsAliveText;
    @FXML private Text maleRatsAliveText;
    @FXML private Text femaleRatsAliveText;
    @FXML private Text winLoseText;
    @FXML private Text scoreEndGameText;
    @FXML private StackPane pauseScreen;
    @FXML private Text timeText;
    @FXML private Text ratsToLoseText;
    @FXML private Text scoreText;

    /**
     * Called once the level is loaded and initialized the scene.
     */
    public void initialize() {
        fpsCountText.setVisible(MainMenu.isShowFPS());

        firstLoop = true;
        isGameFinished = false;

        // Main game loop code, that sets up the timer.
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (firstLoop) {
                    firstLoop = false;
                    lastFrameTime = now;
                    System.out.println(now);
                }

                float deltaTime = (float) ((now - lastFrameTime) / NANO_TO_SECOND);
                lastFrameTime = now;

                if (!isPaused) {
                    update(deltaTime);
                }

                updateFPSCount(deltaTime);

                gameOverBackground.setHeight(mainStackPane.getHeight());
                gameOverBackground.setWidth(mainStackPane.getWidth());
            }
        };
        gameLoop.start();
    }

    /**
     * Initiates the dragging of the item from inventory to the level, getting the item dragged and setting the position to the mouse.
     * @param event The mouse event initiating the drag.
     */
    public void onItemBeginDrag(MouseEvent event) {
        if (isPaused || itemBeingDragged != null) {
            return;
        }
        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();
        ImageView target = (ImageView) event.getTarget();
        itemBeingDragged = new ImageView();
        itemBeingDragged.setImage(target.getImage());
        mainStackPane.getChildren().add(itemBeingDragged);
        itemBeingDragged.setTranslateX(event.getSceneX() - itemBeingDragged.getImage().getWidth()/2);
        itemBeingDragged.setTranslateY(event.getSceneY() - itemBeingDragged.getImage().getHeight()/2);
    }

    /**
     * Sets the dragged items position to be at the position of the cursor.
     * @param event The mouse event triggering the drag.
     */
    public void onItemDrag(MouseEvent event) {
        if (itemBeingDragged != null) {
            itemBeingDragged.setTranslateX(itemBeingDragged.getTranslateX() + (event.getSceneX() - lastMouseX));
            itemBeingDragged.setTranslateY(itemBeingDragged.getTranslateY() + (event.getSceneY() - lastMouseY));
            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();
        }
    }

    /**
     * Checks where the item has been dropped and if it's on a path, then we want to use the item, otherwise return it to the player inventory.
     * @param event The mouse event releasing the drag.
     */
    public void onItemDragFinished(MouseEvent event) {
        // If the game is paused we do not want items to be usable.
        if (isPaused || itemBeingDragged == null) {
            mainStackPane.getChildren().remove(itemBeingDragged);
            itemBeingDragged = null;
            return;
        }

        double droppedAbsoluteXPos = (itemBeingDragged.getTranslateX() + itemBeingDragged.getImage().getWidth()/2);
        double droppedAbsoluteYPos = (itemBeingDragged.getTranslateY() + itemBeingDragged.getImage().getHeight()/2);
        double droppedGridXPos = ((droppedAbsoluteXPos + (0 - levelGridStackPane.getTranslateX()))/Tile.TILE_WIDTH);
        double droppedGridYPos = ((droppedAbsoluteYPos + (0 - levelGridStackPane.getTranslateY()))/Tile.TILE_HEIGHT);
        int gridX = (int) droppedGridXPos;
        int gridY = (int) droppedGridYPos;

        // this gets the type of the item being dropped
        ItemType itemType = null;
        for (ItemType type : ItemType.values()) {
            if (type.getTexture().equals(itemBeingDragged.getImage().getUrl().substring(
                    itemBeingDragged.getImage().getUrl().length() - type.getTexture().length()))){
                itemType = type;
            }
        }

        // Removes the item being dropped from the screen where it was being dragged.
        mainStackPane.getChildren().remove(itemBeingDragged);

        // If item isn't found in the inventory we return, we do not want to use an item they do not own.
        if (itemType == null) {
            itemBeingDragged = null;
            return;
        }

        // Checks if it's on a path, and whether its on the screen, stopping player dropping an item to a path they cannot see.
        if (droppedGridXPos < levelWidth && droppedGridXPos > 0 && droppedGridYPos < levelHeight && droppedGridYPos > 0 &&
                droppedAbsoluteXPos > 0 && droppedAbsoluteXPos < gameScreen.getWidth() &&
                droppedAbsoluteYPos > 0 && droppedAbsoluteYPos < gameScreen.getHeight() &&
                (levelGrid[(int)droppedGridXPos][(int)droppedGridYPos].getType() == TileType.Path &&
                !alreadyItemOnTile(gridX, gridY))) {
            Item itemUsed = itemsInInventory.get(itemType.getIndex()).pop();
            itemUsed.setXPos(gridX);
            itemUsed.setYPos(gridY);
            addItemToLevel(itemUsed);
            itemUsed.use();
            removeItem(itemType);
        }
        itemBeingDragged = null;
    }

    /**
     * Reloads the level when the play again button is pressed.
     * @param event The action that triggered the event.
     * @throws IOException If the FXML file can not be found will throw an exception.
     */
    public void playAgainPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/level.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(loader.load());

        Level controller = loader.getController();

        if (!isSave) {
            controller.createLevel(levelName, false);
        } else {
            String[] allLevels = new File(LEVEL_FOLDER_PATH).list();
            assert allLevels != null;
            for (String level : allLevels) {
                if (level.startsWith(levelName.substring(0, 1))){
                    controller.createLevel(level, false);
                }
            }
        }
    }

    /**
     * pauses the game loop.
     */
    public void pauseLoop() {
        isPaused = !isPaused;
    }

    /**
     * Pause the game if an escape key is pressed.
     * @param event The event which triggered this action.
     */
    public void pauseGameKey(KeyEvent event) {
        if (KeyCode.ESCAPE == event.getCode()) {
            pauseGameAction();
        }
    }

    /**
     * Pauses the game and bring up a pause screen.
     */
    public void pauseGameAction() {
        if (isGameFinished) {
            return;
        }
        pauseLoop();
        pauseScreen.setVisible(isPaused);
    }

    /**
     * Return from the level to the main menu.
     * @param event The action that triggered this.
     * @throws IOException If the FXML file does not exist will throw exception.
     */
    public void exitPressed(ActionEvent event) throws IOException {
        stopGameLoop();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(MAIN_MENU_PATH)));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    /**
     * Stores the mouse position when pressed down.
     * @param event The mouse click event is stored here.
     */
    public void getMousePosOnClicked(MouseEvent event) {
        lastMouseX = event.getX();
        lastMouseY = event.getY();
    }

    /**
     * moves the level around when dragging. Keeping in bounds of the screen.
     * @param event The mouse event is stored here.
     */
    public void onDragLevel(MouseEvent event) {
        levelGridStackPane.setTranslateX(levelGridStackPane.getTranslateX() + (event.getX() - lastMouseX));
        levelGridStackPane.setTranslateY(levelGridStackPane.getTranslateY() + (event.getY() - lastMouseY));
        clampToGameScreen();
    }

    /**
     * Sets up the level from the level file.
     * @param src The level file path that wants to be read.
     */
    public void createLevel(String src, boolean isSave) {
        this.isSave = isSave;
        rats = new ArrayList<>();
        deadRats = new ArrayList<>();
        itemsInPlay = new ArrayList<>();
        timeSinceItemSpawn = new float[NUMBER_OF_ITEM_TYPES];
        itemSpawnTime = new int[NUMBER_OF_ITEM_TYPES];
        pastDeltaTimes = new ArrayDeque<>();
        score = 0;

        if (isSave) {
            String[] allLevels = new File(LEVEL_FOLDER_PATH).list();

            assert allLevels != null;
            for (String level : allLevels) {
                if (level.charAt(0) == src.charAt(0)) {
                    levelName = level;
                }
            }
        } else {
            levelName = src;
        }

        createInventory();

        try {
            if (isSave) {
                loadLevelFile(SAVE_FOLDER_PATH + src);
            } else {
                loadLevelFile(LEVEL_FOLDER_PATH + src);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Can not find file: " + src);
            e.printStackTrace();
        }

        ratsToLoseText.setText("Rats to lose: " + numberOfRatsToLose);
        timeText.setText(TIME_LEFT_TEXT + expectedTime);

        drawTiles();
    }

    /**
     * Saves the current level state, with all the rats, items in inventory.
     */
    public void saveLevel() {
        try {
            File saveFile = createSaveFile();

            FileWriter fileWriter = new FileWriter(saveFile);

            saveExpectedTime(fileWriter);
            saveLoseConditions(fileWriter);
            saveItemSpawns(fileWriter);
            saveLevelGrid(fileWriter);
            saveRatSpawnGrid(fileWriter);
            saveItemsInPlay(fileWriter);

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prevents player from dragging the level off the screen.
     * If level is bigger than screen the level will always be on screen and cannot be dragged off-screen.
     * If level is smaller than the level can be dragged around on the screen freely but not off-screen.
     */
    public void clampToGameScreen() {
        if (levelGridStackPane.getWidth() > gameScreen.getWidth() && levelGridStackPane.getHeight() > gameScreen.getHeight()) {
            System.out.println("1");
            if (levelGridStackPane.getTranslateX() > 0) {
                levelGridStackPane.setTranslateX(0.0);
            } else if (levelGridStackPane.getTranslateX() < gameScreen.getWidth() - levelGridStackPane.getWidth()) {
                levelGridStackPane.setTranslateX(gameScreen.getWidth() - levelGridStackPane.getWidth());
            }
            if (levelGridStackPane.getTranslateY() > 0) {
                levelGridStackPane.setTranslateY(0.0);
            } else if (levelGridStackPane.getTranslateY() < gameScreen.getHeight() - levelGridStackPane.getHeight()) {
                levelGridStackPane.setTranslateY(gameScreen.getHeight() - levelGridStackPane.getHeight());
            }
        } else if (levelGridStackPane.getWidth() > gameScreen.getWidth()) {
            System.out.println("2");
            if (levelGridStackPane.getTranslateX() > 0) {
                levelGridStackPane.setTranslateX(0.0);
            } else if (levelGridStackPane.getTranslateX() < gameScreen.getWidth() - levelGridStackPane.getWidth()) {
                levelGridStackPane.setTranslateX(gameScreen.getWidth() - levelGridStackPane.getWidth());
            }
            if (levelGridStackPane.getTranslateY() < 0) {
                levelGridStackPane.setTranslateY(0.0);
            } else if (levelGridStackPane.getTranslateY() > gameScreen.getHeight() - levelGridStackPane.getHeight() ) {
                levelGridStackPane.setTranslateY(gameScreen.getHeight() - levelGridStackPane.getHeight());
            }
        } else if (levelGridStackPane.getHeight() > gameScreen.getHeight()) {
            System.out.println("3");
            if (levelGridStackPane.getTranslateX() < 0) {
                levelGridStackPane.setTranslateX(0.0);
            } else if (levelGridStackPane.getTranslateX() > gameScreen.getWidth() - levelGridStackPane.getWidth() ) {
                levelGridStackPane.setTranslateX(gameScreen.getWidth() - levelGridStackPane.getWidth());
            }
            if (levelGridStackPane.getTranslateY() > 0) {
                levelGridStackPane.setTranslateY(0.0);
            } else if (levelGridStackPane.getTranslateY() < gameScreen.getHeight() - levelGridStackPane.getHeight() ) {
                levelGridStackPane.setTranslateY(gameScreen.getHeight() - levelGridStackPane.getHeight());
            }
        } else {
            System.out.println("4");
            if (levelGridStackPane.getTranslateY() < 0) {
                levelGridStackPane.setTranslateY(0.0);
            } else if (levelGridStackPane.getTranslateY() + levelGridStackPane.getHeight() > gameScreen.getHeight()) {
                levelGridStackPane.setTranslateY(gameScreen.getHeight() - levelGridStackPane.getHeight());
            }
            if (levelGridStackPane.getTranslateX() < 0) {
                levelGridStackPane.setTranslateX(0.0);
            } else if (levelGridStackPane.getTranslateX() + levelGridStackPane.getWidth() > gameScreen.getWidth()) {
                levelGridStackPane.setTranslateX(gameScreen.getWidth() - levelGridStackPane.getWidth());
            }
        }
    }

    /**
     * Used to update aspect of the game every frame.
     * @param deltaTime The time in seconds since the last frame.
     */
    private void update(float deltaTime) {
        totalTimeOnLevel += deltaTime;
        drawTiles();
        updateRats(deltaTime);
        updateRatsAliveText();
        updateItems(deltaTime);
        updateItemsInPlay(deltaTime);
        checkIfItemSteppedOnByRat();
        checkWinLoseCondition();
        updateTimeText();
        updateScoreText();
        updateDeadRats(deltaTime);
    }

    private void updateScoreText() {
        scoreText.setText("Score: " + score);
    }

    /**
     * Check whether this tile already has an item on it.
     * @param gridX The grid x position to check.
     * @param gridY The grid y position to check.
     * @return True if the tile is occupied with an item, otherwise false;
     */
    private boolean alreadyItemOnTile(int gridX, int gridY) {
        for (Item item : itemsInPlay) {
            if (item.xPos == gridX && item.yPos == gridY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates all the dead rat icons and removes them when needed.
     * @param deltaTime The time since the last frame.
     */
    private void updateDeadRats(float deltaTime) {
        Iterator<ImageView> deadRatsIterator = deadRats.iterator();
        while (deadRatsIterator.hasNext()) {
            ImageView deadRat = deadRatsIterator.next();
            deadRat.setScaleX(deadRat.getScaleX() + deltaTime);
            deadRat.setScaleY(deadRat.getScaleY() + deltaTime);
            if (deadRat.getScaleX() > 1.0f) {
                deadRatsIterator.remove();
                levelGridStackPane.getChildren().removeIf(node -> node == deadRat);
            }
        }
    }

    /**
     * Updates the text which shows the expected time left on the screen.
     */
    private void updateTimeText() {
        if ((int) totalTimeOnLevel != lastTimeStamp) {
            timeText.setText(TIME_LEFT_TEXT + (expectedTime - (int) totalTimeOnLevel));
            lastTimeStamp = (int) totalTimeOnLevel;
        }
    }

    /**
     * Checks if any items need removing, and updates the item.
     * @param deltaTime The time in seconds since the last frame.
     */
    private void updateItemsInPlay(float deltaTime) {
        Iterator<Item> itemIterator = itemsInPlay.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (item.getType() == ItemType.DEATH_RAT) {
                DeathRat deathRat = (DeathRat) item;
                updateDeathRatItem(deathRat);
            } else if (item.getType() == ItemType.BOMB) {
                Bomb bombItem = (Bomb) item;
                updateBomb(bombItem);
            } else if (item.getType() == ItemType.GAS) {
                Gas gasItem = (Gas) item;
                updateGas(gasItem, deltaTime);
            } else if (item.getType() == ItemType.STERILISATION) {
                Sterilisation sterilisationItem = (Sterilisation) item;
                updateSterilization(sterilisationItem);
            }
            if (item.expired) {
                removeExpiredItemFromPlay(item);
                itemIterator.remove();
            }
        }
        for (Item item : itemsInPlay) {
            item.update(deltaTime);
        }

        // Update tunnel after every rat spawns so that tunnels are always on top.
        updateTunnels();
    }

    /**
     * Removes the expired items from play.
     * @param item The expired item to remove from play.
     */
    private void removeExpiredItemFromPlay(Item item) {

        // Gas and sterilisation need to have children removed as well.
        if (item.getType() == ItemType.GAS) {
            levelGridStackPane.getChildren().removeAll(((Gas) item).getGasImageViews());
        }

        if (item.getType() == ItemType.STERILISATION) {
            levelGridStackPane.getChildren().removeAll(((Sterilisation) item).getSterileTilesImages());
        }

        levelGridStackPane.getChildren().remove(item.imageView);
    }

    /**
     * Updates the sterilisation item, getting the tiles needed if not already got, and sterilises rats.
     * @param sterilisationItem The sterilisation item to update.
     */
    private void updateSterilization(Sterilisation sterilisationItem) {
        sterilisationItem.checkIfRatOnSterileTile(rats);
        if (!sterilisationItem.sterileTilesGot()) {
            sterilisationItem.addToSterilizedTiles(levelGrid);
            levelGridStackPane.getChildren().addAll(sterilisationItem.getSterileTilesImages());
        }
    }

    /**
     * Update the gas item, getting rats which are in the gas, and spreading the gas if gas is spreading.
     * @param gasItem The gas item to update.
     * @param deltaTime The time since the last frame.
     */
    private void updateGas(Gas gasItem, float deltaTime) {
        gasItem.checkIfRatsInGas(deltaTime, rats);
        if (gasItem.isSpreadingGas()) {
            gasItem.spreadGas(levelGrid);
            updateGasSpread(gasItem);
        }
    }

    /**
     * Update the death rat item to spawn the death rat when it is spawning.
     * @param deathRatItem The death rat item to update.
     */
    private void updateDeathRatItem(DeathRat deathRatItem) {
        if (deathRatItem.getSpawning()) {
            Rat deathRat = createRat(RatType.DEATH_RAT, deathRatItem.getXPos(), deathRatItem.getYPos(), false);
            rats.add(deathRat);
            deathRatItem.setSpawning(false);
        }
    }

    /**
     * Update the bomb item to explode it if the bomb is exploding and clearing all tiles in explosion.
     * @param bombItem The bomb item to update.
     */
    private void updateBomb(Bomb bombItem) {
        if (bombItem.isExploding()) {
            ArrayList<Pair<Integer, Integer>> tilesToClear = bombItem.getBombTiles(levelGrid);
            for (Pair<Integer, Integer> coordinate : tilesToClear) {
                Integer xPos = coordinate.getKey();
                Integer yPos = coordinate.getValue();
                for (Rat rat : rats) {
                    if ((int) rat.getXPos() == xPos && (int) rat.getYPos() == yPos) {
                        rat.die();
                    }
                }
                for (Item i : itemsInPlay) {
                    if (i.getXPos() == xPos && i.getYPos() == yPos) {
                        i.setExpired(true);
                    }
                }
            }
        }
    }

    /**
     * Creates the image to show when the rat dies, and spawns it.
     * @param rat The rat which has died.
     */
    private void createDeadRatImage(Rat rat) {
        ImageView deadRat = new ImageView();
        deadRat.setImage(new Image("Assets/deadrat.png"));
        deadRat.setTranslateX(rat.getXPos() * Tile.TILE_WIDTH);
        deadRat.setTranslateY(rat.getYPos() * Tile.TILE_HEIGHT);
        deadRat.setScaleX(0.5);
        deadRat.setScaleY(0.5);
        levelGridStackPane.getChildren().add(deadRat);
        deadRats.add(deadRat);
    }

    /**
     * Updates the gas spread images and displays them if not yet displayed.
     * @param item The gas item that the spread is being updated for.
     */
    private void updateGasSpread(Gas item){
        for (ImageView imageView : item.getGasImageViews()) {
            if (!levelGridStackPane.getChildren().contains(imageView)) {
                levelGridStackPane.getChildren().add(imageView);
                imageView.toFront();
            }
        }
    }

    /**
     * Updates the text for how many rats are alive.
     */
    private void updateRatsAliveText() {

        ratsAliveText.setText("Rats alive: " + getNumberOfRatsAlive());
        maleRatsAliveText.setText("Males alive: " + getNumberOfMaleRatsAlive());
        femaleRatsAliveText.setText("Females alive: " + getNumberOfFemaleRatsAlive());
    }

    /**
     * Counts the number of rats alive and returns it.
     * @return The number of rats alive.
     */
    private int getNumberOfRatsAlive() {
        int numberOfRatsAlive = 0;
        for (Rat rat : rats) {
            if (rat.getType() != RatType.DEATH_RAT) {
                numberOfRatsAlive++;
            }
        }
        return numberOfRatsAlive;
    }

    /**
     * counts the number of female rats on the level and returns it.
     * @return The number of female rats alive on the level.
     */
    private int getNumberOfFemaleRatsAlive() {
        int counter = 0;
        for (Rat rat : rats) {
            if (rat.getType() == RatType.FEMALE) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * counts the number of male rats on the level and returns it.
     * @return The number of male rats alive on the level.
     */
    private int getNumberOfMaleRatsAlive() {
        int counter = 0;
        for (Rat rat : rats) {
            if (rat.getType() == RatType.MALE) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Checks if the player has won or lost the game yet.
     */
    private void checkWinLoseCondition() {
        boolean hasWon = false;

        if (getNumberOfRatsAlive() > numberOfRatsToLose) {
            isPaused = true;
            isGameFinished = true;
            System.out.println("You Lose!");
        }

        if (getNumberOfRatsAlive() <= 0) {
            isPaused = true;
            isGameFinished = true;
            hasWon = true;
            System.out.println("You Win!");
        }

        if (isGameFinished) {
            deleteSave();
            if (hasWon) {
                gameWon();
            } else {
                winLoseText.setText("YOU LOSE!");
            }
            scoreEndGameText.setText("You scored: " + score);
            gameOverScreen.setVisible(true);
            gameOverScreen.setDisable(false);
        }
    }

    /**
     * Counts end game score, sets end game text and adds to the leaderboard.
     */
    private void gameWon() {
        winLoseText.setText("YOU WIN!");
        if (totalTimeOnLevel < expectedTime) {
            score += expectedTime - (int)totalTimeOnLevel;
        }
        if (MainMenu.getCurrentProfile() != null) {
            Leaderboard leaderboard = new Leaderboard(levelName);
            leaderboard.updateLeaderboard(MainMenu.getCurrentProfile().getName(), score);

            PlayerProfile player = MainMenu.getCurrentProfile();
            player.levelComplete(Integer.parseInt(levelName.substring(0, 1)), score);
        }
    }

    /**
     * Updates the rats position and removes dead rats from the level.
     * @param deltaTime The time since the last frame in seconds.
     */
    private void updateRats(float deltaTime) {
        // Using an iterator to allow removal of rats from the list as we loop.
        Iterator<Rat> ratIterator = rats.iterator();
        ArrayList<Rat> ratsToAdd = new ArrayList<>();
        while (ratIterator.hasNext()) {
            Rat rat = ratIterator.next();
            if (rat.getIsDead()) {
                createDeadRatImage(rat);
                score += rat.getScore();
                levelGridStackPane.getChildren().remove(rat.getImageView());
                ratIterator.remove();
            } else if (rat.getIsGivingBirth()) {
                Random rand = new Random();
                int num = rand.nextInt(2);
                RatType type;
                if (num == 0) {
                    type = RatType.MALE;
                } else {
                    type = RatType.FEMALE;
                }
                Rat babyRat = createRat(type, (int)rat.getXPos(), (int)rat.getYPos(),true);
                ratsToAdd.add(babyRat);
                rat.setIsGivingBirth();
            } else {
                rat.update(deltaTime, levelGrid);
                checkIfRatCollidesWithRat(rat);
            }
        }
        rats.addAll(ratsToAdd);


        // Update tunnel after every rat spawns so that tunnels are always on top.
        updateTunnels();
    }

    /**
     * Check if rat collides with any other rats.
     * @param rat The rat which is being checked for collision.
     */
    private void checkIfRatCollidesWithRat(Rat rat) {
        for (Rat otherRat : rats) {
            if (!rat.equals(otherRat)) {
                if (rat.getHitBox().beginCollide(otherRat.getHitBox())) {
                    rat.steppedOn(otherRat);
                }
            }
        }
    }

    /**
     * Reads in a file and parses the data for the level.
     * @param src A path to the level wanting to be read.
     */
    private void loadLevelFile(String src) throws FileNotFoundException {
        File levelFile = new File(src);
        Scanner fileReader = new Scanner(levelFile);

        loadTimeLimit(fileReader);
        loadLoseCondition(fileReader);
        loadItems(fileReader);
        loadLevelGrid(fileReader);
        loadRats(fileReader);
        loadItemsInPlay(fileReader);

        fileReader.close();
    }

    /**
     * reads in the expected time for the level and sets it for the level.
     * @param fileReader The scanner which contains the information for the expected time.
     */
    private void loadTimeLimit(Scanner fileReader) {
        String[] expectedTimeString = fileReader.nextLine().split(" ");
        expectedTime = Integer.parseInt(expectedTimeString[0]);
        totalTimeOnLevel = Float.parseFloat(expectedTimeString[1]);
    }

    /**
     * Reads in the win and lose conditions and sets them for the level.
     * @param fileReader The scanner which contains the information for the conditions.
     */
    private void loadLoseCondition(Scanner fileReader) {
        String winLoseCondition = fileReader.nextLine();
        String[] winLoseConditionSplit = winLoseCondition.split(" ");
        numberOfRatsToWin = Integer.parseInt(winLoseConditionSplit[0]);
        numberOfRatsToLose = Integer.parseInt(winLoseConditionSplit[1]);
    }

    /**
     * Initializes the game grid.
     * @param fileReader The file reader containing the information for the level grid.
     */
    private void loadLevelGrid(Scanner fileReader) {
        // Read level dimensions
        String levelDimensions = fileReader.nextLine();
        String[] levelDimensionsSplit = levelDimensions.split(" ");
        levelWidth = Integer.parseInt(levelDimensionsSplit[0]);
        levelHeight = Integer.parseInt(levelDimensionsSplit[1]);

        // Create the 2d array that holds tile positions.
        levelGridCanvas.setHeight(Tile.TILE_HEIGHT * levelHeight);
        levelGridCanvas.setWidth(Tile.TILE_WIDTH * levelWidth);

        levelGrid = new Tile[levelWidth][levelHeight];

        ArrayList<Image> grassImages = new ArrayList<>();
        grassImages.add(new Image("Assets/extras/blueflower.png"));
        grassImages.add(new Image("Assets/extras/cheese.png"));
        grassImages.add(new Image("Assets/extras/pinkflower.png"));
        grassImages.add(new Image("Assets/extras/trap.png"));
        grassImages.add(new Image("Assets/extras/trash.png"));

        for (int row = 0; row < levelHeight; row++) {
            String rowLayout = fileReader.nextLine();
            for (int col = 0; col < levelWidth; col++) {
                Tile tile = null;
                if (rowLayout.charAt(col) == GRASS_CHAR) {
                    tile = new Tile(row, col, TileType.Grass);
                } else if (rowLayout.charAt(col) == PATH_CHAR) {
                    tile = new Tile(row, col, TileType.Path);
                } else if (rowLayout.charAt(col) == TUNNEL_CHAR) {
                    tile = new Tile(row, col, TileType.Tunnel);
                } else if (rowLayout.charAt(col) == VERTICAL_TUNNEL_CHAR) {
                    tile = new Tile(row, col, TileType.VerticalTunnel);
                } else {
                    System.out.println("There seems to be a error in the level file!");
                }

                assert tile != null;

                if (tile.getType() == TileType.Grass) {
                    Random rand = new Random();
                    if (rand.nextInt(10) == 0) {
                        tile.setTexture(grassImages.get(rand.nextInt(grassImages.size())));
                    }
                }

                levelGrid[col][row] = tile;
            }
        }
        // Need to draw tunnels after everything to add them on top of the level.
        drawTunnels();
    }

    /**
     * Initialize items and item spawns.
     * @param fileReader The file reader containing the information for the items.
     */
    private void loadItems(Scanner fileReader) {

        String itemsToStartWith = fileReader.nextLine();
        String[] itemsToStartWithSplit = itemsToStartWith.split(FILE_DELIMITER);

        for (int i = 0; i < NUMBER_OF_ITEM_TYPES; i++) {
            for (int j = 0 ; j < Integer.parseInt(itemsToStartWithSplit[i]); j++) {
                spawnItem(ItemType.values()[i]);
            }
        }

        // Read The item spawn times
        String itemSpawnTimes = fileReader.nextLine();
        String[] itemSpawnTimesSplit = itemSpawnTimes.split(FILE_DELIMITER);

        for (int i = 0; i < NUMBER_OF_ITEM_TYPES; i++) {
            itemSpawnTime[i] = Integer.parseInt(itemSpawnTimesSplit[i]);
        }
    }

    /**
     * Initialize the rats and positions, spawning them into the level.
     * @param fileReader The file reader containing the information for the rats.
     */
    private void loadRats(Scanner fileReader) {
        while (fileReader.hasNextLine()){
            String ratToSpawn = fileReader.nextLine();
            if (ratToSpawn.equals("ITEMS")) {
                return;
            }
            if (ratToSpawn.equals("")) {
                return;
            }
            String[] ratToSpawnSplit = ratToSpawn.split(FILE_DELIMITER);

            RatType type;
            if (ratToSpawnSplit[0].equals("F")) {
                type = RatType.FEMALE;
            } else if (ratToSpawnSplit[0].equals("M")) {
                type = RatType.MALE;
            } else {
                type = RatType.FEMALE;
            }

            int xPos = Integer.parseInt(ratToSpawnSplit[1]);
            int yPos = Integer.parseInt(ratToSpawnSplit[2]);
            boolean isBaby = ratToSpawnSplit[3].equals("T");

            Rat newRat = createRat(type, xPos, yPos, isBaby);

            if (isSave) {
                newRat.setXVel(Integer.parseInt(ratToSpawnSplit[4]));
                newRat.setYVel(Integer.parseInt(ratToSpawnSplit[5]));
                if (Boolean.parseBoolean(ratToSpawnSplit[6])) {
                    newRat.setIsSterile();
                }
                newRat.setPregnant(Boolean.parseBoolean(ratToSpawnSplit[7]));
                newRat.setGivingBirth(Boolean.parseBoolean(ratToSpawnSplit[8]));
                newRat.setGrowUpTime(Float.parseFloat(ratToSpawnSplit[9]));
                newRat.setDead(Boolean.parseBoolean(ratToSpawnSplit[10]));
                newRat.setTotalTimePoisoned(Float.parseFloat(ratToSpawnSplit[11]));
                newRat.setSpawnNumber(Integer.parseInt(ratToSpawnSplit[12]));
                newRat.setSpawns(Integer.parseInt(ratToSpawnSplit[13]));
                newRat.setDeathRatKills(Integer.parseInt(ratToSpawnSplit[14]));
                newRat.setLastX(Integer.parseInt(ratToSpawnSplit[15]));
                newRat.setLastY(Integer.parseInt(ratToSpawnSplit[16]));
            }

            rats.add(newRat);
        }


        // Update tunnel after every rat spawns so that tunnels are always on top.
        updateTunnels();
    }

    /**
     * Setups the items on the board if this is loading from a save.
     * @param fileReader The scanner to read the save file from.
     */
    private void loadItemsInPlay(Scanner fileReader) {
        while (fileReader.hasNextLine()) {
            String[] itemToSpawn = fileReader.nextLine().split(FILE_DELIMITER);
            if (itemToSpawn.length == 0) {
                return;
            }

            Item item = getItemFromSave(itemToSpawn);

            if (item != null) {
                item.getImageView().setImage(item.getTexture());
                if (item instanceof Gas) {
                    updateGasSpread((Gas) item);
                }
                addItemToLevel(item);
            }
        }
    }

    /**
     * Create the item from the file save.
     * @param itemToSpawn The information about the item to spawn.
     * @return The item created.
     */
    public Item getItemFromSave(String[] itemToSpawn){
        String itemType = itemToSpawn[0];
        int x = Integer.parseInt(itemToSpawn[1]);
        int y = Integer.parseInt(itemToSpawn[2]);
        boolean expired = Boolean.getBoolean(itemToSpawn[3]);

        Item item = null;

        switch (itemType) {
            case "BMB":
                float timeSincePlaced = Float.parseFloat(itemToSpawn[4]);
                boolean exploding = Boolean.getBoolean(itemToSpawn[5]);
                item = new Bomb(x, y, expired, timeSincePlaced, exploding);
                break;
            case "DTH":
                float timeLeft = Float.parseFloat(itemToSpawn[4]);
                boolean spawning = Boolean.getBoolean(itemToSpawn[5]);
                item = new DeathRat(x, y, expired, timeLeft, spawning);
                break;
            case "FSX":
                item = new FemaleSexChange(x, y, expired);
                break;
            case "MSX":
                item = new MaleSexChange(x, y, expired);
                break;
            case "GAS":
                float timeTillSpread = Float.parseFloat(itemToSpawn[4]);
                float lifeRemaining = Float.parseFloat(itemToSpawn[5]);
                int currentRange = Integer.parseInt(itemToSpawn[6]);
                boolean spreading = Boolean.getBoolean(itemToSpawn[7]);

                ArrayList<Pair<Integer, Integer>> gasPositions = new ArrayList<>();
                for (int i = 8 ; i < itemToSpawn.length ; i += 2) {
                    int gasX = Integer.parseInt(itemToSpawn[i]);
                    int gasY = Integer.parseInt(itemToSpawn[i + 1]);
                    gasPositions.add(new Pair<>(gasX, gasY));
                }

                item = new Gas(x, y, expired, timeTillSpread, lifeRemaining, currentRange, spreading, gasPositions);
                break;
            case "STP":
                int hp = Integer.parseInt(itemToSpawn[4]);
                item = new NoEntrySign(x, y, expired, hp);
                break;
            case "PSN":
                item = new Poison(x, y, expired);
                break;
            case "STR":
                float sterileTimeSincePlaced = Float.parseFloat(itemToSpawn[4]);
                boolean sterileTilesGot = Boolean.getBoolean(itemToSpawn[5]);

                ArrayList<Pair<Integer, Integer>> sterilePositions = new ArrayList<>();
                for (int i = 6 ; i < itemToSpawn.length ; i += 2) {
                    int sterileX = Integer.parseInt(itemToSpawn[i]);
                    int sterileY = Integer.parseInt(itemToSpawn[i + 1]);
                    sterilePositions.add(new Pair<>(sterileX, sterileY));
                }

                item = new Sterilisation(x, y, expired, sterileTimeSincePlaced, sterileTilesGot, sterilePositions);
                break;
            default:
                System.out.println("Error in level save file");
                break;
        }
        return item;
    }

    /**
     * Spawns a rat onto the level.
     * @param type The type of rat, either male, female, or death, to be spawned.
     * @param xPos The X position of the rat to be spawned.
     * @param yPos The Y position of the rat to be spawned.
     */
    private Rat createRat(RatType type, int xPos, int yPos, boolean isBaby) {
        Rat rat = new Rat(type, xPos, yPos, isBaby);
        levelGridStackPane.getChildren().add(rat.getImageView());
        rat.getImageView().toBack();
        levelGridCanvas.toBack();
        return rat;
    }

    /**
     * Draws the tiles onto the level.
     */
    private void drawTiles() {
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                GraphicsContext levelGraphicsContext = levelGridCanvas.getGraphicsContext2D();
                Image texture = levelGrid[col][row].getTexture();
                levelGraphicsContext.drawImage(texture, col * Tile.TILE_HEIGHT, row * Tile.TILE_WIDTH);
            }
        }
    }

    /**
     * Draws the tunnels onto the level, separate from other tiles as need to be on top of the rats.
     */
    private void drawTunnels() {
        tunnels = new ArrayList<>();
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                if (levelGrid[col][row].getType() == TileType.Tunnel || levelGrid[col][row].getType() == TileType.VerticalTunnel) {
                    ImageView tunnel =  new ImageView();
                    tunnel.setImage(levelGrid[col][row].getTexture());
                    tunnel.setTranslateX(col * Tile.TILE_WIDTH);
                    tunnel.setTranslateY(row * Tile.TILE_HEIGHT);
                    levelGridStackPane.getChildren().add(tunnel);
                    tunnels.add(tunnel);
                }
            }
        }
    }

    /**
     * Sets all the tunnels in the level to the front of the level so that they appear above rats.
     */
    private void updateTunnels() {
        for (ImageView tunnel : tunnels) {
            tunnel.toFront();
        }
    }

    /**
     * Creates the save file to store the level save in.
     * @throws IOException If the file cannot be created will throw an error.
     */
    private File createSaveFile() throws IOException {
        if (new File(SAVE_FOLDER_PATH).mkdirs()){
            System.out.println("Created save folder at: " + SAVE_FOLDER_PATH);
        }

        File levelSaveFile = new File(SAVE_FOLDER_PATH + levelName.charAt(0) + MainMenu.getCurrentProfile().getName());

        if (levelSaveFile.delete()){
            System.out.println("Deleted previous save file: " + levelSaveFile.getName());
        }

        if (levelSaveFile.createNewFile()){
            System.out.println("New save file created: " + levelName);
        }
        return levelSaveFile;
    }

    /**
     * Creates the inventory that holds all the items for the player.
     */
    private void createInventory() {
        itemsInInventory = new ArrayList<>(NUMBER_OF_ITEM_TYPES);
        for (int i = 0; i < NUMBER_OF_ITEM_TYPES; i++) {
            Stack<Item> itemStack = new Stack<>();
            itemsInInventory.add(itemStack);
        }
    }

    /**
     * Deletes this levels save file.
     */
    private void deleteSave() {
        String[] allSaves = new File("src/Saves/").list();
        if (allSaves == null) {
            return;
        }
        for (String level : allSaves) {
            if (level.equals(getSaveFileName())) {
                File oldSave = new File(SAVE_FOLDER_PATH + getSaveFileName());
                if (oldSave.delete()) {
                    System.out.println("Old save file deleted.");
                }
            }
        }
    }

    /**
     * Gets the format for the save file name.
     * @return The name for the save file.
     */
    private String getSaveFileName() {
        return levelName.charAt(0) + MainMenu.getCurrentProfile().getName();
    }

    /**
     * Writes the expected time for the level the save file.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if you cannot write to the file.
     */
    private void saveExpectedTime(FileWriter fileWriter) throws IOException {
        fileWriter.write(expectedTime + " " + totalTimeOnLevel + "\n");
    }

    /**
     * Writes the win and lose conditions to the save file.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if you cannot write to the file.
     */
    private void saveLoseConditions(FileWriter fileWriter) throws IOException {
        fileWriter.write(numberOfRatsToWin + " ");
        fileWriter.write(numberOfRatsToLose + "\n");
    }

    /**
     * Writes the item spawn times to the level save.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if you cannot write to the file.
     */
    private void saveItemSpawns(FileWriter fileWriter) throws IOException {
        StringBuilder itemsHeld = new StringBuilder();
        for (ItemType type : ItemType.values()) {
            itemsHeld.append(itemsInInventory.get(type.getIndex()).size()).append(FILE_DELIMITER);
        }
        fileWriter.write(itemsHeld + "\n");

        StringBuilder itemsSpawns = new StringBuilder();
        for (ItemType type : ItemType.values()) {
            itemsSpawns.append(itemSpawnTime[type.getIndex()]).append(FILE_DELIMITER);
        }
        fileWriter.write(itemsSpawns + "\n");

    }

    /**
     * Writes the level grid to the save file.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if you cannot write to the file.
     */
    private void saveLevelGrid(FileWriter fileWriter) throws IOException {
        fileWriter.write(levelWidth + FILE_DELIMITER + levelHeight + "\n");
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                fileWriter.write(levelGrid[col][row].toString());
            }
            fileWriter.write("\n");
        }
    }

    /**
     * writes each rat to the save file, containing all information necessary.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if the file cannot be written to.
     */
    private void saveRatSpawnGrid(FileWriter fileWriter) throws IOException {
        for (Rat rat : rats) {
            fileWriter.write(rat.toString() + "\n");
        }
        fileWriter.write("ITEMS\n");
    }

    /**
     * Saves all the items in play to the level save.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if the file cannot be written to.
     */
    private void saveItemsInPlay(FileWriter fileWriter) throws IOException {
        for (Item item : itemsInPlay) {
            fileWriter.write(item.toString());
            fileWriter.write("\n");
        }
    }

    /**
     * updates all the item timers which check whether an item is to spawn.
     * @param deltaTime The time since the last frame in seconds.
     */
    private void updateItems(float deltaTime) {
        for (ItemType type : ItemType.values()) {
            timeSinceItemSpawn[type.getIndex()] += deltaTime;
            if (timeSinceItemSpawn[type.getIndex()] > itemSpawnTime[type.getIndex()]) {
                spawnItem(type);
                timeSinceItemSpawn[type.getIndex()] = 0;
            }
        }
    }

    /**
     * Removes an item from the inventory of the player.
     * @param type The type of item that we want to remove.
     */
    private void removeItem(ItemType type) {
        inventoryGrid.getChildren().removeIf(node -> node.getClass() == ImageView.class &&
                ((ImageView) node).getImage().getUrl().endsWith(type.getTexture()));
        for (int i = 0; i < itemsInInventory.get(type.getIndex()).size(); i++) {
            Item item = itemsInInventory.get(type.getIndex()).get(i);
            ImageView itemIcon = createImageIcon(item);
            inventoryGrid.add(itemIcon, i, type.getIndex() + INVENTORY_GRID_OFFSET);
        }
    }

    /**
     * Create the icon for the items in the inventory.
     * @param item the item that we want to create an icon for.
     * @return Returns an ImageView for the item containing the items texture.
     */
    private ImageView createImageIcon(Item item) {
        ImageView itemIcon = new ImageView();
        itemIcon.setImage(item.getTexture());
        inventoryGrid.setAlignment(Pos.CENTER);
        GridPane.setHalignment(itemIcon, HPos.CENTER);
        GridPane.setValignment(itemIcon, VPos.BOTTOM);
        itemIcon.setOnMousePressed(this::onItemBeginDrag);
        itemIcon.setOnMouseDragged(this::onItemDrag);
        itemIcon.setOnMouseReleased(this::onItemDragFinished);
        return itemIcon;
    }

    /**
     * Spawn an item into the inventory of the player, only if the player doesn't already have maximum items of that type.
     * @param type The type of item that we want to spawn into the inventory.
     */
    private void spawnItem(ItemType type) {
        if (itemsInInventory.get(type.getIndex()).size() >= ITEM_MAX_STACK) {
            return;
        }

        Item item = Item.create(type);

        item.setTexture(new Image(type.getTexture()));
        ImageView itemIcon = createImageIcon(item);
        inventoryGrid.add(itemIcon, itemsInInventory.get(type.getIndex()).size(), type.getIndex() + INVENTORY_GRID_OFFSET);
        itemsInInventory.get(type.getIndex()).add(item);
    }

    private void addItemToLevel(Item item){
        item.createHitBox();
        item.getImageView().setTranslateX(item.getXPos() * Tile.TILE_WIDTH);
        item.getImageView().setTranslateY(item.getYPos() * Tile.TILE_HEIGHT);
        levelGridStackPane.getChildren().add(item.getImageView());
        itemsInPlay.add(item);
    }

    /**
     * Stops the game loop from running, stopping the level.
     */
    private void stopGameLoop() {
        gameLoop.stop();
    }

    /**
     * Calculates the fps of the game and displays it in game for the player to see.
     * @param deltaTime Represents the time since the last frame in seconds.
     */
    private void updateFPSCount(float deltaTime) {
        timeSinceFPSRefreshed += deltaTime;
        pastDeltaTimes.addLast(deltaTime);
        // Only update fps at set intervals not every frame
        if (timeSinceFPSRefreshed > FPS_REFRESH_INTERVAL) {
            float sum = 0;
            for (Float num : pastDeltaTimes){
                sum += num;
            }
            fpsCountText.setText("FPS: " + (int) (1/ (sum/ pastDeltaTimes.size())));

            pastDeltaTimes.clear();
            timeSinceFPSRefreshed = 0;
        }
    }

    /**
     * Collision detection for rats and all items on the level. Triggers the item when a rat steps on it.
     */
    private void checkIfItemSteppedOnByRat() {
        for (Rat rat : rats) {
            for (Item item : itemsInPlay) {
                if (rat.getHitBox().beginCollide(item.getHitBox())) {
                    item.steppedOn(rat);
                }
            }
        }
    }
}