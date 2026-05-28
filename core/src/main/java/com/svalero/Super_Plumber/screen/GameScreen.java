package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.Super_Plumber.Super_Plumber;
import com.svalero.Super_Plumber.manager.CameraManager;
import com.svalero.Super_Plumber.manager.FontManager;
import com.svalero.Super_Plumber.manager.LevelManager;
import com.svalero.Super_Plumber.manager.LogicManager;
import com.svalero.Super_Plumber.manager.RenderManager;
import com.svalero.Super_Plumber.manager.ResourceManager;
import com.svalero.Super_Plumber.util.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.svalero.Super_Plumber.manager.AudioManager;

public class GameScreen implements Screen {

    private static final int INITIAL_LIVES = 3;

    private final Super_Plumber game;

    private SpriteBatch batch;
    private BitmapFont hudFont;
    private float animationTimer;

    private ResourceManager resourceManager;
    private CameraManager cameraManager;
    private LevelManager levelManager;
    private LogicManager logicManager;
    private RenderManager renderManager;
    private AudioManager audioManager;

    private int currentLevel;
    private int remainingLives;
    private boolean deathAlreadyCounted;

    private static final String[] LEVELS = {
        Constants.LEVEL_1,
        Constants.LEVEL_2
    };

    private Texture coinHudTexture;
    private Texture marioHudTexture;

    public GameScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        hudFont = FontManager.createMarioFont(18, Color.BLACK);

        coinHudTexture = new Texture(Gdx.files.internal("assets/sprites/items/moneda1.png"));
        marioHudTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-dcha.png"));

        animationTimer = 0;

        resourceManager = new ResourceManager();
        resourceManager.load();

        audioManager = new AudioManager();
        audioManager.load();
        audioManager.playBackgroundMusic();

        levelManager = new LevelManager();
        cameraManager = new CameraManager();
        logicManager = new LogicManager(levelManager);
        logicManager.setAudioManager(audioManager);
        renderManager = new RenderManager(resourceManager, levelManager, logicManager);

        logicManager.setOnGoalReached(this::advanceLevel);

        currentLevel = 0;
        remainingLives = INITIAL_LIVES;
        deathAlreadyCounted = false;

        levelManager.loadLevel(LEVELS[currentLevel]);
        logicManager.reset();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        animationTimer += delta;

        logicManager.update(delta);
        updateLives();

        cameraManager.update(logicManager.getPlayerX(), logicManager.getPlayerWidth());

        drawScene();
    }

    private void updateLives() {
        if (logicManager.isDead() && !deathAlreadyCounted) {
            remainingLives--;
            deathAlreadyCounted = true;

            if (remainingLives <= 0) {
                game.setScreen(new GameOverScreen(game));
            }
        }

        if (!logicManager.isDead()) {
            deathAlreadyCounted = false;
        }
    }

    private void drawScene() {
        Gdx.gl.glClearColor(0.35f, 0.65f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        levelManager.setView(cameraManager.getCamera());
        levelManager.renderBase();

        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        batch.begin();

        renderManager.drawCoins(batch);
        renderManager.drawBlockRewards(batch);
        renderManager.drawEnemies(batch);

        batch.end();

        levelManager.renderForeground();

        batch.begin();

        renderManager.drawPlayer(batch, animationTimer);
        drawHud();

        batch.end();
    }

    private void drawHud() {

        float cameraX = cameraManager.getCamera().position.x;
        float cameraY = cameraManager.getCamera().position.y;
        float cameraWidth = cameraManager.getCamera().viewportWidth;
        float cameraHeight = cameraManager.getCamera().viewportHeight;

        float topY = cameraY + cameraHeight / 2 - 40;

        // MONEDAS

        float coinX = cameraX - cameraWidth / 2 + 20;

        batch.draw(
            coinHudTexture,
            coinX,
            topY - 10,
            28,
            28
        );

        hudFont.draw(
            batch,
            "x " + logicManager.getCoinCount(),
            coinX + 38,
            topY + 12
        );

        // NIVEL

        hudFont.draw(
            batch,
            "WORLD 1-" + (currentLevel + 1),
            cameraX - 80,
            topY + 12
        );

        // VIDAS

        float livesX = cameraX + cameraWidth / 2 - 180;

        batch.draw(
            marioHudTexture,
            livesX,
            topY - 10,
            28,
            28
        );

        hudFont.draw(
            batch,
            "x " + remainingLives,
            livesX + 38,
            topY + 12
        );
    }

    private void advanceLevel() {
        currentLevel++;

        if (currentLevel >= LEVELS.length) {
            game.setScreen(new VictoryScreen(game));
            return;
        }

        levelManager.loadLevel(LEVELS[currentLevel]);
        logicManager.setLevelManager(levelManager);
        logicManager.reset();
    }

    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (hudFont != null) hudFont.dispose();
        if (resourceManager != null) resourceManager.dispose();
        if (levelManager != null) levelManager.dispose();
        if (audioManager != null) audioManager.dispose();
    }
}
