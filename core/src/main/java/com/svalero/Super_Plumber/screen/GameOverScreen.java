package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.Super_Plumber.Super_Plumber;
import com.svalero.Super_Plumber.manager.FontManager;

//Sin usar aun
public class GameOverScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch        batch;
    private OrthographicCamera camera;
    private BitmapFont         font;
    private GlyphLayout        layout;

    public GameOverScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch  = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font   = FontManager.createMarioFont(20, Color.RED);
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        FontManager.drawCentered(batch, font, layout, "GAME OVER", sw, sh / 2f + 60, 3f);
        FontManager.drawCentered(batch, font, layout, "Pulsa ENTER para volver al menu", sw, sh / 2f, 1.2f);

        batch.end();
    }

    @Override public void resize(int w, int h) { camera.setToOrtho(false, w, h); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font  != null) font.dispose();
    }
}
