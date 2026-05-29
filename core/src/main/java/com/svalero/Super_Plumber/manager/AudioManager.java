package com.svalero.Super_Plumber.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    private Music backgroundMusic;
    private Music starSound;

    private Sound jumpSound;
    private Sound coinSound;
    private Sound powerupSound;
    private Sound stompSound;
    private Sound hitSound;
    private Sound deathSound;
    private Sound victorySound;

    public void load() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/background_music.mp3"));
        starSound = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/star.mp3"));

        backgroundMusic.setLooping(true);
        starSound.setLooping(true);

        jumpSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/jump.mp3"));
        coinSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/coin.mp3"));
        powerupSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/powerup.mp3"));
        stompSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/stomp.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/hit.mp3"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/death.mp3"));
        victorySound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/victory.mp3"));
    }


    public void playBackgroundMusic() {
        if (!ConfigurationManager.isMusicEnabled()) {
            backgroundMusic.stop();
            return;
        }

        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    public void playStarSound() {
        if (!ConfigurationManager.isSoundEnabled()) return;
        starSound.play();
    }

    public void stopStarSound() {
        starSound.stop();
    }

    public void stopBackgroundMusic() {
        backgroundMusic.stop();
    }

    public void playJump() {
        if (ConfigurationManager.isSoundEnabled()) jumpSound.play();
    }

    public void playCoin() {
        if (ConfigurationManager.isSoundEnabled()) coinSound.play();
    }

    public void playPowerup() {
        if (ConfigurationManager.isSoundEnabled()) powerupSound.play();
    }

    public void playStomp() {
        if (ConfigurationManager.isSoundEnabled()) stompSound.play();
    }

    public void playHit() {
        if (ConfigurationManager.isSoundEnabled()) hitSound.play();
    }

    public void playDeath() {
        if (ConfigurationManager.isSoundEnabled()) deathSound.play();
    }

    public void playVictory() {
        if (ConfigurationManager.isSoundEnabled()) victorySound.play();
    }

    public void dispose() {
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (starSound != null) starSound.dispose();
        if (jumpSound != null) jumpSound.dispose();
        if (coinSound != null) coinSound.dispose();
        if (powerupSound != null) powerupSound.dispose();
        if (stompSound != null) stompSound.dispose();
        if (hitSound != null) hitSound.dispose();
        if (deathSound != null) deathSound.dispose();
        if (victorySound != null) victorySound.dispose();
    }
}
