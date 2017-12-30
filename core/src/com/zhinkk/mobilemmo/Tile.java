package com.zhinkk.mobilemmo;

/**
 * Created by Shivang on 12/27/2017.
 */

public class Tile {
    int x;
    int y;

    Tile() {
        // Can be initialized later with set() method
    }

    Tile(int x, int y) {
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
