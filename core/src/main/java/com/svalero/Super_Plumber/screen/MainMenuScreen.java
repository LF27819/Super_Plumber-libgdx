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

public class MainMenuScreen implements Screen {

    private static final int MAX_OPTION = 3;

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
        batch  = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font   = FontManager.createMarioFont(20, Color.WHITE);
        layout = new GlyphLayout();

        logoTexture   = new Texture(Gdx.files.internal("assets/sprites/ui/logo.png"));
        selectedOption = 0;
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.35f, 0.65f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        float logoW = 500, logoH = 220;
        batch.draw(logoTexture, (sw - logoW) / 2f, sh - 250, logoW, logoH);

        float firstY  = sh / 2f - 40;
        float spacing = 50;

        drawOption(sw, firstY,"JUGAR",0);
        drawOption(sw, firstY - spacing,"INSTRUCCIONES",1);
        drawOption(sw, firstY - spacing * 2,"CONFIGURACION",2);
        drawOption(sw, firstY - spacing * 3,"SALIR",3);

        batch.end();
    }

    private void drawOption(float sw, float y, String label, int index) {
        String text = (selectedOption == index ? "> " : "  ") + label;
        FontManager.drawOption(batch, font, layout, text, sw, y, 1.4f);
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
                case 0: game.setScreen(new GameScreen(game)); break;
                case 1: game.setScreen(new InstructionsScreen(game)); break;
                case 2: game.setScreen(new ConfigurationScreen(game)); break;
                case 3: Gdx.app.exit(); break;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (logoTexture != null) logoTexture.dispose();
    }
}
