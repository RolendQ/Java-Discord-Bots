package com.roland.whitehall.game;

import java.io.File;

public class Piece {
    public int x;
    public int y;
    public File pic;
    public boolean isJack;

    public Piece(File pic, boolean isJack, int x, int y) {
        this.pic = pic;
        this.isJack = isJack;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isJack() {
        return isJack;
    }

    public File getPic() {
        return pic;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPic(File pic) {
        this.pic = pic;
    }
}
