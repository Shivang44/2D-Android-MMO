package com.zhinkk.mobilemmo.DataStructures;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by Shivang on 1/1/2018.
 */

public class Sprite extends com.badlogic.gdx.graphics.g2d.Sprite {
    ObjectMap<String, Animation<TextureRegion>> animationMap;
    float animationTime;
    Texture idleTexture;

    public Sprite(Texture idleTexture, ObjectMap<String, Animation<TextureRegion>> animationMap) {
        super(idleTexture);
        this.animationMap = animationMap;
        this.idleTexture = idleTexture;
    }

    /* Sets the region of the animation sheet to match the current animation. */
    public void playAnimation(String animationName) {
        TextureRegion currentFrame = animationMap.get(animationName).getKeyFrame(animationTime, true);
        this.setRegion(currentFrame);
        animationTime += Gdx.graphics.getDeltaTime();
    }

    /* Sets the region to the "idle" animation. */
    public void stopAnimation() {
        this.setRegion(animationMap.get("downWalk").getKeyFrame(0.40f));
    }



}
