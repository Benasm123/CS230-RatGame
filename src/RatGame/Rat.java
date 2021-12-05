package RatGame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import java.util.Random;
import java.util.ArrayList;

/**
 * this class controls the whole functionality of the rats
 * @author Oluwatimilehin Abajingin, Benas Montrimas
 * @version 1.0
 */
public class Rat {

    // Constants
    private static final int SCORE = 10;
    private static final float TIME_TILL_DIES_OF_POISON = 2.0f;
    private static final float POISON_RECOVERY_RATE = 0.1f;
    private static final float ADULT_SPEED_MULTIPLIER = 0.6f;
    private static final float BABY_SPEED_MULTIPLIER = 0.8f;
    private static final int MOVEMENT_SPEED = 5;
    private static final float TIME_TO_GROW_UP = 10.0f;
    private static final float TIME_TILL_BIRTH = 5.0f;
    private static final float TIME_TILL_STOP_SEX = 5.0f;
    private static final int MAX_DEATH_RAT_KILLS = 5;
    private static final float HITBOX_OFFSET = 0.3f;
    private static final float HITBOX_WIDTH_HEIGHT = 1.0f - HITBOX_OFFSET * 2;

    // Texture paths
    private static final String BABY_TEXTURE_PATH = "Assets/Baby.png";
    private static final String MALE_TEXTURE_PATH = "Assets/Male.png";
    private static final String FEMALE_TEXTURE_PATH = "Assets/Female.png";
    private static final String DEATH_RAT_TEXTURE_PATH = "Assets/Death.png";
    private static final String STERILE_MALE_TEXTURE_PATH = "Assets/SterileRatFamily/SterileMale.png";
    private static final String STERILE_FEMALE_TEXTURE_PATH = "Assets/SterileRatFamily/SterileFemale.png";
    private static final String FEMALE_PREGNANT_TEXTURE_PATH = "Assets/FemalePregnant.png";


    // Variables
    private Image texture;
    private final ImageView imageView;
    private final HitBox hitBox;

    private float xPos;
    private float yPos;
    private float movementSpeedMultiplier;

    private float xVel;
    private float yVel;


    private int lastX;
    private int lastY;
    private boolean isSterile = false;

    private float sexTimer;
    private boolean isBaby;
    private boolean havingSex;
    private boolean isPregnant;
    private boolean isGivingBirth;
    private float birthTime;
    private float growUpTime;
    private boolean isDead;
    private float totalTimePoisoned;

    private int spawnNumber;
    private int spawns;

    private int deathRatKills;
    private RatType type;

    /**
     * Constructor to initialize the attributes of the rat.
     * @param type The type of the rat.
     * @param xPos The rats x position.
     * @param yPos The rats y position.
     * @param isBaby If the rat is a baby.
     */
    public Rat(RatType type, int xPos, int yPos, boolean isBaby){

        this.isBaby = isBaby;
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;

        havingSex = false;
        isPregnant = false;
        isGivingBirth = false;
        isDead = false;

        if (isBaby) {
            movementSpeedMultiplier = BABY_SPEED_MULTIPLIER;
        } else {
            movementSpeedMultiplier = ADULT_SPEED_MULTIPLIER;
        }

        // Initialize last position to something impossible so rat chooses random direction on start.
        lastX = -1;
        lastY = -1;

        hitBox = new HitBox(xPos + HITBOX_OFFSET, yPos + HITBOX_OFFSET, HITBOX_WIDTH_HEIGHT, HITBOX_WIDTH_HEIGHT);

        imageView = new ImageView();
        if (type == RatType.DEATH_RAT && !isBaby) {
            texture = new Image(DEATH_RAT_TEXTURE_PATH);
            imageView.setImage(texture);
        } else if (type == RatType.MALE && !isBaby) {
            texture = new Image(MALE_TEXTURE_PATH);
            imageView.setImage(texture);
        } else if (type == RatType.FEMALE && !isBaby) {
            texture = new Image(FEMALE_TEXTURE_PATH);
            imageView.setImage(texture);
        } else {
            texture = new Image(BABY_TEXTURE_PATH);
            imageView.setImage(texture);
        }
        imageView.setTranslateX(xPos * Tile.TILE_WIDTH);
        imageView.setTranslateY(yPos * Tile.TILE_HEIGHT);
    }

