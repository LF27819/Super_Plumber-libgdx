package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.Super_Plumber.Super_Plumber;

public class MainMenuScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture logoTexture;

    public MainMenuScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.getData().setScale(2);

        layout = new GlyphLayout();

        logoTexture = new Texture(Gdx.files.internal("assets/sprites/ui/logo.png"));
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.app.exit();
        }

        Gdx.gl.glClearColor(0.35f, 0.65f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float logoWidth = 500;
        float logoHeight = 220;
        float logoX = (Gdx.graphics.getWidth() - logoWidth) / 2f;

        batch.begin();
        batch.draw(logoTexture, logoX, 240, logoWidth, logoHeight);
        drawCenteredText("PULSA ENTER PARA JUGAR", 210);
        drawCenteredText("PULSA Q PARA SALIR", 170);
        batch.end();
    }

    private void drawCenteredText(String text, float y) {
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.draw(batch, text, x, y);
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (logoTexture != null) logoTexture.dispose();
    }
}
