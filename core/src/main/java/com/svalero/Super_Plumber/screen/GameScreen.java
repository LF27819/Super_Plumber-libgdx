package com.svalero.Super_Plumber.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.svalero.Super_Plumber.Super_Plumber;
import com.svalero.Super_Plumber.domain.Coin;
import com.svalero.Super_Plumber.domain.Enemy;

public class GameScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;
    private BitmapFont font;

    private OrthographicCamera camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer rendererMapa;
    private int[] capasVisibles;

    private Texture marioTexture;
    private Texture coinTexture;
    private Texture goombaTexture;
    private Texture ninjiTexture;
    private Texture goombaDeadTexture;

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

    private Array<Rectangle> terrainCollisions;
    private Array<Coin> coinsList;
    private Array<Enemy> enemiesList;

    private int coins;

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
        goombaTexture = new Texture(Gdx.files.internal("assets/sprites/enemies/enemigo-seta.png"));
        goombaDeadTexture = new Texture(Gdx.files.internal("assets/sprites/enemies/goomba-aplastado.png"));
        ninjiTexture = new Texture(Gdx.files.internal("assets/sprites/enemies/ninji-izda.png"));

        marioWidth = 64;
        marioHeight = 64;

        marioX = 100;
        marioY = 220;

        speed = 250;
        verticalSpeed = 0;
        gravity = -900;
        jumpForce = 520;

        isOnGround = false;

        marioBounds = new Rectangle(marioX, marioY, marioWidth, marioHeight);

        coins = 0;
        coinsList = new Array<>();
        enemiesList = new Array<>();
        terrainCollisions = new Array<>();

        loadTerrainCollisionsFromTiled();
        loadCoinsFromTiled();
        loadEnemiesFromTiled();
        prepareVisibleLayers();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        handleInput(delta);
        applyGravity(delta);
        updateEnemies(delta);
        checkCollisions();
        checkCoinCollision();
        checkEnemyCollision();
        checkPlayerFall();
        updateCamera();

        Gdx.gl.glClearColor(0.35f, 0.65f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        rendererMapa.setView(camara);
        rendererMapa.render(capasVisibles);

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        drawCoins();
        drawEnemies();

        batch.draw(marioTexture, marioX, marioY, marioWidth, marioHeight);

        font.draw(
            batch,
            "MONEDAS: " + coins,
            camara.position.x - camara.viewportWidth / 2 + 20,
            camara.position.y + camara.viewportHeight / 2 - 20
        );

        batch.end();
    }

    private void loadTerrainCollisionsFromTiled() {
        TiledMapTileLayer terrainLayer = (TiledMapTileLayer) mapa.getLayers().get("terrain");

        if (terrainLayer == null) {
            System.out.println("No existe la capa terrain en Tiled");
            return;
        }

        float tileWidth = terrainLayer.getTileWidth();
        float tileHeight = terrainLayer.getTileHeight();

        for (int x = 0; x < terrainLayer.getWidth(); x++) {
            for (int y = 0; y < terrainLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = terrainLayer.getCell(x, y);

                if (cell != null) {
                    TiledMapTile tile = cell.getTile();

                    float collisionWidth = tile.getTextureRegion().getRegionWidth();
                    float collisionHeight = tile.getTextureRegion().getRegionHeight();

                    terrainCollisions.add(new Rectangle(
                        x * tileWidth,
                        y * tileHeight,
                        collisionWidth,
                        collisionHeight
                    ));
                }
            }
        }
    }

    private void loadCoinsFromTiled() {
        TiledMapTileLayer coinsLayer = (TiledMapTileLayer) mapa.getLayers().get("coins");

        if (coinsLayer == null) {
            System.out.println("No existe la capa coins en Tiled");
            return;
        }

        float tileWidth = coinsLayer.getTileWidth();
        float tileHeight = coinsLayer.getTileHeight();

        for (int x = 0; x < coinsLayer.getWidth(); x++) {
            for (int y = 0; y < coinsLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = coinsLayer.getCell(x, y);

                if (cell != null) {
                    coinsList.add(new Coin(
                        x * tileWidth,
                        y * tileHeight,
                        tileWidth,
                        tileHeight
                    ));
                }
            }
        }
    }

    private void loadEnemiesFromTiled() {

        com.badlogic.gdx.maps.MapObjects objects =
            mapa.getLayers().get("enemies_objects").getObjects();

        for (com.badlogic.gdx.maps.MapObject object : objects) {

            Rectangle rectangle =
                ((com.badlogic.gdx.maps.objects.RectangleMapObject) object)
                    .getRectangle();

            String type = object.getProperties().get("type", String.class);

            if (type == null || type.isBlank()) {
                type = "goomba";
            }

            float enemyWidth = 32;
            float enemyHeight = 32;

            if (type.equals("ninji")) {
                enemyWidth = 32;
                enemyHeight = 32;
            }

            enemiesList.add(new Enemy(
                rectangle.x,
                rectangle.y,
                enemyWidth,
                enemyHeight,
                type
            ));
        }
    }

    private void prepareVisibleLayers() {
        Array<Integer> visibleLayers = new Array<>();

        for (int i = 0; i < mapa.getLayers().getCount(); i++) {
            MapLayer layer = mapa.getLayers().get(i);

            if (!layer.getName().equals("coins") && !layer.getName().equals("enemies")) {
                visibleLayers.add(i);
            }
        }

        capasVisibles = new int[visibleLayers.size];

        for (int i = 0; i < visibleLayers.size; i++) {
            capasVisibles[i] = visibleLayers.get(i);
        }
    }

    private void updateCamera() {
        float cameraX = marioX + marioWidth / 2;
        float halfViewport = camara.viewportWidth / 2;

        if (cameraX < halfViewport) {
            cameraX = halfViewport;
        }

        camara.position.x = cameraX;
        camara.position.y = Gdx.graphics.getHeight() / 2f;
        camara.update();
    }

    private void drawCoins() {
        for (Coin coin : coinsList) {
            if (!coin.isCollected()) {
                Rectangle bounds = coin.getBounds();
                batch.draw(coinTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
    }

    private void updateEnemies(float delta) {
        for (Enemy enemy : enemiesList) {
            enemy.update(delta, terrainCollisions);
        }
    }

    private void drawEnemies() {
        for (Enemy enemy : enemiesList) {
            if (!enemy.isAlive()) {
                continue;
            }

            Rectangle bounds = enemy.getBounds();

            if (enemy.getType().equals("ninji")) {
                batch.draw(ninjiTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            } else if (enemy.isDying()) {
                batch.draw(goombaDeadTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            } else {
                batch.draw(goombaTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            }
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
        for (Rectangle terrain : terrainCollisions) {
            checkPlatformCollision(terrain);
        }
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
        for (Coin coin : coinsList) {
            if (!coin.isCollected() && marioBounds.overlaps(coin.getBounds())) {
                coin.setCollected(true);
                coins++;
            }
        }
    }

    private void checkEnemyCollision() {
        for (Enemy enemy : enemiesList) {
            if (!enemy.isAlive() || enemy.isDying()) {
                continue;
            }

            if (marioBounds.overlaps(enemy.getBounds())) {
                boolean isFalling = verticalSpeed < 0;
                boolean isGoomba = enemy.getType().equals("goomba");
                boolean marioAboveEnemy = marioY > enemy.getBounds().y + enemy.getBounds().height / 2;

                if (isGoomba && isFalling && marioAboveEnemy) {
                    enemy.die();
                    verticalSpeed = jumpForce / 2;
                } else {
                    resetPlayer();
                }
            }
        }
    }

    private void checkPlayerFall() {
        if (marioY < -200) {
            resetPlayer();
        }
    }

    private void resetPlayer() {
        marioX = 100;
        marioY = 220;
        verticalSpeed = 0;
        marioBounds.setPosition(marioX, marioY);
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
        if (goombaTexture != null) goombaTexture.dispose();
        if (goombaDeadTexture != null) goombaDeadTexture.dispose();
        if (ninjiTexture != null) ninjiTexture.dispose();

        if (mapa != null) mapa.dispose();
        if (rendererMapa != null) rendererMapa.dispose();
    }
}
