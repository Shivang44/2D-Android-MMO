package com.zhinkk.mobilemmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
//import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.zhinkk.mobilemmo.DataStructures.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.Iterator;
import java.util.PriorityQueue;

import com.zhinkk.mobilemmo.DataStructures.GraphNode;
import com.zhinkk.mobilemmo.DataStructures.Tile;

/**
 * Created by Shivang on 12/24/2017.
 */

public class PlayerMovement {
    private Sprite playerSprite;
    private boolean moving;
    private Tile targetPos;
    private Tile startingPos;
    private Tile previousTile;
    private GraphNode path;
    private float timeSinceStartedMoving;
    private static double movementSpeed = 0.5;
    private String previousDirection;
    private Vector2 previousPosition;


    /* Constructor sets up the player sprite, target position, and starting position. */
    public PlayerMovement(Sprite sprite, Camera camera) {
        this.playerSprite = sprite;
        this.moving = false;
        this.targetPos = new Tile();
        this.startingPos = new Tile();
        this.previousPosition = new Vector2(playerSprite.getX(), playerSprite.getY());

    }

    /*
     * For a given node S, discovers all adjacent (non-diagonal) walkable tiles, sets their position
     * and F-score (for use in A* path finding), and returns them in an array.
     *
     * @params
     *      S, the graph node for which to discover adjacent tiles
     *      walkableLayer, a tile layer that contains user-walkable tiles to check against
     *
     * @returns
     *      An array containing all adjacent walkable tiles, with each tile's F-score and position calculated
     *
     */
    private Array<GraphNode> neighbours(GraphNode S, TiledMapTileLayer walkableLayer) {
        Array<GraphNode> neighbourArray = new Array<GraphNode>();
        int g, h;

        // Tile above
        if (S.getY() + 1 < walkableLayer.getHeight() && walkableLayer.getCell(S.getX(), S.getY()+1) != null) {
            // Calculate F = G + H score
            g = S.getG() + 1;
            h = Math.abs(targetPos.getX() - S.getX()) + Math.abs(targetPos.getY() - (S.getY()+1));
            GraphNode top = new GraphNode(g, h);

            // Set position
            top.setPosition(S.getX(), S.getY() +  1);

            // Add to array we will return
            neighbourArray.add(top);
        }

        // Tile to the right
        if (S.getX() + 1 < walkableLayer.getWidth() && walkableLayer.getCell(S.getX()+1, S.getY()) != null) {
            g = S.getG() + 1;
            h = Math.abs(targetPos.getX() - (S.getX()+1)) + Math.abs(targetPos.getY() - S.getY());
            GraphNode right = new GraphNode(g, h);
            right.setPosition(S.getX()+1, S.getY());
            neighbourArray.add(right);
        }

        // Tile below
        if (S.getY() - 1 >= 0 && walkableLayer.getCell(S.getX(), S.getY()-1) != null) {
            g = S.getG() + 1;
            h = Math.abs(targetPos.getX() - S.getX()) + Math.abs(targetPos.getY() - (S.getY()-1));
            GraphNode below = new GraphNode(g, h);
            below.setPosition(S.getX(), S.getY() -  1);
            neighbourArray.add(below);
        }

        // Tile to the left
        if (S.getX() - 1 >= 0 && walkableLayer.getCell(S.getX() - 1, S.getY()) != null) {
            g = S.getG() + 1;
            h = Math.abs(targetPos.getX() - (S.getX()-1)) + Math.abs(targetPos.getY() - S.getY());
            GraphNode right = new GraphNode(g, h);
            right.setPosition(S.getX()-1, S.getY());
            neighbourArray.add(right);
        }

        return neighbourArray;
    }

    /*
     * Given a singly linked-list with "parent" pointers, reverses the linked list and returns the new head.
     *
     * @params
     *      head, the linked-list to reverse
     *
     * @returns
     *      the new head of the reversed linked-list
     *
     * @ensures
     *      a linked-list (target ==  head), Start <- Start+1 <- Start+2 <- ... <- Target, is altered to become:
     *                     Start -> Start+1 -> Start+2 -> ... -> Target, and Start is returned as the new head
     *
     */
    private GraphNode reverseLinkedList(GraphNode head) {
        if (head == null || head.getParent() == null) return head;
        GraphNode prev = null;
        GraphNode cur = head;
        while (cur != null) {
            GraphNode curNext = cur.getParent();
            cur.setParent(prev);
            prev = cur;
            cur = curNext;
        }
        return prev;
    }

