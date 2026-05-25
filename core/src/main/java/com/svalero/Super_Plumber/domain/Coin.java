package com.svalero.Super_Plumber.domain;

import com.badlogic.gdx.math.Rectangle;

public class Coin {

    private Rectangle bounds;
    private boolean collected;

    public Coin(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);
        collected = false;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}
