package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.svalero.Super_Plumber.Super_Plumber;

public class GameScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;

    private Texture marioTexture;
    private Texture groundBlockTexture;

    private float marioX;
    private float marioY;
    private float marioWidth;
    private float marioHeight;

    private float speed;
    private float verticalSpeed;
    private float gravity;
    private float jumpForce;

    private boolean isOnGround;

    private Rectangle marioBounds;
    private Rectangle ground;
    private Rectangle platform;

    private final int blockSize = 48;

    public GameScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        marioTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-dcha.png"));
        groundBlockTexture = new Texture(Gdx.files.internal("assets/sprites/tiles/bloque-ladrillo.png"));

        marioWidth = 64;
        marioHeight = 64;

        marioX = 100;
        marioY = 150;

        speed = 250;
        verticalSpeed = 0;
        gravity = -900;
        jumpForce = 520;

        isOnGround = false;

        marioBounds = new Rectangle(marioX, marioY, marioWidth, marioHeight);

        ground = new Rectangle(0, 0, Gdx.graphics.getWidth(), blockSize);
        platform = new Rectangle(260, 145, blockSize * 5, blockSize);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        handleInput(delta);
        applyGravity(delta);
        checkCollisions();

        Gdx.gl.glClearColor(0.35f, 0.65f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        drawGround();
        drawPlatform();

        batch.draw(marioTexture, marioX, marioY, marioWidth, marioHeight);

        batch.end();
    }

    private void drawGround() {
        for (int x = 0; x < Gdx.graphics.getWidth(); x += blockSize) {
            batch.draw(groundBlockTexture, x, ground.y, blockSize, blockSize);
        }
    }

    private void drawPlatform() {
        for (int x = (int) platform.x; x < platform.x + platform.width; x += blockSize) {
            batch.draw(groundBlockTexture, x, platform.y, blockSize, blockSize);
        }
    }

    private void handleInput(float delta) {
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
    }

    private void applyGravity(float delta) {
        verticalSpeed += gravity * delta;
        marioY += verticalSpeed * delta;

        marioBounds.setPosition(marioX, marioY);
        isOnGround = false;
    }

    private void checkCollisions() {
        checkPlatformCollision(ground);
        checkPlatformCollision(platform);
    }

    private void checkPlatformCollision(Rectangle rectangle) {
        if (marioBounds.overlaps(rectangle) && verticalSpeed <= 0) {
            float previousBottom = marioY - verticalSpeed * Gdx.graphics.getDeltaTime();

            if (previousBottom >= rectangle.y + rectangle.height - 5) {
                marioY = rectangle.y + rectangle.height;
                verticalSpeed = 0;
                isOnGround = true;
                marioBounds.setPosition(marioX, marioY);
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (marioTexture != null) marioTexture.dispose();
        if (groundBlockTexture != null) groundBlockTexture.dispose();
    }
}
