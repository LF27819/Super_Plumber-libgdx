package com.svalero.Super_Plumber.manager;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.svalero.Super_Plumber.domain.BlockReward;
import com.svalero.Super_Plumber.domain.Coin;
import com.svalero.Super_Plumber.domain.Enemy;
import com.svalero.Super_Plumber.domain.QuestionBlock;


public class LevelManager {

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int[] baseLayerIndices;
    private int[] foregroundLayerIndices;

    // Entidades del nivel

    private final Array<Rectangle> terrainCollisions = new Array<>();
    private final Array<Coin> coins = new Array<>();
    private final Array<Enemy> enemies = new Array<>();
    private final Array<QuestionBlock> questionBlocks = new Array<>();
    private final Array<BlockReward> blockRewards = new Array<>();
    private Rectangle goalBounds;

    // CARGA NIVEL

    public void loadLevel(String tmxPath) {

        if (map != null) {
            mapRenderer.dispose();
            map.dispose();
        }
        clearEntities();

        map = new TmxMapLoader().load(tmxPath);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        loadTerrainCollisions();
        loadCoins();
        loadEnemies();
        loadQuestionBlocks();
        loadGoal();
        buildLayerArrays();
    }

    // TERRENO

    private void loadTerrainCollisions() {

        TiledMapTileLayer terrainLayer =
            (TiledMapTileLayer) map.getLayers().get("terrain");
        if (terrainLayer == null) {
            return;
        }

        float tileWidth = terrainLayer.getTileWidth();
        float tileHeight = terrainLayer.getTileHeight();

        for (int x = 0; x < terrainLayer.getWidth(); x++) {
            for (int y = 0; y < terrainLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell =
                    terrainLayer.getCell(x, y);
                if (cell != null) {
                    TiledMapTile tile = cell.getTile();
                    float collisionWidth =
                        tile.getTextureRegion().getRegionWidth();
                    float collisionHeight =
                        tile.getTextureRegion().getRegionHeight();
                    terrainCollisions.add(
                        new Rectangle(
                            x * tileWidth,
                            y * tileHeight,
                            collisionWidth,
                            collisionHeight
                        )
                    );
                }
            }
        }
    }

    // MONEDAS

    private void loadCoins() {
        TiledMapTileLayer coinsLayer =
            (TiledMapTileLayer) map.getLayers().get("coins");
        if (coinsLayer == null) {
            return;
        }
        float tileWidth = coinsLayer.getTileWidth();
        float tileHeight = coinsLayer.getTileHeight();
        for (int x = 0; x < coinsLayer.getWidth(); x++) {
            for (int y = 0; y < coinsLayer.getHeight(); y++) {
                if (coinsLayer.getCell(x, y) != null) {

                    coins.add(
                        new Coin(
                            x * tileWidth,
                            y * tileHeight,
                            tileWidth,
                            tileHeight
                        )
                    );
                }
            }
        }
    }

    // ENEMIGOS

    private void loadEnemies() {

        MapLayer enemiesLayer =
            map.getLayers().get("enemies_objects");
        if (enemiesLayer == null) {
            return;
        }

        for (MapObject mapObject : enemiesLayer.getObjects()) {
            Rectangle enemyBounds =
                ((RectangleMapObject) mapObject).getRectangle();
            String enemyType =
                mapObject.getProperties().get("type", String.class);
            if (enemyType == null || enemyType.isBlank()) {
                enemyType = "goomba";
            }

            enemies.add(
                new Enemy(
                    enemyBounds.x,
                    enemyBounds.y,
                    32,
                    32,
                    enemyType
                )
            );
        }
    }

    // BLOQUES PREGUNTA

    private void loadQuestionBlocks() {

        MapLayer questionBlocksLayer =
            map.getLayers().get("question_blocks");

        if (questionBlocksLayer == null) {
            return;
        }

        for (MapObject mapObject : questionBlocksLayer.getObjects()) {

            Rectangle blockBounds =
                ((RectangleMapObject) mapObject).getRectangle();

            String rewardType =
                mapObject.getProperties().get("type", String.class);

            if (rewardType == null || rewardType.isBlank()) {
                rewardType = "coin";
            }

            questionBlocks.add(
                new QuestionBlock(
                    blockBounds.x,
                    blockBounds.y,
                    blockBounds.width,
                    blockBounds.height,
                    rewardType
                )
            );
        }
    }

    // META

    private void loadGoal() {
        goalBounds = null;
        MapLayer goalLayer =
            map.getLayers().get("goal");
        if (goalLayer == null) {
            return;
        }
        MapObjects objects = goalLayer.getObjects();
        for (MapObject mapObject : objects) {
            String objectType =
                mapObject.getProperties().get("type", String.class);
            if ("finish".equals(objectType)) {
                goalBounds =
                    ((RectangleMapObject) mapObject).getRectangle();
                break;
            }
        }
    }

    // CAPAS RENDER

    private void buildLayerArrays() {
        Array<Integer> baseLayers = new Array<>();
        Array<Integer> foregroundLayers = new Array<>();

        for (int i = 0; i < map.getLayers().getCount(); i++) {
            String layerName = map.getLayers().get(i).getName();

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
        baseLayerIndices = toIntArray(baseLayers);
        foregroundLayerIndices = toIntArray(foregroundLayers);
    }

    private int[] toIntArray(Array<Integer> sourceArray) {
        int[] result = new int[sourceArray.size];
        for (int i = 0; i < sourceArray.size; i++) {

            result[i] = sourceArray.get(i);
        }
        return result;
    }



    private void clearEntities() {

        terrainCollisions.clear();
        coins.clear();
        enemies.clear();
        questionBlocks.clear();
        blockRewards.clear();
        goalBounds = null;
    }


    public void renderBase() {
        mapRenderer.render(baseLayerIndices);
    }

    public void renderForeground() {
        mapRenderer.render(foregroundLayerIndices);
    }

    public void setView(com.badlogic.gdx.graphics.OrthographicCamera camera) {
        mapRenderer.setView(camera);
    }


    public Array<Rectangle> getTerrainCollisions() {
        return terrainCollisions;
    }

    public Array<Coin> getCoins() {
        return coins;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Array<QuestionBlock> getQuestionBlocks() {
        return questionBlocks;
    }

    public Array<BlockReward> getBlockRewards() {
        return blockRewards;
    }

    public Rectangle getGoalBounds() {
        return goalBounds;
    }

    public float getLevelWidth() {
        return map.getProperties().get("width", Integer.class)
            * map.getProperties().get("tilewidth", Integer.class);
    }

    public void dispose() {

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        if (map != null) {
            map.dispose();
        }
    }
}
