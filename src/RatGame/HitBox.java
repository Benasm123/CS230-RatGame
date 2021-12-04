package RatGame;

import java.util.ArrayList;

/**
 * HitBox class which handles collisions between all game objects.
 * @author Benas Montrimas.
 */
public class HitBox {

    // Variables
    private float xPos;
    private float yPos;
    private final float width;
    private final float height;
    private final ArrayList<HitBox> collidingWith;

    /**
     * Constructor for the hitbox.
     * @param xPos The x position of the hitbox.
     * @param yPos The y position of the hitbox.
     * @param width The width of the hitbox.
     * @param height The height of the hitbox.
     */
    public HitBox(float xPos, float yPos, float width, float height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        collidingWith = new ArrayList<>();
    }

    /**
     * Returns true if the hitbox has started a collision with a hitbox, but not if they are already colliding.
     * @param other The other hitbox being checked for collisions with.
     * @return Returns true if the hitboxes have begun a collision.
     */
    public boolean beginCollide(HitBox other) {
        // Check whether the two hitboxes are overlapping.
        if ((this.xPos >= other.xPos && this.xPos <= other.xPos + other.width &&
            this.yPos >= other.yPos && this.yPos <= other.yPos + other.height) ||
            (this.xPos + this.width >= other.xPos && this.xPos + this.width <= other.xPos + other.width &&
            this.yPos + this.height >= other.yPos && this.yPos + this.height <= other.yPos + other.height)) {
            // Returns false if this item is already collided before
            if (collidingWith.contains(other)) {
                return false;
            }
            collidingWith.add(other);
            return true;
        }

        collidingWith.remove(other);
        return false;
    }

    /**
     * Gets whether the hitbox is currently colliding with the other hitbox.
     * @param other The other hitbox being checked for collisions with.
     * @return Returns true if the hitboxes are currently colliding, else false.
     */
    public boolean isColliding(HitBox other) {
        return collidingWith.contains(other);
    }

    /**
     * Sets the x and y positions of the hitbox.
     * @param x The new x positions to set the x position to.
     * @param y The new y positions to set the y position to.
     */
    public void setPos (float x, float y) {
        this.xPos = x;
        this.yPos = y;
    }

    /**
     * Gets the hitboxes y position.
     * @return Returns the y position.
     */
    public float getyPos() {
        return yPos;
    }

    /**
     * Gets the hitboxes x position.
     * @return Returns the x position.
     */
    public float getxPos() {
        return xPos;
    }
}
