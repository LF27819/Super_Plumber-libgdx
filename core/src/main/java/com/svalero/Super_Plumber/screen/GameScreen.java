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
import com.svalero.Super_Plumber.manager.*;
import com.svalero.Super_Plumber.util.Constants;
import com.svalero.Super_Plumber.manager.AudioManager;

public class GameScreen implements Screen {

    private static final int INITIAL_LIVES = 3;
    private static final int MAX_PAUSE_OPTION = 5;

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

        if (ConfigurationManager.isHardMode()) {
            remainingLives = 1;
        } else {
            remainingLives = INITIAL_LIVES;
        }

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
                ConfigurationManager.toggleMusic();

                if (audioManager != null) {
                    audioManager.applyConfiguration();
                }
                break;

            case 3:
                ConfigurationManager.toggleSound();
                break;

            case 4:
                game.setScreen(new MainMenuScreen(game));
                break;

            case 5:
                Gdx.app.exit();
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

        float panelWidth = 540;
        float panelHeight = 390;
        float panelX = cameraX - panelWidth / 2;
        float panelY = cameraY - panelHeight / 2;

        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        batch.begin();

        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(coinHudTexture, panelX, panelY, panelWidth, panelHeight);
        batch.setColor(Color.WHITE);

        float titleY = cameraY + 155;
        float firstY = cameraY + 95;
        float spacing = 43;

        drawPauseOption("PAUSA", cameraX - 75, titleY, false);
        drawPauseOption("CONTINUAR", cameraX - 135, firstY, selectedPauseOption == 0);
        drawPauseOption("REINICIAR NIVEL", cameraX - 195, firstY - spacing, selectedPauseOption == 1);
        drawPauseOption("MUSICA: " + onOff(ConfigurationManager.isMusicEnabled()), cameraX - 180, firstY - spacing * 2, selectedPauseOption == 2);
        drawPauseOption("SONIDOS: " + onOff(ConfigurationManager.isSoundEnabled()), cameraX - 190, firstY - spacing * 3, selectedPauseOption == 3);
        drawPauseOption("VOLVER AL MENU", cameraX - 190, firstY - spacing * 4, selectedPauseOption == 4);
        drawPauseOption("SALIR DEL JUEGO", cameraX - 190, firstY - spacing * 5, selectedPauseOption == 5);

        batch.end();
    }

    private String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    private void drawPauseOption(String text, float x, float y, boolean selected) {
        String optionText = selected ? "> " + text : "  " + text;
        pauseFont.draw(batch, optionText, x, y);
    }

    private void advanceLevel() {
        currentLevel++;

        if (currentLevel >= LEVELS.length) {
            int finalScore = calculateFinalScore();
            game.setScreen(new VictoryScreen(game, finalScore));
            return;
        }

        levelManager.loadLevel(LEVELS[currentLevel]);
        logicManager.setLevelManager(levelManager);
        logicManager.reset();
    }

    private int calculateFinalScore() {
        int coinScore = logicManager.getCoinCount() * 100;
        int livesScore = remainingLives * 500;
        int levelScore = LEVELS.length * 1000;

        return coinScore + livesScore + levelScore;
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
