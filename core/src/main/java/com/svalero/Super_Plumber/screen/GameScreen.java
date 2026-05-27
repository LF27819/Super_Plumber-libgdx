package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.Super_Plumber.Super_Plumber;
import com.svalero.Super_Plumber.manager.CameraManager;
import com.svalero.Super_Plumber.manager.LevelManager;
import com.svalero.Super_Plumber.manager.LogicManager;
import com.svalero.Super_Plumber.manager.RenderManager;
import com.svalero.Super_Plumber.manager.ResourceManager;
import com.svalero.Super_Plumber.util.Constants;

/**
 * Pantalla principal del juego. Coordina los managers sin contener
 * lógica de juego, texturas ni colisiones directamente.
 */
public class GameScreen implements Screen {

    private final Super_Plumber game;

    //  Rendering
    private SpriteBatch batch;
    private BitmapFont hudFont;
    private float animationTimer;

    //  Managers
    private ResourceManager resourceManager;
    private CameraManager   cameraManager;
    private LevelManager    levelManager;
    private LogicManager    logicManager;
    private RenderManager   renderManager;

    //  Control de nivel
    private int currentLevel;
    private static final String[] LEVELS = {
        Constants.LEVEL_1,
        Constants.LEVEL_2
    };

    public GameScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch         = new SpriteBatch();
        hudFont       = new BitmapFont();
        hudFont.getData().setScale(2);
        animationTimer = 0;

        resourceManager = new ResourceManager();
        resourceManager.load();

        levelManager = new LevelManager();
        cameraManager = new CameraManager();
        logicManager  = new LogicManager(levelManager);
        renderManager = new RenderManager(resourceManager, levelManager, logicManager);

        // Cuando el jugador llega a la meta, cargamos el siguiente nivel
        logicManager.setOnGoalReached(this::advanceLevel);

        currentLevel = 0;
        levelManager.loadLevel(LEVELS[currentLevel]);
        logicManager.reset();
    }


    // Ciclo de juego

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        animationTimer += delta;

        logicManager.update(delta);
        cameraManager.update(logicManager.getPlayerX(), logicManager.getPlayerWidth());

        drawScene();
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
        float camX = cameraManager.getCamera().position.x;
        float camW = cameraManager.getCamera().viewportWidth;
        float camH = cameraManager.getCamera().viewportHeight;
        float camY = cameraManager.getCamera().position.y;

        hudFont.draw(batch,
            "MONEDAS: " + logicManager.getCoinCount(),
            camX - camW / 2 + 20,
            camY + camH / 2 - 20);
    }

    // Cambio de nivel

    private void advanceLevel() {
        currentLevel++;
        if (currentLevel >= LEVELS.length) {
            // Todos los niveles completados
            game.setScreen(new VictoryScreen(game));
            return;
        }

        levelManager.loadLevel(LEVELS[currentLevel]);
        logicManager.setLevelManager(levelManager);
        logicManager.reset();
    }

    // Ciclo de vida de la pantalla

    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);
    }

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        if (batch          != null) batch.dispose();
        if (hudFont        != null) hudFont.dispose();
        if (resourceManager != null) resourceManager.dispose();
        if (levelManager   != null) levelManager.dispose();
    }
}
