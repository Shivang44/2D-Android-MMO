package com.zhinkk.mobilemmo.DataStructures;

/**
 * Created by Shivang on 12/27/2017.
 */

public class Tile {

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


    public Tile() {
        // Can be initialized later with set() method
    }

    public Tile(int x, int y) {
        this.x = x;
        this. y= y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // TODO:  hashCode()
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Tile)) return false;
        Tile other = (Tile) o;
        return other.x == this.x && other.y == this.y;

    }

    @Override
    public int hashCode() {
        return 0;
    }

}
