package com.roland.wager.gameelements;

import com.roland.wager.bot.GlobalVars;
import net.dv8tion.jda.core.entities.User;

public class Player {
    public String playerID;
    public User user;
    public int playerNumber; // 1-4
    public Card[] hand;
    public PlayPile[] playPiles = {new PlayPile(0), new PlayPile(1), new PlayPile(2), new PlayPile(3), new PlayPile(4)};
    public int emptyIndex;
    public boolean dropped = false;
    public String name;
    public Player nextPlayer;
    public int lastDropColor; // Both discard and play
    public String lastActionString = "";
    public Card lastDrawnCard;

    public String[] emojiSet;
    public String gambleEmoji;
//
//    public int playedWagers = 0;
//    public int startedColors = 0;

    public String linkedCommand = null;

    public Player(User user, String playerID, int playerNumber, String name, String emojiType) {
        this.user = user;
        this.playerID = playerID;
        this.playerNumber = playerNumber;
        this.name = name;
        this.emojiSet = GlobalVars.colorEmojis.get(emojiType);
        this.lastDropColor = -1;
        this.lastDrawnCard = null;
    }

//    public int calculatePoints() {
//        int total = 0;
//        for (PlayPile pp : playPiles) {
//            int pileTotal = 0;
//            if (pp.getCards().size() > 0) {
//                pileTotal -= 20;
//                for (Card c : pp.getCards()) {
//                    // Wager is 0
//                    pileTotal += c.getValue();
//                }
//                pileTotal *= pp.getWagersMultiplier();
//                if (pp.getCards().size() >= 8) pileTotal += 20;
//            }
//            total += pileTotal;
//        }
//        return total;
//    }

    public void clearHand() {
        hand = new Card[8];
    }

    public int getLastDropColor() {
        return lastDropColor;
    }

    public void setLastDropColor(int lastDropColor) {
        this.lastDropColor = lastDropColor;
    }

    public void setLastDrawnCard(Card lastDrawnCard) {
        this.lastDrawnCard = lastDrawnCard;
    }

    public Card getLastDrawnCard() {
        return lastDrawnCard;
    }

    public User getUser() {
        return user;
    }
    public Player getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public String getName() {
        return name;
    }

    public Card[] getHand() {
        return hand;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setEmptyIndex(int emptyIndex) {
        this.emptyIndex = emptyIndex;
    }

    public int getEmptyIndex() {
        return emptyIndex;
    }

    public PlayPile[] getPlayPiles() {
        return playPiles;
    }

    public boolean hasDropped() {
        return dropped;
    }

    public void setDropped(boolean dropped) {
        this.dropped = dropped;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setLinkedCommand(String linkedCommand) {
        this.linkedCommand = linkedCommand;
    }

    public String getLinkedCommand() {
        return linkedCommand;
    }

    public boolean isLinking() {
        return linkedCommand != null;
    }

    public String getLastActionString() {
        return lastActionString;
    }

    public void setLastActionString(String lastActionString) {
        this.lastActionString = lastActionString;
    }

    public void addLastActionString(String s) {
        this.lastActionString += "\n"+s;
    }

    public int getPlayedWagers() {
        int total = 0;
        for (PlayPile pp : playPiles) {
            total += pp.getWagersMultiplier() - 1;
        }
        return total;
    }

    public int getStartedColors() {
        int total = 0;
        for (PlayPile pp : playPiles) {
            if (pp.getCards().size() > 0) total++;
        }
        return total;
    }

    public String getEmoji(int color) {
        return emojiSet[color];
    }

    public String getGambleEmoji() {
        return gambleEmoji;
    }
}
