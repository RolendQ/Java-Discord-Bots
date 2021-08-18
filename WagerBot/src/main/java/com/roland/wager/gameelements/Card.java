package com.roland.wager.gameelements;

import com.roland.wager.bot.Utils;

public class Card implements Comparable<Card>{
    public int value; // 0 is wager
    public int color; // 0-4

    public Card(int value, int color) {
        this.value = value;
        this.color = color;
    }

    public boolean isWager() {
        return value == 0;
    }

    public int getValue() {
        return value;
    }

    public String getValueString() {
        if (isWager()) return ":game_die:";
        return "**"+value+"**";
    }

    public int getColor() {
        return color;
    }

    public String toString(Player p) {
        //return Utils.numToColorString[color]+" "+getValueString();
        return p.getEmoji(color)+" "+getValueString();
    }

    public int compareTo(Card c) {
        if (color == c.getColor()) return value - c.getValue();
        return color - c.getColor();
    }
}
