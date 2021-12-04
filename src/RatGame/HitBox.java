package RatGame;

import java.util.ArrayList;

public class HitBox {
    private float xPos;
    private float yPos;
    private final float width;
    private final float height;
    private final ArrayList<HitBox> collidingWith;

    public HitBox(float xPos, float yPos, float width, float height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        collidingWith = new ArrayList<>();
    }

    public boolean beginCollide(HitBox other) {
        if ((this.xPos >= other.xPos && this.xPos <= other.xPos + other.width &&
            this.yPos >= other.yPos && this.yPos <= other.yPos + other.height) ||
            (this.xPos + this.width >= other.xPos && this.xPos + this.width <= other.xPos + other.width &&
            this.yPos + this.height >= other.yPos && this.yPos + this.height <= other.yPos + other.height)) {
            if (collidingWith.contains(other)) {
                return false;
            }
            collidingWith.add(other);
            return true;
        }

        collidingWith.remove(other);
        return false;
    }

    public boolean isColliding(HitBox other) {
        return collidingWith.contains(other);
    }

    public void setPos (float x, float y) {
        this.xPos = x;
        this.yPos = y;
    }

    public float getyPos() {
        return yPos;
    }

    public float getxPos() {
        return xPos;
    }
}
