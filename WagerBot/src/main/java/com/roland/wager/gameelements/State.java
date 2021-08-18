package com.roland.wager.gameelements;

import java.util.ArrayList;

public class State {
    public Player cPlayer;
    public ArrayList<Player> players;
    public ArrayList<Card> deck;
    public DiscardPile[] discardPiles;

    public State(Player cPlayer, ArrayList<Player> players, ArrayList<Card> deck, DiscardPile[] discardPiles) {
        this.cPlayer = cPlayer;
        this.players = players;
        this.deck = deck;
        this.discardPiles = discardPiles;
    }

    public int getCurrentScore() {
        return getCurrentScore(cPlayer);
    }

    public int getCurrentScore(Player p) {
        int total = 0;
        for (PlayPile pp : p.getPlayPiles()) {
            int pileTotal = 0;
            if (pp.getCards().size() > 0) {
                pileTotal -= 20;
                for (Card c : pp.getCards()) {
                    // Wager is 0
                    pileTotal += c.getValue();
                }
                pileTotal *= pp.getWagersMultiplier();
                if (pp.getCards().size() >= 8) pileTotal += 20;
            }
            total += pileTotal;
        }
        return total;
    }

    public int getMinimumScore() {
        return getMinimumScore(cPlayer);
    }

    // Current score + playable cards in hand
    public int getMinimumScore(Player p) {
        int total = getCurrentScore(p);
        for (Card c : p.getHand()) {
            // Make sure it's in a started color AND playable
            if (p.getPlayPiles()[c.getColor()].getCards().size() > 0 && c.getValue() >= p.getPlayPiles()[c.getColor()].getHighestValue()) {
                total += p.getPlayPiles()[c.getColor()].getWagersMultiplier() * c.getValue();
            }
        }
        return total;
    }

    public int getMaximumScore() {
        return getMaximumScore(cPlayer);
    }

    // Highest possible score (including cards in opp's board)
    public int getMaximumScore(Player p) {
        int total = 0;

        Player opp = players.get(0);
        if (p == players.get(0)) opp = players.get(1);

        //for (int color = 0; color < 5; color++) {
        int color = 0;
        for (PlayPile pp : p.getPlayPiles()) {
            int pileTotal = 0;
            int num = 1;
            int pileCount = 0;
            while (num <= 10) {
                // Add if player has it
                if (pp.hasNum(num)) {
                    pileTotal += num;
                    pileCount++;
                }
                // Add if opponent doesn't have it
                if (num > pp.getHighestValue() && !opp.getPlayPiles()[color].hasNum(num)) {
                    pileTotal += num;
                    pileCount++;
                }
                num++;
            }
            color++;
            total += (pileTotal * pp.getWagersMultiplier());
            if (pileCount >= 8) total += 20;
        }
        return total;
    }


    public int getDemand(Card c) {
        return getDemand(c, cPlayer);
    }

    public int getDemand(Card c, Player p) {

        Player opp = players.get(0);
        if (p == players.get(0)) opp = players.get(1);

        if (opp.getPlayPiles()[c.getColor()].getHighestValue() > c.getValue()) return 0;

        if (c.isWager()) {
            if (opp.getPlayPiles()[c.getColor()].getWagersMultiplier() > 1) return 500;
            return 200; // TODO needs balancing
        }

        // Number

        // Perfect
        if (opp.getPlayPiles()[c.getColor()].getHighestValue() == c.getValue() - 1) return 1000;

        // 50 * number of wagers
        if (opp.getPlayPiles()[c.getColor()].getWagersMultiplier() > 0) return 50 * opp.getPlayPiles()[c.getColor()].getWagersMultiplier();

        return 10;
    }

    public Player getCurrentPlayer() {
        return cPlayer;
    }
}
