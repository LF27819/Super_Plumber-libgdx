package com.svalero.Super_Plumber;

import com.badlogic.gdx.Game;
import com.svalero.Super_Plumber.screen.MainMenuScreen;

public class Super_Plumber extends Game {

    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
