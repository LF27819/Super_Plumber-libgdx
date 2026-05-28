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

// Gestiona la lógica del juego: movimiento, físicas,colisiones, enemigos, monedas y power-ups.

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

    // Audio y musica
    private AudioManager audioManager;

    //Termino nivel y espero
    private boolean levelCompleted;
    private float victoryTimer;

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

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    private void playSound(String soundName) {
        if (audioManager == null) return;

        switch (soundName) {
            case "jump":
                audioManager.playJump();
                break;
            case "coin":
                audioManager.playCoin();
                break;
            case "powerup":
                audioManager.playPowerup();
                break;
            case "stomp":
                audioManager.playStomp();
                break;
            case "hit":
                audioManager.playHit();
                break;
            case "death":
                audioManager.playDeath();
                break;
        }
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

        playerBounds.set(
            playerX,
            playerY,
            playerWidth,
            playerHeight
        );

        levelCompleted = false;
        victoryTimer = 0;
    }

    // Update principal

    public void update(float delta) {
        if (dead) {
            updateDeath(delta);
            return;
        }

        if (levelCompleted) {
            victoryTimer += delta;
            if (victoryTimer >= 2f) {
                if (onGoalReached != null) {
                    onGoalReached.run();
                }
            }
            return;
        }

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
            playSound("jump");
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
                float previousBottom =
                    playerY - verticalSpeed * Gdx.graphics.getDeltaTime();

                if (previousBottom >= terrain.y + terrain.height - 5) {
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
                playSound("block");
                spawnBlockReward(block);
                break;
            }
        }
    }

    private void spawnBlockReward(QuestionBlock block) {
        Rectangle blockBounds = block.getBounds();
        switch (block.getType()) {
            case "coin":
                coinCount++;
                playSound("coin");
                levelManager.getBlockRewards().add(
                    new BlockReward(
                        blockBounds.x,
                        blockBounds.y + blockBounds.height,
                        24,
                        32,
                        "coin"
                    )
                );
                break;

            case "mushroom":
                levelManager.getBlockRewards().add(
                    new BlockReward(
                        blockBounds.x,
                        blockBounds.y + blockBounds.height,
                        40,
                        40,
                        "mushroom"
                    )
                );
                break;

            case "star":
                levelManager.getBlockRewards().add(
                    new BlockReward(
                        blockBounds.x,
                        blockBounds.y + blockBounds.height,
                        40,
                        40,
                        "star"
                    )
                );
                break;
        }
    }

    // Monedas y recompensas

    private void checkCoinCollisions() {
        for (Coin coin : levelManager.getCoins()) {
            if (!coin.isCollected()
                && playerBounds.overlaps(coin.getBounds())) {
                coin.setCollected(true);
                coinCount++;
                playSound("coin");
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
                playerWidth = Constants.PLAYER_BIG_WIDTH;
                playerHeight = Constants.PLAYER_BIG_HEIGHT;
                playerBounds.setSize(playerWidth, playerHeight);
                playSound("powerup");
                break;

            case "star":
                starPower = true;
                starTimer = 8f;
                playSound("powerup");

                if (audioManager != null) {
                    audioManager.playStarSound();
                }
                break;
        }
    }

    // Enemigos

    private void updateEnemies(float delta) {
        for (Enemy enemy : levelManager.getEnemies()) {
            enemy.update(
                delta,
                levelManager.getTerrainCollisions()
            );
        }
    }

    private void checkEnemyCollisions() {
        for (Enemy enemy : levelManager.getEnemies()) {
            if (!enemy.isAlive() || enemy.isDying()) continue;
            if (playerBounds.overlaps(enemy.getBounds())) {
                boolean falling = verticalSpeed < 0;
                boolean goomba =
                    enemy.getType().equals("goomba");
                boolean above =
                    playerY > enemy.getBounds().y
                        + enemy.getBounds().height / 2f;

                if (goomba && falling && above) {
                    enemy.die();
                    verticalSpeed =
                        Constants.PLAYER_JUMP_FORCE / 2f;
                    playSound("stomp");
                } else if (starPower) {
                    enemy.die();
                    playSound("stomp");
                } else {
                    damagePlayer();
                }
            }
        }
    }

    // Daño y muerte del jugador

    private void damagePlayer() {
        if (big) {
            playSound("hit");
            big = false;
            playerWidth = Constants.PLAYER_SMALL_WIDTH;
            playerHeight = Constants.PLAYER_SMALL_HEIGHT;
            playerBounds.setSize(playerWidth, playerHeight);
            verticalSpeed = 250;
            moving = false;
        } else {
            killPlayer();
        }
    }

    private void killPlayer() {
        if (dead) return;
        dead = true;
        deathTimer = 0;
        big = false;
        moving = false;
        playerWidth = Constants.PLAYER_SMALL_WIDTH;
        playerHeight = Constants.PLAYER_SMALL_HEIGHT;
        playerBounds.setSize(playerWidth, playerHeight);
        verticalSpeed = 250;
        playSound("death");
        if (audioManager != null && starPower) {
            audioManager.stopStarSound();
        }
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
        if (goal != null
            && playerBounds.overlaps(goal)
            && !levelCompleted) {
            levelCompleted = true;
            if (audioManager != null) {
                audioManager.stopStarSound();
                audioManager.playVictory();
            }
        }
    }

    private void checkPlayerFall() {
        if (playerY < -200) {
            killPlayer();
        }
    }

    // Recompensas de bloque y estrella

    private void updateBlockRewards(float delta) {
        Array<BlockReward> rewards =
            levelManager.getBlockRewards();
        for (int i = rewards.size - 1; i >= 0; i--) {
            BlockReward reward = rewards.get(i);
            reward.update(delta);
            if (!reward.isActive()) {
                rewards.removeIndex(i);
            }
        }
    }

    private void updateStarPower(float delta) {
        if (starPower) {
            starTimer -= delta;
            if (starTimer <= 0) {
                starPower = false;
                if (audioManager != null) {
                    audioManager.stopStarSound();
                }
            }
        }
    }

    // Getters para render y pantalla

    public float getPlayerX() {
        return playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public float getPlayerWidth() {
        return playerWidth;
    }

    public float getPlayerHeight() {
        return playerHeight;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isBig() {
        return big;
    }

    public boolean isLookingRight() {
        return lookingRight;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean hasStarPower() {
        return starPower;
    }

    public int getCoinCount() {
        return coinCount;
    }
}
