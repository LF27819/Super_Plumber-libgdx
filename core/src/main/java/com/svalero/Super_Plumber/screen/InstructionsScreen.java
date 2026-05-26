package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.svalero.Super_Plumber.Super_Plumber;

public class InstructionsScreen implements Screen {

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

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/mario.ttf")
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 10;
        parameter.color = Color.WHITE;

        font = generator.generateFont(parameter);

        generator.dispose();

        layout = new GlyphLayout();

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

        drawCenteredText("INSTRUCCIONES", screenWidth, screenHeight - 90, 2f);

        font.getData().setScale(1.1f);

        float x = 20;
        float y = screenHeight - 140;
        float spacing = 30;

        font.draw(batch, "Objetivo: supera los niveles y llega al castillo final.", x, y);
        font.draw(batch, "Flecha izquierda / derecha: mover a Mario.", x, y - spacing);
        font.draw(batch, "Barra espaciadora: saltar.", x, y - spacing * 2);
        font.draw(batch, "Recoge monedas para aumentar el contador.", x, y - spacing * 3);
        font.draw(batch, "Salta encima de los goombas para eliminarlos.", x, y - spacing * 4);
        font.draw(batch, "Evita ninjis y plantas: si los tocas, vuelves al inicio.", x, y - spacing * 5);
        font.draw(batch, "ESC durante la partida: volver al menu principal.", x, y - spacing * 6);

        drawCenteredText("> VOLVER AL MENU", screenWidth, 100, 1.2f);
        drawCenteredText("Pulsa ENTER o ESC para volver", screenWidth, 75, 0.9f);

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
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
