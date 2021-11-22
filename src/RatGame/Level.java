package RatGame;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// TODO: Use item class and items

enum ItemType {
    BOMB(0, "Assets/Bomb.png"),
    GAS(1, "Assets/Gas.png"),
    STERILISATION(2, "Assets/Sterilisation.png"),
    POISON(3, "Assets/Poison.png"),
    MALE_SEX_CHANGE(4, "Assets/MaleSexChange.png"),
    FEMALE_SEX_CHANGE(5, "Assets/FemaleSexChange.png"),
    NO_ENTRY_SIGN(6, "Assets/Stop.png"),
    DEATH_RAT(7, "Assets/Death.png");

    private final int arrayPos;
    private final String texture;
    ItemType(int arrayPos, String texture){
        this.arrayPos = arrayPos;
        this.texture = texture;
    }

    public int getArrayPos() {
        return arrayPos;
    }

    public String getTexture(){
        return texture;
    }
}

public class Level {
    private static final int TILE_HEIGHT = 50;
    private static final int TILE_WIDTH = 50;
    private static final int NUMBER_OF_ITEMS = 8;

    private static final String SAVE_FOLDER_PATH = "src/Saves/";

    ArrayDeque<Float> last60deltaTime = new ArrayDeque<>();
    long lastFrameTime;
    float timeSinceFPSRefreshed;

    boolean isPaused;
    boolean firstLoop;

    private String levelName;
    private int levelHeight;
    private int levelWidth;

    GraphicsContext levelGraphicsContext;

    private Tile[][] levelGrid;
    ArrayList<ImageView> tunnels;

    private ArrayList<Rat> rats;
    private int numOfRatsAlive;
    private int numOfMaleRatsAlive;
    private int numOfFemaleRatsAlive;

    private int numberOfRatsToLose;
    private int numberOfRatsToWin;

    private ArrayList<Stack<Item>> items;

    private double lastMouseX;
    private double lastMouseY;

    AnimationTimer gameLoop;

    long pauseTime;

    float[] timeSinceItemSpawn = new float[8];

    int[] itemSpawnTime = new int[8];

    ImageView itemBeingDragged;

    // All FXML variables.
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

    // TODO: This need to be added to the rat class.
    public void drawRat(Rat rat){
        rat.img.setFitHeight(50.0);
        rat.img.setFitWidth(50.0);
        rat.img.setViewport(new Rectangle2D(-14, -4, 50, 50));
    }

