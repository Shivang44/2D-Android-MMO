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
        super();
        this.animationMap = animationMap;
        this.idleTexture = idleTexture;
    }

    public void playAnimation(String animationName) {
        TextureRegion currentFrame = animationMap.get(animationName).getKeyFrame(animationTime);
        this.setRegion(currentFrame);   // TODO: Does this do what it seems? https://stackoverflow.com/questions/35135667/how-to-convert-a-textureregion-to-a-texture-to-set-a-sprites-texture
        animationTime += Gdx.graphics.getDeltaTime();
    }

    public void stopAnimation() {
        // TODO: Do we need to clear the previous setRegion call somehow before setting the texture again?
        this.setTexture(idleTexture);
    }



}
