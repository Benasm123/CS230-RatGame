package RatGame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import java.util.Random;
import java.util.ArrayList;


// General notes: Careful with magic numbers, and make sure almost all variables are private and if we need to use them create getters
// and setters.

// All class variables need to be private. some of yours arnt set to anything.
// Need to look at variable names, they need to be meaningful, i see a timer and timer2... might be better to change
// Try to assign things in constructors or if your keeping things constant then make them constants eg:
// adult and babySpeed can be static final, make sure to name all caps like ADULT_SPEED, and BABY_SPEED

// Add a variable for the velocity of the rat, right now in the check paths you have 5 and 0 everywhere, you can keep
// the 0 but make sure the 5 is a variable not a magic number. This will make it easier for us and also its required by
// liam.

// Try to move all initialization into the constructor to stay consistent.

// Youre choosing the number of baby rats to spawn in the class, better to do it when the rat gets pregnant as this will
// add variance and should be in a function really anyway.

// You have stuff like isBaby==true, first this can be simplified to isBaby and would be preferred, and also you need to
// Make sure there's a space on both sides of binary operators.
// Same with !isBaby

// I would move the ratType enum into its own file and out of the rat class, as they are technically classes, and it
// specifies on the conventions that classes need to be in their own files.
// this will cause errors through the code, but if you go through and just remove the Rat. from all the Rat.ratType calls
// Should fix it.

// Check paths can still get rid of most of the paramaters, inlucidng x,y and lastx,y to clean it up.
// Also can set velocity directly instead of returning a pair.

// Once above has been done you can tidy it by having the if statements only changing the x and y, to:
        //       if (xVel < 0){
        //            if ((int)xPos+1 != lastX) {
        //                xPos = (int)xPos+1;
        //                yPos = (int)yPos;
        //            }
        //        } else if (yVel < 0) {
        //            if ((int)yPos+1 != lastY) {
        //                xPos = (int)xPos;
        //                yPos = (int)yPos+1;
        //            }
        //        } else {
        //            if((int)xPos != lastX || (int)yPos != lastY){
        //                xPos = (int)xPos;
        //                yPos = (int)yPos;
        //        }
        //        checkPaths(levelGrid);
        //        lastX = (int) xPos;
        //        lastY = (int) yPos;
    // }
// Formating might be off, but that should work and looks a lot cleaner
// This is still quite long mihgt be worth splitting up into another method if its doign too many things

// You have a method called setGetRotation, and this is confusing and doesnt need to do that, just name it
// rotate() and it doesnt ened to return anything.

// Try go through the method and work out what doesn't need to be called in other classes and set them to private.
// Also, methods need to be ordered in the order:
// First: public
// protected
// then private.
public class Rat {

    private static final int SCORE = 10;

    Image texture;
    ImageView img;

    private float xPos;
    private float yPos;
    private float adultSpeed = 0.6f;
    private float babySpeed = 0.8f;
    float timer = 10.0F;
    float timer2= 5.0f;
    float timer3= 5.0f;
    private float movementSpeed = adultSpeed;

    private float xVel=0.0f;
    private float yVel=0.0f;


    int lastX;
    int lastY;
    boolean isSterile=false;

    private float sexTimer=0.0f;
    private boolean isBaby;
    private boolean havingSex=false;
    private boolean isPregnant = false;
    private boolean isGivingBirth = false;
    private float birthTime = 0.0f;
    private float growUpTime = 0.0f;
    private boolean isDead = false;
    private boolean isPoisoned;
    private float totalTimePoisoned;
    private float timeTillDiesOfPoison;

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

