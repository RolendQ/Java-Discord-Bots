package com.roland.bang.game;

import java.util.Random;

public class Die {
    public static final int ARROW = 0;
    public static final int DYNAMITE = 1;
    public static final int SHOOT_1 = 2;
    public static final int SHOOT_2 = 3;
    public static final int GATLING = 4;
    public static final int BEER = 5;

    public int currentState = -1;
    public int id;

    public Die(int id) {
        this.id = id;
    }

    public void setCurrentState(int state) {
        this.currentState = state;
    }

    public int roll(boolean isReroll) {
        if (isReroll && currentState == DYNAMITE) { // Cannot reroll dynamites
            return currentState;
        }
        Random r = new Random();
        currentState = r.nextInt(6);
        //System.out.println("Rolled Die#"+id+": "+currentState);
        return currentState;
    }
}
