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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.svalero.Super_Plumber.Super_Plumber;
import com.svalero.Super_Plumber.manager.ConfigurationManager;

public class ConfigurationScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout layout;

    private int selectedOption;

    private Texture backgroundTexture;

    public ConfigurationScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/mario.ttf")
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 15;
        parameter.color = Color.WHITE;

        font = generator.generateFont(parameter);

        generator.dispose();

        layout = new GlyphLayout();

        selectedOption = 0;

        backgroundTexture = new Texture(Gdx.files.internal("assets/sprites/ui/menu-background.png"));
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        batch.begin();

        batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);

        drawCenteredText("CONFIGURACION", screenWidth, screenHeight - 120, 2f);

        float firstOptionY = screenHeight / 2f + 60;
        float spacing = 45;

        drawOption(
            (selectedOption == 0 ? "> " : "  ") + "MUSICA: " +
                (ConfigurationManager.musicEnabled ? "ON" : "OFF"),
            screenWidth, firstOptionY, 1.2f);

        drawOption(
            (selectedOption == 1 ? "> " : "  ") + "SONIDOS: " +
                (ConfigurationManager.soundEnabled ? "ON" : "OFF"),
            screenWidth, firstOptionY - spacing, 1.2f);

        drawOption(
            (selectedOption == 2 ? "> " : "  ") + "MODO DIFICIL: " +
                (ConfigurationManager.hardMode ? "ON" : "OFF"),
            screenWidth,firstOptionY - spacing * 2, 1.2f);

        drawOption(
            (selectedOption == 3 ? "> " : "  ") + "VOLVER AL MENU",
            screenWidth, firstOptionY - spacing * 3, 1.2f);

        batch.end();
    }

    private void handleInput() {
        int maxOption = 3;

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption--;

            if (selectedOption < 0) {
                selectedOption = maxOption;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption++;

            if (selectedOption > maxOption) {
                selectedOption = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedOption) {
                case 0:
                    ConfigurationManager.musicEnabled = !ConfigurationManager.musicEnabled;
                    break;
                case 1:
                    ConfigurationManager.soundEnabled = !ConfigurationManager.soundEnabled;
                    break;
                case 2:
                    ConfigurationManager.hardMode = !ConfigurationManager.hardMode;
                    break;
                case 3:
                    game.setScreen(new MainMenuScreen(game));
                    break;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void drawOption(String text, float screenWidth, float y, float scale) {
        font.getData().setScale(scale);
        layout.setText(font, text);

        float x = (screenWidth - layout.width) / 2f;

        font.draw(batch, text, x, y);
    }

    private void drawCenteredText(String text, float screenWidth, float y) {
        layout.setText(font, text);
        font.draw(batch, text, (screenWidth - layout.width) / 2f, y);
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
        if (font != null) font.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }

    private void drawCenteredText(String text, float screenWidth, float y, float scale) {
        font.getData().setScale(scale);
        layout.setText(font, text);

        float x = (screenWidth - layout.width) / 2f;

        font.draw(batch, text, x, y);
    }
}
