package RatGame;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

// General notes: Careful with magic numbers, and make sure almost all variables are private and if we need to use them create getters
// and setters.

public class Rat {
    // I think better to call this texture, as Rat.rat isnt going to be clear as what this variable acutally holds.
    Image texture;
    ImageView img;

    private float xPos;
    private float yPos;

    private float xVel=5.0f;
    private float yVel=0.0f;

    // rat doesnt care about what other rats exist, only itself. Dont think as this class as all the rats, but each individual rat.
    // So each instance will only ever care about itself and no other rat (Other than when they need to interact, but well manage that in the level class).
    private int[] maleRats;
    private int[] femaleRats;

    // You want to set this in the constructor as you want hte lastX/Y to be set on where the rat spawns, but if you do it here it will always initialize to 0.
    int lastX;
    int lastY;

    private boolean isBaby;

    float rotation;
    ratType type;
    enum ratType{
        MALE, FEMALE, DEATHRAT;
    }

    public Rat(ratType type, int xPos, int yPos, boolean isBaby){
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
        lastX = (int)xPos;
        lastY = (int)yPos;
        // You can just put isDeathRat, as its a bool it will return either true or false already == true is redundant and messy imo.
        // This looks good, but again id just refactor the rat variable to texture
        // Also it would be a lot better if instead of a string for type you had an enum (like in the Tile class) that way typos wont be a common bug.
        // Then id have ratType.DeathRat or RatType.BabyRat or RatType.AdultRat... and so on
        img = new ImageView();
        if(type == ratType.DEATHRAT && isBaby==false){
            texture = new Image("Assets/Death.png");
            img.setImage(texture);
        }else if(type == ratType.MALE && isBaby==false){
            texture = new Image("Assets/Male.png");
            img.setImage(texture);
        }else if(type==ratType.FEMALE && isBaby==false){
            texture = new Image("Assets/Female.png");
            img.setImage(texture);
        }else {
            texture = new Image("Assets/Baby.png");
            img.setImage(texture);
        }
        img.setTranslateX(xPos*50);
        img.setTranslateY(yPos*50);
    }

    // I know i did but i wouldn't return a pair here, you can directly edit the velocity at the end, and saves memory,
    // so when you do everything instead of returning just directly set the xVel and yVel, tidies it up too,
    // and maybe change the name to something like setDirection, just to be more clear it edits it
    private Pair<Integer, Integer> checkPaths(Tile[][] levelGrid, int x, int y, int lastX, int lastY){
        ArrayList<Pair<Integer, Integer>> paths = new ArrayList<>();

        if (levelGrid[x+1][y].getType().isTraversable && x + 1 != lastX) {
            paths.add(new Pair<>(5, 0));
        }
        if (levelGrid[x-1][y].getType().isTraversable && x - 1 != lastX) {
            paths.add(new Pair<>(-5, 0));
        }
        if (levelGrid[x][y+1].getType().isTraversable && y + 1 != lastY) {
            paths.add(new Pair<>(0, 5));
        }
        if (levelGrid[x][y-1].getType().isTraversable && y - 1 != lastY) {
            paths.add(new Pair<>(0, -5));
        }

        if (paths.size() == 0) {
            return new Pair<>((lastX - x)*5, (lastY - y)*5);
        }
        Random rand = new Random();
        return paths.get(rand.nextInt(paths.size()));
    }

    // Added Level grid as a parameter here and in checkPaths so that i can pass it in to the update method so you have access to the level.
    // This need to be tidied up, lots of repeated code and a pain to work with, i know this is how ive done it but it was
    // for testing purposes.
    public void move(float deltaTime, Tile[][] levelGrid){
        xPos += xVel * deltaTime;
        yPos += yVel * deltaTime;
        img.setTranslateX(xPos*50);
        img.setTranslateY(yPos*50);
        if (xVel < 0){
            if ((int)xPos+1 != lastX) {
                xPos = (int)xPos+1;
                yPos = (int)yPos;
                Pair<Integer, Integer> vel = checkPaths(levelGrid, (int)xPos, (int)yPos, lastX, lastY);
                xVel = vel.getKey();
                yVel = vel.getValue();
                lastX = (int) xPos;
                lastY = (int) yPos;
            }
        }else if (yVel < 0) {
            if ((int)yPos+1 != lastY) {
                xPos = (int)xPos;
                yPos = (int)yPos+1;
                Pair<Integer, Integer> vel = checkPaths(levelGrid, (int)xPos, (int)yPos, lastX, lastY);
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
                Pair<Integer, Integer> vel = checkPaths(levelGrid, (int)xPos, (int)yPos, lastX, lastY);
                xVel = vel.getKey();
                yVel = vel.getValue();
                lastX = (int) xPos;
                lastY = (int) yPos;
            }
        }

    }

    // this can setRotation as well, as this always refers to the current instance of rat you can just set rotation to the return value and not return anything.
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

    // yeah here this is redundant you can just copy the code above and set rotation instead of return and set getRotation to just return this.rotation or however java does that.
    // Oh also just noticed your changing the rotation of the img directly, i would use this to set the variable you have called rotation. Because we might need rotation later for more
    // uses and then when we need to actually draw the image well set the rotation of the rat there.
    public void setRotation(ImageView img){
        img.setRotate(getRotation());
    }

    // Commented out move as right now this is trying ot check a null array.
    public void update(float deltaTime, Tile[][] levelGrid){
        this.move(deltaTime, levelGrid);
        this.setRotation(img);
   }

   public void changeSex(Rat rat){
        if(rat.type == ratType.MALE){
            rat.type = ratType.FEMALE;
            rat.texture = new Image("Assets/Female.png");
            rat.img.setImage(rat.texture);
        }else if(rat.type == ratType.FEMALE){
            rat.type = ratType.MALE;
            rat.texture = new Image("Assets/Male.png");
            rat.img.setImage(rat.texture);
        }
   }

    public float getxPos() {
        return xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public String toString(){
        String stringOfType="";
        String stringOfBaby="";
        String properties ="";
        if(type == ratType.MALE){
            stringOfType += "M";
        }else if(type == ratType.FEMALE){
            stringOfType += "F";
        }else{
            stringOfType += "D";
        }

        if(isBaby){
            stringOfBaby += "T";
        }else{
            stringOfBaby += "F";
        }

        properties += stringOfType+", x:"+ xPos +", y:" + yPos + " ," + stringOfBaby;
        return properties;
    }
}