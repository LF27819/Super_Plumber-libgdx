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
import com.svalero.Super_Plumber.domain.BlockReward;
import com.svalero.Super_Plumber.domain.Coin;
import com.svalero.Super_Plumber.domain.Enemy;
import com.svalero.Super_Plumber.domain.QuestionBlock;

public class GameScreen implements Screen {

    private final Super_Plumber game;

    private SpriteBatch batch;
    private BitmapFont font;

    private OrthographicCamera camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer rendererMapa;

    private int[] capasMapaBase;
    private int[] capasForeground;

    private Texture marioTexture;
    private Texture marioLeftTexture;

    private Texture marioWalkRightTexture;
    private Texture marioRunRightTexture;
    private Texture marioWalkLeftTexture;
    private Texture marioRunLeftTexture;

    private Texture marioJumpRightTexture;
    private Texture marioJumpLeftTexture;

    private Texture marioBigRightTexture;
    private Texture marioBigLeftTexture;
    private Texture marioBigWalkRightTexture;
    private Texture marioBigRunRightTexture;
    private Texture marioBigWalkLeftTexture;
    private Texture marioBigRunLeftTexture;
    private Texture marioBigJumpRightTexture;
    private Texture marioBigJumpLeftTexture;

    private Texture marioDeadTexture;

    private Texture coinTexture;
    private Texture coinBlockTexture;
    private Texture mushroomTexture;
    private Texture starTexture;

    private Texture goombaTexture;
    private Texture goombaDeadTexture;
    private Texture ninjiTexture;
    private Texture plantaTexture;

    private float marioX;
    private float marioY;
    private float marioWidth;
    private float marioHeight;

    private float speed;
    private float verticalSpeed;
    private float gravity;
    private float jumpForce;

    private boolean isOnGround;
    private boolean isBig;
    private boolean lookingRight;
    private boolean isMoving;
    private boolean isDead;

    private float animationTimer;
    private float deathTimer;

    private Rectangle marioBounds;
    private Rectangle goalBounds;

    private Array<Rectangle> terrainCollisions;
    private Array<Coin> coinsList;
    private Array<Enemy> enemiesList;
    private Array<QuestionBlock> questionBlocksList;
    private Array<BlockReward> blockRewardsList;

    private int coins;

    private boolean starPower;
    private float starTimer;

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
        marioLeftTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-izda.png"));

        marioWalkRightTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-anda-pd.png"));
        marioRunRightTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-corre-pd.png"));
        marioWalkLeftTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-anda-pi.png"));
        marioRunLeftTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-corre-pi.png"));

        marioJumpRightTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-salto-pd.png"));
        marioJumpLeftTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-salto-pi.png"));

        marioBigRightTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-dcha-grande.png"));
        marioBigLeftTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-izda-grande.png"));

        marioBigWalkRightTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-anda-gd.png"));
        marioBigRunRightTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-carrera-gd.png"));
        marioBigWalkLeftTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-anda-gi.png"));
        marioBigRunLeftTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-carrera-gi.png"));

        marioBigJumpRightTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-salto-gd.png"));
        marioBigJumpLeftTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-salto-gi.png"));

        marioDeadTexture = new Texture(Gdx.files.internal("assets/sprites/player/mario-muerto.png"));

        coinTexture = new Texture(Gdx.files.internal("assets/sprites/items/moneda1.png"));
        coinBlockTexture = new Texture(Gdx.files.internal("assets/sprites/items/moneda2.png"));
        mushroomTexture = new Texture(Gdx.files.internal("assets/sprites/items/setagrande.png"));
        starTexture = new Texture(Gdx.files.internal("assets/sprites/items/estrella.png"));

        goombaTexture = new Texture(Gdx.files.internal("assets/sprites/enemies/enemigo-seta.png"));
        goombaDeadTexture = new Texture(Gdx.files.internal("assets/sprites/enemies/goomba-aplastado.png"));
        ninjiTexture = new Texture(Gdx.files.internal("assets/sprites/enemies/ninji-izda.png"));
        plantaTexture = new Texture(Gdx.files.internal("assets/sprites/enemies/planta.png"));

        marioWidth = 64;
        marioHeight = 64;

        marioX = 100;
        marioY = 220;

        speed = 250;
        verticalSpeed = 0;
        gravity = -900;
        jumpForce = 520;

        isOnGround = false;
        isBig = false;
        lookingRight = true;
        isMoving = false;
        isDead = false;

        animationTimer = 0;
        deathTimer = 0;

        starPower = false;
        starTimer = 0;

        marioBounds = new Rectangle(marioX, marioY, marioWidth, marioHeight);

