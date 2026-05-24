package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.Super_Plumber.Super_Plumber;

public class GameScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;
    private Texture marioTexture;

    private float marioX;
    private float marioY;

    private float speed;
    private float verticalSpeed;
    private float gravity;
    private float jumpForce;
    private float groundY;

    private boolean isOnGround;

    public GameScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        marioTexture = new Texture(
            Gdx.files.internal("assets/sprites/player/mario-dcha.png")
        );

        marioX = 100;
        groundY = 120;
        marioY = groundY;

        speed = 250;
        verticalSpeed = 0;
        gravity = -900;
        jumpForce = 450;

        isOnGround = true;
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            marioX += speed * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            marioX -= speed * delta;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isOnGround) {
            verticalSpeed = jumpForce;
            isOnGround = false;
        }

        verticalSpeed += gravity * delta;
        marioY += verticalSpeed * delta;

        if (marioY <= groundY) {
            marioY = groundY;
            verticalSpeed = 0;
            isOnGround = true;
        }

        Gdx.gl.glClearColor(0.4f, 0.7f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.draw(
            marioTexture,
            marioX,
            marioY,
            64,
            64
        );

        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (marioTexture != null) marioTexture.dispose();
    }
}
