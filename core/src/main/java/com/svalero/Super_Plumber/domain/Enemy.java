package com.svalero.Super_Plumber.domain;

import com.badlogic.gdx.math.Rectangle;
import com.svalero.Super_Plumber.manager.ConfigurationManager;

public class Enemy {

    private Rectangle bounds;
    private String type;

    private float initialX;
    private float initialY;
    private float speed;

    private boolean movingRight;
    private boolean movingUp;

    private boolean alive;
    private boolean dying;
    private float deathTimer;

    public Enemy(float x, float y, float width, float height, String type) {
        this.bounds = new Rectangle(x, y, width, height);
        this.type = type;

        this.initialX = x;
        this.initialY = y;

        this.speed = ConfigurationManager.isHardMode() ? 120 : 80;

        this.movingRight = true;
        this.movingUp = true;

        this.alive = true;
        this.dying = false;
        this.deathTimer = 0;
    }

    public void update(float delta, com.badlogic.gdx.utils.Array<Rectangle> terrainCollisions) {
        if (!alive) {
            return;
        }

        if (dying) {
            deathTimer += delta;

            if (deathTimer >= 0.4f) {
                alive = false;
            }

            return;
        }

        if (type.equals("goomba")) {
            moveHorizontal(delta);
        }

        if (type.equals("ninji")) {
            moveVertical(delta);
        }

        if (type.equals("planta")) {
            movePlant(delta);
        }
    }

    private void moveHorizontal(float delta) {
        if (movingRight) {
            bounds.x += speed * delta;

            if (bounds.x >= initialX + 25) {
                movingRight = false;
            }
        } else {
            bounds.x -= speed * delta;

            if (bounds.x <= initialX - 25) {
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

    private void movePlant(float delta) {
        float visibleHeight = 56;
        float pauseTime = 1.0f;

        if (movingUp) {
            deathTimer += delta;

            if (deathTimer < pauseTime) {
                return;
            }

            bounds.y += speed * delta;

            if (bounds.y >= initialY + visibleHeight) {
                bounds.y = initialY + visibleHeight;
                movingUp = false;
                deathTimer = 0;
            }
        } else {
            deathTimer += delta;

            if (deathTimer < pauseTime) {
                return;
            }

            bounds.y -= speed * delta;

            if (bounds.y <= initialY) {
                bounds.y = initialY;
                movingUp = true;
                deathTimer = 0;
            }
        }
    }

    public void die() {
        if (type.equals("goomba")) {
            dying = true;
            bounds.height = bounds.height / 2;
        } else {
            alive = false;
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getType() {
        return type;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDying() {
        return dying;
    }
}
