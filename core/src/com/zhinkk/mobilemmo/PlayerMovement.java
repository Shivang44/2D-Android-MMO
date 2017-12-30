package com.zhinkk.mobilemmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Graph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.zhinkk.mobilemmo.BinaryHeap;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Created by Shivang on 12/24/2017.
 */

public class PlayerMovement {
    private Sprite playerSprite;
    private  boolean moving;
    private Tile targetPos;
    private Tile startingPos;
    private Vector2 currentPos;
    private Vector2 targetPosFloat;
    private Camera camera;
    private boolean isMoving;
    private Array<GraphNode> path;
    private int currentPathIndex;

    private static double EPSILON = 0.0000001;


    public PlayerMovement(Sprite sprite, Camera camera) {
        this.playerSprite = sprite;
        this.camera = camera;
        this.moving = false;
        this.targetPos = new Tile();
        this.startingPos = new Tile();
        this.currentPos = new Vector2();
        this.targetPosFloat = new Vector2();
        this.currentPathIndex = 1; // 0th node is the starting tile
        this.path = new Array<GraphNode>();
    }

    // Returns all adjacent neighbours to s, setting their position and F values.
    private Array<GraphNode> neighbours(GraphNode S, TiledMapTileLayer walkableLayer) {
        Array<GraphNode> neighbourArray = new Array<GraphNode>();
        int g, h;

        // Tile above
        if (S.y + 1 < walkableLayer.getHeight() && walkableLayer.getCell(S.x, S.y+1) != null) {
            g = S.getG() + 1;
            h = Math.abs(targetPos.x - S.getX()) + Math.abs(targetPos.y - (S.getY()+1));
            GraphNode top = new GraphNode(g, h);
            top.setPosition(S.x, S.y +  1);
            neighbourArray.add(top);
        }

        // Tile to the right
        if (S.x + 1 < walkableLayer.getWidth() && walkableLayer.getCell(S.x+1, S.y) != null) {
            g = S.getG() + 1;
            h = Math.abs(targetPos.x - (S.getX()+1)) + Math.abs(targetPos.y - S.getY());
            GraphNode right = new GraphNode(g, h);
            right.setPosition(S.x+1, S.y);
            neighbourArray.add(right);
        }

        // Tile below
        if (S.y - 1 >= 0 && walkableLayer.getCell(S.x, S.y-1) != null) {
            g = S.getG() + 1;
            h = Math.abs(targetPos.x - S.getX()) + Math.abs(targetPos.y - (S.getY()-1));
            GraphNode below = new GraphNode(g, h);
            below.setPosition(S.x, S.y -  1);
            neighbourArray.add(below);
        }

        // Tile to the left
        if (S.x - 1 >= 0 && walkableLayer.getCell(S.x - 1, S.y) != null) {
            g = S.getG() + 1;
            h = Math.abs(targetPos.x - (S.getX()-1)) + Math.abs(targetPos.y - S.getY());
            GraphNode right = new GraphNode(g, h);
            right.setPosition(S.x-1, S.y);
            neighbourArray.add(right);
        }

        return neighbourArray;
    }

