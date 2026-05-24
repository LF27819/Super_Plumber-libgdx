package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.svalero.Super_Plumber.Super_Plumber;

public class GameScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture marioTexture;

    // POSICION Y TAMAÑO
    private float marioX;
    private float marioY;
    private float marioWidth;
    private float marioHeight;

    // MOVIMIENTO Y FISICAS
    private float speed;
    private float verticalSpeed;
    private float gravity;
    private float jumpForce;

    // ESTADO
    private boolean isOnGround;

    // COLISIONES
    private Rectangle marioBounds;
    private Rectangle ground;
    private Rectangle platform;

    public GameScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();

        marioTexture = new Texture(
            Gdx.files.internal("assets/sprites/player/mario-dcha.png")
        );

        // TAMAÑO MARIO
        marioWidth = 48;
        marioHeight = 64;

        // POSICION INICIAL
        marioX = 100;
        marioY = 150;

        // MOVIMIENTO
        speed = 250;

        // FISICAS
        verticalSpeed = 0;
        gravity = -900;
        jumpForce = 480;

        isOnGround = false;

        // RECTANGULO MARIO
        marioBounds = new Rectangle(
            marioX,
            marioY,
            marioWidth,
            marioHeight
        );

        // SUELO
        ground = new Rectangle(
            0,
            80,
            Gdx.graphics.getWidth(),
            40
        );

        // PLATAFORMA
        platform = new Rectangle(
            280,
            190,
            260,
            30
        );
    }

    @Override
    public void render(float delta) {

        // VOLVER AL MENU
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        handleInput(delta);

        applyGravity(delta);

        checkCollisions();

        // LIMPIAR PANTALLA
        Gdx.gl.glClearColor(0.4f, 0.7f, 1f, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // DIBUJAR SUELO Y PLATAFORMAS
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(
            ground.x,
            ground.y,
            ground.width,
            ground.height
        );

        shapeRenderer.rect(
            platform.x,
            platform.y,
            platform.width,
            platform.height
        );

        shapeRenderer.end();

        // DIBUJAR MARIO
        batch.begin();

        batch.draw(
            marioTexture,
            marioX,
            marioY,
            marioWidth,
            marioHeight
        );

        batch.end();
    }

    private void handleInput(float delta) {

        // DERECHA
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            marioX += speed * delta;
        }

        // IZQUIERDA
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            marioX -= speed * delta;
        }

        // SALTO
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

            float previousBottom =
                marioY - verticalSpeed * Gdx.graphics.getDeltaTime();

            if (previousBottom >= rectangle.y + rectangle.height - 5) {

                marioY = rectangle.y + rectangle.height;

                verticalSpeed = 0;

                isOnGround = true;

                marioBounds.setPosition(marioX, marioY);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {

        if (batch != null) {
            batch.dispose();
        }

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }

        if (marioTexture != null) {
            marioTexture.dispose();
        }
    }
}
