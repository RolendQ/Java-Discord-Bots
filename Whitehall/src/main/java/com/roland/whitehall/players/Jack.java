package com.roland.whitehall.players;

import com.roland.whitehall.core.Utils;
import com.roland.whitehall.game.Location;
import com.roland.whitehall.game.Piece;

import java.awt.*;

public class Jack extends Player {
    public int alleys = 2;
    public int boats = 2;
    public int coaches = 2;

    public Jack(Color color, String playerID, Piece piece, String displayName) {
        super(color, playerID, piece, displayName);
    }

    public void setCurrentNode(int id) {
        this.currentNode = Utils.ls[id];

        piece.setX(Utils.locationCoords[id][0]);
        piece.setY(Utils.locationCoords[id][1]);
    }

    public Location getCurrentNode() {
        return (Location) currentNode;
    }

    public int getTotalLeft() {
        return alleys+boats+coaches;
    }
}
