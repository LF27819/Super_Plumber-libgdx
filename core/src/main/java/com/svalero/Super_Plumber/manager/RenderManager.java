package com.svalero.Super_Plumber.manager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.svalero.Super_Plumber.domain.BlockReward;
import com.svalero.Super_Plumber.domain.Coin;
import com.svalero.Super_Plumber.domain.Enemy;

/**
 * Gestiona el renderizado de todas las entidades del juego:
 * monedas, recompensas de bloque, enemigos y el jugador.
 * No contiene lógica de juego, solo dibujo.
 */
public class RenderManager {

    private final ResourceManager resources;
    private final LevelManager levelManager;
    private final LogicManager logicManager;

    // Tamaño fijo de la planta piranha para centrarla en su tubo
    private static final float PLANT_DRAW_WIDTH  = 48f;
    private static final float PLANT_DRAW_HEIGHT = 64f;

    public RenderManager(ResourceManager resources,
                         LevelManager levelManager,
                         LogicManager logicManager) {
        this.resources    = resources;
        this.levelManager = levelManager;
        this.logicManager = logicManager;
    }


    // Entidades

    public void drawCoins(SpriteBatch batch) {
        for (Coin coin : levelManager.getCoins()) {
            if (!coin.isCollected()) {
                Rectangle b = coin.getBounds();
                batch.draw(resources.coin, b.x, b.y, b.width, b.height);
            }
        }
    }

    public void drawBlockRewards(SpriteBatch batch) {
        for (BlockReward reward : levelManager.getBlockRewards()) {
            Rectangle b = reward.getBounds();
            Texture tex = getRewardTexture(reward.getType());
            if (tex != null) {
                batch.draw(tex, b.x, b.y, b.width, b.height);
            }
        }
    }

    private Texture getRewardTexture(String type) {
        switch (type) {
            case "coin":     return resources.coinBlock;
            case "mushroom": return resources.mushroom;
            case "star":     return resources.star;
            default:         return null;
        }
    }

    public void drawEnemies(SpriteBatch batch) {
        for (Enemy enemy : levelManager.getEnemies()) {
            if (!enemy.isAlive()) continue;

            Rectangle b = enemy.getBounds();

            switch (enemy.getType()) {
                case "planta":
                    float drawX = b.x + (b.width - PLANT_DRAW_WIDTH) / 2f + 15;
                    batch.draw(resources.plant, drawX, b.y, PLANT_DRAW_WIDTH, PLANT_DRAW_HEIGHT);
                    break;
                case "ninji":
                    batch.draw(resources.ninji, b.x, b.y, b.width, b.height);
                    break;
                default: // goomba
                    Texture tex = enemy.isDying() ? resources.goombaDead : resources.goomba;
                    batch.draw(tex, b.x, b.y, b.width, b.height);
                    break;
            }
        }
    }


    // Jugador

    public void drawPlayer(SpriteBatch batch, float animationTimer) {
        Texture tex = resolvePlayerTexture(animationTimer);
        batch.draw(tex,
            logicManager.getPlayerX(),
            logicManager.getPlayerY(),
            logicManager.getPlayerWidth(),
            logicManager.getPlayerHeight());
    }

    private Texture resolvePlayerTexture(float animationTimer) {
        if (logicManager.isDead()) {
            return resources.marioDead;
        }

        if (!logicManager.isOnGround()) {
            return resolveJumpTexture();
        }

        if (logicManager.isMoving()) {
            return resolveWalkTexture(animationTimer);
        }

        return resolveIdleTexture();
    }

    private Texture resolveJumpTexture() {
        boolean right = logicManager.isLookingRight();
        return logicManager.isBig()
            ? (right ? resources.marioBigJumpRight : resources.marioBigJumpLeft)
            : (right ? resources.marioJumpRight    : resources.marioJumpLeft);
    }

    private Texture resolveWalkTexture(float animationTimer) {
        boolean right = logicManager.isLookingRight();
        boolean alt   = ((int) (animationTimer * 8)) % 2 == 0;

        if (logicManager.isBig()) {
            if (right) return alt ? resources.marioBigWalkRight : resources.marioBigRunRight;
            else       return alt ? resources.marioBigWalkLeft  : resources.marioBigRunLeft;
        } else {
            if (right) return alt ? resources.marioWalkRight : resources.marioRunRight;
            else       return alt ? resources.marioWalkLeft  : resources.marioRunLeft;
        }
    }

    private Texture resolveIdleTexture() {
        boolean right = logicManager.isLookingRight();
        return logicManager.isBig()
            ? (right ? resources.marioBigRight : resources.marioBigLeft)
            : (right ? resources.marioRight    : resources.marioLeft);
    }
}
