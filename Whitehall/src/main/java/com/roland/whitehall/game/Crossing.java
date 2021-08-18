package com.roland.whitehall.game;

import com.roland.whitehall.core.Utils;

import java.util.ArrayList;
import java.util.HashSet;

public class Crossing extends Node implements Comparable {
    public Crossing(Node[] adj) {
        //this.adj = adj;
    }

    public Crossing() {

    }

    @Override
    public int compareTo(Object c2) {
        if (c2 instanceof Crossing) {
            return getTempID() - ((Crossing) c2).getTempID();
        }
        return 0;
    }

    public int getTempID() {
        return Utils.indexOfCrossing(this);
    }

    public ArrayList<Crossing> getAdjCro() {
        return Utils.getAdjCro(this);
    }

    public HashSet<Crossing> getAllMoves() {
        return Utils.getAllMoves(this);
    }

    public ArrayList<Location> getAdjLoc() {
        return Utils.getAdjLoc(this);
    }
}