        timeTillDiesOfPoison = 3.0f;
        isPoisoned = false;

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
    * method controls the movement of a rat object
    * */
    public void move(float deltaTime, Tile[][] levelGrid) {

        if(havingSex==true) {
            sexTimer+=deltaTime;
            return;
        }

        xPos += xVel * deltaTime;
        yPos += yVel * deltaTime;


        img.setTranslateX(xPos*50);
        img.setTranslateY(yPos*50);

        if (xVel < 0) {
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
            if((int)xPos != lastX || (int)yPos != lastY) {
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
        }
        else if (xVel > 0) {
            rotation = 270.0f;
        }
        else if (yVel > 0) {
            rotation = 0.0f ;
        }
        else {
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
    public void update(float deltaTime, Tile[][] levelGrid) {
        if(isPoisoned) {
            totalTimePoisoned += deltaTime;
            if(totalTimePoisoned >= timeTillDiesOfPoison) {
                System.out.println("Hello");
                die();
            }
        }
        else {
            totalTimePoisoned -= deltaTime;
            if(totalTimePoisoned < 0) {
                totalTimePoisoned = 0;
            }
        }

        growUpTime += deltaTime;
        this.sex(deltaTime);
        sterile();
        this.setGetRotation(img);
        this.move(deltaTime, levelGrid);
        if(growUpTime>=timer){
            growUp();
        }

        timeToBirth(deltaTime);
   }

   /*
   * @rat
   * sets rat sex to Male
   * */
   public void changeSexMale(Rat rat) {
        rat.type = ratType.MALE;
        if(rat.isBaby==false  && rat.isSterile==false) {
            rat.texture = new Image("Assets/Male.png");
            rat.img.setImage(rat.texture);
        }else if(rat.isBaby==false && rat.isSterile==true){
            rat.texture = new Image("SterileRatFamily/SterileMale.png");
            rat.img.setImage(rat.texture);
        }

   }
   /*
   * @rat
   * sets rat sex to Female
   * */
   public void changeSexFemale(Rat rat) {
        rat.type = ratType.FEMALE;
        if(rat.isBaby==false && rat.isSterile==false) {
            rat.texture = new Image("Assets/Female.png");
            rat.img.setImage(rat.texture);
        }else if(rat.isBaby==false && rat.isSterile==true){
            rat.texture = new Image("SterileRatFamily/SterileFemale.png");
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
        if(type == ratType.MALE) {
            stringOfType += "M";
        }
        else if(type == ratType.FEMALE) {
            stringOfType += "F";
        }
        else {
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

        if (isPregnant==true){
            birthTime += deltaTime;
            if(birthTime>=timer2){
                isGivingBirth = true;
                spawns+=1;
                birthTime=0.0f;
                if(spawns==spawnNumber){
                    isPregnant=false;
                    texture = new Image("Assets/Female.png");
                    img.setImage(texture);
                    spawns=0;
                }
            }
        }
    }
    /*
    *@param deltaTime
    * sets timer for a rat to become pregnant after having sex
    * */
    private void sex(float deltaTime){
        if(havingSex==true && type==ratType.FEMALE){
            sexTimer+=deltaTime;
            if(sexTimer>=timer3){
                havingSex=false;
                isPregnant=true;
                texture = new Image("Assets/FemalePregnant.png");
                img.setImage(texture);
                sexTimer=0;
            }
        }else if(havingSex==true){
            sexTimer+=deltaTime;
            if(sexTimer>=timer3){
                havingSex=false;
                sexTimer=0;
            }
        }

    }
    /*
    * prevents sterile rats from becoming pregnant
    * */
    private void sterile(){
        if(isSterile==true && type==ratType.MALE){
            havingSex=false;
            isPregnant=false;
            texture = new Image("Assets/SterileRatFamily/SterileMale.png");
            img.setImage(texture);
        }else if(isSterile==true && type==ratType.FEMALE){
            havingSex=false;
            img.setImage(texture);
        }
    }

    /*
    * @param deltaTime
    * makes baby rat an adult after some time
    * alters speed of rats to make them slower as adults
    * */
    private void growUp(){
        isBaby = false;
        movementSpeed = adultSpeed;
        if(type == ratType.MALE && isSterile==false){
            texture = new Image("Assets/Male.png");
            img.setImage(texture);
        }else if(type==ratType.FEMALE && isSterile==false){
            texture = new Image("Assets/Female.png");
            img.setImage(texture);
        }else if(type==ratType.FEMALE && isSterile==true){
            texture = new Image("Assets/SterileRatFamily/SterileFemale.png");
            img.setImage(texture);
        }else if(type==ratType.MALE && isSterile==true){
            texture = new Image("Assets/SterileRatFamily/SterileMale.png");
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
            if(havingSex==false && otherRat.havingSex==false && isPregnant==false && isSterile==false && otherRat.isSterile==false){
                otherRat.havingSex=true;
                havingSex=true;
            }

        }else if (otherRat.type == ratType.FEMALE && type==ratType.MALE && isBaby==false && otherRat.isBaby==false){
            if(otherRat.havingSex==false && havingSex==false && otherRat.isPregnant==false && isSterile==false && otherRat.isSterile==false){
                havingSex=true;
                otherRat.havingSex=true;
            }

        }else if (type == ratType.DEATHRAT){
            otherRat.isDead=true;
            deathRatKills +=1;
            if(deathRatKills==5){
                isDead = true;
            }
        }else if (otherRat.type == ratType.DEATHRAT) {
            isDead = true;
            otherRat.deathRatKills +=1;
            if(otherRat.deathRatKills==5){
                otherRat.isDead = true;
            }
        }
    }
    public boolean getIsPoisoned(){
        return isPoisoned;
    }

    public void setIsPoisoned(boolean isPoisoned){
        this.isPoisoned = isPoisoned;
    }

    public int getScore(){
        if (type == ratType.DEATHRAT){
            return 0;
        } else if (type == ratType.MALE) {
            return SCORE;
        } else {
            if (isPregnant) {
                return SCORE + (spawnNumber - spawns) * SCORE;
            } else {
                return SCORE;
            }
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
    public void setIsSterile(){
        isSterile=true;
    }
}