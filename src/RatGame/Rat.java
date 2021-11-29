package RatGame;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import java.util.Random;
import java.util.ArrayList;
import java.util.Random;

// General notes: Careful with magic numbers, and make sure almost all variables are private and if we need to use them create getters
// and setters.

public class Rat {
    Image texture;
    ImageView img;

    private float xPos;
    private float yPos;
    private float adultSpeed = 0.6f;
    private float babySpeed = 0.8f;
    float timer = 10.0F;
    float timer2= 5.0f;
    private float movementSpeed = adultSpeed;

    int points=0;

    private float xVel=0.0f;
    private float yVel=0.0f;


    int lastX;
    int lastY;
    boolean isSterile=false;

    private boolean isBaby;
    private boolean isPregnant = false;
    private boolean isGivingBirth = false;
    private float birthTime = 0.0f;
    private float growUpTime = 0.0f;
    private boolean isDead = false;

    Random rnd = new Random();
    int spawnNumber=rnd.nextInt(3)+2;
    int spawns=0;

    private int deathRatKills=0;
    float rotation;
    ratType type;

    /*
    * enum containing the rat types
    * */
    enum ratType{
        MALE, FEMALE, DEATHRAT;
    }

    /*
    * @param type
    * @param xPos
    * @param yPos
    * @param isBaby
    * constructor to initialize the attributes of the rat
    * */
    public Rat(ratType type, int xPos, int yPos, boolean isBaby){
        if(isBaby==true){
            movementSpeed = babySpeed;
        }
        this.isBaby=isBaby;
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
        lastX =-1;
        lastY =-1;


        isPregnant=false;
        isDead=false;

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

     /*
     * @param levelGrid
     * @param x
     * @param y
     * @param lastX
     * @param lastY
     * method checks the paths of the levelGrid to see which tiles are available for the rat to move on
     * */
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

    /*
    * @param deltaTime
    * @param levelGrid
    * method controls the movement of rats
    * */
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
    /*
    * @param img
    * rotates image and returns the angle the image is rotated at
    * */
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

    /*
    * @param deltaTime
    * @levelGrid
    * provides update on the rats
    * */
    public void update(float deltaTime, Tile[][] levelGrid){
        growUpTime += deltaTime;
        this.move(deltaTime, levelGrid);
        this.setGetRotation(img);
        if(growUpTime>=timer){
            growUp();
        }
        timeToBirth(deltaTime);
   }

   /*
   * @rat
   * sets rat sex to Male
   * */
   public void changeSexMale(Rat rat){
        rat.type = ratType.MALE;
        if(rat.isBaby==false) {
            rat.texture = new Image("Assets/Male.png");
            rat.img.setImage(rat.texture);
        }

   }
   /*
   * @rat
   * sets rat sex to Male
   * */
   public void changeSexFemale(Rat rat){
        rat.type = ratType.FEMALE;
        if(rat.isBaby==false){
            rat.texture = new Image("Assets/Female.png");
            rat.img.setImage(rat.texture);
        }
   }

   /*
   * return x coordinate of rat
   * */
   public float getxPos() {
        return xPos;
   }

   /*
   * return y coordinate of rat
   * */
   public float getyPos() {
        return yPos;
   }

    /*
    * return properties of the rats as a string
    * */
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

    /*
    * @param deltaTime
    * timer for pregnant rat to give birth
    * */
    private void timeToBirth(float deltaTime){
        if(spawns==spawnNumber){
            isPregnant=false;
        }
        if (isPregnant==true){
            birthTime += deltaTime;
            if(birthTime>=timer2){
                isGivingBirth = true;
                spawns+=1;
                birthTime=0;
            }
        }
    }

    /*
    * @param deltaTime
    * makes baby rat an adult after some time
    * */
    private void growUp(){
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

    /*
    * @param otherRat
    * determines the actions that happen once a rat steps on or is stepped on by another
    * */
    public void steppedOn(Rat otherRat) {
        if(type == ratType.FEMALE && otherRat.type == Rat.ratType.MALE && isBaby==false && otherRat.isBaby==false){
            isPregnant = true;
        }else if (otherRat.type == ratType.FEMALE && type==ratType.MALE && isBaby==false && otherRat.isBaby==false){
            otherRat.isPregnant = true;
        }else if (type == ratType.DEATHRAT){
            otherRat.isDead=true;
            deathRatKills +=1;
            if(isPregnant == true){
                otherRat.points+=(10*spawnNumber)-(10*spawns);
            }
            points +=10;
            if(deathRatKills==5){
                isDead = true;
            }
        }else if (otherRat.type == ratType.DEATHRAT) {
            isDead = true;
            otherRat.deathRatKills +=1;
            if(isPregnant == true){
                otherRat.points+=(10*spawnNumber)-(10*spawns);
            }
            otherRat.points+=10;
            if(otherRat.deathRatKills==5){
                otherRat.isDead = true;
            }
        }
    }

    public int getPoints(Rat rat){
        return points;
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
    public void setIsSterile(){
        isSterile=true;
    }
}