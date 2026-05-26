package com.svalero.Super_Plumber.domain;

import com.badlogic.gdx.math.Rectangle;

public class QuestionBlock {

    private Rectangle bounds;
    private String type;
    private boolean used;

    public QuestionBlock(float x, float y, float width, float height, String type) {
        this.bounds = new Rectangle(x, y, width, height);
        this.type = type;
        this.used = false;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getType() {
        return type;
    }

    public boolean isUsed() {
        return used;
    }

    public void use() {
        used = true;
    }
}
