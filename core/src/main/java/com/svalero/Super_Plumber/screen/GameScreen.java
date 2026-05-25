package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.svalero.Super_Plumber.Super_Plumber;

public class GameScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;
    private BitmapFont font;

    private OrthographicCamera camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer rendererMapa;

    private Texture marioTexture;
    private Texture coinTexture;

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

    private Rectangle coinBounds;
    private boolean coinCollected;
    private int coins;

    private final int blockSize = 48;

    public GameScreen(Super_Plumber game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);

        camara = new OrthographicCamera();
        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        mapa = new TmxMapLoader().load("levels/nivel1.tmx");
        rendererMapa = new OrthogonalTiledMapRenderer(mapa);

        marioTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-dcha.png"));
        coinTexture = new Texture(Gdx.files.internal("assets/sprites/items/moneda1.png"));

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

        // Suelo temporal antiguo, solo para que Mario no caiga.
        // Luego lo cambiaremos por colisiones reales desde Tiled.
        ground = new Rectangle(0, 0, 5000, blockSize);

        coinBounds = new Rectangle(360, 200, 32, 32);
        coinCollected = false;
        coins = 0;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        handleInput(delta);
        applyGravity(delta);
        checkCollisions();
        checkCoinCollision();

        camara.position.x = marioX + marioWidth / 2;
        camara.position.y = Gdx.graphics.getHeight() / 2f;
        camara.update();

        Gdx.gl.glClearColor(0.35f, 0.65f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        rendererMapa.setView(camara);
        rendererMapa.render();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        if (!coinCollected) {
            batch.draw(coinTexture, coinBounds.x, coinBounds.y, coinBounds.width, coinBounds.height);
        }

        batch.draw(marioTexture, marioX, marioY, marioWidth, marioHeight);

        font.draw(
            batch,
            "MONEDAS: " + coins,
            camara.position.x - camara.viewportWidth / 2 + 20,
            camara.position.y + camara.viewportHeight / 2 - 20
        );

        batch.end();
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

    private void checkCoinCollision() {
        if (!coinCollected && marioBounds.overlaps(coinBounds)) {
            coinCollected = true;
            coins++;
        }
    }

    @Override
    public void resize(int width, int height) {
        camara.setToOrtho(false, width, height);
    }

    @Override public void pause() {}

    @Override public void resume() {}

    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();

        if (marioTexture != null) marioTexture.dispose();
        if (coinTexture != null) coinTexture.dispose();

        if (mapa != null) mapa.dispose();
        if (rendererMapa != null) rendererMapa.dispose();
    }
}
