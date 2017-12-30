package com.zhinkk.mobilemmo.DataStructures;



/**
 * Created by Shivang on 12/28/2017.
 *
 * This class is mainly to be used with a PriortyQueue in the context of the A* graph search algorithm.
 * It includes fields for the position (x,y) along with score-values that are used in the A* graph search algorithm (f, g, h).
 * It implements
 */

public class GraphNode implements Comparable<GraphNode> {
    int f;
    int g;
    int h;
    int x;
    int y;

    public GraphNode getParent() {
        return parent;
    }

    GraphNode parent;

    public void setParent(GraphNode parent) {
        this.parent = parent;
    }

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

    @Override
    public String toString() {
        return "[(x,y) = " + this.x + ", " + this.y + "),   (f, g, h) = (" + this.f + ", " + this.g + ", " + this.h + ")";
    }



    @Override
    public int hashCode() {
        return 31 * x + y;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof GraphNode)) {

            return false;
        }

        GraphNode other = (GraphNode) o;

        return other.getX() == this.getX() && other.getY() == this.getY();
    }


}
