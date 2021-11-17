package RatGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Level {
    private String levelName;
    private int levelHeight;
    private int levelWidth;

    private static final int TILE_HEIGHT = 50;
    private static final int TILE_WIDTH = 50;

    private char[][] levelGrid;

    private double lastMouseX;
    private double lastMouseY;

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


    public void initialize(){
    }

    /**
     * Switch scene to the main menu.
     * @param event The action that triggered this.
     * @throws IOException If file does not exist will throw exception.
     */
    public void switchToMainMenu(ActionEvent event) throws IOException {
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
        GameBoard.setTranslateX(GameBoard.getTranslateX() + (event.getX() - lastMouseX));
        GameBoard.setTranslateY(GameBoard.getTranslateY() + (event.getY() - lastMouseY));
        clampToGameScreen();
    }


    /**
     * Prevents player from dragging the level off the screen.
     * If level is bigger than screen the level will always be on screen and cannot be dragged off-screen.
     * If level is smaller than the level can be dragged around on the screen freely but not off-screen.
     */
    public void clampToGameScreen(){
        if (GameBoard.getWidth() > GameScreen.getWidth() && GameBoard.getHeight() > GameScreen.getHeight()) {
            if (GameBoard.getTranslateX() > 0) {
                GameBoard.setTranslateX(0.0);
            } else if (GameBoard.getTranslateX() < GameScreen.getWidth() - GameBoard.getWidth()) {
                GameBoard.setTranslateX(GameScreen.getWidth() - GameBoard.getWidth());
            }
            if (GameBoard.getTranslateY() > 0) {
                GameBoard.setTranslateY(0.0);
            } else if (GameBoard.getTranslateY() < GameScreen.getHeight() - GameBoard.getHeight()) {
                GameBoard.setTranslateY(GameScreen.getHeight() - GameBoard.getHeight());
            }
        } else if (GameBoard.getWidth() > GameScreen.getWidth()){
            if (GameBoard.getTranslateX() > 0){
                GameBoard.setTranslateX(0.0);
            } else if (GameBoard.getTranslateX() < GameScreen.getWidth() - GameBoard.getWidth() ){
                GameBoard.setTranslateX(GameScreen.getWidth() - GameBoard.getWidth());
            }
            if (GameBoard.getTranslateY() < 0){
                GameBoard.setTranslateY(0.0);
            } else if (GameBoard.getTranslateY() > GameScreen.getHeight() - GameBoard.getHeight() ){
                GameBoard.setTranslateY(GameScreen.getHeight() - GameBoard.getHeight());
            }
        } else if (GameBoard.getHeight() > GameScreen.getHeight()){
            if (GameBoard.getTranslateX() < 0){
                GameBoard.setTranslateX(0.0);
            } else if (GameBoard.getTranslateX() > GameScreen.getWidth() - GameBoard.getWidth() ){
                GameBoard.setTranslateX(GameScreen.getWidth() - GameBoard.getWidth());
            }
            if (GameBoard.getTranslateY() > 0){
                GameBoard.setTranslateY(0.0);
            } else if (GameBoard.getTranslateY() < GameScreen.getHeight() - GameBoard.getHeight() ){
                GameBoard.setTranslateY(GameScreen.getHeight() - GameBoard.getHeight());
            }
        } else {
            if (GameBoard.getTranslateY() < 0){
                GameBoard.setTranslateY(0.0);
            } else if (GameBoard.getTranslateY() + GameBoard.getHeight() > GameScreen.getHeight()){
                GameBoard.setTranslateY(GameScreen.getHeight() - GameBoard.getHeight());
            }
            if (GameBoard.getTranslateX() < 0) {
                GameBoard.setTranslateX(0.0);
            } else if (GameBoard.getTranslateX() + GameBoard.getWidth() > GameScreen.getWidth()){
                GameBoard.setTranslateX(GameScreen.getWidth() - GameBoard.getWidth());
            }
        }
    }

    // TODO create a createLevel method and separate readLevelFile and parseLevel and spawnTiles.
    /**
     * Reads in a file and parses the data for the level.
     * @param src A path to the level wanting to be read.
     */
    public void readLevelFile(String src){
        try {
            File levelFile = new File(src);
            Scanner fileReader = new Scanner(levelFile);
            levelName = src.substring(11);
            System.out.println(levelName);
            String levelDimensions = fileReader.nextLine();
            String[] levelDimensionsSplit = levelDimensions.split(" ");
            levelWidth = Integer.parseInt(levelDimensionsSplit[0]);
            levelHeight = Integer.parseInt(levelDimensionsSplit[1]);

            GameBoard.setHeight(TILE_HEIGHT * levelHeight);
            GameBoard.setWidth(TILE_WIDTH * levelWidth);

            levelGrid = new char[levelHeight][levelWidth];
            for (int row = 0; row < levelHeight; row++) {
                String rowLayout = fileReader.nextLine();
                for (int col = 0; col < levelWidth; col++) {
                    levelGrid[row][col] = rowLayout.charAt(col);
                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

//        GraphicsContext gc = GameBoard.getGraphicsContext2D();

        spawnTiles(GameBoard.getGraphicsContext2D());
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
                    if (levelGrid[row][col] == 'G'){
                        fileWriter.write("G");
                    } else if (levelGrid[row][col] == 'P'){
                        fileWriter.write("P");
                    } else if (levelGrid[row][col] == 'T'){
                        fileWriter.write("T");
                    } else if (levelGrid[row][col] == 'V'){
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

    // TODO Once we have a tiles class we will need to use those instead of hard coding to draw.#

    /**
     * Draws the tiles onto the level.
     * @param gc Takes in the graphics content of what we want to draw on.
     */
    private void spawnTiles(GraphicsContext gc){
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                if (levelGrid[row][col] == 'G'){
                    gc.drawImage(grassTile, col * TILE_HEIGHT, row * TILE_WIDTH);
                } else if (levelGrid[row][col] == 'P'){
                    gc.drawImage(pathTile, col * TILE_HEIGHT, row * TILE_WIDTH);
                } else if (levelGrid[row][col] == 'T'){
                    gc.drawImage(tunnelTile, col * TILE_HEIGHT, row * TILE_WIDTH);
                } else if (levelGrid[row][col] == 'V'){
                    gc.drawImage(tunnelVertTile, col * TILE_HEIGHT, row * TILE_WIDTH);
                } else {
                    System.out.print("Hmm there shouldn't be any other tile????");
                }
            }
        }
    }
}