    /**
     * Gets the rats image view.
     * @return imageView which is just the image
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * method controls the movement of a rat object
     * @param deltaTime Time since last frame.
     * @param levelGrid The level grid the rat is on.
     */
    public void move(float deltaTime, Tile[][] levelGrid) {
        if (havingSex) {
            return;
        }

        xPos += xVel * deltaTime;
        yPos += yVel * deltaTime;

        imageView.setTranslateX(xPos * Tile.TILE_WIDTH);
        imageView.setTranslateY(yPos * Tile.TILE_HEIGHT);

        if (xVel < 0) {
            if ((int) xPos + 1 != lastX) {
                xPos = (int) xPos + 1;
                yPos = (int) yPos;
                checkPaths(levelGrid);
                lastX = (int) xPos;
                lastY = (int) yPos;
            }
        } else if (yVel < 0) {
            if ((int) yPos + 1 != lastY) {
                xPos = (int) xPos;
                yPos = (int) yPos + 1;
                checkPaths(levelGrid);
                lastX = (int) xPos;
                lastY = (int) yPos;
            }
        } else {
            if ((int) xPos != lastX || (int) yPos != lastY) {
                xPos = (int) xPos;
                yPos = (int) yPos;
                checkPaths(levelGrid);
                lastX = (int) xPos;
                lastY = (int) yPos;
            }
        }
    }

    /**
     *
     * @return the type of rat
     */
    public RatType getType() {
        return type;
    }

    /**
    * rotates image and returns the angle the image is rotated at
    * */
    public void rotate() {
        float rotation;
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
        imageView.setRotate(rotation);
    }

    /**
     * Updates the rat every frame.
     * @param deltaTime The time since the last frame
     * @param levelGrid The level grid of the level the rat is currently on.
     */
    public void update(float deltaTime, Tile[][] levelGrid) {
        if (totalTimePoisoned >= TIME_TILL_DIES_OF_POISON) {
            die();
        }

        totalTimePoisoned -= POISON_RECOVERY_RATE * deltaTime;
        if (totalTimePoisoned < 0) {
            totalTimePoisoned = 0;
        }

        growUpTime += deltaTime;
        if (growUpTime >= TIME_TO_GROW_UP) {
            growUp();
        }
        sex(deltaTime);
        sterile();
        rotate();
        move(deltaTime, levelGrid);
        updateHitBox();
        timeToBirth(deltaTime);
   }

    /**
     * updates the rats hitbox.
     */
    public void updateHitBox() {
        this.hitBox.setPos(this.xPos + HITBOX_OFFSET, this.yPos + HITBOX_OFFSET);
   }

    /**
     * Changes the sex of the rat to male.
     */
    public void changeSexMale() {
        type = RatType.MALE;
        if (!isBaby && !isSterile) {
            texture = new Image(MALE_TEXTURE_PATH);
            imageView.setImage(texture);
        } else if (!isBaby) {
            texture = new Image(STERILE_MALE_TEXTURE_PATH);
            imageView.setImage(texture);
        }

   }

    /**
     * Changes the sex of the rat to female.
     */
    public void changeSexFemale() {
        type = RatType.FEMALE;
        if (!isBaby && !isSterile) {
            texture = new Image(FEMALE_TEXTURE_PATH);
            imageView.setImage(texture);
        } else if (!isBaby) {
            texture = new Image(STERILE_FEMALE_TEXTURE_PATH);
            imageView.setImage(texture);
        }
   }

    /**
    * return x coordinate of rat
    * */
    public float getXPos() {
        return xPos;
   }

    /**
    * return y coordinate of rat
    * */
    public float getYPos() {
        return yPos;
   }

    /**
    * return properties of the rats as a string
    * */
    public String toString() {
        String stringOfType="";
        String stringOfBaby="";
        String properties ="";

        if (type == RatType.MALE) {
            stringOfType += "M";
        } else if (type == RatType.FEMALE) {
            stringOfType += "F";
        } else {
            stringOfType += "D";
        }

        if (isBaby) {
            stringOfBaby += "T";
        } else {
            stringOfBaby += "F";
        }

        properties += stringOfType + " " + (int)xPos + " " + (int)yPos + " " + stringOfBaby + " " +
                (int) xVel + " " + (int) yVel + " " + isSterile + " " + isPregnant + " " + isGivingBirth + " " +
                growUpTime + " " + isDead + " " + totalTimePoisoned + " " + spawnNumber + " " +
                spawns + " " + deathRatKills + " " + lastX + " " + lastY;

        return properties;
    }

    /**
     * sets isPregnant boolean to the parameter passed in.
     * @param pregnant The value to set isPregnant to.
     */
    public void setPregnant(boolean pregnant) {
        isPregnant = pregnant;
    }

    /**
     * sets isGivingBirth boolean to the parameter passed in.
     * @param givingBirth The value ot set isGivingBirth to.
     */
    public void setGivingBirth(boolean givingBirth) {
        isGivingBirth = givingBirth;
    }

