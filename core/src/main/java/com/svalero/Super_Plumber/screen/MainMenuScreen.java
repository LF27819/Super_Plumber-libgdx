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

public class MainMenuScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture logoTexture;

    private int selectedOption;

    public MainMenuScreen(Super_Plumber game) {
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

        parameter.size = 20;
        parameter.color = Color.WHITE;

        font = generator.generateFont(parameter);

        generator.dispose();
        layout = new GlyphLayout();

        logoTexture = new Texture(Gdx.files.internal("assets/sprites/ui/logo.png"));

        selectedOption = 0;
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.35f, 0.65f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float logoWidth = 500;
        float logoHeight = 220;
        float logoX = (screenWidth - logoWidth) / 2f;

        batch.draw(logoTexture, logoX, screenHeight - 250, logoWidth, logoHeight);

        float firstOptionY = screenHeight / 2f - 40;
        float spacing = 50;

        drawOption((selectedOption == 0 ? "> " : "  ") + "JUGAR", screenWidth, firstOptionY, 1.4f);
        drawOption((selectedOption == 1 ? "> " : "  ") + "INSTRUCCIONES", screenWidth, firstOptionY - spacing, 1.4f);
        drawOption((selectedOption == 2 ? "> " : "  ") + "CONFIGURACION", screenWidth, firstOptionY - spacing * 2, 1.4f);
        drawOption((selectedOption == 3 ? "> " : "  ") + "SALIR", screenWidth, firstOptionY - spacing * 3, 1.4f);

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
                    game.setScreen(new GameScreen(game));
                    break;
                case 1:
                    game.setScreen(new InstructionsScreen(game));
                    break;
                case 2:
                    game.setScreen(new ConfigurationScreen(game));
                    break;
                case 3:
                    Gdx.app.exit();
                    break;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
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
        if (logoTexture != null) logoTexture.dispose();
    }

}
