package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.svalero.Super_Plumber.Super_Plumber;
import com.svalero.Super_Plumber.manager.FontManager;
import com.svalero.Super_Plumber.manager.ScoreManager;

public class ScoreEntryScreen implements Screen {

    private enum Phase {
        INPUT,
        RANKING
    }

    private static final int NAME_MAX_LENGTH = 10;
    private static final float ROW_HEIGHT = 34f;

    private final Super_Plumber game;
    private final int finalScore;

    private Phase phase;
    private String playerName;
    private int playerRank;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private BitmapFont smallFont;
    private BitmapFont highlightFont;
    private GlyphLayout layout;
    private Texture backgroundTexture;

    public ScoreEntryScreen(Super_Plumber game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        titleFont = FontManager.createMarioFont(18, Color.YELLOW);
        textFont = FontManager.createMarioFont(12, Color.WHITE);
        smallFont = FontManager.createMarioFont(9, Color.WHITE);
        highlightFont = FontManager.createMarioFont(12, Color.YELLOW);

        layout = new GlyphLayout();

        backgroundTexture = new Texture(Gdx.files.internal("assets/sprites/ui/menu-background.png"));

        phase = Phase.INPUT;
        playerName = "";
        playerRank = -1;
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);

        if (phase == Phase.INPUT) {
            drawInputPhase(screenWidth, screenHeight);
        } else {
            drawRankingPhase(screenWidth, screenHeight);
        }

        batch.end();
    }

    private void drawInputPhase(float screenWidth, float screenHeight) {
        FontManager.drawCentered(batch, titleFont, layout, "PUNTUACION", screenWidth, screenHeight - 90, 1.4f);

        FontManager.drawCentered(batch, textFont, layout, "TOTAL: " + finalScore, screenWidth, screenHeight / 2f + 90, 1f);

        FontManager.drawCentered(batch, textFont, layout, "INTRODUCE TU NOMBRE", screenWidth, screenHeight / 2f + 35, 1f);

        String cursor = System.currentTimeMillis() % 800 < 400 ? "|" : " ";
        FontManager.drawCentered(batch, titleFont, layout, playerName + cursor, screenWidth, screenHeight / 2f - 20, 1f);

        FontManager.drawCentered(batch, smallFont, layout, "ENTER confirmar  |  BACKSPACE borrar", screenWidth, screenHeight / 2f - 90, 1f);
    }

    private void drawRankingPhase(float screenWidth, float screenHeight) {
        FontManager.drawCentered(batch, titleFont, layout, "TOP 10", screenWidth, screenHeight - 80, 1.4f);

        Array<ScoreManager.ScoreEntry> scores = ScoreManager.loadScores();

        float startY = screenHeight - 150;

        for (int i = 0; i < scores.size; i++) {
            ScoreManager.ScoreEntry entry = scores.get(i);

            boolean currentPlayer = i + 1 == playerRank
                && entry.name.equals(playerName)
                && entry.score == finalScore;

            BitmapFont rowFont = currentPlayer ? highlightFont : textFont;

            String line = (i + 1) + ". " + entry.name + " - " + entry.score;

            FontManager.drawCentered(batch, rowFont, layout, line, screenWidth, startY - i * ROW_HEIGHT, 1f);
        }

        FontManager.drawCentered(batch, smallFont, layout, "Pulsa ENTER o ESC para volver al menu", screenWidth, 70, 1f);
    }

    private void handleInput() {
        if (phase == Phase.INPUT) {
            handleNameInput();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void handleNameInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            confirmName();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && !playerName.isEmpty()) {
            playerName = playerName.substring(0, playerName.length() - 1);
            return;
        }

        if (playerName.length() >= NAME_MAX_LENGTH) return;

        for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                playerName += Input.Keys.toString(key).toUpperCase();
                return;
            }
        }

        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                playerName += (char) ('0' + (key - Input.Keys.NUM_0));
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !playerName.isEmpty()) {
            playerName += " ";
        }
    }

    private void confirmName() {
        String name = playerName.trim();

        if (name.isEmpty()) {
            name = "PLAYER";
        }

        playerName = name;
        playerRank = ScoreManager.addScore(playerName, finalScore);
        phase = Phase.RANKING;
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (textFont != null) textFont.dispose();
        if (smallFont != null) smallFont.dispose();
        if (highlightFont != null) highlightFont.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
