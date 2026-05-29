package com.svalero.Super_Plumber.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;


 //Gestiona la cámara del juego: creación, actualización y seguimiento del jugador.

public class CameraManager {

    private final OrthographicCamera camera;

    public CameraManager() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

     //Actualiza la posición de la cámara para seguir al jugador horizontalmente,
     //con límite izquierdo y derecho para no mostrar fuera del mapa.

    public void update(float playerX, float playerWidth, float levelWidth) {
        float targetX = playerX + playerWidth / 2f;
        float halfViewport = camera.viewportWidth / 2f;

        if (targetX < halfViewport) {
            targetX = halfViewport;
        }

        if (targetX > levelWidth - halfViewport) {
            targetX = levelWidth - halfViewport;
        }

        camera.position.x = targetX;
        camera.position.y = Gdx.graphics.getHeight() / 2f;
        camera.update();
    }

    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
