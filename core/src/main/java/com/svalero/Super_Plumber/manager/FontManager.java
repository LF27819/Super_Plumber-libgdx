package com.svalero.Super_Plumber.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontManager {

    private static final String MARIO_FONT_PATH = "assets/fonts/mario.ttf";

    private FontManager() {}

    //Genera una fuente Mario con el tamaño y color indicados.

    public static BitmapFont createMarioFont(int size, Color color) {
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal(MARIO_FONT_PATH));

        FreeTypeFontGenerator.FreeTypeFontParameter param =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        param.size  = size;
        param.color = color;

        BitmapFont font = generator.generateFont(param);
        generator.dispose();
        return font;
    }

     //Dibuja un texto centrado con escala personalizada.

    public static void drawCentered(SpriteBatch batch, BitmapFont font, GlyphLayout layout,
                                    String text, float screenWidth, float y, float scale) {
        font.getData().setScale(scale);
        layout.setText(font, text);
        font.draw(batch, text, (screenWidth - layout.width) / 2f, y);
    }


     // Dibuja una opción de menú centrada con escala personalizada.

    public static void drawOption(SpriteBatch batch, BitmapFont font, GlyphLayout layout,
                                  String text, float screenWidth, float y, float scale) {
        font.getData().setScale(scale);
        layout.setText(font, text);
        float x = (screenWidth - layout.width) / 2f;
        font.draw(batch, text, x, y);
    }
}