    /**
     * sets growUpTime variable to the parameter passed in.
     * @param growUpTime The value ot set growUpTime to.
     */
    public void setGrowUpTime(float growUpTime) {
        this.growUpTime = growUpTime;
    }

    /**
     * sets isDead boolean to the parameter passed in.
     * @param dead The value to set isDead to.
     */
    public void setDead(boolean dead) {
        isDead = dead;
    }

    /**
     * sets totalTimePoisoned variable to the parameter passed in.
     * @param totalTimePoisoned The value to set totalTimePoisoned to.
     */
    public void setTotalTimePoisoned(float totalTimePoisoned) {
        this.totalTimePoisoned = totalTimePoisoned;
    }

    /**
     * sets spawnNumber variable to the parameter passed in.
     * @param spawnNumber The value ot set spawn number to.
     */
    public void setSpawnNumber(int spawnNumber) {
        this.spawnNumber = spawnNumber;
    }

    /**
     * sets spawns variable to the parameter passed in.
     * @param spawns The value to set spawns to.
     */
    public void setSpawns(int spawns) {
        this.spawns = spawns;
    }

    /**
     * sets deathRatKills variable to the parameter passed in.
     * @param deathRatKills The value to set the death rat kills to.
     */
    public void setDeathRatKills(int deathRatKills) {
        this.deathRatKills = deathRatKills;
    }

    /**
     * sets lastX variable to the parameter passed in.
     * @param lastX The value to set the last x coordinate to.
     */
    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    /**
     * sets lastY variable to the parameter passed in.
     * @param lastY The value to set the last y coordinate to.
     */
    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    /**
    * makes baby rat an adult after some time.
    * alters speed of rats to make them slower when they're adults.
    * */
    private void growUp() {
        isBaby = false;
        movementSpeedMultiplier = ADULT_SPEED_MULTIPLIER;
        if (type == RatType.MALE && !isSterile) {
            texture = new Image(MALE_TEXTURE_PATH);
            imageView.setImage(texture);
        } else if (type == RatType.FEMALE && !isSterile) {
            texture = new Image(FEMALE_TEXTURE_PATH);
            imageView.setImage(texture);
        } else if (type == RatType.FEMALE) {
            texture = new Image(STERILE_FEMALE_TEXTURE_PATH);
            imageView.setImage(texture);
        } else if (type == RatType.MALE) {
            texture = new Image(STERILE_MALE_TEXTURE_PATH);
            imageView.setImage(texture);
        }
    }

    /**
     * Get if the rat is giving birth.
     * @return True if the rat is giving birth.
     */
    public boolean getIsGivingBirth() {
        return isGivingBirth;
    }

    /**
     * sets the isGivingBirth boolean into the parameter passed in.
     */
    public void setIsGivingBirth() {
        isGivingBirth = false;
    }

    /**
     * Get if the rat is dead.
     * @return True if the rat is dead.
     */
    public boolean getIsDead() {
        return isDead;
    }

    /**
     * Sets the rat as dead.
     */
    public void die() {
        this.isDead = true;
    }

    /**
     * Decides what happens when a rat steps on another rat.
     * @param otherRat The rat being stepped on.
     */
    public void steppedOn(Rat otherRat) {
        if (type == RatType.FEMALE && otherRat.type == RatType.MALE && !isBaby && !otherRat.isBaby) {
            if(!havingSex && !otherRat.havingSex && !isPregnant && !isSterile && !otherRat.isSterile) {
                otherRat.havingSex = true;
                havingSex = true;
            }

        } else if (otherRat.type == RatType.FEMALE && type == RatType.MALE && !isBaby && !otherRat.isBaby) {
            if (!otherRat.havingSex && !havingSex && !otherRat.isPregnant && !isSterile && !otherRat.isSterile) {
                havingSex=true;
                otherRat.havingSex=true;
            }

        } else if (type == RatType.DEATH_RAT) {
            if (otherRat.type == RatType.DEATH_RAT) {
                return;
            }
            otherRat.isDead = true;
            deathRatKills += 1;
            if (deathRatKills == MAX_DEATH_RAT_KILLS) {
                isDead = true;
            }
        }
    }

    /**
     * increment the totalTimePoisoned to the float passed in.
     * @param amount The amount of damage to poison the rat.
     */
    public void poison(float amount) {
        totalTimePoisoned += amount;
    }

    /**
     * Get the rats score, counting if they're pregnant.
     * @return the score value of the rat.
     */
    public int getScore() {
        if (type == RatType.DEATH_RAT) {
            return 0;
        } else if (type == RatType.MALE) {
            return SCORE;
        } else {
            if (isPregnant) {
                return SCORE + (spawnNumber - spawns) * SCORE;
            } else {
                return SCORE;
            }
        }
    }

