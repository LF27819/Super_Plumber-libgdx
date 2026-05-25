package com.svalero.Super_Plumber.domain;

import com.badlogic.gdx.math.Rectangle;

public class Enemy {

    private Rectangle bounds;
    private String type;

    private float initialX;
    private float initialY;
    private float speed;

    private boolean movingRight;
    private boolean movingUp;

    public Enemy(float x, float y, float width, float height, String type) {
        this.bounds = new Rectangle(x, y, width, height);
        this.type = type;

        this.initialX = x;
        this.initialY = y;

        this.speed = 80;
        this.movingRight = true;
        this.movingUp = true;
    }

    public void update(float delta) {
        if (type.equals("goomba")) {
            moveHorizontal(delta);
        }

        if (type.equals("ninji")) {
            moveVertical(delta);
        }
    }

    private void moveHorizontal(float delta) {
        if (movingRight) {
            bounds.x += speed * delta;

            if (bounds.x >= initialX + 120) {
                movingRight = false;
            }
        } else {
            bounds.x -= speed * delta;

            if (bounds.x <= initialX - 120) {
                movingRight = true;
            }
        }
    }

    private void moveVertical(float delta) {
        if (movingUp) {
            bounds.y += speed * delta;

            if (bounds.y >= initialY + 70) {
                movingUp = false;
            }
        } else {
            bounds.y -= speed * delta;

            if (bounds.y <= initialY) {
                movingUp = true;
            }
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getType() {
        return type;
    }
}
