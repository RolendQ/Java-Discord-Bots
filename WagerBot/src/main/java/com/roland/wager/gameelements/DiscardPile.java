package com.roland.wager.gameelements;

import com.roland.wager.bot.Utils;

import java.util.ArrayList;

public class DiscardPile {
    public ArrayList<Card> cards = new ArrayList<Card>();
    public int color;

    public DiscardPile(int color) {
        this.color = color;
    }

    public void addCard(Card c) {
        cards.add(c);
    }
    public Card removeTop() {
        return cards.remove(cards.size()-1);
    }

    public Card peekTop() {
        return cards.get(cards.size()-1);
    }

    public int getSize() {
        return cards.size();
    }

    public String toString(Player p) {
        String s = p.getNextPlayer().getEmoji(color)+"** **"; // Shows correct player's discard pile emoji
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            if (i == cards.size()-1) { // Last card
                s += " __"+c.getValueString()+"__";
            } else {
                s += " "+c.getValueString();
            }
        }
        // Add last played indicator
        if (p.getLastActionString().startsWith("Discard") && p.getLastDropColor() == color) {
            s += "*****"; // one bold star
        }
        return s;
    }
}
