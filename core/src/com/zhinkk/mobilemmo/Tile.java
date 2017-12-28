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

    // TODO: Implement equals() and hashCode()
    @Override
    public boolean equals(Object other) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
