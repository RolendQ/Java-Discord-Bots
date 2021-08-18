package com.roland.wager.core;

import com.roland.wager.gameelements.Card;
import com.roland.wager.gameelements.Player;
import com.roland.wager.gameelements.State;

public class Computer {
    public static int sacrificeAmount = 4;
    public static int sacrificeIncrementRate = 5;

    public static int getBestDrop(Game game) {
        Player cPlayer = game.getCurrentPlayer();
        int choice = 1;
        int maxMaxScore = game.getMaximumScore();
        int bestChoice = -1;

        // Play wagers if less than 5 and started less than 3 colors
        if (cPlayer.getPlayedWagers() < 5 && cPlayer.getStartedColors() < 3) {
            while (choice <= 8) {
                //System.out.println(state.players.get(0).name + ", " + state.players.get(1).name);
                Card card = cPlayer.getHand()[choice - 1];
                if (cPlayer.getPlayPiles()[card.getColor()].addCard(card)) {
                    int newMaxScore = game.getMaximumScore();
                    if (newMaxScore > maxMaxScore) {
                        maxMaxScore = newMaxScore;
                        bestChoice = choice;
                    }
                    // Reset
                    cPlayer.getPlayPiles()[card.getColor()].removeLastCard();
                }
                choice++;
            }
        }

        // No way to increase max score [wager]
        if (bestChoice == -1) {
            choice = 1;
            int maxCurrScore = game.getCurrentScore();
            int origMaxScore = game.getMaximumScore();
            while (choice <= 8) {
                Card card = cPlayer.getHand()[choice-1];
                if (cPlayer.getPlayPiles()[card.getColor()].addCard(card)) {
                    int buffer = sacrificeAmount + (game.getTurn() / sacrificeIncrementRate); // Most sacrifices later in game
                    if (game.getCardsLeft() < 10) { // Play more when running out of turns
                        App.logger.info("CPU Note: Running out of turns");
                        buffer += 10 - game.getCardsLeft();
                    }
                    if (!(game.getMaximumScore() + (cPlayer.getPlayPiles()[card.getColor()].getWagersMultiplier() * buffer) < origMaxScore)) {
                        int newCurrScore = game.getCurrentScore();
                        if (newCurrScore > maxCurrScore) {
                            maxCurrScore = newCurrScore;
                            bestChoice = choice;
                        }
                    }
                    // Reset
                    cPlayer.getPlayPiles()[card.getColor()].removeLastCard();
                }
                choice++;
            }
        } else {
            App.logger.info("Path: Increase Max Score");
            return bestChoice;
        }

        // Discard a card
        if (bestChoice == -1) {
            choice = 1;
            int maxDifference = -100000;
            while (choice <= 8) {
                Card card = cPlayer.getHand()[choice - 1];
                int difference = game.getMinimumScore();
                if (cPlayer.getPlayPiles()[card.getColor()].getHighestValue() <= card.getValue()) {
                    difference -= (10 * card.getValue() * cPlayer.getPlayPiles()[card.getColor()].getWagersMultiplier());
                }
                difference -= game.getDemand(card);
                if (difference > maxDifference) {
                    maxDifference = difference;
                    bestChoice = choice;
                }
                choice++;
            }
            App.logger.info("Path: Minimize Value Lost and Demand");
            return bestChoice+8;
        } else {
            App.logger.info("Path: Increase Curr Score");
            return bestChoice;
        }
    }


    public static int getBestPick(Game game, int index) {
        Player cPlayer = game.getCurrentPlayer();
        int choice = 1;
        int maxMinScore = game.getMinimumScore();
        int bestChoice = -1;

        while (choice <= 5) {
            // Discard pile is not empty AND play pile for that color is started
            if (game.discardPiles[choice-1].getSize() > 0 && cPlayer.getPlayPiles()[choice-1].getCards().size() > 0) {
                cPlayer.getHand()[index] = game.discardPiles[choice-1].peekTop(); // Just peek to avoid having to undo anything
                int newMinScore = game.getMinimumScore();
                if (newMinScore > maxMinScore) {
                    maxMinScore = newMinScore;
                    bestChoice = choice;
                }
            }
            choice++;
        }

        if (bestChoice == -1) {
            App.logger.info("Path: Else");
            return 0; // Draw from deck
        } else {
            App.logger.info("Path: Increase Min Score");
            return bestChoice;
        }
    }
}
