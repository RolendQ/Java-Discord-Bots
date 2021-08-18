package com.roland.whitehall.players;

import com.roland.whitehall.core.Utils;
import com.roland.whitehall.game.Crossing;
import com.roland.whitehall.game.Location;
import com.roland.whitehall.game.Piece;

import java.awt.*;

public class Detective extends Player {
    public boolean canMove;
    public boolean canSearch;
    public boolean canArrest;
    public boolean hasAbility;

    public Detective(Color color, String playerID, Piece piece, String displayName) {
        super(color, playerID, piece, displayName);
        hasAbility = true;
    }

    public void setCurrentNode(int id) {
        this.currentNode = Utils.cs[id];

        piece.setX(Utils.crossingCoords[id][0]);
        piece.setY(Utils.crossingCoords[id][1]);
    }

    public Crossing getCurrentNode() {
        return (Crossing) currentNode;
    }

    public boolean canMove() {return canMove;}

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean hasAbility() {
        return hasAbility;
    }

    public void setHasAbility(boolean hasAbility) {
        this.hasAbility = hasAbility;
    }

    public void setCanSearch(boolean canSearch) {
        this.canSearch = canSearch;
    }

    public boolean canSearch() {
        return canSearch;
    }

    public void setCanArrest(boolean canArrest) {
        this.canArrest = canArrest;
    }

    public boolean canArrest() {
        return canArrest;
    }

    public String getColorString() {
        if (color == Color.YELLOW) return "(Y)";
        if (color == Color.BLUE) return "(B)";
        if (color == Color.RED) return "(R)";
        return "";
    }
}
