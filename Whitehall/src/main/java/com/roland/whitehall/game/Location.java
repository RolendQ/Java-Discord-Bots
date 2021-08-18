package com.roland.whitehall.game;

import com.roland.whitehall.core.Utils;
import com.roland.whitehall.enums.LocationType;

import java.util.ArrayList;

public class Location extends Node implements Comparable {
    public int id;
    public int type;
    public ArrayList<Alley> alleys;
    public ArrayList<WaterBlock> waterBlocks;

    public Location(int id) {
        this.id = id;
        this.type = LocationType.MID; // default
        alleys = new ArrayList<Alley>();
    }

    public ArrayList<Location> getAdjLoc() {
        return Utils.getAdjLoc(this);
    }

    @Override
    public int compareTo(Object l2) {
        if (l2 instanceof Location) {
            return id - ((Location) l2).id;
        }
        return 0;
    }

    public boolean hasSameAlley(Location loc) {
        for (Alley alley : alleys) {
            if (loc.alleys.contains(alley)) return true;
        }
        return false;
    }

    public boolean hasSameWaterBlock(Location loc) {
        if (waterBlocks == null || loc.waterBlocks == null) return false;

        for (WaterBlock waterBlock : waterBlocks) {
            if (loc.waterBlocks.contains(waterBlock)) return true;
        }
        return false;
    }

//    public boolean isAdjLoc(int locID) {
//        for (Location loc : Utils.getAdjLoc(this)) {
//            if (loc.id == locID) return true;
//        }
//        return false;
//    }
}
