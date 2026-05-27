package com.svalero.Super_Plumber.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

//Rutas sprites

public class ResourceManager {

    // Jugador pequeño
    public Texture marioRight;
    public Texture marioLeft;
    public Texture marioWalkRight;
    public Texture marioRunRight;
    public Texture marioWalkLeft;
    public Texture marioRunLeft;
    public Texture marioJumpRight;
    public Texture marioJumpLeft;

    // Jugador grande
    public Texture marioBigRight;
    public Texture marioBigLeft;
    public Texture marioBigWalkRight;
    public Texture marioBigRunRight;
    public Texture marioBigWalkLeft;
    public Texture marioBigRunLeft;
    public Texture marioBigJumpRight;
    public Texture marioBigJumpLeft;

    // Jugador muerto
    public Texture marioDead;

    // Ítems
    public Texture coin;
    public Texture coinBlock;
    public Texture mushroom;
    public Texture star;

    // Enemigos
    public Texture goomba;
    public Texture goombaDead;
    public Texture ninji;
    public Texture plant;

    public void load() {
        marioRight      = new Texture(Gdx.files.internal("assets/sprites/player/mario-dcha.png"));
        marioLeft       = new Texture(Gdx.files.internal("assets/sprites/player/mario-izda.png"));
        marioWalkRight  = new Texture(Gdx.files.internal("assets/sprites/player/mario-anda-pd.png"));
        marioRunRight   = new Texture(Gdx.files.internal("assets/sprites/player/mario-corre-pd.png"));
        marioWalkLeft   = new Texture(Gdx.files.internal("assets/sprites/player/mario-anda-pi.png"));
        marioRunLeft    = new Texture(Gdx.files.internal("assets/sprites/player/mario-corre-pi.png"));
        marioJumpRight  = new Texture(Gdx.files.internal("assets/sprites/player/mario-salto-pd.png"));
        marioJumpLeft   = new Texture(Gdx.files.internal("assets/sprites/player/mario-salto-pi.png"));

        marioBigRight     = new Texture(Gdx.files.internal("assets/sprites/player/mario-dcha-grande.png"));
        marioBigLeft      = new Texture(Gdx.files.internal("assets/sprites/player/mario-izda-grande.png"));
        marioBigWalkRight = new Texture(Gdx.files.internal("assets/sprites/player/mario-anda-gd.png"));
        marioBigRunRight  = new Texture(Gdx.files.internal("assets/sprites/player/mario-carrera-gd.png"));
        marioBigWalkLeft  = new Texture(Gdx.files.internal("assets/sprites/player/mario-anda-gi.png"));
        marioBigRunLeft   = new Texture(Gdx.files.internal("assets/sprites/player/mario-carrera-gi.png"));
        marioBigJumpRight = new Texture(Gdx.files.internal("assets/sprites/player/mario-salto-gd.png"));
        marioBigJumpLeft  = new Texture(Gdx.files.internal("assets/sprites/player/mario-salto-gi.png"));

        marioDead = new Texture(Gdx.files.internal("assets/sprites/player/mario-muerto.png"));

        coin       = new Texture(Gdx.files.internal("assets/sprites/items/moneda1.png"));
        coinBlock  = new Texture(Gdx.files.internal("assets/sprites/items/moneda2.png"));
        mushroom   = new Texture(Gdx.files.internal("assets/sprites/items/setagrande.png"));
        star       = new Texture(Gdx.files.internal("assets/sprites/items/estrella.png"));

        goomba     = new Texture(Gdx.files.internal("assets/sprites/enemies/enemigo-seta.png"));
        goombaDead = new Texture(Gdx.files.internal("assets/sprites/enemies/goomba-aplastado.png"));
        ninji      = new Texture(Gdx.files.internal("assets/sprites/enemies/ninji-izda.png"));
        plant      = new Texture(Gdx.files.internal("assets/sprites/enemies/planta.png"));
    }

    public void dispose() {
        marioRight.dispose();
        marioLeft.dispose();
        marioWalkRight.dispose();
        marioRunRight.dispose();
        marioWalkLeft.dispose();
        marioRunLeft.dispose();
        marioJumpRight.dispose();
        marioJumpLeft.dispose();

        marioBigRight.dispose();
        marioBigLeft.dispose();
        marioBigWalkRight.dispose();
        marioBigRunRight.dispose();
        marioBigWalkLeft.dispose();
        marioBigRunLeft.dispose();
        marioBigJumpRight.dispose();
        marioBigJumpLeft.dispose();

        marioDead.dispose();

        coin.dispose();
        coinBlock.dispose();
        mushroom.dispose();
        star.dispose();

        goomba.dispose();
        goombaDead.dispose();
        ninji.dispose();
        plant.dispose();
    }
}
