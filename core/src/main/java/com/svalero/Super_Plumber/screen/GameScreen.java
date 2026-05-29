package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.Super_Plumber.Super_Plumber;
import com.svalero.Super_Plumber.manager.AudioManager;
import com.svalero.Super_Plumber.manager.CameraManager;
import com.svalero.Super_Plumber.manager.FontManager;
import com.svalero.Super_Plumber.manager.LevelManager;
import com.svalero.Super_Plumber.manager.LogicManager;
import com.svalero.Super_Plumber.manager.RenderManager;
import com.svalero.Super_Plumber.manager.ResourceManager;
import com.svalero.Super_Plumber.util.Constants;

public class GameScreen implements Screen {

    private static final int INITIAL_LIVES = 3;
    private static final int MAX_PAUSE_OPTION = 2;

    private final Super_Plumber game;

    private SpriteBatch batch;
    private BitmapFont hudFont;
    private BitmapFont pauseFont;
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

    private Texture coinHudTexture;
    private Texture marioHudTexture;

    private boolean paused;
    private int selectedPauseOption;

    private static final String[] LEVELS = {
        Constants.LEVEL_1,
        Constants.LEVEL_2
    };

    public GameScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        hudFont = FontManager.createMarioFont(18, Color.BLACK);
        pauseFont = FontManager.createMarioFont(18, Color.WHITE);

        coinHudTexture = new Texture(Gdx.files.internal("assets/sprites/items/moneda1.png"));
        marioHudTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-dcha.png"));

        animationTimer = 0;
        paused = false;
        selectedPauseOption = 0;

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
        handlePauseInput();

        if (!paused) {
            animationTimer += delta;
            logicManager.update(delta);
            updateLives();
            cameraManager.update(logicManager.getPlayerX(), logicManager.getPlayerWidth());
        }

        drawScene();

        if (paused) {
            drawPauseMenu();
        }
    }

    private void handlePauseInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            selectedPauseOption = 0;
            return;
        }

        if (!paused) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedPauseOption = (selectedPauseOption - 1 + MAX_PAUSE_OPTION + 1) % (MAX_PAUSE_OPTION + 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedPauseOption = (selectedPauseOption + 1) % (MAX_PAUSE_OPTION + 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executePauseOption();
        }
    }

    private void executePauseOption() {

        switch (selectedPauseOption) {

            case 0:
                paused = false;
                break;

            case 1:
                logicManager.reset();
                paused = false;
                break;

            case 2:
                game.setScreen(new MainMenuScreen(game));
                break;
        }
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

        float coinX = cameraX - cameraWidth / 2 + 20;

        batch.draw(coinHudTexture, coinX, topY - 10, 28, 28);
        hudFont.draw(batch, "x " + logicManager.getCoinCount(), coinX + 38, topY + 12);

        hudFont.draw(batch, "WORLD 1-" + (currentLevel + 1), cameraX - 80, topY + 12);

        float livesX = cameraX + cameraWidth / 2 - 180;

        batch.draw(marioHudTexture, livesX, topY - 10, 28, 28);
        hudFont.draw(batch, "x " + remainingLives, livesX + 38, topY + 12);
    }

    private void drawPauseMenu() {
        float cameraX = cameraManager.getCamera().position.x;
        float cameraY = cameraManager.getCamera().position.y;
        float cameraWidth = cameraManager.getCamera().viewportWidth;
        float cameraHeight = cameraManager.getCamera().viewportHeight;

        float panelWidth = 420;
        float panelHeight = 280;
        float panelX = cameraX - panelWidth / 2;
        float panelY = cameraY - panelHeight / 2;

        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        batch.begin();

        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(coinHudTexture, panelX, panelY, panelWidth, panelHeight);
        batch.setColor(Color.WHITE);

        float titleY = cameraY + 100;
        float firstY = cameraY + 40;
        float spacing = 45;

        drawPauseOption("PAUSA", cameraX - 75, titleY, false);
        drawPauseOption("CONTINUAR", cameraX - 120, firstY, selectedPauseOption == 0);
        drawPauseOption("REINICIAR NIVEL", cameraX - 180, firstY - spacing, selectedPauseOption == 1);
        drawPauseOption("VOLVER AL MENU", cameraX - 170, firstY - spacing * 3, selectedPauseOption == 2);

        batch.end();
    }

    private void drawPauseOption(String text, float x, float y, boolean selected) {
        String optionText = selected ? "> " + text : "  " + text;
        pauseFont.draw(batch, optionText, x, y);
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

    @Override
    public void hide() {
        if (audioManager != null) {
            audioManager.stopBackgroundMusic();
            audioManager.stopStarSound();
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (hudFont != null) hudFont.dispose();
        if (pauseFont != null) pauseFont.dispose();
        if (coinHudTexture != null) coinHudTexture.dispose();
        if (marioHudTexture != null) marioHudTexture.dispose();
        if (resourceManager != null) resourceManager.dispose();
        if (levelManager != null) levelManager.dispose();
        if (audioManager != null) audioManager.dispose();
    }
}
