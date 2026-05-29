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
        if (!backgroundMusic.isPlaying()) backgroundMusic.play();
    }

    public void stopBackgroundMusic() {
        backgroundMusic.stop();
    }

    public void playStarSound() {
        backgroundMusic.pause();
        starSound.play();
    }

    public void stopStarSound() {
        starSound.stop();
        backgroundMusic.play();
    }

    public void playJump() { jumpSound.play(); }
    public void playCoin() { coinSound.play(); }
    public void playPowerup() { powerupSound.play(); }
    public void playStomp() { stompSound.play(); }
    public void playHit() { hitSound.play(); }
    public void playDeath() { deathSound.play(); }
    public void playVictory() { victorySound.play(); }

    public void dispose() {
        backgroundMusic.dispose();
        starSound.dispose();
        jumpSound.dispose();
        coinSound.dispose();
        powerupSound.dispose();
        stompSound.dispose();
        hitSound.dispose();
        deathSound.dispose();
        victorySound.dispose();
    }
}
