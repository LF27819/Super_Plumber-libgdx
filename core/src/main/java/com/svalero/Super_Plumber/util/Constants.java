package com.svalero.Super_Plumber.util;

public class Constants {

    // Tamaños del jugador
    public static final float PLAYER_SMALL_WIDTH  = 64;
    public static final float PLAYER_SMALL_HEIGHT = 64;
    public static final float PLAYER_BIG_WIDTH    = 80;
    public static final float PLAYER_BIG_HEIGHT   = 96;

    // Posición inicial
    public static final float PLAYER_START_X = 100;
    public static final float PLAYER_START_Y = 220;

    // Física del jugador
    public static final float PLAYER_SPEED      = 250;
    public static final float PLAYER_GRAVITY    = -900;
    public static final float PLAYER_JUMP_FORCE = 520;

    // Rutas de niveles
    public static final String LEVEL_1 = "levels/nivel1.tmx";
    public static final String LEVEL_2 = "levels/nivel2.tmx";

    private Constants() {}
}
