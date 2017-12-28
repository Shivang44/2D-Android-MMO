package com.zhinkk.mobilemmo;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BinaryHeap;

/**
 * Created by Shivang on 12/24/2017.
 */

public class PlayerMovement {
    private Sprite playerSprite;
    private  boolean moving;
    private Tile targetPos;
    private Tile startingPos;
    private Camera camera;
    private boolean isMoving;
    private Array<GraphNode> path;



    public PlayerMovement(Sprite sprite, Camera camera) {
        this.playerSprite = sprite;
        this.camera = camera;
        this.moving = false;
        this.targetPos = new Tile();
        this.startingPos = new Tile();
        this.path = new Array<GraphNode>();
    }

    // Finds shortest path from sprite's position to targetPosition using the A* algo.
    // Returns a boolean indicating whether path was found.
    public boolean findPath(Tile targetPos, TiledMapTileLayer walkableLayer) {
        // TODO: Use A* to fill the path array with a list of (x,y) points to walk along shortest path
        /* A* Algorithm definitions:

        *  F = G + H
        *  F = "score" of each walkable tile. The smaller the better.
        *  G = distance (in number of tiles) from starting point to current tile
        *  H = Heuristic, indicating the estimated cost from the current tile to the target tile.
        *      In our case we use "city-block" method to calculate sum(Vertical+Horizontal) tiles from
        *      the tile to the target tile. This is known to be a "good-enough" heuristic.
        *
        *  The "open list" is our minHeap, indicating tiles that have the potential to be in our shortest path.
        *  The "closed list" is our Array of Graph nodes, indicating the tiles already in the shortest path.
        *  An "adjacent tile" in our case is a tile that is to the top, right, bottom, or left of a tile.
        *       (Diagonal tiles are not considered. This may be changed in the future.)
        *  "Relaxing" refers to the process: Given a tile S, can the adjacent tile be reached
        *       faster (i.e. a smaller F-score) if it is reached through S, rather than its earlier path.
        *       If it can be, its F-score and path is updated so it is reached through S rather than
        *       whatever it was being reached through before.
        *       This "relaxing" process, along with the open/closed list, is the basis for A* and Dijkstra's Algorithm.
        *
        *  Each tile is given a F score. The algorithm is as follows:
        *    1. Add starting tile to minHeap.
        *    2. While heap is not empty,
        *    3.     Get minimum F-value tile, S, from minHeap (open list)
        *    4.     Add tile S to closed list (it is now in our shortest path)
        *    5.     For all adjacent tiles to S,
        *    6.         - If the tile is already in our closed list, ignore it
        *    7.         - If the tile is NOT in the open list (minHeap), compute its F-score and add it
        *    8.         - If the tile IS in the open list, "relax" it.
        *    9. Either we reached our target tile by now, or if the heap is empty and we haven't, target is not reachable.
        *
        */
        // Min heap used to get lowest F-value node every time (false in constructor indicates minHeap)
        BinaryHeap<GraphNode> minHeap = new BinaryHeap<GraphNode>(10000, false);
        this.startingPos.set(Math.round(playerSprite.getX()), Math.round(playerSprite.getY()));
        this.targetPos = targetPos;

        //
        minHeap.add(new GraphNode())


        return true;
    }

    public boolean isMoving() {
        return this.moving;
    }

    public void moveTo(Tile targetPos, TiledMapTileLayer walkableLayer) {
        moving = true;
    }

    public void handleMovement() {
        //if (!moving) return;
        //playerSprite.setPosition(Math.round(target.x), Math.round(target.y));
        //moving = true;
        // Gdx.app.log("playermovement", "Moving player!");
    }


}
