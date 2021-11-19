package RatGame;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class Rat {
    Image rat;
    ImageView img;

    private float xPos;
    private float yPos;

    private float xVel=5.0f;
    private float yVel=0.0f;

    private int[] maleRats;
    private int[] femaleRats;

    private boolean isDeathRat;
    private char[][] levelGrid;

    int lastX = (int)xPos;
    int lastY = (int)yPos;

    float rotation;

    // TODO: Move function
    public Rat(String type, int xPos, int yPos, boolean isDeathRat){
        this.xPos = xPos;
        this.yPos = yPos;
        this.isDeathRat = isDeathRat;
        if(isDeathRat==true){
            rat = new Image("Assets/Death.png");
        }else if(type.equalsIgnoreCase("male")){
            rat = new Image("Assets/Male.png");
        }else if(type.equalsIgnoreCase("female")){
            rat = new Image("Assets/Female.png");
        }
    }

    private Pair<Integer, Integer> checkPaths(int x, int y, int lastX, int lastY){
        ArrayList<Pair<Integer, Integer>> paths = new ArrayList<>();

        if (levelGrid[x+1][y] != 'G' && x + 1 != lastX) {
            paths.add(new Pair<>(5, 0));
        }
        if (levelGrid[x-1][y] != 'G' && x - 1 != lastX) {
            paths.add(new Pair<>(-5, 0));
        }
        if (levelGrid[x][y+1] != 'G' && y + 1 != lastY) {
            paths.add(new Pair<>(0, 5));
        }
        if (levelGrid[x][y-1] != 'G' && y - 1 != lastY) {
            paths.add(new Pair<>(0, -5));
        }

        if (paths.size() == 0) {
            return new Pair<>((lastX - x)*5, (lastY - y)*5);
        }
        Random rand = new Random();
        return paths.get(rand.nextInt(paths.size()));
    }

    public void move(float deltaTime){
        xPos += xVel * deltaTime;
        yPos += yVel * deltaTime;
        img.setTranslateX(xPos*50);
        img.setTranslateY(yPos*50);
        if (xVel < 0){
            if ((int)xPos+1 != lastX) {
                xPos = (int)xPos+1;
                yPos = (int)yPos;
                Pair<Integer, Integer> vel = checkPaths((int)xPos, (int)yPos, lastX, lastY);
                xVel = vel.getKey();
                yVel = vel.getValue();
                lastX = (int) xPos;
                lastY = (int) yPos;
            }
        }else if (yVel < 0) {
            if ((int)yPos+1 != lastY) {
                xPos = (int)xPos;
                yPos = (int)yPos+1;
                Pair<Integer, Integer> vel = checkPaths((int)xPos, (int)yPos, lastX, lastY);
                xVel = vel.getKey();
                yVel = vel.getValue();
                lastX = (int) xPos;
                lastY = (int) yPos;
                img.setRotate(180.0);
            }
        }else {
            if ((int)xPos != lastX || (int)yPos != lastY) {
                xPos = (int)xPos;
                yPos = (int)yPos;
                Pair<Integer, Integer> vel = checkPaths((int)xPos, (int)yPos, lastX, lastY);
                xVel = vel.getKey();
                yVel = vel.getValue();
                lastX = (int) xPos;
                lastY = (int) yPos;
            }
        }

    }

    public float getRotation() {
        if (xVel < 0) {
            rotation = 90.0f;
        } else if (xVel > 0) {
            rotation = 270.0f;
        } else if (yVel > 0) {
            rotation = 0.0f ;
        } else {
            rotation = 180.0f;
        }
        return rotation;
    }

    public void setRotation(ImageView img){
        img.setRotate(getRotation());
    }

    public void update(float deltaTime){
        this.move(deltaTime);
        this.setRotation(img);
   }


}