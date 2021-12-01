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

// TODO: Add Game timer and Expected time to the save and load.

/**
 * This class is the level that the game is played on and controls all elements of the game whilst it runs.
 * @author Benas Montrimas.
 */
public class Level {
    // Constant Variables
    // TODO: can make these public and allow people to use these for tile size.
    private static final int NUMBER_OF_ITEMS = 8;

    private static final float FPS_REFRESH_INTERVAL = 0.1f;

    private static final double NANO_TO_SECOND = 1e9;

    private static final String SAVE_FOLDER_PATH = "src/Saves/";
    private static final String MAIN_MENU_PATH = "FXML/mainMenu.fxml";
    private static final String LEVEL_FOLDER_PATH = "src/Levels/";
    private static final char GRASS_CHAR = 'G';
    private static final char PATH_CHAR = 'P';
    private static final char TUNNEL_CHAR = 'T';
    private static final char VERTICAL_TUNNEL_CHAR = 'V';
    private static final String FILE_DELIMITER = " ";
    private static final int INVENTORY_GRID_OFFSET = 1;
    private static final int ITEM_MAX_STACK = 4;

    // Fps related variables
    private ArrayDeque<Float> pastDeltaTimes;
    private long lastFrameTime;
    private float timeSinceFPSRefreshed;

    // Game Loop variables
    private boolean isPaused;
    private boolean firstLoop;

    // Level setup variables
    private AnimationTimer gameLoop;

    private String levelName;
    private int levelHeight;
    private int levelWidth;

    private GraphicsContext levelGraphicsContext;

    private Tile[][] levelGrid;
    private ArrayList<ImageView> tunnels;

    private ArrayList<Rat> rats;

    private int numberOfRatsToLose;
    private int numberOfRatsToWin;

    private ArrayList<Stack<Item>> itemsInInventory;
    private ArrayList<Item> itemsInPlay;

    private float[] timeSinceItemSpawn;

    private int[] itemSpawnTime;

    private int score;

    private int expectedTime;

    private float totalTimeOnLevel;

    // Mouse variables
    private double lastMouseX;
    private double lastMouseY;

    private ImageView itemBeingDragged;

    // All FXML variables.
    // TODO: Refactor all these to have lowercase first letters.
    @FXML
    private AnchorPane GameScreen;

    @FXML
    private Canvas GameBoard;

    @FXML
    private StackPane levelPane;

    @FXML
    private Text fpsCount;

    @FXML
    private Text ratsAlive;

    @FXML
    private Text maleRatsAlive;

    @FXML
    private Text femaleRatsAlive;

    @FXML
    private GridPane inventoryGrid;

    @FXML
    private StackPane levelGameScreen;

    @FXML
    private StackPane GameOverScreen;

    @FXML
    private Rectangle GameOverBackground;

    @FXML
    private Text WinLoseText;

    @FXML
    private Text ScoreText;

