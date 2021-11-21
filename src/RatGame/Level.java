package RatGame;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
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
// TODO: Tidy timer and pausing code. Duplicate code right now
// TODO: Reorganise file
// TODO: Implement number of rats alive
// TODO: Create timers for all items
// TODO: Create a close level method to shutdown all necessary timers and events
// TODO: Make fps timer take average not current frame
// TODO: Update saves method so save rat positions and items
// TODO: Create a setupLevel method and move most code out of read level file

enum ItemTypes {
    BOMB(0),
    GAS(1),
    STERILISATION(2),
    POISON(3),
    MALE_SEX_CHANGE(4),
    FEMALE_SEX_CHANGE(5),
    NO_ENTRY_SIGN(6),
    DEATH_RAT(7);
    
    private final int arrayPos;
    ItemTypes(int arrayPos){
        this.arrayPos = arrayPos;
    }

    public int getArrayPos() {
        return arrayPos;
    }
}

public class Level {
    long lastFrameTime;

    boolean isPaused;
    boolean firstLoop;

    private String levelName;
    private int levelHeight;
    private int levelWidth;

    private static final int TILE_HEIGHT = 50;
    private static final int TILE_WIDTH = 50;

    private static final int SECOND_TO_MILLI = 1000;

    private Tile[][] levelGrid;
    private char[][] ratSpawnGrid;

    private ArrayList<Rat> rats = new ArrayList<>();
    private int numOfRatsAlive;
    private int numOfMaleRatsAlive;
    private int numOfFemaleRatsAlive;

    private ArrayList<Stack<Item>> items;

    private double lastMouseX;
    private double lastMouseY;

    AnimationTimer gameLoop;

    long pauseTime;
    long timeLeftOnTimer;

