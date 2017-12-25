package com.zhinkk.mobilemmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Shivang on 12/24/2017.
 */

public class PlayerMovement {
    private Sprite playerSprite;
    private  boolean moving;
    private Vector3 target;
    private Camera camera;



    public PlayerMovement(Sprite sprite, Camera camera) {
        this.playerSprite = sprite;
        this.camera = camera;
        this.moving = false;
        this.target = new Vector3();
    }

    private void findPath() {
        // Find path from playerSprite to target
    }

    public void moveTo(int screenX, int screenY) {
        target.set(screenX, screenY, 0);
        camera.unproject(target);
        moving = true;
        findPath();
    }

    public void handleMovement() {
        if (!moving) return;
        // Gdx.app.log("playermovement", "Moving player!");
    }

    // Determines if the target location is moveable by the player
    boolean targetWalkable(Vector3 targetLocation) {
        return false;
    }

    // Moves the player to the target location
    public void moveTo(Vector3 targetLocation) {

    }
}