    // Finds shortest path from sprite's position to targetPosition using the A* algo.
    // Returns a boolean indicating whether path was found.
    public boolean findPath(Tile targetPos, TiledMapTileLayer walkableLayer) {
        Gdx.app.log("playermovement", "Player starting position is: " + "(" + playerSprite.getX() + ", " + playerSprite.getY() +")");
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
        PriorityQueue<GraphNode> minHeap = new PriorityQueue<GraphNode>();

        // Set starting and ending positions
        this.startingPos.set(Math.round(playerSprite.getX()), Math.round(playerSprite.getY()));
        this.targetPos = targetPos;

        int g, h; // Will be used for each node

        // 1. Add starting node to min heap
        g = 0; // Distance from starting node to starting node is 0
        h = Math.abs(targetPos.x - startingPos.x) + Math.abs(targetPos.y - startingPos.y);
        GraphNode startingNode = new GraphNode(g, h);
        startingNode.setPosition(startingPos.x, startingPos.y);
        minHeap.add(startingNode);

        // 2. While heap is not empty
        while (minHeap.size() > 0) {
            // 3. Get minimum F-value Tile, S, from open-list
            GraphNode S = minHeap.poll();



            // 4. Add tile S to closed list
            path.add(S);

            if (S.getX() == targetPos.x && S.getY() == targetPos.y) {
                for (GraphNode n : path) {
                    Gdx.app.log("playermovement", n.toString());
                }
                return true; // Reached target!
            }

            // 5.     For all adjacent tiles to S,
            // 6.         - If the tile is already in our closed list, ignore it
            // 7.         - If the tile is NOT in the open list , compute its F-score and add it
            // 8.         - If the tile IS in the open list, "relax" it.

            for (GraphNode neighbour : neighbours(S, walkableLayer)) {
                // 6. If tile is already in our closed list, ignore it
                if (!path.contains(neighbour, false)) {
                    if (!minHeap.contains(neighbour)) {
                        // 7. Otherwise, check if not in min heap. If not, compute F-score and add it.
                        minHeap.add(neighbour);
                    } else {
                        // If it is in the open list, check if we need to relax it.
                        // Remove the top/north element from minHeap
                        Iterator<GraphNode> it = minHeap.iterator();
                        GraphNode s = null;
                        while (it.hasNext()) {
                            s = it.next();
                            if (s.equals(neighbour)) {
                                it.remove();
                                break;
                            }
                        }

                        // Relax it if needed
                        if (S.getG() + 1 < s.getG()) {
                            s.setG(S.getG() + 1);
                        }

                        // Add back to min heap
                        minHeap.add(s);
                    }
                }
            }
        }

        Gdx.app.log("playermovement", path.toString());
        return false;   // Target not found!
    }

    public boolean isMoving() {
        return this.moving;
    }

    public void startMoving() {
        moving = true;
    }

    boolean reachedTargetTile() {
        return playerSprite.getX() >= targetPos.x && playerSprite.getX() <= targetPos.x + 1
                && playerSprite.getY() >= targetPos.y && playerSprite.getY() <= targetPos.y + 1;
    }

    boolean reachedNextTileInPath() {
        return playerSprite.getX() >= path.get(currentPathIndex).getX() && playerSprite.getX() <= path.get(currentPathIndex).getX() + 1
                && playerSprite.getY() >= path.get(currentPathIndex).getY() && playerSprite.getY() <= path.get(currentPathIndex).getY() + 1;
    }

    public void handleMovement() {
        if (!moving) return;

        if (reachedTargetTile()) {
            Gdx.app.log("playermovement", "reached target!");
            // Move playerSprite to exact tile position (e.g. (x,y) = (1,1) rather than (1.001, 1.003))
            playerSprite.setPosition(targetPos.x, targetPos.y);
            moving = false; // Finish current movement
            path = new Array<GraphNode>();  // Clear shortest path to target
            currentPathIndex = 1;   // Reset current path in shortest path
            return;
        }

        if (reachedNextTileInPath()) {
            Gdx.app.log("playermovement", "Player reached next tile in path. Players position is: (" + playerSprite.getX() + ", " + playerSprite.getY() +")");
            currentPathIndex++;
        }

        // Next tile in path is to right
        if (playerSprite.getX() < path.get(currentPathIndex).getX()) {
            playerSprite.translateX(3 * Gdx.graphics.getDeltaTime());
            return;
        }

        // Next tile in path is to left
        if (playerSprite.getX() > path.get(currentPathIndex).getX()) {
            playerSprite.translateX(-3 * Gdx.graphics.getDeltaTime());
            return;
        }

        // Next tile in path is above
        if (playerSprite.getY() < path.get(currentPathIndex).getY()) {
            playerSprite.translateY(3 * Gdx.graphics.getDeltaTime());
            return;
        }

        // Next tile in path is below
        if (playerSprite.getY() > path.get(currentPathIndex).getY()) {
            playerSprite.translateY(-3 * Gdx.graphics.getDeltaTime());
            return;
        }

    }


}