    /*
     * Finds the single-source shortest path using the A* path finding algorithm (a modification to Dijkstra's).
     * Returns true if path is found and generated. See comment below for a in-depth overview of algorithm.
     *
     * @params
     *      targetPos, a Tile indicating the tile the player wants to walk to
     *      walkableLayer, a layer containing the walkable tiles
     *
     * @returns
     *      true if a path is found and generated, false otherwise
     *
     * @ensures
     *      this.startingPos contains the user's starting position
     *      this.targetPos contains the user's desired tile to wallk to
     *      this.path contains the user-walkable shortest path from the starting position to end position
     *              this path is a linked list of graph nodes, from start+1 -> ... -> target (starting node is skipped)
     *
     */
    public boolean findPath(Tile targetPos, TiledMapTileLayer walkableLayer) {
        /* A* Algorithm definitions:

        *  F = G + H
        *  F = "score" of each walkable tile. The smaller the better.
        *  G = distance (in number of tiles) from starting point to current tile
        *  H = Heuristic, indicating the estimated cost from the current tile to the target tile.
        *      In our case we use "city-block" method to calculate sum(Vertical+Horizontal) tiles from
        *      the tile to the target tile. This is known to be a "good-enough" heuristic.
        *
        *  The "open list" is our minHeap, indicating tiles that have the potential to be in our shortest path.
        *  The "closed list" is our Array of Graph nodes, indicating the tiles that have the shortest path to them already found.
        *       This means no further relaxing will be done on those tiles.
        *  An "adjacent tile" in our case is a tile that is to the top, right, bottom, or left of a tile.
        *       (Diagonal tiles are not considered. This may be changed in the future.)
        *  "Relaxing" refers to the process: Given a tile S, can the adjacent tile be reached
        *       faster (i.e. a smaller F-score) if it is reached through S, rather than its earlier path.
        *       If it can be, its F-score and path is updated so it is reached through S rather than
        *       whatever it was being reached through before. The parent is also updated.
        *       This "relaxing" process, along with the open/closed list, is the basis for A* and Dijkstra's Algorithm.
        *
        *  Each tile is given a F score. The algorithm is as follows:
        *    1. Add starting tile to minHeap.
        *    2. While heap is not empty,
        *    3.     Get minimum F-value tile, S, from minHeap (open list)
        *    4.     Add tile S to closed list (it is now maximally relaxed; The shortest path to it is discovered.)
        *    5.     For all adjacent tiles to S,
        *    6.         - If the tile is already in our closed list, ignore it
        *    7.         - If the tile is NOT in the open list (minHeap), compute its F-score, set parent, and add it.
        *    8.         - If the tile IS in the open list, "relax" it. Update parent if relaxing results in a shorter path to tile.
        *    9. Either we reached our target tile by now, or if the heap is empty and we haven't, target is not reachable.
        *    10. If found target, generate shortest path by backtracking from target to starting point by
        *           following parent pointers in each tile/GraphNode. This is logically the same as reversing the singly linked list.
        *
        */

        ObjectSet<GraphNode> closedList = new ObjectSet<GraphNode>(); // We use a hash set to achieve O(1) lookup and add

        // Min heap used to get lowest F-value node every time (false in constructor indicates minHeap)
        PriorityQueue<GraphNode> minHeap = new PriorityQueue<GraphNode>();

        // Set starting and ending positions
        this.startingPos.set(Math.round(playerSprite.getX()), Math.round(playerSprite.getY()));
        this.targetPos = targetPos;

        int g, h; // Will be used for each node

        // 1. Add starting node to min heap
        g = 0; // Distance from starting node to starting node is 0
        h = Math.abs(targetPos.getX() - startingPos.getX()) + Math.abs(targetPos.getY() - startingPos.getY());
        GraphNode startingNode = new GraphNode(g, h);
        startingNode.setPosition(startingPos.getX(), startingPos.getY());
        minHeap.add(startingNode);

        // 2. While heap is not empty
        while (minHeap.size() > 0) {
            // 3. Get minimum F-value Tile, S, from open-list
            GraphNode S = minHeap.poll();



            // 4. Add tile S to closed list
            closedList.add(S);

            // Check if we reached our target
            if (S.getX() == targetPos.getX() && S.getY() == targetPos.getY()) {

                /* Reversing the parent pointers in the linked list (target -> t-1 -> t-2 -> ... -> start)
                *  allows us to generate the shortest path from the start to the target (start -> s+1 -> ... -> target)
                */

                this.path = reverseLinkedList(S);
                this.path = this.path.getParent(); // Skip starting node
                return true; // Reached target!
            }

            // 5.     For all adjacent tiles to S,
            // 6.         - If the tile is already in our closed list, ignore it
            // 7.         - If the tile is NOT in the open list , compute its F-score and add it
            // 8.         - If the tile IS in the open list, "relax" it.

            for (GraphNode neighbour : neighbours(S, walkableLayer)) {
                // 6. If tile is already in our closed list, ignore it
                if (!closedList.contains(neighbour)) {
                    if (!minHeap.contains(neighbour)) {
                        // 7. Otherwise, check if not in min heap. If not, compute F-score, set parent, and add it.
                        neighbour.setParent(S);
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
                            s.setParent(S);
                            s.setG(S.getG() + 1);
                        }

                        // Add back to min heap
                        minHeap.add(s);
                    }
                }
            }
        }