    public void initialize(){
        firstLoop = true;

        levelGraphicsContext = GameBoard.getGraphicsContext2D();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (firstLoop){
                    firstLoop = false;
                    lastFrameTime = now;
                }

                float deltaTime = (float) ((now - lastFrameTime) / 1e9);
                lastFrameTime = now;

                if (!isPaused){
                    update(deltaTime);
                }

                updateFPSCount(deltaTime);
            }
        };
        gameLoop.start();
    }

    public void updateFPSCount(float deltaTime){
        timeSinceFPSRefreshed += deltaTime;
        last60deltaTime.addLast(deltaTime);
        if (last60deltaTime.size() > 60){
            last60deltaTime.pop();
        }
        if (timeSinceFPSRefreshed > 0.1){
            float sum = 0;
            for (Float num : last60deltaTime){
                sum += num;
            }
            fpsCount.setText("FPS: " + (int) (1/ (sum/last60deltaTime.size())));

            timeSinceFPSRefreshed = 0;
        }
    }

    private void stopGameLoop(){
        gameLoop.stop();
    }

    public void update(float deltaTime){
        drawTiles();
        updateRats(deltaTime);
        updateRatsAliveText();
        updateItems(deltaTime);
        checkWinLoseCondition();
    }

    private void updateRatsAliveText() {
        ratsAlive.setText("Rats alive: " + rats.size());
        maleRatsAlive.setText("Males alive: " + numOfMaleRatsAlive);
        femaleRatsAlive.setText("Females alive: " + numOfFemaleRatsAlive);
    }

    private void checkWinLoseCondition() {
        // TODO: Add lose mechanic
        if (numOfRatsAlive > numberOfRatsToLose){
            System.out.println("You Lose!");
        }

        if (numOfRatsAlive < numberOfRatsToWin){
            System.out.println("You Win!");
        }
    }

    private void updateRats(float deltaTime){
        for (Rat rat:rats){
            rat.update(deltaTime, levelGrid);
        }
    }

    public void pauseLoop(){
        isPaused = !isPaused;
        firstLoop = true;
        pauseTime = new Date().getTime();
    }

    // TODO: Add popup to ask whether user wants to save the level if leaving early.
    /**
     * Return from the level to the main menu.
     * @param event The action that triggered this.
     * @throws IOException If the FXML file does not exist will throw exception.
     */
    public void exitPressed(ActionEvent event) throws IOException {
        stopGameLoop();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
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

    public void createLevel(String src){
        rats = new ArrayList<>();

        numberOfRatsToLose = 100;
        numberOfRatsToWin = 1;

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
        File levelFile = new File("src/Levels/" + src);
        Scanner fileReader = new Scanner(levelFile);

        setupItems(fileReader);
        setupLevelGrid(fileReader);
        setupRatSpawns(fileReader);
    }

    private void setupLevelGrid(Scanner fileReader){
        // Read level dimensions
        String levelDimensions = fileReader.nextLine();
        String[] levelDimensionsSplit = levelDimensions.split(" ");
        levelWidth = Integer.parseInt(levelDimensionsSplit[0]);
        levelHeight = Integer.parseInt(levelDimensionsSplit[1]);

        // Create the 2d array that holds tile positions.
        GameBoard.setHeight(TILE_HEIGHT * levelHeight);
        GameBoard.setWidth(TILE_WIDTH * levelWidth);

        levelGrid = new Tile[levelWidth][levelHeight];
        for (int row = 0; row < levelHeight; row++) {
            String rowLayout = fileReader.nextLine();
            for (int col = 0; col < levelWidth; col++) {
                Tile tile = null;
                if (rowLayout.charAt(col) == 'G') {
                    tile = new Tile(row, col, TileType.Grass);
                } else if (rowLayout.charAt(col) == 'P') {
                    tile = new Tile(row, col, TileType.Path);
                } else if (rowLayout.charAt(col) == 'T') {
                    tile = new Tile(row, col, TileType.Tunnel);
                } else if (rowLayout.charAt(col) == 'V') {
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

    private void setupItems(Scanner fileReader){

        String itemsToStartWith = fileReader.nextLine();
        String[] itemsToStartWithSplit = itemsToStartWith.split(" ");

        // TODO: Add items here instead of hard code.
        for (int i = 0; i < 8; i++){
            for (int j = 0 ; j < Integer.parseInt(itemsToStartWithSplit[i]); j++) {
                spawnItem(ItemType.values()[i]);
            }
        }

        // Read The item spawn times
        String itemSpawnTimes = fileReader.nextLine();
        String[] itemSpawnTimesSplit = itemSpawnTimes.split(" ");

        for (int i = 0; i < 8; i++){
            itemSpawnTime[i] = Integer.parseInt(itemSpawnTimesSplit[i]);
        }
    }

    private void setupRatSpawns(Scanner fileReader) {
        while (fileReader.hasNextLine()){
            String ratToSpawn = fileReader.nextLine();
            if (ratToSpawn.equals("")){
                return;
            }
            String[] ratToSpawnSplit = ratToSpawn.split(" ");
            String type;
            if (ratToSpawnSplit[0].equals("F")){
                type = "female";
            } else if (ratToSpawnSplit[0].equals("M")){
                type = "male";
            } else {
                type = "death";
            }

            int xPos = Integer.parseInt(ratToSpawnSplit[1]);
            int yPos = Integer.parseInt(ratToSpawnSplit[2]);
            // TODO: Needs to add an isBaby bool to constructor as loads can have full adults.
//            boolean isBaby = ratToSpawnSplit[3].equals("T");

            // TODO: most of this should be in rat class, only should construct here and add
            spawnRat(type, xPos, yPos, false);
        }
    }

    // TODO: Right now female and male gets incremented even if they're babies, need to only happen when they grow.
    private void spawnRat(String type, int xPos, int yPos, boolean isDeathRat){
        Rat rat = new Rat(type, xPos, yPos, isDeathRat);
        levelPane.getChildren().add(rat.img);
        rats.add(rat);
        numOfRatsAlive++;
        if (type.equals("female")){
            numOfFemaleRatsAlive++;
        } else {
            numOfMaleRatsAlive++;
        }
        drawRat(rat);

        // Update tunnel after every rat spawns so that tunnels are always on top.
        updateTunnels();
    }

    /**
     * Draws the tiles onto the level.
     */
    private void drawTiles(){
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                levelGraphicsContext.drawImage(levelGrid[col][row].getTexture(), col * TILE_HEIGHT, row * TILE_WIDTH);
            }
        }
    }

    private void drawTunnels(){
        tunnels = new ArrayList<>();
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                if (levelGrid[col][row].getType() == TileType.Tunnel || levelGrid[col][row].getType() == TileType.VerticalTunnel) {
                    ImageView tunnel =  new ImageView();
                    tunnel.setImage(levelGrid[col][row].getTexture());
                    tunnel.setTranslateX(col * TILE_WIDTH);
                    tunnel.setTranslateY(row * TILE_HEIGHT);
                    levelPane.getChildren().add(tunnel);
                    tunnels.add(tunnel);
                }
            }
        }
    }

    private void updateTunnels(){
        for (ImageView tunnel : tunnels){
            tunnel.toFront();
        }
    }

    public void createInventory(){
        items = new ArrayList<>(NUMBER_OF_ITEMS);
        for (int i = 0; i < NUMBER_OF_ITEMS; i++){
            Stack<Item> itemStack = new Stack<>();
            items.add(itemStack);
        }
    }

    // TODO Once we have a player class store all saves for each player in the player folder instead.
    // TODO If a level save is already active for a player we need to only let the player resume.
    public void saveLevel(){
        try {
            createSaveFile();

            FileWriter fileWriter = new FileWriter(SAVE_FOLDER_PATH + levelName);

            writeItemSpawns(fileWriter);

            writeLevelGrid(fileWriter);

            writeRatSpawnGrid(fileWriter);

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private void writeItemSpawns(FileWriter fileWriter) throws IOException {
        StringBuilder itemsHeld = new StringBuilder();
        for (ItemType type : ItemType.values()){
            itemsHeld.append(items.get(type.getArrayPos()).size()).append(" ");
        }
        fileWriter.write(itemsHeld + "\n");

        StringBuilder itemsSpawns = new StringBuilder();
        for (ItemType type : ItemType.values()){
            itemsSpawns.append(itemSpawnTime[type.getArrayPos()]).append(" ");
        }
        fileWriter.write(itemsSpawns + "\n");

    }

    private void writeLevelGrid(FileWriter fileWriter) throws IOException {
        fileWriter.write(levelWidth + " " + levelHeight + "\n");
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                // TODO: tile class need a toSave() method and can replace this whole block with:
                fileWriter.write(levelGrid[col][row].toString());
            }
            fileWriter.write("\n");
        }
    }

    // TODO: Rats need a toString() method
    private void writeRatSpawnGrid(FileWriter fileWriter) throws IOException {
        for (Rat rat : rats){
            fileWriter.write(rat.toString());
            fileWriter.write("\n");
        }
    }

    private void updateItems(float deltaTime){
        for (ItemType type : ItemType.values()){
            timeSinceItemSpawn[type.getArrayPos()] += deltaTime;
            if (timeSinceItemSpawn[type.getArrayPos()] > itemSpawnTime[type.getArrayPos()]){
                spawnItem(type);
                timeSinceItemSpawn[type.getArrayPos()] = 0;
            }
        }
    }

    // TODO: Tidy this up and remove duplicate code
    private void removeItem(ItemType type){
        inventoryGrid.getChildren().removeIf(node -> node.getClass() == ImageView.class &&
                ((ImageView) node).getImage().getUrl().endsWith(type.getTexture()));
        for (int i = 0; i < items.get(type.getArrayPos()).size(); i++){
            Item item = items.get(type.getArrayPos()).get(i);
            ImageView itemIcon = createImageIcon(item);
            inventoryGrid.add(itemIcon, i, type.getArrayPos() + 1);
        }
    }

    private ImageView createImageIcon(Item item) {
        ImageView itemIcon = new ImageView();
        itemIcon.setImage(item.texture);
        inventoryGrid.setAlignment(Pos.CENTER);
        GridPane.setHalignment(itemIcon, HPos.CENTER);
        GridPane.setValignment(itemIcon, VPos.CENTER);
        itemIcon.setOnMousePressed(this::onItemBeginDrag);
        itemIcon.setOnMouseDragged(this::onItemDrag);
        itemIcon.setOnMouseReleased(this::onItemDragFinished);
        return itemIcon;
    }

    private void spawnItem(ItemType type){
        if (items.get(type.getArrayPos()).size() >= 4){
            return;
        }

        Item item = new Item() {
            @Override
            public void use() {

            }

            @Override
            public void steppedOn() {

            }
        };

        item.texture = new Image(type.getTexture());
        ImageView itemIcon = createImageIcon(item);
        inventoryGrid.add(itemIcon, items.get(type.getArrayPos()).size(), type.getArrayPos() + 1);
        items.get(type.getArrayPos()).add(item);
    }

    // TODO: Need to add inventory methods and add functionality.
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

    public void onItemDrag(MouseEvent event){
        if (itemBeingDragged != null) {
            itemBeingDragged.setTranslateX(itemBeingDragged.getTranslateX() + (event.getSceneX() - lastMouseX));
            itemBeingDragged.setTranslateY(itemBeingDragged.getTranslateY() + (event.getSceneY() - lastMouseY));
            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();
        }
    }

    public void onItemDragFinished(MouseEvent event){
        if (isPaused){
            return;
        }
        double droppedAbsoluteXPos = (itemBeingDragged.getTranslateX() + itemBeingDragged.getImage().getWidth()/2);
        double droppedAbsoluteYPos = (itemBeingDragged.getTranslateY() + itemBeingDragged.getImage().getHeight()/2);
        double droppedGridXPos = ((droppedAbsoluteXPos + (0 - levelPane.getTranslateX()))/TILE_WIDTH);
        double droppedGridYPos = ((droppedAbsoluteYPos + (0 - levelPane.getTranslateY()))/TILE_HEIGHT);

        ItemType itemType = null;
        for (ItemType type : ItemType.values()){
            if (type.getTexture().equals(itemBeingDragged.getImage().getUrl().substring(itemBeingDragged.getImage().getUrl().length() - type.getTexture().length()))){
                itemType = type;
            }
        }

        levelGameScreen.getChildren().remove(itemBeingDragged);

        if (itemType == null){
            return;
        }

        if (droppedGridXPos < levelWidth && droppedGridXPos > 0 && droppedGridYPos < levelHeight && droppedGridYPos > 0 &&
                droppedAbsoluteXPos > 0 && droppedAbsoluteXPos < GameScreen.getWidth() &&
                droppedAbsoluteYPos > 0 && droppedAbsoluteYPos < GameScreen.getHeight()) {
            if (levelGrid[(int)droppedGridXPos][(int)droppedGridYPos].getType() == TileType.Path) {
                items.get(itemType.getArrayPos()).pop().use();
                removeItem(itemType);
            }
        }
        itemBeingDragged = null;
    }
}