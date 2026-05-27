package com.svalero.Super_Plumber.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.svalero.Super_Plumber.domain.BlockReward;
import com.svalero.Super_Plumber.domain.Coin;
import com.svalero.Super_Plumber.domain.Enemy;
import com.svalero.Super_Plumber.domain.QuestionBlock;
import com.svalero.Super_Plumber.util.Constants;

//Gestiona la lógica del juego: entrada del jugador, física, colisiones  con terreno, monedas, enemigos y bloques pregunta.

public class LogicManager {

    // Estado del jugador
    private float playerX;
    private float playerY;
    private float playerWidth;
    private float playerHeight;

    private float verticalSpeed;
    private boolean onGround;
    private boolean big;
    private boolean lookingRight;
    private boolean moving;
    private boolean dead;

    private float deathTimer;

    // Power-ups
    private boolean starPower;
    private float starTimer;

    // HUD
    private int coinCount;

    // Hitbox del jugador
    private final Rectangle playerBounds;

    // Referencia al nivel actual
    private LevelManager levelManager;

    // Callback para cambio de nivel
    private Runnable onGoalReached;

    public LogicManager(LevelManager levelManager) {
        this.levelManager = levelManager;
        this.playerBounds = new Rectangle();
        reset();
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public void setOnGoalReached(Runnable callback) {
        this.onGoalReached = callback;
    }

    // Reset / init

    public void reset() {
        playerX = Constants.PLAYER_START_X;
        playerY = Constants.PLAYER_START_Y;
        playerWidth = Constants.PLAYER_SMALL_WIDTH;
        playerHeight = Constants.PLAYER_SMALL_HEIGHT;

        verticalSpeed = 0;
        onGround = false;
        big = false;
        lookingRight = true;
        moving = false;
        dead = false;
        deathTimer = 0;

        starPower = false;
        starTimer = 0;

        playerBounds.set(playerX, playerY, playerWidth, playerHeight);
    }


    public void update(float delta) {
        if (dead) {
            updateDeath(delta);
        } else {
            handleInput(delta);
            applyGravity(delta);
            checkTerrainCollisions();
            checkQuestionBlockCollisions();
            checkCoinCollisions();
            checkEnemyCollisions();
            checkGoalCollision();
            checkPlayerFall();
            updateEnemies(delta);
            updateBlockRewards(delta);
            updateStarPower(delta);
        }
    }


    // Input y física

    private void handleInput(float delta) {
        moving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerX += Constants.PLAYER_SPEED * delta;
            lookingRight = true;
            moving = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerX -= Constants.PLAYER_SPEED * delta;
            lookingRight = false;
            moving = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            verticalSpeed = Constants.PLAYER_JUMP_FORCE;
            onGround = false;
        }
    }

    private void applyGravity(float delta) {
        verticalSpeed += Constants.PLAYER_GRAVITY * delta;
        playerY += verticalSpeed * delta;
        playerBounds.setPosition(playerX, playerY);
        onGround = false;
    }


    // Colisiones con terreno

    private void checkTerrainCollisions() {
        for (Rectangle terrain : levelManager.getTerrainCollisions()) {
            if (playerBounds.overlaps(terrain) && verticalSpeed <= 0) {
                float prevBottom = playerY - verticalSpeed * Gdx.graphics.getDeltaTime();
                if (prevBottom >= terrain.y + terrain.height - 5) {
                    playerY = terrain.y + terrain.height;
                    verticalSpeed = 0;
                    onGround = true;
                    playerBounds.setPosition(playerX, playerY);
                }
            }
        }
    }

    // Bloques pregunta

    private void checkQuestionBlockCollisions() {
        if (verticalSpeed <= 0) return;

        Rectangle head = new Rectangle(
            playerX + 8,
            playerY + playerHeight - 4,
            playerWidth - 16,
            8
        );

        for (QuestionBlock block : levelManager.getQuestionBlocks()) {
            if (block.isUsed()) continue;
            if (head.overlaps(block.getBounds())) {
                block.use();
                verticalSpeed = -150;
                spawnBlockReward(block);
                break;
            }
        }
    }

    private void spawnBlockReward(QuestionBlock block) {
        Rectangle b = block.getBounds();
        switch (block.getType()) {
            case "coin":
                coinCount++;
                levelManager.getBlockRewards().add(new BlockReward(
                    b.x, b.y + b.height, 24, 32, "coin"));
                break;
            case "mushroom":
                levelManager.getBlockRewards().add(new BlockReward(
                    b.x, b.y + b.height, 40, 40, "mushroom"));
                break;
            case "star":
                levelManager.getBlockRewards().add(new BlockReward(
                    b.x, b.y + b.height, 40, 40, "star"));
                break;
        }
    }

