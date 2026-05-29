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
import com.svalero.Super_Plumber.manager.ConfigurationManager;
import com.svalero.Super_Plumber.manager.FontManager;

public class ConfigurationScreen implements Screen {

    private static final int MAX_OPTION = 3;

    private final Super_Plumber game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture backgroundTexture;

    private int selectedOption;

    public ConfigurationScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font = FontManager.createMarioFont(15, Color.WHITE);
        layout = new GlyphLayout();

        backgroundTexture = new Texture(Gdx.files.internal("assets/sprites/ui/menu-background.png"));
        selectedOption = 0;
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, sw, sh);

        FontManager.drawCentered(batch, font, layout, "CONFIGURACION", sw, sh - 120, 2f);

        float firstY = sh / 2f + 60;
        float spacing = 45;

        drawOption(sw, firstY, "MUSICA: " + onOff(ConfigurationManager.isMusicEnabled()), 0);
        drawOption(sw, firstY - spacing, "SONIDOS: " + onOff(ConfigurationManager.isSoundEnabled()), 1);
        drawOption(sw, firstY - spacing * 2, "MODO DIFICIL: " + onOff(ConfigurationManager.isHardMode()), 2);
        drawOption(sw, firstY - spacing * 3, "VOLVER AL MENU", 3);

        batch.end();
    }

    private void drawOption(float sw, float y, String label, int index) {
        String text = (selectedOption == index ? "> " : "  ") + label;
        FontManager.drawOption(batch, font, layout, text, sw, y, 1.2f);
    }

    private static String onOff(boolean flag) {
        return flag ? "ON" : "OFF";
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + MAX_OPTION + 1) % (MAX_OPTION + 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % (MAX_OPTION + 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedOption) {
                case 0:
                    ConfigurationManager.toggleMusic();
                    break;
                case 1:
                    ConfigurationManager.toggleSound();
                    break;
                case 2:
                    ConfigurationManager.toggleHardMode();
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
}
