package com.zhinkk.mobilemmo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Created by Shivang on 12/19/2017.
 */

public class MobileMMO extends Game {
    public SpriteBatch batch;
    public FreeTypeFontGenerator generator;
    public FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    public BitmapFont font12;

    /**
     * Called when the Application is first created.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();

        // Generate Open Sans font, size 12, for use in app.
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/open-sans/OpenSans-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 22;
        font12 = generator.generateFont(parameter);
        generator.dispose();

        this.setScreen(new MainMenuScreen(this));



    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font12.dispose();
        this.getScreen().dispose(); // Call dispose on last loaded screen
    }
}
