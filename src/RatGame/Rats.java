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

public class Rats {
    private static final int WINDOW_WIDTH = 40;
    private static final int WINDOW_HEIGHT = 10;
    
    Image maleRat = new Image("Assets/Male.png");
    Image femaleRat = new Image("Assets/Female.png");
    Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
    Random rnd = new Random();

    private int x = rnd.nextInt(2);//male rat number
    private int y = rnd.nextInt(2);//female rat number

    private int playerX = 1;
    private int playerY = 1;

    private int[] maleRats;
    private int[] femaleRats;


    public void drawRat(){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        for(int i=0 ;i<WINDOW_WIDTH; i+=5){
            if(i <= x){
                gc.drawImage(maleRat, playerX,playerY * WINDOW_HEIGHT);
                gc.drawImage(femaleRat, playerX,playerY * WINDOW_WIDTH);
            }
        }
    }
    public void tick() {

        playerX = playerX + 1;
        drawRat();

        if (playerX > 11) {
            for(int i=11; i>0 ; i--)
            playerX = playerX - 1;
            drawRat();
        }
        // We then redraw the whole canvas.

    }
}