    // TODO: Reorder all methods in order: Public protected, private
    /**
     * Called once the level is loaded and initialized the scene.
     */
    public void initialize(){
        firstLoop = true;

        levelGraphicsContext = GameBoard.getGraphicsContext2D();

        // Main game loop code, that sets up the timer.
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                GameOverBackground.setHeight(levelGameScreen.getHeight());
                GameOverBackground.setWidth(levelGameScreen.getWidth());

                if (firstLoop){
                    firstLoop = false;
                    lastFrameTime = now;
                    System.out.println(now);
                }

                float deltaTime = (float) ((now - lastFrameTime) / NANO_TO_SECOND);
                lastFrameTime = now;

                if (!isPaused){
                    update(deltaTime);
                }

                updateFPSCount(deltaTime);
            }
        };
        gameLoop.start();
    }

    /**
     * Calculates the fps of the game and displays it in game for the player to see.
     * @param deltaTime Represents the time since the last frame in seconds.
     */
    public void updateFPSCount(float deltaTime){
        timeSinceFPSRefreshed += deltaTime;
        pastDeltaTimes.addLast(deltaTime);
        // Only update fps at set intervals not every frame
        if (timeSinceFPSRefreshed > FPS_REFRESH_INTERVAL){
            float sum = 0;
            for (Float num : pastDeltaTimes){
                sum += num;
            }
            fpsCount.setText("FPS: " + (int) (1/ (sum/ pastDeltaTimes.size())));

            pastDeltaTimes.clear();
            timeSinceFPSRefreshed = 0;
        }
    }

    /**
     * Stops the game loop from running, stopping the level.
     */
    private void stopGameLoop(){
        gameLoop.stop();
    }

    /**
     * Used to update aspect of the game every frame.
     * @param deltaTime The time in seconds since the last frame.
     */
    public void update(float deltaTime){
        totalTimeOnLevel += deltaTime;

        drawTiles();
        updateRats(deltaTime);
        updateRatsAliveText();
        updateItems(deltaTime);
        updateItemsInPlay(deltaTime);
        checkSteppedOn();
        checkWinLoseCondition();
    }

    /**
     * Collision detection for rats and all items on the level. Triggers the item when a rat steps on it.
     */
    private void checkSteppedOn(){
        for (Rat rat : rats){
            for (Item item : itemsInPlay){
                if (rat.getxPos() == item.getXPos() && rat.getyPos() == item.getYPos()){
                    item.steppedOn(rat);
                }
            }
        }
    }

    /**
     * Checks if any items need removing, and updates the item.
     * @param deltaTime The time in seconds since the last frame.
     */
    private void updateItemsInPlay(float deltaTime) {
        Iterator<Item> itemIterator = itemsInPlay.iterator();
        while (itemIterator.hasNext()){
            Item item = itemIterator.next();
            if (item.getType() == ItemType.DEATH_RAT) {
                if (((DeathRat) item).getSpawning()){
                    Rat deathRat = createRat(Rat.ratType.DEATHRAT, item.getXPos(), item.getYPos(), false);
                    rats.add(deathRat);
                    ((DeathRat) item).setSpawning(false);
                }
            } else if (item.getType() == ItemType.BOMB) {
                if (((Bomb) item).isExploding()){
                    ArrayList<Pair<Integer, Integer>> tilesToClear = ((Bomb) item).getBombTiles(levelGrid);
                    for (Pair<Integer, Integer> coordinate : tilesToClear){
                        Integer xPos = coordinate.getKey();
                        Integer yPos = coordinate.getValue();
                        for (Rat rat : rats){
                            if ((int)rat.getxPos() == xPos && (int)rat.getyPos() == yPos){
                                rat.die();
                            }
                        }
                        for (Item i : itemsInPlay){
                            if (i.getXPos() == xPos && i.getYPos() == yPos){
                                i.setExpired(true);
                            }
                        }
                    }
                }
            } else if (item.getType() == ItemType.GAS) {
                Gas gasItem = (Gas) item;
                gasItem.checkIfRatsInGas(rats);
                if (gasItem.isSpreadingGas()){
                    gasItem.spreadGas(levelGrid);
                    for (ImageView imageView : gasItem.getGasImageViews()){
                        if (!levelPane.getChildren().contains(imageView)){
                            levelPane.getChildren().add(imageView);
                            imageView.toFront();
                        }
                    }
                }
            } else if (item.getType() == ItemType.STERILISATION) {
                Sterilisation sterilisationItem = (Sterilisation) item;
                sterilisationItem.checkIfRatOnSterileTile(rats);
                if (!sterilisationItem.sterileTilesGot()) {
                    sterilisationItem.getSterilizedTiles(levelGrid);
                    levelPane.getChildren().addAll(sterilisationItem.getSterileTilesImages());
                }
            }
            if (item.expired){
                if (item.getType() == ItemType.GAS){
                    levelPane.getChildren().removeAll(((Gas) item).getGasImageViews());
                }

                if (item.getType() == ItemType.STERILISATION){
                    levelPane.getChildren().removeAll(((Sterilisation) item).getSterileTilesImages());
                }

                levelPane.getChildren().remove(item.imageView);
                itemIterator.remove();
            }
        }
        for (Item item : itemsInPlay){
            item.update(deltaTime);
        }

        // Update tunnel after every rat spawns so that tunnels are always on top.
        updateTunnels();
    }

    /**
     * Updates the text for how many rats are alive.
     */
    private void updateRatsAliveText() {

        ratsAlive.setText("Rats alive: " + getNumberOfRatsAlive());
        maleRatsAlive.setText("Males alive: " + getNumberOfMaleRatsAlive());
        femaleRatsAlive.setText("Females alive: " + getNumberOfFemaleRatsAlive());
    }

    private int getNumberOfRatsAlive(){
        int numberOfRatsAlive = 0;
        for (Rat rat : rats){
            if (rat.type != Rat.ratType.DEATHRAT){
                numberOfRatsAlive++;
            }
        }
        return numberOfRatsAlive;
    }

    /**
     * counts the number of female rats on the level and returns it.
     * @return The number of female rats alive on the level.
     */
    private int getNumberOfFemaleRatsAlive(){
        int counter = 0;
        for (Rat rat : rats){
            if (rat.type == Rat.ratType.FEMALE){
                counter++;
            }
        }
        return counter;
    }

    /**
     * counts the number of male rats on the level and returns it.
     * @return The number of male rats alive on the level.
     */
    private int getNumberOfMaleRatsAlive(){
        int counter = 0;
        for (Rat rat : rats){
            if (rat.type == Rat.ratType.MALE){
                counter++;
            }
        }
        return counter;
    }

    /**
     * Checks if the player has won or lost the game yet.
     */
    private void checkWinLoseCondition() {
        boolean isGameFinished = false;
        boolean hasWon = false;

        if (getNumberOfRatsAlive() > numberOfRatsToLose){
            isPaused = true;
            isGameFinished = true;
            System.out.println("You Lose!");
        }

        if (getNumberOfRatsAlive() <= 0){
            isPaused = true;
            isGameFinished = true;
            hasWon = true;
            System.out.println("You Win!");
        }

        // Spec says you win when all rats are dead. So removing this.
//        if (getNumberOfFemaleRatsAlive() == 0){
//            isPaused = true;
//            isGameFinished = true;
//            hasWon = true;
//            System.out.println("You win only males alive and cannot reproduce!");
//        }
//
//        if (getNumberOfMaleRatsAlive() == 0){
//            isPaused = true;
//            isGameFinished = true;
//            hasWon = true;
//            System.out.println("You win only female alive and cannot reproduce!");
//        }

        if (isGameFinished){
            if (hasWon){
                System.out.println(MainMenu.getCurrentProfile() == null);
                WinLoseText.setText("YOU WIN!");
                if (totalTimeOnLevel < expectedTime){
                    score += expectedTime - (int)totalTimeOnLevel;
                }
                if (MainMenu.getCurrentProfile() != null){
                    PlayerProfile player = MainMenu.getCurrentProfile();
                    player.levelComplete(Integer.parseInt(levelName.substring(0, 1)), score);
                }
            } else {
                WinLoseText.setText("YOU LOSE!");
            }
            ScoreText.setText("You scored: " + score);
            GameOverScreen.setVisible(true);
            GameOverScreen.setDisable(false);
        }
    }

    /**
     * Updates the rats position and removes dead rats from the level.
     * @param deltaTime The time since the last frame in seconds.
     */
    private void updateRats(float deltaTime){
        // Using an iterator to allow removal of rats from the list as we loop.
        Iterator<Rat> ratIterator = rats.iterator();
        ArrayList<Rat> ratsToAdd = new ArrayList<>();
        while (ratIterator.hasNext()){
            Rat rat = ratIterator.next();
            if (rat.getIsDead()){
                score += rat.getScore();
                levelPane.getChildren().remove(rat.img);
                ratIterator.remove();
            } else if (rat.getIsGivingBirth()){
                Random rand = new Random();
                int num = rand.nextInt(2);
                Rat.ratType type;
                if (num == 0){
                    type = Rat.ratType.MALE;
                } else {
                    type = Rat.ratType.FEMALE;
                }
                Rat babyRat = createRat(type, (int)rat.getxPos(), (int)rat.getyPos(),true);
                ratsToAdd.add(babyRat);
                rat.setIsGivingBirth();
            } else {
                rat.update(deltaTime, levelGrid);
                for (Rat otherRat : rats) {
                    if (rat.getxPos() == otherRat.getxPos() &&
                            rat.getyPos() == otherRat.getyPos() &&
                            rat != otherRat) {
                        rat.steppedOn(otherRat);
                    }
                }
            }
        }
        rats.addAll(ratsToAdd);


        // Update tunnel after every rat spawns so that tunnels are always on top.
        updateTunnels();
    }

    /**
     * pauses the game loop.
     */
    public void pauseLoop(){
        isPaused = !isPaused;
    }

    // TODO: Add popup to ask whether user wants to save the level if leaving early.
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
    public void getMousePosOnClicked(MouseEvent event){
        lastMouseX = event.getX();
        lastMouseY = event.getY();
    }

    /**
     * moves the level around when dragging. Keeping in bounds of the screen.
     * @param event The mouse event is stored here.
     */
    public void onDragLevel(MouseEvent event){
        levelPane.setTranslateX(levelPane.getTranslateX() + (event.getX() - lastMouseX));
        levelPane.setTranslateY(levelPane.getTranslateY() + (event.getY() - lastMouseY));
        clampToGameScreen();
    }

    /**
     * Prevents player from dragging the level off the screen.
     * If level is bigger than screen the level will always be on screen and cannot be dragged off-screen.
     * If level is smaller than the level can be dragged around on the screen freely but not off-screen.
     */
    public void clampToGameScreen(){
        if (levelPane.getWidth() > GameScreen.getWidth() && levelPane.getHeight() > GameScreen.getHeight()) {
            if (levelPane.getTranslateX() > 0) {
                levelPane.setTranslateX(0.0);
            } else if (levelPane.getTranslateX() < GameScreen.getWidth() - levelPane.getWidth()) {
                levelPane.setTranslateX(GameScreen.getWidth() - levelPane.getWidth());
            }
            if (levelPane.getTranslateY() > 0) {
                levelPane.setTranslateY(0.0);
            } else if (levelPane.getTranslateY() < GameScreen.getHeight() - levelPane.getHeight()) {
                levelPane.setTranslateY(GameScreen.getHeight() - levelPane.getHeight());
            }
        } else if (levelPane.getWidth() > GameScreen.getWidth()){
            if (levelPane.getTranslateX() > 0){
                levelPane.setTranslateX(0.0);
            } else if (levelPane.getTranslateX() < GameScreen.getWidth() - levelPane.getWidth() ){
                levelPane.setTranslateX(GameScreen.getWidth() - levelPane.getWidth());
            }
            if (levelPane.getTranslateY() < 0){
                levelPane.setTranslateY(0.0);
            } else if (levelPane.getTranslateY() > GameScreen.getHeight() - levelPane.getHeight() ){
                levelPane.setTranslateY(GameScreen.getHeight() - levelPane.getHeight());
            }
        } else if (levelPane.getHeight() > GameScreen.getHeight()){
            if (levelPane.getTranslateX() < 0){
                levelPane.setTranslateX(0.0);
            } else if (levelPane.getTranslateX() > GameScreen.getWidth() - levelPane.getWidth() ){
                levelPane.setTranslateX(GameScreen.getWidth() - levelPane.getWidth());
            }
            if (levelPane.getTranslateY() > 0){
                levelPane.setTranslateY(0.0);
            } else if (levelPane.getTranslateY() < GameScreen.getHeight() - levelPane.getHeight() ){
                levelPane.setTranslateY(GameScreen.getHeight() - levelPane.getHeight());
            }
        } else {
            if (levelPane.getTranslateY() < 0){
                levelPane.setTranslateY(0.0);
            } else if (levelPane.getTranslateY() + levelPane.getHeight() > GameScreen.getHeight()){
                levelPane.setTranslateY(GameScreen.getHeight() - levelPane.getHeight());
            }
            if (levelPane.getTranslateX() < 0) {
                levelPane.setTranslateX(0.0);
            } else if (levelPane.getTranslateX() + levelPane.getWidth() > GameScreen.getWidth()){
                levelPane.setTranslateX(GameScreen.getWidth() - levelPane.getWidth());
            }
        }
    }

    /**
     * Sets up the level from the level file.
     * @param src The level file path that wants to be read.
     */
    public void createLevel(String src){
        rats = new ArrayList<>();
        itemsInPlay = new ArrayList<>();
        timeSinceItemSpawn = new float[NUMBER_OF_ITEMS];
        itemSpawnTime = new int[NUMBER_OF_ITEMS];
        pastDeltaTimes = new ArrayDeque<>();
        score = 0;

        try {
            readLevelFile(src);
        } catch (FileNotFoundException e) {
            System.out.println("Can not find file: " + src);
            e.printStackTrace();
            return;
        }
        drawTiles();
        createInventory();
    }

    /**
     * Reads in a file and parses the data for the level.
     * @param src A path to the level wanting to be read.
     */
    public void readLevelFile(String src) throws FileNotFoundException {
        levelName = src;
        File levelFile = new File(LEVEL_FOLDER_PATH + src);
        Scanner fileReader = new Scanner(levelFile);

        setupTimeLimit(fileReader);
        setupWinLoseCondition(fileReader);
        setupItems(fileReader);
        setupLevelGrid(fileReader);
        setupRatSpawns(fileReader);
    }

    /**
     * reads in the expected time for the level and sets it for the level.
     * @param fileReader The scanner which contains the information for the expected time.
     */
    private void setupTimeLimit(Scanner fileReader){
        String expectedTimeString = fileReader.nextLine();
        expectedTime = Integer.parseInt(expectedTimeString);
    }

    /**
     * Reads in the win and lose conditions and sets them for the level.
     * @param fileReader The scanner which contains the information for the conditions.
     */
    private void setupWinLoseCondition(Scanner fileReader){
        String winLoseCondition = fileReader.nextLine();
        String[] winLoseConditionSplit = winLoseCondition.split(" ");
        numberOfRatsToWin = Integer.parseInt(winLoseConditionSplit[0]);
        numberOfRatsToLose = Integer.parseInt(winLoseConditionSplit[1]);
    }

    /**
     * Initializes the game grid.
     * @param fileReader The file reader containing the information for the level grid.
     */
    private void setupLevelGrid(Scanner fileReader){
        // Read level dimensions
        String levelDimensions = fileReader.nextLine();
        String[] levelDimensionsSplit = levelDimensions.split(" ");
        levelWidth = Integer.parseInt(levelDimensionsSplit[0]);
        levelHeight = Integer.parseInt(levelDimensionsSplit[1]);

        // Create the 2d array that holds tile positions.
        GameBoard.setHeight(Tile.TILE_HEIGHT * levelHeight);
        GameBoard.setWidth(Tile.TILE_WIDTH * levelWidth);

        levelGrid = new Tile[levelWidth][levelHeight];
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
    private void setupItems(Scanner fileReader){

        String itemsToStartWith = fileReader.nextLine();
        String[] itemsToStartWithSplit = itemsToStartWith.split(FILE_DELIMITER);

        for (int i = 0; i < NUMBER_OF_ITEMS; i++){
            for (int j = 0 ; j < Integer.parseInt(itemsToStartWithSplit[i]); j++) {
                spawnItem(ItemType.values()[i]);
            }
        }

        // Read The item spawn times
        String itemSpawnTimes = fileReader.nextLine();
        String[] itemSpawnTimesSplit = itemSpawnTimes.split(FILE_DELIMITER);

        for (int i = 0; i < NUMBER_OF_ITEMS; i++){
            itemSpawnTime[i] = Integer.parseInt(itemSpawnTimesSplit[i]);
        }
    }

    /**
     * Initialize the rats and positions, spawning them into the level.
     * @param fileReader The file reader containing the information for the rats.
     */
    private void setupRatSpawns(Scanner fileReader) {
        while (fileReader.hasNextLine()){
            String ratToSpawn = fileReader.nextLine();
            if (ratToSpawn.equals("ITEMS")) {
                return;
            }
            if (ratToSpawn.equals("")){
                return;
            }
            String[] ratToSpawnSplit = ratToSpawn.split(FILE_DELIMITER);

            Rat.ratType type;
            if (ratToSpawnSplit[0].equals("F")){
                type = Rat.ratType.FEMALE;
            } else if (ratToSpawnSplit[0].equals("M")){
                type = Rat.ratType.MALE;
            } else {
                type = Rat.ratType.FEMALE;
            }

            int xPos = Integer.parseInt(ratToSpawnSplit[1]);
            int yPos = Integer.parseInt(ratToSpawnSplit[2]);
            boolean isBaby = ratToSpawnSplit[3].equals("T");

            Rat newRat = createRat(type, xPos, yPos, isBaby);
            rats.add(newRat);
        }


        // Update tunnel after every rat spawns so that tunnels are always on top.
        updateTunnels();
    }

    /**
     * Setups the items on the board if this is loading from a save.
     * @param fileReader The scanner to read the save file from.
     */
    private void setupItemsOnBoard(Scanner fileReader){
        while (fileReader.hasNextLine()){
            String itemToSpawn = fileReader.nextLine();
            if (itemToSpawn.equals("")){
                return;
            }
//            String[] itemToSpawnSplit = itemToSpawn.split(FILE_DELIMITER);

            // TODO: find out how to save items, probably something like {TYPE X Y} would suffice.
        }
    }

    /**
     * Spawns a rat onto the level.
     * @param type The type of rat, either male, female, or death, to be spawned.
     * @param xPos The X position of the rat to be spawned.
     * @param yPos The Y position of the rat to be spawned.
     */
    private Rat createRat(Rat.ratType type, int xPos, int yPos, boolean isBaby){
        Rat rat = new Rat(type, xPos, yPos, isBaby);
        levelPane.getChildren().add(rat.img);
        rat.img.toBack();
        GameBoard.toBack();
        return rat;
    }

    /**
     * Draws the tiles onto the level.
     */
    private void drawTiles(){
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                levelGraphicsContext.drawImage(levelGrid[col][row].getTexture(), col * Tile.TILE_HEIGHT, row * Tile.TILE_WIDTH);
            }
        }
    }

    /**
     * Draws the tunnels onto the level, separate from other tiles as need to be on top of the rats.
     */
    private void drawTunnels(){
        tunnels = new ArrayList<>();
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                if (levelGrid[col][row].getType() == TileType.Tunnel || levelGrid[col][row].getType() == TileType.VerticalTunnel) {
                    ImageView tunnel =  new ImageView();
                    tunnel.setImage(levelGrid[col][row].getTexture());
                    tunnel.setTranslateX(col * Tile.TILE_WIDTH);
                    tunnel.setTranslateY(row * Tile.TILE_HEIGHT);
                    levelPane.getChildren().add(tunnel);
                    tunnels.add(tunnel);
                }
            }
        }
    }

    /**
     * Sets all the tunnels in the level to the front of the level so that they appear above rats.
     */
    private void updateTunnels(){
        for (ImageView tunnel : tunnels){
            tunnel.toFront();
        }
    }

    /**
     * Creates the inventory that holds all the items for the player.
     */
    public void createInventory(){
        itemsInInventory = new ArrayList<>(NUMBER_OF_ITEMS);
        for (int i = 0; i < NUMBER_OF_ITEMS; i++){
            Stack<Item> itemStack = new Stack<>();
            itemsInInventory.add(itemStack);
        }
    }

    // TODO Once we have a player class store all saves for each player in the player folder instead.
    // TODO If a level save is already active for a player we need to only let the player resume.

    // TODO Add a way to save and load items on the board.
    /**
     * Saves the current level state, with all the rats, items in inventory.
     */
    public void saveLevel(){
        try {
            createSaveFile();

            FileWriter fileWriter = new FileWriter(SAVE_FOLDER_PATH + levelName);

            writeExpectedTime(fileWriter);
            writeWinLoseConditions(fileWriter);
            writeItemSpawns(fileWriter);
            writeLevelGrid(fileWriter);
            writeRatSpawnGrid(fileWriter);

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the save file to store the level save in.
     * @throws IOException If the file cannot be created will throw an error.
     */
    private void createSaveFile() throws IOException {
        if (new File(SAVE_FOLDER_PATH).mkdirs()){
            System.out.println("Created save folder at: " + SAVE_FOLDER_PATH);
        }

        // TODO: Can possibly add a popup to check if user wants to delete the save or not.
        File levelSaveFile = new File(SAVE_FOLDER_PATH + levelName);
        if (levelSaveFile.delete()){
            System.out.println("Deleted previous save file: " + levelName);
        }

        if (levelSaveFile.createNewFile()){
            System.out.println("New save file created: " + levelName);
        }
    }

    /**
     * Writes the expected time for the level the save file.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if you cannot write to the file.
     */
    private void writeExpectedTime(FileWriter fileWriter) throws IOException {
        fileWriter.write(expectedTime + "\n");
    }

    /**
     * Writes the win and lose conditions to the save file.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if you cannot write to the file.
     */
    private void writeWinLoseConditions(FileWriter fileWriter) throws IOException {
        fileWriter.write(numberOfRatsToWin + " ");
        fileWriter.write(numberOfRatsToLose + "\n");
    }

    /**
     * Writes the item spawn times to the level save.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if you cannot write to the file.
     */
    private void writeItemSpawns(FileWriter fileWriter) throws IOException {
        StringBuilder itemsHeld = new StringBuilder();
        for (ItemType type : ItemType.values()){
            itemsHeld.append(itemsInInventory.get(type.getIndex()).size()).append(FILE_DELIMITER);
        }
        fileWriter.write(itemsHeld + "\n");

        StringBuilder itemsSpawns = new StringBuilder();
        for (ItemType type : ItemType.values()){
            itemsSpawns.append(itemSpawnTime[type.getIndex()]).append(FILE_DELIMITER);
        }
        fileWriter.write(itemsSpawns + "\n");

    }

    /**
     * Writes the level grid to the save file.
     * @param fileWriter The file writer containing the file we want to write to.
     * @throws IOException Throws an exception if you cannot write to the file.
     */
    private void writeLevelGrid(FileWriter fileWriter) throws IOException {
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
     * @throws IOException Throws an exception if you cannot write to the file.
     */
    private void writeRatSpawnGrid(FileWriter fileWriter) throws IOException {
        for (Rat rat : rats){
            fileWriter.write(rat.toString());
            fileWriter.write("\n");
        }
    }

    /**
     * updates all the item timers which check whether an item is to spawn.
     * @param deltaTime The time since the last frame in seconds.
     */
    private void updateItems(float deltaTime){
        for (ItemType type : ItemType.values()){
            timeSinceItemSpawn[type.getIndex()] += deltaTime;
            if (timeSinceItemSpawn[type.getIndex()] > itemSpawnTime[type.getIndex()]){
                spawnItem(type);
                timeSinceItemSpawn[type.getIndex()] = 0;
            }
        }
    }

    /**
     * Removes an item from the inventory of the player.
     * @param type The type of item that we want to remove.
     */
    private void removeItem(ItemType type){
        inventoryGrid.getChildren().removeIf(node -> node.getClass() == ImageView.class &&
                ((ImageView) node).getImage().getUrl().endsWith(type.getTexture()));
        for (int i = 0; i < itemsInInventory.get(type.getIndex()).size(); i++){
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
        GridPane.setValignment(itemIcon, VPos.CENTER);
        itemIcon.setOnMousePressed(this::onItemBeginDrag);
        itemIcon.setOnMouseDragged(this::onItemDrag);
        itemIcon.setOnMouseReleased(this::onItemDragFinished);
        return itemIcon;
    }

    /**
     * Spawn an item into the inventory of the player, only if the player doesn't already have maximum items of that type.
     * @param type The type of item that we want to spawn into the inventory.
     */
    private void spawnItem(ItemType type){
        if (itemsInInventory.get(type.getIndex()).size() >= ITEM_MAX_STACK){
            return;
        }

        Item item = Item.create(type);

        item.setTexture(new Image(type.getTexture()));
        ImageView itemIcon = createImageIcon(item);
        inventoryGrid.add(itemIcon, itemsInInventory.get(type.getIndex()).size(), type.getIndex() + INVENTORY_GRID_OFFSET);
        itemsInInventory.get(type.getIndex()).add(item);
    }

    /**
     * Initiates the dragging of the item from inventory to the level, getting the item dragged and setting the position to the mouse.
     * @param event The mouse event initiating the drag.
     */
    public void onItemBeginDrag(MouseEvent event){
        if (isPaused){
            return;
        }
        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();
        ImageView target = (ImageView) event.getTarget();
        itemBeingDragged = new ImageView();
        itemBeingDragged.setImage(target.getImage());
        levelGameScreen.getChildren().add(itemBeingDragged);
        itemBeingDragged.setTranslateX(event.getSceneX() - itemBeingDragged.getImage().getWidth()/2);
        itemBeingDragged.setTranslateY(event.getSceneY() - itemBeingDragged.getImage().getHeight()/2);
    }

    /**
     * Sets the dragged items position to be at the position of the cursor.
     * @param event The mouse event triggering the drag.
     */
    public void onItemDrag(MouseEvent event){
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
    public void onItemDragFinished(MouseEvent event){
        // If the game is paused we do not want items to be usable.
        if (isPaused){
            return;
        }
        double droppedAbsoluteXPos = (itemBeingDragged.getTranslateX() + itemBeingDragged.getImage().getWidth()/2);
        double droppedAbsoluteYPos = (itemBeingDragged.getTranslateY() + itemBeingDragged.getImage().getHeight()/2);
        double droppedGridXPos = ((droppedAbsoluteXPos + (0 - levelPane.getTranslateX()))/Tile.TILE_WIDTH);
        double droppedGridYPos = ((droppedAbsoluteYPos + (0 - levelPane.getTranslateY()))/Tile.TILE_HEIGHT);
        int gridX = (int) droppedGridXPos;
        int gridY = (int) droppedGridYPos;

        // this gets the type of the item being dropped
        ItemType itemType = null;
        for (ItemType type : ItemType.values()){
            if (type.getTexture().equals(itemBeingDragged.getImage().getUrl().substring(itemBeingDragged.getImage().getUrl().length() - type.getTexture().length()))){
                itemType = type;
            }
        }

        // Removes the item being dropped from the screen where it was being dragged.
        levelGameScreen.getChildren().remove(itemBeingDragged);

        // If item isn't found in the inventory we return, we do not want to use an item they do not own.
        if (itemType == null){
            return;
        }

        // Checks if it's on a path, and whether its on the screen, stopping player dropping an item to a path they cannot see.
        if (droppedGridXPos < levelWidth && droppedGridXPos > 0 && droppedGridYPos < levelHeight && droppedGridYPos > 0 &&
                droppedAbsoluteXPos > 0 && droppedAbsoluteXPos < GameScreen.getWidth() &&
                droppedAbsoluteYPos > 0 && droppedAbsoluteYPos < GameScreen.getHeight()) {
            if (levelGrid[(int)droppedGridXPos][(int)droppedGridYPos].getType() == TileType.Path) {
                for (Item item : itemsInPlay){
                    if (item.xPos == gridX && item.yPos == gridY){
                        return;
                    }
                }

                // TODO: TESTING ONLY DELETE
                System.out.println("X: " + gridX + " Y: " + gridY);

                Item itemUsed = itemsInInventory.get(itemType.getIndex()).pop();
                itemUsed.setXPos(gridX);
                itemUsed.setYPos(gridY);
                itemUsed.getImageView().setTranslateX(itemUsed.getXPos() * Tile.TILE_WIDTH);
                itemUsed.getImageView().setTranslateY(itemUsed.getYPos() * Tile.TILE_HEIGHT);
                levelPane.getChildren().add(itemUsed.getImageView());
                itemUsed.use();
                itemsInPlay.add(itemUsed);
                removeItem(itemType);
            }
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
        controller.createLevel(levelName);
    }
}