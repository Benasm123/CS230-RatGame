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
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Level {
    private static final int GRID_HEIGHT = 50;
    private static final int GRID_WIDTH = 50;

    int levelHeight;
    int levelWidth;

    char[][] levelGrid;

    Image grassTile = new Image("Assets/Grass.png");
    Image pathTile = new Image("Assets/Path.png");
    Image tunnelTile = new Image("Assets/Tunnel.png");
    Image tunnelVertTile = new Image("Assets/TunnelVertical.png");

    @FXML
    private Canvas GameBoard;

    public void initialize(){
    }


    public void switchToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FXML/mainMenu.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    private double lastMouseX;
    private double lastMouseY;

    public void getMousePosOnClicked(MouseEvent event){
        lastMouseX = event.getX();
        lastMouseY = event.getY();
    }

    public void onDragLevel(MouseEvent event){
        System.out.println(GameBoard.getTranslateX() + " " + GameBoard.getTranslateY());
        GameBoard.setTranslateX(GameBoard.getTranslateX() + (event.getX() - lastMouseX));
        GameBoard.setTranslateY(GameBoard.getTranslateY() + (event.getY() - lastMouseY));
    snapBackLevel();
    }

    @FXML
    AnchorPane GameScreen;

    public void snapBackLevel(){
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

    public void readLevelFile(String src){
        try {
            File levelFile = new File(src);
            Scanner fileReader = new Scanner(levelFile);
            String levelDimensions = fileReader.nextLine();
            String[] levelDimensionsSplit = levelDimensions.split(" ");
            levelWidth = Integer.parseInt(levelDimensionsSplit[0]);
            levelHeight = Integer.parseInt(levelDimensionsSplit[1]);

            GameBoard.setHeight(GRID_HEIGHT * levelHeight);
            GameBoard.setWidth(GRID_WIDTH * levelWidth);

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

    public void saveLevel(){
        File levelSaveFile = new File("");
    }

    public void spawnTiles(GraphicsContext gc){
        for (int row = 0; row < levelHeight; row++) {
            for (int col = 0; col < levelWidth; col++) {
                if (levelGrid[row][col] == 'G'){
                    gc.drawImage(grassTile, col * GRID_HEIGHT, row * GRID_WIDTH);
                } else if (levelGrid[row][col] == 'P'){
                    gc.drawImage(pathTile, col * GRID_HEIGHT, row * GRID_WIDTH);
                } else if (levelGrid[row][col] == 'T'){
                    gc.drawImage(tunnelTile, col * GRID_HEIGHT, row * GRID_WIDTH);
                } else if (levelGrid[row][col] == 'V'){
                    gc.drawImage(tunnelVertTile, col * GRID_HEIGHT, row * GRID_WIDTH);
                } else {
                    System.out.print("Hmm there shouldn't be any other tile????");
                    assert true;
                }
            }
        }
    }

}