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

public class InstructionsScreen implements Screen {

    private static final String[] LINES = {
        "Objetivo: supera los niveles y llega al castillo final.",
        "Flecha izquierda / derecha: mover a Mario.",
        "Barra espaciadora: saltar.",
        "Recoge monedas para aumentar el contador.",
        "Salta encima de los goombas para eliminarlos.",
        "Evita ninjis y plantas: si los tocas, vuelves al inicio.",
        "ESC durante la partida: volver al menu principal."
    };

    private final Super_Plumber game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture backgroundTexture;

    public InstructionsScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font = FontManager.createMarioFont(10, Color.WHITE);
        layout = new GlyphLayout();

        backgroundTexture = new Texture(Gdx.files.internal("assets/sprites/ui/menu-background.png"));
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

        FontManager.drawCentered(batch, font, layout, "INSTRUCCIONES", sw, sh / 2f + 160, 2f);

        font.getData().setScale(1.1f);

        float textBlockWidth = 760;
        float textX = Math.max(20, (sw - textBlockWidth) / 2f);
        float y = sh / 2f + 90;
        float spacing = 30;

        for (String line : LINES) {
            font.draw(batch, line, textX, y);
            y -= spacing;
        }

        FontManager.drawCentered(batch, font, layout, "> VOLVER AL MENU", sw, 100, 1.2f);
        FontManager.drawCentered(batch, font, layout, "Pulsa ENTER o ESC para volver", sw, 75, 0.9f);

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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
