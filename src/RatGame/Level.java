package RatGame;

import javafx.animation.AnimationTimer;
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
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Level {
    long pauseStart;
    long playStart;

    long lastFrameTime;

    boolean isPaused;
    boolean firstLoop;

    private String levelName;
    private int levelHeight;
    private int levelWidth;

    private static final int TILE_HEIGHT = 50;
    private static final int TILE_WIDTH = 50;

    private char[][] levelGrid;
    private char[][] ratSpawnGrid;

    private ArrayList<Rat> rats = new ArrayList<>();
    private int numOfRatsAlive;
    private int numOfMaleRatsAlive;
    private int numOfFemaleRatsAlive;

    private double lastMouseX;
    private double lastMouseY;

    AnimationTimer gameLoop;

    // TODO Remove, these should be stored in the tiles
    Image grassTile = new Image("Assets/Grass.png");
    Image pathTile = new Image("Assets/Path.png");
    Image tunnelTile = new Image("Assets/Tunnel.png");
    Image tunnelVertTile = new Image("Assets/TunnelVertical.png");

    // All FXML variables.
    @FXML
    private AnchorPane GameScreen;

    @FXML
    private Canvas GameBoard;

    @FXML
    private StackPane levelPane;

    @FXML
    private Text fpsCount;

//    float testX = 1.0f;
//    float testY = 1.0f;
//
//    float testXVel = 5.0f;
//    float testYVel = 0.0f;
//
//    ImageView testImg;
//    Image testIm = new Image("Assets/Male.png");
//
//    int lastX = (int)testX;
//    int lastY = (int)testX;
//
//    private Pair<Integer, Integer> checkPaths(int x, int y, int lastX, int lastY){
//        ArrayList<Pair<Integer, Integer>> paths = new ArrayList<>();
//
////        System.out.println(levelGrid[x+1][y]);
//        if (levelGrid[x+1][y] != 'G' && x + 1 != lastX) {
//            paths.add(new Pair<>(5, 0));
//        }
//        if (levelGrid[x-1][y] != 'G' && x - 1 != lastX) {
//            paths.add(new Pair<>(-5, 0));
//        }
//        if (levelGrid[x][y+1] != 'G' && y + 1 != lastY) {
//            paths.add(new Pair<>(0, 5));
//        }
//        if (levelGrid[x][y-1] != 'G' && y - 1 != lastY) {
//            paths.add(new Pair<>(0, -5));
//        }
//
//        if (paths.size() == 0) {
//
//            return new Pair<>((lastX - x)*5, (lastY - y)*5);
//        }
//        Random rand = new Random();
//        return paths.get(rand.nextInt(paths.size()));
//    }
//
//    public void setRotate(){
//        if (testXVel < 0) {
//            testImg.setRotate(90.0);
//        } else if (testXVel > 0) {
//            testImg.setRotate(270);
//        } else if (testYVel > 0) {
//            testImg.setRotate(0);
//        } else {
//            testImg.setRotate(180);
//        }
//    }
//
//    public void testMove(float deltaTime){
//        testX += testXVel * deltaTime;
//        testY += testYVel * deltaTime;
//        testImg.setTranslateX(testX*50);
//        testImg.setTranslateY(testY*50);
//        if (testXVel < 0){
//            if ((int)testX+1 != lastX) {
//                testX = (int)testX+1;
//                testY = (int)testY;
//                Pair<Integer, Integer> vel = checkPaths((int)testX, (int)testY, lastX, lastY);
//                testXVel = vel.getKey();
//                testYVel = vel.getValue();
//                lastX = (int) testX;
//                lastY = (int) testY;
//            }
//        } else if (testYVel < 0) {
//            if ((int)testY+1 != lastY) {
//                testX = (int)testX;
//                testY = (int)testY+1;
//                Pair<Integer, Integer> vel = checkPaths((int)testX, (int)testY, lastX, lastY);
//                testXVel = vel.getKey();
//                testYVel = vel.getValue();
//                lastX = (int) testX;
//                lastY = (int) testY;
//                testImg.setRotate(180.0);
//            }
//        } else {
//            if ((int)testX != lastX || (int)testY != lastY) {
//                testX = (int)testX;
//                testY = (int)testY;
//                Pair<Integer, Integer> vel = checkPaths((int)testX, (int)testY, lastX, lastY);
//                testXVel = vel.getKey();
//                testYVel = vel.getValue();
//                lastX = (int) testX;
//                lastY = (int) testY;
//            }
//        }
//    }


    ArrayList<ImageView> toAdd = new ArrayList<>();

    public void drawRat(Rat rat){
        rat.img.setFitHeight(50.0);
        rat.img.setFitWidth(50.0);
        rat.img.setViewport(new Rectangle2D(-14, -4, 50, 50));
        toAdd.add(rat.img);
    }

    public void initialize(){
//        testImg = new ImageView();
//        testImg.setImage(testIm);
//        testImg.setFitHeight(50.0);
//        testImg.setFitWidth(50.0);
//        testImg.setViewport(new Rectangle2D(-14, -4, 50, 50));
//        levelPane.getChildren().add(testImg);


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

    }

    public void update(float deltaTime){
        GraphicsContext gc = GameBoard.getGraphicsContext2D();
        spawnTiles(gc);
        for (Rat rat:rats){
            rat.update(deltaTime);
        }
//        setRotate();
//        testMove(deltaTime);
        //gc.drawImage(testImg.getImage(), testX * TILE_HEIGHT, testY * TILE_WIDTH);
    }

    public void pauseLoop(){
        isPaused = !isPaused;
        firstLoop = true;
    }

    public void updateFPSCount(float deltaTime){
        fpsCount.setText("FPS: " + (int)(1/deltaTime));
    }

    /**
     * Switch scene to the main menu.
     * @param event The action that triggered this.
     * @throws IOException If file does not exist will throw exception.
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
            levelGrid = new char[levelWidth][levelHeight];
            for (int row = 0; row < levelHeight; row++) {
                String rowLayout = fileReader.nextLine();
                for (int col = 0; col < levelWidth; col++) {
                    levelGrid[col][row] = rowLayout.charAt(col);
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

//        GraphicsContext gc = GameBoard.getGraphicsContext2D();

        spawnTiles(GameBoard.getGraphicsContext2D());
        spawnRats();
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
                    if (levelGrid[col][row] == 'G'){
                        fileWriter.write("G");
                    } else if (levelGrid[col][row] == 'P'){
                        fileWriter.write("P");
                    } else if (levelGrid[col][row] == 'T'){
                        fileWriter.write("T");
                    } else if (levelGrid[col][row] == 'V'){
                        fileWriter.write("V");
                    } else {
                        System.out.print("Hmm there shouldn't be any other tile????");
                    }
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
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
                if (levelGrid[col][row] == 'G'){
                    gc.drawImage(grassTile, col * TILE_HEIGHT, row * TILE_WIDTH);
                } else if (levelGrid[col][row] == 'P'){
                    gc.drawImage(pathTile, col * TILE_HEIGHT, row * TILE_WIDTH);
                } else if (levelGrid[col][row] == 'T'){
                    gc.drawImage(tunnelTile, col * TILE_HEIGHT, row * TILE_WIDTH);
                } else if (levelGrid[col][row] == 'V'){
                    gc.drawImage(tunnelVertTile, col * TILE_HEIGHT, row * TILE_WIDTH);
                } else {
                    System.out.print("Hmm there shouldn't be any other tile????");
                }
            }
        }
    }
}