        coins = 0;
        coinsList = new Array<>();
        enemiesList = new Array<>();
        terrainCollisions = new Array<>();
        questionBlocksList = new Array<>();
        blockRewardsList = new Array<>();

        loadTerrainCollisionsFromTiled();
        loadCoinsFromTiled();
        loadEnemiesFromTiled();
        loadQuestionBlocksFromTiled();
        loadGoalFromTiled();
        prepareVisibleLayers();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }

        animationTimer += delta;

        if (!isDead) {
            handleInput(delta);
            applyGravity(delta);
            updateEnemies(delta);
            updateBlockRewards(delta);
            updateStarPower(delta);
            checkQuestionBlockCollision();
            checkCollisions();
            checkCoinCollision();
            checkEnemyCollision();
            checkGoalCollision();
            checkPlayerFall();
        } else {
            updateDeath(delta);
        }

        updateCamera();

        Gdx.gl.glClearColor(0.35f, 0.65f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        rendererMapa.setView(camara);
        rendererMapa.render(capasMapaBase);

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        drawCoins();
        drawBlockRewards();
        drawEnemies();

        batch.end();

        rendererMapa.render(capasForeground);

        batch.begin();

        Texture currentMarioTexture = getCurrentMarioTexture();
        batch.draw(currentMarioTexture, marioX, marioY, marioWidth, marioHeight);

        font.draw(
            batch,
            "MONEDAS: " + coins,
            camara.position.x - camara.viewportWidth / 2 + 20,
            camara.position.y + camara.viewportHeight / 2 - 20
        );

        batch.end();
    }

    private Texture getCurrentMarioTexture() {
        if (isDead) {
            return marioDeadTexture;
        }

        if (!isOnGround) {
            if (isBig) {
                return lookingRight ? marioBigJumpRightTexture : marioBigJumpLeftTexture;
            } else {
                return lookingRight ? marioJumpRightTexture : marioJumpLeftTexture;
            }
        }

        if (isMoving) {
            boolean alternateFrame = ((int) (animationTimer * 8)) % 2 == 0;

            if (isBig) {
                if (lookingRight) {
                    return alternateFrame ? marioBigWalkRightTexture : marioBigRunRightTexture;
                } else {
                    return alternateFrame ? marioBigWalkLeftTexture : marioBigRunLeftTexture;
                }
            } else {
                if (lookingRight) {
                    return alternateFrame ? marioWalkRightTexture : marioRunRightTexture;
                } else {
                    return alternateFrame ? marioWalkLeftTexture : marioRunLeftTexture;
                }
            }
        }

        if (isBig) {
            return lookingRight ? marioBigRightTexture : marioBigLeftTexture;
        } else {
            return lookingRight ? marioTexture : marioLeftTexture;
        }
    }

    private void handleInput(float delta) {
        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            marioX += speed * delta;
            lookingRight = true;
            isMoving = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            marioX -= speed * delta;
            lookingRight = false;
            isMoving = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isOnGround) {
            verticalSpeed = jumpForce;
            isOnGround = false;
        }
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
        MapLayer enemiesLayer = mapa.getLayers().get("enemies_objects");

        if (enemiesLayer == null) {
            System.out.println("No existe la capa enemies_objects en este nivel");
            return;
        }

        com.badlogic.gdx.maps.MapObjects objects = enemiesLayer.getObjects();

        for (com.badlogic.gdx.maps.MapObject object : objects) {
            Rectangle rectangle =
                ((com.badlogic.gdx.maps.objects.RectangleMapObject) object)
                    .getRectangle();

            String type = object.getProperties().get("type", String.class);

            if (type == null || type.isBlank()) {
                type = "goomba";
            }

            enemiesList.add(new Enemy(
                rectangle.x,
                rectangle.y,
                32,
                32,
                type
            ));
        }
    }

    private void loadQuestionBlocksFromTiled() {
        MapLayer questionLayer = mapa.getLayers().get("question_blocks");

        if (questionLayer == null) {
            System.out.println("No existe la capa question_blocks en este nivel");
            return;
        }

        com.badlogic.gdx.maps.MapObjects objects = questionLayer.getObjects();

        for (com.badlogic.gdx.maps.MapObject object : objects) {
            Rectangle rectangle =
                ((com.badlogic.gdx.maps.objects.RectangleMapObject) object)
                    .getRectangle();

            String type = object.getProperties().get("type", String.class);

            if (type == null || type.isBlank()) {
                type = "coin";
            }

            questionBlocksList.add(new QuestionBlock(
                rectangle.x,
                rectangle.y,
                rectangle.width,
                rectangle.height,
                type
            ));
        }
    }

    private void loadGoalFromTiled() {
        goalBounds = null;

        MapLayer goalLayer = mapa.getLayers().get("goal");

        if (goalLayer == null) {
            System.out.println("No existe la capa goal en este nivel");
            return;
        }

        com.badlogic.gdx.maps.MapObjects objects = goalLayer.getObjects();

        for (com.badlogic.gdx.maps.MapObject object : objects) {
            Rectangle rectangle =
                ((com.badlogic.gdx.maps.objects.RectangleMapObject) object)
                    .getRectangle();

            String type = object.getProperties().get("type", String.class);

            if ("finish".equals(type)) {
                goalBounds = rectangle;
            }
        }
    }

    private void prepareVisibleLayers() {
        Array<Integer> baseLayers = new Array<>();
        Array<Integer> foregroundLayers = new Array<>();

        for (int i = 0; i < mapa.getLayers().getCount(); i++) {
            MapLayer layer = mapa.getLayers().get(i);
            String layerName = layer.getName();

            if (layerName.equals("coins")
                || layerName.equals("enemies")
                || layerName.equals("enemies_objects")
                || layerName.equals("goal")
                || layerName.equals("question_blocks")) {
                continue;
            }

            if (layerName.equals("foreground")) {
                foregroundLayers.add(i);
            } else {
                baseLayers.add(i);
            }
        }

        capasMapaBase = new int[baseLayers.size];

        for (int i = 0; i < baseLayers.size; i++) {
            capasMapaBase[i] = baseLayers.get(i);
        }

        capasForeground = new int[foregroundLayers.size];

        for (int i = 0; i < foregroundLayers.size; i++) {
            capasForeground[i] = foregroundLayers.get(i);
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

            if (enemy.getType().equals("planta")) {
                float plantWidth = 48;
                float plantHeight = 64;

                float drawX = bounds.x + (bounds.width - plantWidth) / 2f + 15;
                float drawY = bounds.y;

                batch.draw(plantaTexture, drawX, drawY, plantWidth, plantHeight);
            } else if (enemy.getType().equals("ninji")) {
                batch.draw(ninjiTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            } else if (enemy.isDying()) {
                batch.draw(goombaDeadTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            } else {
                batch.draw(goombaTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            }
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

        for (int i = blockRewardsList.size - 1; i >= 0; i--) {
            BlockReward reward = blockRewardsList.get(i);

            if (reward.getType().equals("mushroom")
                && marioBounds.overlaps(reward.getBounds())) {

                isBig = true;

                marioWidth = 80;
                marioHeight = 96;

                marioBounds.setSize(marioWidth, marioHeight);

                blockRewardsList.removeIndex(i);
            }

            if (reward.getType().equals("star")
                && marioBounds.overlaps(reward.getBounds())) {

                starPower = true;
                starTimer = 8f;

                blockRewardsList.removeIndex(i);
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
                    if (starPower) {
                        enemy.die();
                        continue;
                    }

                    damagePlayer();
                }
            }
        }
    }

    private void damagePlayer() {
        if (isBig) {
            isBig = false;

            marioWidth = 64;
            marioHeight = 64;

            marioBounds.setSize(marioWidth, marioHeight);

            verticalSpeed = 250;
            isMoving = false;
        } else {
            diePlayer();
        }
    }

    private void diePlayer() {
        isDead = true;
        deathTimer = 0;
        isBig = false;
        isMoving = false;

        marioWidth = 64;
        marioHeight = 64;
        marioBounds.setSize(marioWidth, marioHeight);

        verticalSpeed = 250;
    }

    private void updateDeath(float delta) {
        deathTimer += delta;

        verticalSpeed += gravity * delta;
        marioY += verticalSpeed * delta;

        marioBounds.setPosition(marioX, marioY);

        if (deathTimer >= 1.5f) {
            isDead = false;
            resetPlayer();
        }
    }

    private void checkQuestionBlockCollision() {
        if (verticalSpeed <= 0) {
            return;
        }

        Rectangle headSensor = new Rectangle(
            marioX + 8,
            marioY + marioHeight - 4,
            marioWidth - 16,
            8
        );

        for (QuestionBlock block : questionBlocksList) {
            if (block.isUsed()) {
                continue;
            }

            Rectangle blockBounds = block.getBounds();

            if (headSensor.overlaps(blockBounds)) {
                block.use();
                verticalSpeed = -150;
                activateQuestionBlock(block);
                break;
            }
        }
    }

    private void activateQuestionBlock(QuestionBlock block) {
        Rectangle bounds = block.getBounds();

        if (block.getType().equals("coin")) {
            coins++;

            blockRewardsList.add(new BlockReward(
                bounds.x,
                bounds.y + bounds.height,
                24,
                32,
                "coin"
            ));
        }

        if (block.getType().equals("mushroom")) {
            blockRewardsList.add(new BlockReward(
                bounds.x,
                bounds.y + bounds.height,
                40,
                40,
                "mushroom"
            ));
        }

        if (block.getType().equals("star")) {
            blockRewardsList.add(new BlockReward(
                bounds.x,
                bounds.y + bounds.height,
                40,
                40,
                "star"
            ));
        }
    }

    private void updateBlockRewards(float delta) {
        for (int i = blockRewardsList.size - 1; i >= 0; i--) {
            BlockReward reward = blockRewardsList.get(i);
            reward.update(delta);

            if (!reward.isActive()) {
                blockRewardsList.removeIndex(i);
            }
        }
    }

    private void drawBlockRewards() {
        for (BlockReward reward : blockRewardsList) {
            Rectangle bounds = reward.getBounds();

            if (reward.getType().equals("coin")) {
                batch.draw(coinBlockTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            }

            if (reward.getType().equals("mushroom")) {
                batch.draw(mushroomTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            }

            if (reward.getType().equals("star")) {
                batch.draw(starTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
    }

    private void updateStarPower(float delta) {
        if (starPower) {
            starTimer -= delta;

            if (starTimer <= 0) {
                starPower = false;
            }
        }
    }

    private void checkPlayerFall() {
        if (marioY < -200) {
            diePlayer();
        }
    }

    private void checkGoalCollision() {
        if (goalBounds != null && marioBounds.overlaps(goalBounds)) {
            mapa.dispose();
            rendererMapa.dispose();

            mapa = new TmxMapLoader().load("levels/nivel2.tmx");
            rendererMapa = new OrthogonalTiledMapRenderer(mapa);

            terrainCollisions.clear();
            coinsList.clear();
            enemiesList.clear();
            questionBlocksList.clear();
            blockRewardsList.clear();

            loadTerrainCollisionsFromTiled();
            loadCoinsFromTiled();
            loadEnemiesFromTiled();
            loadQuestionBlocksFromTiled();
            loadGoalFromTiled();
            prepareVisibleLayers();

            resetPlayer();
        }
    }

    private void resetPlayer() {
        marioX = 100;
        marioY = 220;
        verticalSpeed = 0;

        isBig = false;
        isMoving = false;
        isDead = false;

        marioWidth = 64;
        marioHeight = 64;
        marioBounds.setSize(marioWidth, marioHeight);
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
        if (marioLeftTexture != null) marioLeftTexture.dispose();

        if (marioWalkRightTexture != null) marioWalkRightTexture.dispose();
        if (marioRunRightTexture != null) marioRunRightTexture.dispose();
        if (marioWalkLeftTexture != null) marioWalkLeftTexture.dispose();
        if (marioRunLeftTexture != null) marioRunLeftTexture.dispose();

        if (marioJumpRightTexture != null) marioJumpRightTexture.dispose();
        if (marioJumpLeftTexture != null) marioJumpLeftTexture.dispose();

        if (marioBigRightTexture != null) marioBigRightTexture.dispose();
        if (marioBigLeftTexture != null) marioBigLeftTexture.dispose();
        if (marioBigWalkRightTexture != null) marioBigWalkRightTexture.dispose();
        if (marioBigRunRightTexture != null) marioBigRunRightTexture.dispose();
        if (marioBigWalkLeftTexture != null) marioBigWalkLeftTexture.dispose();
        if (marioBigRunLeftTexture != null) marioBigRunLeftTexture.dispose();
        if (marioBigJumpRightTexture != null) marioBigJumpRightTexture.dispose();
        if (marioBigJumpLeftTexture != null) marioBigJumpLeftTexture.dispose();

        if (marioDeadTexture != null) marioDeadTexture.dispose();

        if (coinTexture != null) coinTexture.dispose();
        if (coinBlockTexture != null) coinBlockTexture.dispose();
        if (mushroomTexture != null) mushroomTexture.dispose();
        if (starTexture != null) starTexture.dispose();

        if (goombaTexture != null) goombaTexture.dispose();
        if (goombaDeadTexture != null) goombaDeadTexture.dispose();
        if (ninjiTexture != null) ninjiTexture.dispose();
        if (plantaTexture != null) plantaTexture.dispose();

        if (mapa != null) mapa.dispose();
        if (rendererMapa != null) rendererMapa.dispose();
    }
}
