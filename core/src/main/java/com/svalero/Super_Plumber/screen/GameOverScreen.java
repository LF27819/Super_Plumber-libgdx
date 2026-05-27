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
import com.svalero.Super_Plumber.Super_Plumber;
import com.svalero.Super_Plumber.manager.FontManager;

public class GameOverScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private GlyphLayout textLayout;
    private Texture backgroundImage;

    public GameOverScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        titleFont = FontManager.createMarioFont(18, Color.RED);
        textFont = FontManager.createMarioFont(12, Color.WHITE);
        textLayout = new GlyphLayout();

        backgroundImage = new Texture(Gdx.files.internal("assets/sprites/ui/game_over_background.png"));
    }

    @Override
    public void render(float delta) {
        handleInput();
        clearScreen();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        drawBackground(screenWidth, screenHeight);

        FontManager.drawCentered(batch, textFont, textLayout, "Pulsa ENTER para volver", screenWidth, screenHeight / 2f - 120, 1f);
        FontManager.drawCentered(batch, textFont, textLayout, "al menu principal", screenWidth, screenHeight / 2f - 160, 1f);

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void drawBackground(float screenWidth, float screenHeight) {
        if (backgroundImage != null) {
            batch.draw(backgroundImage, 0, 0, screenWidth, screenHeight);
        }
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        camera.setToOrtho(false, screenWidth, screenHeight);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (textFont != null) textFont.dispose();
        if (backgroundImage != null) backgroundImage.dispose();
    }
}