    Timer bombSpawnTimer;
    Timer gasSpawnTimer;
    Timer sterilisationSpawnTimer;
    Timer poisonSpawnTimer;
    Timer maleSexChangeSpawnTimer;
    Timer femaleSexChangeSpawnTimer;
    Timer noEntrySignSpawnTimer;
    Timer deathRatSpawnTimer;

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
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (firstLoop){
                    firstLoop = false;
                    lastFrameTime = now;
                }
                float deltaTime = (float) ((now - lastFrameTime) / 1e9);
                updateFPSCount(deltaTime);
                if (!isPaused){
                    update(deltaTime);
                }
                lastFrameTime = now;
            }
        };
        gameLoop.start();

        bombSpawnTimer = new Timer();
    }

    private void stopGameLoop(){
        gameLoop.stop();

    }

    public void update(float deltaTime){
        GraphicsContext gc = GameBoard.getGraphicsContext2D();
        spawnTiles(gc);
        for (Rat rat:rats){
            rat.update(deltaTime, levelGrid);
        }
    }

    public void pauseLoop(){
        isPaused = !isPaused;
        firstLoop = true;
        pauseTime = new Date().getTime();
        if (!isPaused){
            TimerTask addBomb = new TimerTask() {
                public void run(){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (isPaused){
                                timeLeftOnTimer = (new Date().getTime()) - pauseTime;
                                bombSpawnTimer.cancel();
                            } else {
                                spawnItem(ItemTypes.BOMB);
                            }
                        }
                    });
                }
            };
            bombSpawnTimer = new Timer();
            bombSpawnTimer.scheduleAtFixedRate(addBomb, timeLeftOnTimer, (long) bombSpawnTime * SECOND_TO_MILLI);
        }
    }

    public void updateFPSCount(float deltaTime){
        fpsCount.setText("FPS: " + (int)(1/deltaTime));
    }

    /**
     * Return from the level to the main menu.
     * @param event The action that triggered this.
     * @throws IOException If the FXML file does not exist will throw exception.
     */
    public void exitPressed(ActionEvent event) throws IOException {
        gameLoop.stop();

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

    int bombSpawnTime;
    int gasSpawnTime;
    int sterilisationSpawnTime;
    int poisonSpawnTime;
    int maleSexChangeSpawnTime;
    int femaleSexChangeSpawnTime;
    int noEntrySpawnTime;
    int deathRatSpawnTime;

    // TODO create a createLevel method and separate readLevelFile and parseLevel and spawnTiles.
    /**
     * Reads in a file and parses the data for the level.
     * @param src A path to the level wanting to be read.
     */
    public void readLevelFile(String src){
        try {
            // Read in file
            File levelFile = new File(src);
            Scanner fileReader = new Scanner(levelFile);

            // Get the level Name from the path
            levelName = src.substring(11);
            System.out.println(levelName);

            // Read level dimensions
            String levelDimensions = fileReader.nextLine();
            String[] levelDimensionsSplit = levelDimensions.split(" ");
            levelWidth = Integer.parseInt(levelDimensionsSplit[0]);
            levelHeight = Integer.parseInt(levelDimensionsSplit[1]);

            // Read The item spawn times
            String itemSpawnTimes = fileReader.nextLine();
            String[] itemSpawnTimesSplit = itemSpawnTimes.split(" ");

            bombSpawnTime = Integer.parseInt(itemSpawnTimesSplit[0]);
            gasSpawnTime = Integer.parseInt(itemSpawnTimesSplit[1]);
            sterilisationSpawnTime = Integer.parseInt(itemSpawnTimesSplit[2]);
            poisonSpawnTime = Integer.parseInt(itemSpawnTimesSplit[3]);
            maleSexChangeSpawnTime = Integer.parseInt(itemSpawnTimesSplit[4]);
            femaleSexChangeSpawnTime = Integer.parseInt(itemSpawnTimesSplit[5]);
            noEntrySpawnTime = Integer.parseInt(itemSpawnTimesSplit[6]);
            deathRatSpawnTime = Integer.parseInt(itemSpawnTimesSplit[7]);

            // Create the 2d array that holds tile positions.
            GameBoard.setHeight(TILE_HEIGHT * levelHeight);
            GameBoard.setWidth(TILE_WIDTH * levelWidth);

            // TODO: Set this as a array of Tiles instead.
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

            // Ignore the newline between the grids
            fileReader.nextLine();

            // Read rat positions and store to grid
            ratSpawnGrid = new char[levelWidth][levelHeight];
            for (int row = 0; row < levelHeight; row++) {
                String rowLayout = fileReader.nextLine();
                for (int col = 0; col < levelWidth; col++) {
                    ratSpawnGrid[col][row] = rowLayout.charAt(col);
                }
            }


        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        spawnTiles(GameBoard.getGraphicsContext2D());
        spawnRats();

        TimerTask addBomb = new TimerTask(){
            public void run(){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (isPaused){
                            timeLeftOnTimer = (new Date().getTime()) - pauseTime;
                            bombSpawnTimer.cancel();
                        } else {
                            spawnItem(ItemTypes.BOMB);
                        }
                    }
                });
            }
        };

        bombSpawnTimer.scheduleAtFixedRate(addBomb, (long) bombSpawnTime * SECOND_TO_MILLI, (long) bombSpawnTime * SECOND_TO_MILLI);

        items = new ArrayList<>(8);
        for (int i = 0; i < 8; i++){
            Stack<Item> itemStack = new Stack<>();
            items.add(itemStack);
        }
    }

    // TODO Once we have a player class store all saves for each player in the player folder instead.
    // TODO If a level save is already active for a player we need to only let the player resume.
    public void saveLevel(ActionEvent event){
        try {
            File levelSaveFile = new File("src/Saves/" + levelName);
            if (levelSaveFile.createNewFile()){
                System.out.println("File Created Successfully!");
            } else {
                System.out.println("Error creating file!");
            }

            FileWriter fileWriter = new FileWriter("src/Saves/" + levelName);
            fileWriter.write(levelHeight + " " + levelWidth + "\n");
            for (int row = 0; row < levelHeight; row++) {
                for (int col = 0; col < levelWidth; col++) {
                    if (levelGrid[col][row].getType() == TileType.Grass){
                        fileWriter.write("G");
                    } else if (levelGrid[col][row].getType() == TileType.Path){
                        fileWriter.write("P");
                    } else if (levelGrid[col][row].getType() == TileType.Tunnel){
                        fileWriter.write("T");
                    } else if (levelGrid[col][row].getType() == TileType.VerticalTunnel){
                        fileWriter.write("V");
                    } else {
                        System.out.print("There seems to be an error on the tile type, We should never see this.");
                    }
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int[] itemsInInventory = new int[8];

    public void updateItems(){
        for (ItemTypes type : ItemTypes.values()){
            if (items.get(type.getArrayPos()).size() < itemsInInventory[type.getArrayPos()]) {
                inventoryGrid.getChildren().removeIf(node -> node.getClass() == ImageView.class);
                itemsInInventory[type.getArrayPos()] = 0;
            }
            if (items.get(type.getArrayPos()).size() > itemsInInventory[type.getArrayPos()]) {
                while (items.get(type.getArrayPos()).size() > itemsInInventory[type.getArrayPos()]) {
                    Item item = items.get(type.getArrayPos()).get(itemsInInventory[type.getArrayPos()]);
                    ImageView itemIcon = new ImageView();
                    item.texture = new Image("Assets/Bomb.png");
                    itemIcon.setImage(item.texture);
                    itemIcon.setOnDragDetected(this::onBeginDrag);
                    itemIcon.setOnMouseDragged(this::onItemDrag);
                    itemIcon.setOnMouseReleased(this::onItemDragFinished);
                    inventoryGrid.add(itemIcon, itemsInInventory[type.getArrayPos()], type.getArrayPos() + 1);
                    itemsInInventory[type.getArrayPos()]++;
                }
            }
        }
    }

    public void removeItem(ItemTypes item){
        for (Node node : inventoryGrid.getChildren()){
            if (node.getClass() == ImageView.class){
                String imgURL = ((ImageView) node).getImage().getUrl();
                String toCmpURL = "Assets/Bomb.png";
                String imgFile = imgURL.substring(imgURL.length()-toCmpURL.length());
                if (imgFile.equals(toCmpURL)){
                    inventoryGrid.getChildren().remove(node);
                    return;
                }
            }
        }
    }

    public void spawnItem(ItemTypes item){
        if (items.get(item.getArrayPos()).size() < 4) {
            Item test = new Item() {
                @Override
                public void use() {
                    System.out.println("Item uses!");
                }
            };
            test.texture = new Image("Assets/Bomb.png");
            items.get(item.getArrayPos()).push(test);
            updateItems();
        }
    }

    ImageView toDrag;

    public void onBeginDrag(MouseEvent event){
        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();
        ImageView target = (ImageView) event.getTarget();
        toDrag = new ImageView();
        toDrag.setImage(target.getImage());
        levelGameScreen.getChildren().add(toDrag);
        toDrag.setTranslateX(event.getSceneX() - toDrag.getImage().getWidth()/2);
        toDrag.setTranslateY(event.getSceneY() - toDrag.getImage().getHeight()/2);
    }

    public void onItemDrag(MouseEvent event){
        if (toDrag != null) {
            toDrag.setTranslateX(toDrag.getTranslateX() + (event.getSceneX() - lastMouseX));
            toDrag.setTranslateY(toDrag.getTranslateY() + (event.getSceneY() - lastMouseY));
            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();
        }
    }

    public void onItemDragFinished(MouseEvent event){
        double droppedAbsoluteXPos = (toDrag.getTranslateX() + toDrag.getImage().getWidth()/2);
        double droppedAbsoluteYPos = (toDrag.getTranslateY() + toDrag.getImage().getHeight()/2);
        double droppedGridXPos = ((droppedAbsoluteXPos + (0 - levelPane.getTranslateX()))/TILE_WIDTH);
        double droppedGridYPos = ((droppedAbsoluteYPos + (0 - levelPane.getTranslateY()))/TILE_HEIGHT);
        levelGameScreen.getChildren().remove(toDrag);
        if (droppedGridXPos < levelWidth && droppedGridXPos > 0 && droppedGridYPos < levelHeight && droppedGridYPos > 0 &&
                droppedAbsoluteXPos > 0 && droppedAbsoluteXPos < GameScreen.getWidth() &&
                droppedAbsoluteYPos > 0 && droppedAbsoluteYPos < GameScreen.getHeight()) {
            if (levelGrid[(int)droppedGridXPos][(int)droppedGridYPos].getType() == TileType.Path) {
                ItemTypes itemType = ItemTypes.BOMB;
                items.get(itemType.getArrayPos()).pop().use();
                updateItems();
            }
        }
    }

    public void spawnRats(){
        for (int row = 0; row < levelHeight ; row++){
            for (int col = 0; col < levelWidth ; col++){
                Rat rat;
                if (ratSpawnGrid[col][row] == 'F'){
                    rat = new Rat("female", col, row, false);
                    levelPane.getChildren().add(rat.img);
                    rats.add(rat);
                    numOfFemaleRatsAlive++;
                    numOfRatsAlive++;
                    drawRat(rat);
                } else if (ratSpawnGrid[col][row] == 'M'){
                    rat = new Rat("male", col, row, false);
                    levelPane.getChildren().add(rat.img);
                    rats.add(rat);
                    numOfMaleRatsAlive++;
                    numOfRatsAlive++;
                    drawRat(rat);
                }
            }
        }
    }

    // TODO Once we have a tiles class we will need to use those instead of hard coding to draw.#

    /**
     * Draws the tiles onto the level.
     * @param gc Takes in the graphics content of what we want to draw on.
     */
    private void spawnTiles(GraphicsContext gc){
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                gc.drawImage(levelGrid[col][row].getTexture(), col * TILE_HEIGHT, row * TILE_WIDTH);
            }
        }
    }
}