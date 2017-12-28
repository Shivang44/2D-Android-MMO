package com.zhinkk.mobilemmo;

import com.badlogic.gdx.utils.BinaryHeap;

/**
 * Created by Shivang on 12/28/2017.
 */

public class GraphNode extends BinaryHeap.Node {
    int f;
    int g;
    int h;
    int x;
    int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setF(int f) {
        this.f = f;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setH(int h) {
        this.h = h;
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
    public GraphNode(float value) {
        super(value);
    }




}