    /**
     * Turns the rat to move in the opposite direction.
     */
    public void turnAround() {
        if (xVel < 0 || yVel < 0) {
            lastX = (int) xPos;
            lastY = (int) yPos;
        } else if (yVel > 0){
            lastX = (int) xPos;
            lastY = (int) yPos + 1;
        } else if (xVel > 0) {
            lastX = (int) xPos + 1;
            lastY = (int) yPos;
        }
        xVel *= -1;
        yVel *= -1;
    }

    /**
     * Gets the x velocity of the rat.
     * @return The x velocity of the rat.
     */
    public float getXVel() {
        return xVel;
    }

    /**
     * Gets the hitbox of the rat.
     * @return The hitbox of the rat.
     */
    public HitBox getHitBox() {
        return hitBox;
    }

    /**
     * sets xVel variable equal to the float parameter passed in.
     * @param xVel The x velocity to set xVel to.
     */
    public void setXVel(float xVel) {
		this.xVel = xVel;
	}

    /**
     * sets yVel variable equal to the float parameter passed in.
     * @param yVel The value to set yVel to.
     */
	public void setYVel(float yVel) {
		this.yVel = yVel;
	}

    /**
     * sets isSterile boolean to true.
     */
    public void setIsSterile() {
        isSterile = true;
    }

    /**
     * @param levelGrid
     * method checks the paths of the levelGrid to see which tiles are available for the rat to move on
     * */
    private void checkPaths(Tile[][] levelGrid) {
        ArrayList<Pair<Integer, Integer>> paths = new ArrayList<>();

        int x = (int) xPos;
        int y = (int) yPos;

        if (levelGrid[x+1][y].getType().isTraversable && x + 1 != lastX) {
            paths.add(new Pair<>(MOVEMENT_SPEED, 0));
        }
        if (levelGrid[x-1][y].getType().isTraversable && x - 1 != lastX) {
            paths.add(new Pair<>(-MOVEMENT_SPEED, 0));
        }
        if (levelGrid[x][y+1].getType().isTraversable && y + 1 != lastY) {
            paths.add(new Pair<>(0, MOVEMENT_SPEED));
        }
        if (levelGrid[x][y-1].getType().isTraversable && y - 1 != lastY) {
            paths.add(new Pair<>(0, -MOVEMENT_SPEED));
        }

        Pair<Integer, Integer> direction;

        if (paths.size() == 0) {
            direction = new Pair<>((lastX - x) * MOVEMENT_SPEED, (lastY - y) * MOVEMENT_SPEED);
        } else {
            Random rand = new Random();
            direction = paths.get(rand.nextInt(paths.size()));
        }

        xVel = direction.getKey() * movementSpeedMultiplier;
        yVel = direction.getValue() * movementSpeedMultiplier;
    }

    /**
     * Updates the time to get birth and set rat as giving birth if past timer.
     * @param deltaTime The time since the last frame.
     */
    private void timeToBirth(float deltaTime) {
        if (isPregnant) {
            texture = new Image(FEMALE_PREGNANT_TEXTURE_PATH);
            imageView.setImage(texture);
            birthTime += deltaTime;
            if (birthTime >= TIME_TILL_BIRTH) {
                isGivingBirth = true;
                spawns += 1;
                birthTime = 0.0f;
                if (spawns==spawnNumber) {
                    isPregnant=false;
                    texture = new Image(FEMALE_TEXTURE_PATH);
                    imageView.setImage(texture);
                    spawns = 0;
                }
            }
        }
    }

    /**
     * Checks whether the rat is having sex and if it's finished.
     * @param deltaTime The time since the last frame.
     */
    private void sex(float deltaTime) {
        if (havingSex && type == RatType.FEMALE) {
            sexTimer += deltaTime;
            if (sexTimer >= TIME_TILL_STOP_SEX) {
                Random rnd = new Random();
                spawnNumber = rnd.nextInt(3) + 2;
                havingSex = false;
                isPregnant = true;
                sexTimer = 0;
            }
        } else if (havingSex) {
            sexTimer += deltaTime;
            if (sexTimer >= TIME_TILL_STOP_SEX) {
                havingSex = false;
                sexTimer = 0;
            }
        }
    }

    /**
     * prevents sterile rats from becoming pregnant.
     * */
    private void sterile() {
        if (isSterile && type == RatType.MALE) {
            havingSex = false;
            isPregnant = false;
        } else if (isSterile && type == RatType.FEMALE) {
            havingSex = false;
        }
    }
}