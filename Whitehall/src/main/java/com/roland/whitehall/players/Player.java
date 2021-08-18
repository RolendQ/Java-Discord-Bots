package com.roland.whitehall.players;

import com.roland.whitehall.game.Node;
import com.roland.whitehall.game.Piece;
import com.roland.whitehall.core.Utils;

import java.awt.*;

public class Player {

    public Color color;
    public String playerID;
    public Piece piece;
    public Player nextPlayer;
    public String displayName;

    public Node currentNode;
//    public boolean canSearch;
//    public boolean canArrest;
//    public boolean hasAbility;

    public Player(Color color, String playerID, Piece piece, String displayName) {
        this.color = color;
        this.playerID = playerID;
        this.piece = piece;
        this.displayName = displayName;
        this.currentNode = null;
        //this.hasAbility = true;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(int id) {
        // Overrided
    }


    public Piece getPiece() {
        return piece;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getColor() {
        return color.toString();
    }
}