    // Monedas y recompensas

    private void checkCoinCollisions() {
        for (Coin coin : levelManager.getCoins()) {
            if (!coin.isCollected() && playerBounds.overlaps(coin.getBounds())) {
                coin.setCollected(true);
                coinCount++;
            }
        }

        Array<BlockReward> rewards = levelManager.getBlockRewards();
        for (int i = rewards.size - 1; i >= 0; i--) {
            BlockReward reward = rewards.get(i);
            if (playerBounds.overlaps(reward.getBounds())) {
                collectReward(reward);
                rewards.removeIndex(i);
            }
        }
    }

    private void collectReward(BlockReward reward) {
        switch (reward.getType()) {
            case "mushroom":
                big = true;
                playerWidth  = Constants.PLAYER_BIG_WIDTH;
                playerHeight = Constants.PLAYER_BIG_HEIGHT;
                playerBounds.setSize(playerWidth, playerHeight);
                break;
            case "star":
                starPower = true;
                starTimer = 8f;
                break;
        }
    }

    // Enemigos

    private void updateEnemies(float delta) {
        for (Enemy enemy : levelManager.getEnemies()) {
            enemy.update(delta, levelManager.getTerrainCollisions());
        }
    }

    private void checkEnemyCollisions() {
        for (Enemy enemy : levelManager.getEnemies()) {
            if (!enemy.isAlive() || enemy.isDying()) continue;

            if (playerBounds.overlaps(enemy.getBounds())) {
                boolean falling = verticalSpeed < 0;
                boolean goomba = enemy.getType().equals("goomba");
                boolean above = playerY > enemy.getBounds().y + enemy.getBounds().height / 2f;

                if (goomba && falling && above) {
                    enemy.die();
                    verticalSpeed = Constants.PLAYER_JUMP_FORCE / 2f;
                } else if (starPower) {
                    enemy.die();
                } else {
                    damagePlayer();
                }
            }
        }
    }

    // Daño y muerte del jugador

    private void damagePlayer() {
        if (big) {
            big = false;
            playerWidth  = Constants.PLAYER_SMALL_WIDTH;
            playerHeight = Constants.PLAYER_SMALL_HEIGHT;
            playerBounds.setSize(playerWidth, playerHeight);
            verticalSpeed = 250;
            moving = false;
        } else {
            killPlayer();
        }
    }

    private void killPlayer() {
        dead = true;
        deathTimer = 0;
        big = false;
        moving = false;
        playerWidth  = Constants.PLAYER_SMALL_WIDTH;
        playerHeight = Constants.PLAYER_SMALL_HEIGHT;
        playerBounds.setSize(playerWidth, playerHeight);
        verticalSpeed = 250;
    }

    private void updateDeath(float delta) {
        deathTimer += delta;
        verticalSpeed += Constants.PLAYER_GRAVITY * delta;
        playerY += verticalSpeed * delta;
        playerBounds.setPosition(playerX, playerY);

        if (deathTimer >= 1.5f) {
            reset();
        }
    }

    // Meta y caída

    private void checkGoalCollision() {
        Rectangle goal = levelManager.getGoalBounds();
        if (goal != null && playerBounds.overlaps(goal)) {
            if (onGoalReached != null) {
                onGoalReached.run();
            }
        }
    }

    private void checkPlayerFall() {
        if (playerY < -200) {
            killPlayer();
        }
    }


    // Recompensas de bloque y star

    private void updateBlockRewards(float delta) {
        Array<BlockReward> rewards = levelManager.getBlockRewards();
        for (int i = rewards.size - 1; i >= 0; i--) {
            BlockReward r = rewards.get(i);
            r.update(delta);
            if (!r.isActive()) rewards.removeIndex(i);
        }
    }

    private void updateStarPower(float delta) {
        if (starPower) {
            starTimer -= delta;
            if (starTimer <= 0) starPower = false;
        }
    }

    // Getters para render y pantalla

    public float getPlayerX()      { return playerX; }
    public float getPlayerY()      { return playerY; }
    public float getPlayerWidth()  { return playerWidth; }
    public float getPlayerHeight() { return playerHeight; }

    public boolean isOnGround()    { return onGround; }
    public boolean isBig()         { return big; }
    public boolean isLookingRight(){ return lookingRight; }
    public boolean isMoving()      { return moving; }
    public boolean isDead()        { return dead; }

    public boolean hasStarPower()  { return starPower; }
    public int getCoinCount()      { return coinCount; }

}
