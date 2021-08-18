package com.roland.wager.gameelements;

import com.roland.wager.bot.GlobalVars;
import com.roland.wager.bot.Utils;

import java.util.ArrayList;

public class PlayPile {
    public ArrayList<Card> cards = new ArrayList<Card>();
    public int highestValue = 0;
    public int color;

    public PlayPile(int color) {
        this.color = color;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public int getHighestValue() {
        return highestValue;
    }

    public int getWagersMultiplier() {
        int multiplier = 1;
        for (Card c : cards) {
            if (c.isWager()) multiplier++;
        }
        return multiplier;
    }

    public boolean addCard(Card c) {
        if (c.getValue() >= highestValue) {
            cards.add(c);
            highestValue = c.getValue();
            return true;
        }
        return false;
    }

    public Card removeLastCard() {
        Card c = cards.remove(cards.size()-1);
        if (cards.size() > 0) highestValue = cards.get(cards.size()-1).getValue();
        else highestValue = 0;
        return c;
    }

    public boolean hasNum(int num) {
        for (Card c : cards) {
            if (c.getValue() == num) return true;
        }
        return false;
    }

    public String toString(Player p) {
        String s;
        if (p.getPlayPiles()[color] == this) s = p.getEmoji(color)+"** **";
        else s = p.getNextPlayer().getEmoji(color)+"** **";

        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            if (i == cards.size()-1) {
                s += " __"+c.getValueString()+"__";
            } else {
                s += " "+c.getValueString();
            }
        }
        // Add last played indicator
        if (p.getPlayPiles()[color] == this && p.getLastActionString().startsWith("Play") && p.getLastDropColor() == color) {
            s += "*****"; // one bold star
        }
        return s;
    }
}
