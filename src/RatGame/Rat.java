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
    Image texture;
    ImageView img;

    private float xPos;
    private float yPos;
    private float adultSpeed = 2.0f;
    private float babySpeed = 1.0f;
    private float movementSpeed = adultSpeed;

    private float xVel=5.0f;
    private float yVel=0.0f;


    // You want to set this in the constructor as you want hte lastX/Y to be set on where the rat spawns, but if you do it here it will always initialize to 0.
    int lastX;
    int lastY;

    private boolean isBaby;
    private boolean isPregnant = false;
    private boolean isGivingBirth = false;
    private float birthTime = 0.0f;
    private float growUpTime = 0.0f;
    private boolean isDead = false;

    float rotation;
    ratType type;
    enum ratType{
        MALE, FEMALE, DEATHRAT;
    }

    public Rat(ratType type, int xPos, int yPos, boolean isBaby){
        if(isBaby==true){
            movementSpeed = babySpeed;
        }
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
        lastX = (int)xPos;
        lastY = (int)yPos;

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
                xVel = vel.getKey()*movementSpeed;
                yVel = vel.getValue()*movementSpeed;
                lastX = (int) xPos;
                lastY = (int) yPos;

            }
        }else if (yVel < 0) {
            if ((int)yPos+1 != lastY) {
                xPos = (int)xPos;
                yPos = (int)yPos+1;
                Pair<Integer, Integer> vel = checkPaths(levelGrid, (int)xPos, (int)yPos, lastX, lastY);
                xVel = vel.getKey()*movementSpeed;
                yVel = vel.getValue()*movementSpeed;
                lastX = (int) xPos;
                lastY = (int) yPos;
                img.setRotate(180.0);
            }
        }else {
            if((int)xPos != lastX || (int)yPos != lastY){
                xPos = (int)xPos;
                yPos = (int)yPos;
                Pair<Integer, Integer> vel = checkPaths(levelGrid, (int)xPos, (int)yPos, lastX, lastY);
                xVel = vel.getKey()*movementSpeed;
                yVel = vel.getValue()*movementSpeed;
                lastX = (int) xPos;
                lastY = (int) yPos;
            }

        }


    }

    public float setGetRotation(ImageView img) {
        if (xVel < 0) {
            rotation = 90.0f;
        } else if (xVel > 0) {
            rotation = 270.0f;
        } else if (yVel > 0) {
            rotation = 0.0f ;
        } else {
            rotation = 180.0f;
        }
        img.setRotate(rotation);
        return rotation;
    }

    // Commented out move as right now this is trying ot check a null array.
    public void update(float deltaTime, Tile[][] levelGrid){
        this.move(deltaTime, levelGrid);
        this.setGetRotation(img);
        if(isBaby==true){
            growUp(deltaTime);
        }
   }

   public void changeSexMale(Rat rat){
        rat.type = ratType.MALE;
        rat.texture = new Image("Assets/Male.png");
        rat.img.setImage(rat.texture);
   }
    public void changeSexFemale(Rat rat){
        rat.type = ratType.FEMALE;
        rat.texture = new Image("Assets/Female.png");
        rat.img.setImage(rat.texture);
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

        properties += stringOfType + " " + (int)xPos + " " + (int)yPos + " " + stringOfBaby;
        return properties;
    }

    private void timeToBirth(float deltaTime){
        if (isPregnant==true){
            birthTime += deltaTime;
            isGivingBirth = true;
            birthTime += deltaTime;
        }
    }
    private void growUp(float deltaTime){
        growUpTime += deltaTime;
        isBaby = false;
        movementSpeed = adultSpeed;
        if(type == ratType.MALE){
            texture = new Image("Assets/Male.png");
            img.setImage(texture);
        }else if(type==ratType.FEMALE){
            texture = new Image("Assets/Female.png");
            img.setImage(texture);
        }
    }

    public boolean getIsGivingBirth(){
        return isGivingBirth;
    }

    public void setIsGivingBirth(){
        isGivingBirth = false;
    }

    public boolean getIsDead(){
        return isDead;
    }

    public void die(){
        this.isDead = true;
    }

    public void steppedOn(Rat otherRat) {
        if(type == ratType.FEMALE && otherRat.type == Rat.ratType.MALE){
            isPregnant = true;
        }else if (otherRat.type == ratType.FEMALE && type==ratType.MALE){
            otherRat.isPregnant = true;
        }else if (type == ratType.DEATHRAT){
            otherRat.isDead=true;
        }
    }

    public float getxVel() {
        return xVel;
    }

    public float getyVel() {
        return yVel;
    }

    public void setxVel(float xVel)
	{
		this.xVel = xVel;
	}

	public void setyVel(float yVel)
	{
		this.yVel = yVel;
	}

	public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }
}