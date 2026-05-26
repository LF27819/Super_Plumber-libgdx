package com.svalero.Super_Plumber.domain;

import com.badlogic.gdx.math.Rectangle;

public class BlockReward {

    private Rectangle bounds;
    private String type;
    private float initialY;
    private boolean active;

    public BlockReward(float x, float y, float width, float height, String type) {
        this.bounds = new Rectangle(x, y, width, height);
        this.type = type;
        this.initialY = y;
        this.active = true;
    }

    public void update(float delta) {
        if (type.equals("coin")) {
            bounds.y += 260 * delta;

            if (bounds.y >= initialY + 28) {
                active = false;
            }
        }

        if (type.equals("mushroom")) {
            // De momento la seta se queda quieta para poder cogerla
        }

        if (type.equals("star")) {
            // quieta de momento
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }
}
