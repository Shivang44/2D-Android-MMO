package com.zhinkk.mobilemmo;


/**
 * Created by Shivang on 12/28/2017.
 */

// TODO: Implement equals, string, hashcode
// Write equals() based on x,y position ONLY
public class GraphNode implements Comparable<GraphNode> {
    int f;
    int g;
    int h;
    int x;
    int y;

    public int getX() {
        return x;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }



    public void setF(int f) {
        this.f = f;
    }

    public void setG(int g) {
        this.g = g;
        this.f = this.g + this.h;       // Update F
    }

    public void setH(int h) {
        this.h = h;
        this.f = this.g + this.h;       // Update F
    }

    public int getF() {
        return f;
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }



    // Generates a graph node and sets "F" value (F = G + H in A* algorithm)
    public GraphNode(int g, int h) {
        this.g = g;
        this.h = h;
        this.f = g + h;
    }


    @Override
    public int compareTo(GraphNode graphNode) {
        return this.f - graphNode.f;    // TODO: Check if this is valid
    }
}
