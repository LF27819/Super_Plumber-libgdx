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

public class VictoryScreen implements Screen {

    private final Super_Plumber game;
    private final int finalScore;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture backgroundImage;

    public VictoryScreen(Super_Plumber game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font = FontManager.createMarioFont(20, Color.YELLOW);
        layout = new GlyphLayout();

        backgroundImage = new Texture(Gdx.files.internal("assets/sprites/ui/victory_background.png"));
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new ScoreEntryScreen(game, finalScore));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(backgroundImage, 0, 0, sw, sh);

        FontManager.drawCentered(batch, font, layout, "¡HAS GANADO!", sw, sh / 2f + 80, 1.2f);
        FontManager.drawCentered(batch, font, layout, "PUNTOS: " + finalScore, sw, sh / 2f + 20, 0.8f);
        FontManager.drawCentered(batch, font, layout, "ENTER para guardar ranking", sw, sh / 2f - 100, 0.6f);

        batch.end();
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
        if (backgroundImage != null) backgroundImage.dispose();
    }
}
