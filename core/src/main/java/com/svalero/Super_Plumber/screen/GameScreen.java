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

    private Texture bushTexture;
    private Texture smallMountainTexture;
    private Texture mountainTexture;
    private Texture cloudTexture;
    private Texture smallCloudTexture;

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

        bushTexture = new Texture(Gdx.files.internal("assets/sprites/background/arbusto.png"));
        smallMountainTexture = new Texture(Gdx.files.internal("assets/sprites/background/montanapeq.png"));
        mountainTexture = new Texture(Gdx.files.internal("assets/sprites/background/montana.png"));
        cloudTexture = new Texture(Gdx.files.internal("assets/sprites/background/nube.png"));
        smallCloudTexture = new Texture(Gdx.files.internal("assets/sprites/background/nubepeq.png"));

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

        drawBackground();
        drawGround();
        drawPlatform();

        batch.draw(marioTexture, marioX, marioY, marioWidth, marioHeight);

        batch.end();
    }

    private void drawBackground() {

        // NUBES
        batch.draw(cloudTexture, 90, 360, 70, 50);
        batch.draw(smallCloudTexture, 300, 400, 90, 45);
        batch.draw(cloudTexture, 610, 370, 70, 50);
        batch.draw(smallCloudTexture, 820, 410, 90, 45);

        // MONTAÑAS
        batch.draw(mountainTexture, 30, blockSize, 140, 90);
        batch.draw(smallMountainTexture, 390, blockSize, 100, 70);
        batch.draw(mountainTexture, 690, blockSize, 140, 90);

        // ARBUSTOS
        batch.draw(bushTexture, 210, blockSize, 100, 40);
        batch.draw(bushTexture, 560, blockSize, 100, 40);
        batch.draw(bushTexture, 900, blockSize, 100, 40);
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

        if (bushTexture != null) bushTexture.dispose();
        if (smallMountainTexture != null) smallMountainTexture.dispose();
        if (mountainTexture != null) mountainTexture.dispose();
        if (cloudTexture != null) cloudTexture.dispose();
        if (smallCloudTexture != null) smallCloudTexture.dispose();
    }
}
