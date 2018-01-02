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

    public void playAnimation(String animationName) {
        TextureRegion currentFrame = animationMap.get(animationName).getKeyFrame(animationTime, true);
        this.setRegion(currentFrame);
        animationTime += Gdx.graphics.getDeltaTime();
    }

    public void stopAnimation() {
        // TODO: Do we need to clear the previous setRegion call somehow before setting the texture again?
        this.setRegion(animationMap.get("downWalk").getKeyFrame(0.40f));
    }



}
