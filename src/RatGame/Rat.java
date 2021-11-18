package RatGame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import java.util.Random;

public class Rat {
    private static final int WINDOW_WIDTH = 40;
    private static final int WINDOW_HEIGHT = 10;

    Image maleRat = new Image("Assets/Male.png");
    Image femaleRat = new Image("Assets/Female.png");
    Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);

    Random rnd = new Random();

    private int x = rnd.nextInt(2);//male rat number
    private int y = rnd.nextInt(2);//female rat number

    private int ratX = 1;
    private int ratY = 1;

    private int[] maleRats;
    private int[] femaleRats;

    // TODO: Constructor
    // TODO: Move function and rotate function
    // TODO: Add an update function.
//    public Rat(type, xPos, yPos, isDeathRat){
//        this.xPos = xPos
//        }

//    public void move(float deltaTime){
//        posX = velX * deltaTime;
//        posY = velY * deltaTime;
//    }
//
//    float rotation;
//
//    public float getRotation() {
//        return rotation;
//    }
//
//    public void update(float deltaTime){
//        this.move(deltaTime);
//        this.rotate();
//    }

    public void draw(){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        for(int i=0 ;i<WINDOW_WIDTH; i+=5){
            if(i <= x){
                gc.drawImage(maleRat, ratX,ratY * WINDOW_HEIGHT);
                gc.drawImage(femaleRat, ratX,ratY * WINDOW_WIDTH);
            }
        }
    }
    public void tick() {

        ratX = ratX + 1;
        draw();

        if (ratX > 11) {
            for(int i=11; i>0 ; i--)
            ratX = ratX - 1;
            draw();
        }
        // We then redraw the whole canvas.

    }
}