        return false;   // Target not found!
    }

    // Simply returns whether player is currently moving
    public boolean isMoving() {
        return this.moving;
    }

    public String getMoveDirection() {
        if (previousPosition.x < playerSprite.getX()) {
            previousDirection = "right";
        } else if (previousPosition.x > playerSprite.getX()) {
            previousDirection = "left";
        } else if (previousPosition.y < playerSprite.getY()) {
            previousDirection = "up";
        } else if (previousPosition.y > playerSprite.getY()) {
            previousDirection = "down";
        }

        previousPosition.set(playerSprite.getX(), playerSprite.getY());
        return previousDirection;

       /*
        if (path.getParent() == null) {
            return previousDirection;
        }

        if (path.getParent().getX() > previousTile.getX()) {
            previousDirection = "right";
            return previousDirection;
        } else if (path.getParent().getX() < previousTile.getX()) {
            previousDirection = "left";
            return previousDirection;
        } else if (path.getParent().getY() > previousTile.getY()) {
            previousDirection = "up";
            return previousDirection;
        } else {
            previousDirection = "down";
            return previousDirection;
        }
        */
    }

    // Starts the movement process
    public void startMoving() {
        this.moving = true;
        this.timeSinceStartedMoving = 0;
        this.previousTile = new Tile(startingPos.getX(), startingPos.getY());
    }

    /*
     * If currently moving (i.e. the user tapped a movable location and we found a walkable path to that location),
     * this method will move along each node/tile in this.path (a singly linked-list) until it reaches the last
     * node/tile (the user's target location).
     */
    public void handleMovement() {
        if (!moving) return;

        /* This logic allows us to translate from one tile to another over some time (i.e. 5 seconds), as defined by movement speed.
         * timeSinceStartedMoving interpolates the user's position from start to end, meaning a value of 0 is the start, 0.5 is
         * in the middle, and 1.0 is the end. Thus, when t=1.0, we can have reached the next node/tile in the path and can move
         * onto translating the user to the next node/tile, and so on, until the target node/tile is reached.
         */
        if (timeSinceStartedMoving < 1.0f) {
            timeSinceStartedMoving += Gdx.graphics.getDeltaTime() / movementSpeed;
            float currentPosX = ((path.getX() - previousTile.getX())*timeSinceStartedMoving) + previousTile.getX();
            float currentPosY = ((path.getY() - previousTile.getY())*timeSinceStartedMoving) + previousTile.getY();
            playerSprite.setPosition(currentPosX, currentPosY);
        } else if (timeSinceStartedMoving >= 1.0f && path.getParent() == null) {
            // Reached target tile
            // Move playerSprite to exact tile position (e.g. (x,y) = (1,1) rather than (1.001, 1.003))
            playerSprite.setPosition(targetPos.getX(), targetPos.getY());
            moving = false; // Finish current movement
        } else {
            // Reached next tile
            previousTile.set(path.getX(), path.getY());
            path = path.getParent();
            timeSinceStartedMoving = 0;
        }

        /*
        * if (moving forward) playerSprite.animate("forward")
        * ..
        * ..
        * ..
        *
         */

    }


}
