package com.roland.wager.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.roland.wager.bot.GlobalVars;
import com.roland.wager.bot.Utils;
import com.roland.wager.gameelements.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class Game {

    public Game(MessageChannel channel, Guild guild, User user, String hostID) {
        this.guild = guild;
        this.gameChannel = channel;
        this.hostID = hostID;
        addPlayer(user, hostID, "squares",true);
        //setupNewGame();
    }

    public Game(MessageChannel channel, Guild guild, User hostUser, User challengedUser, String hostID, String challengedID) {
        this.guild = guild;
        this.gameChannel = channel;
        this.hostID = hostID;
        addPlayer(hostUser, hostID, "sea_creatures", false); // TODO
        addPlayer(challengedUser, challengedID, "people", false);
        if (challengedID.equals("659047099530346556")) {
            // Computer
            computer = true;
        }
        //setupNewGame();
    }

    public void addPlayer(User user, String id, String emojiType, boolean announce) {
        // Limit at 2 for now
        if (players.size() < 2) {
            Player p = new Player(user, id, players.size()+1, Utils.getName(id, guild), emojiType);
            if (players.size() > 0) {
                players.get(players.size()-1).setNextPlayer(p);
            }
            players.add(p);
            p.setNextPlayer(players.get(0));
            if (announce) gameChannel.sendMessage(p.getName()+" joined the game").queue();

            // Auto start at 2
            if (announce && players.size() == 2) {
                start();
            }
        }
    }

    private void setupNewGame() {
        //state = new State(cPlayer, players, deck, discardPiles);
    }

    public void start() {
        cPlayer = players.get(0);
        do {
            constructDeck();
            Collections.shuffle(deck);
            for (Player p : players) {
                p.clearHand();
                for (int i = 0; i < p.getHand().length; i++) {
                    drawCard(p, 0, i, true);
                }
            }
        } while (!fairStartingHands()); // 3 wagers each
        newTurn(cPlayer);
    }

    public void endTurn() {
        System.out.println("[DEBUG LOG/Game.java] Ending turn");
        cPlayer.setDropped(false);
        newTurn(cPlayer.getNextPlayer());
    }

    public void newTurn(Player p) {
        System.out.println("[DEBUG LOG/Game.java] New turn");
        if (cPlayer == players.get(0)) turn++;
        cPlayer = p;
        //if (spectateMode) gameChannel.sendMessage(cPlayer.getName()+"'s turn").queue();
        //displayPlayPiles(p.getNextPlayer());
        for (Player player : players) {
            if (!player.getUser().isBot() && !cPlayer.getUser().isBot()) {
                displayDiscardPiles(player);
                displayPlayPiles(player);
                displayHand(player);
            }
        }
        cPlayer.setLastActionString("");
        cPlayer.setLastDropColor(-1);

        if (cPlayer.getUser().isBot() && computer) {
            App.logger.info("Maximum Score: " + getMaximumScore());
            displayCPUHand(cPlayer);
            // Drop (Play or discard)
            int dropMove = Computer.getBestDrop(this);
            App.logger.info("[DROP] "+dropMove);
            if (dropMove <= 8) {
                playCard(dropMove);
            } else {
                dropMove -= 8;
                discardCard(dropMove);
            }
            int pickMove = Computer.getBestPick(this, dropMove-1);
            App.logger.info("[PICK] "+pickMove);
            if (drawCard(cPlayer, pickMove, dropMove-1, false)) {
                endTurn();
            } else {
                System.out.println("CPU ERROR: Invalid draw");
            }
            // Pick (Pick from discard or deck)
        }
    }

    public boolean playCard(int index) {
        if (index > 0 && index <= cPlayer.getHand().length) {
            Card c = cPlayer.getHand()[index-1];
            if (cPlayer.getPlayPiles()[c.getColor()].addCard(c)) {
                cPlayer.setLastActionString("Played "+c.toString(cPlayer));
                cPlayer.setLastDropColor(c.getColor());
                afterDrop(index-1); // starts at 0
                System.out.println("[DEBUG LOG/Game.java] Played card at letter "+index); // starts at 1
                return true;
            }
        }
        return false;
    }

    public void undo() {
        if (cPlayer.getLastActionString().equals("")) {

        } if (cPlayer.getLastActionString().startsWith("Play")) {
            System.out.println("[DEBUG LOG/Game.java] Undoing play");
            cPlayer.getPlayPiles()[cPlayer.getLastDropColor()].removeLastCard();
        } else if (cPlayer.getLastActionString().startsWith("Discard")) {
            System.out.println("[DEBUG LOG/Game.java] Undoing discard");
            discardPiles[cPlayer.getLastDropColor()].removeTop();
        }
        cPlayer.setLastActionString("");

        // Send new round of embeds
        if (!cPlayer.getUser().isBot()) {
            cPlayer.setLinkedCommand(null);
            cPlayer.setDropped(false);
            cPlayer.setLastDropColor(-1);
            displayDiscardPiles(cPlayer);
            displayPlayPiles(cPlayer);
            displayHand(cPlayer);
        }
    }

    // Can always discard
    public void discardCard(int index) {
        if (index > 0 && index <= cPlayer.getHand().length) {
            Card c = cPlayer.getHand()[index-1];
            discardPiles[c.getColor()].addCard(c);
            cPlayer.setLastActionString("Discarded "+c.toString(cPlayer));
            cPlayer.setLastDropColor(c.getColor());
            afterDrop(index-1);
            System.out.println("[DEBUG LOG/Game.java] Discarded card at index "+index);
        }
    }

    private void afterDrop(int index) {
        cPlayer.setEmptyIndex(index); // This index is stored so when the player draws, it easily replaces the index
        cPlayer.setDropped(true);
    }

    public boolean drawCard(Player p, int pileIndex, int handIndex, boolean isStarting) {
        if (!isStarting) System.out.println("[DEBUG LOG/Game.java] Drawing from "+pileIndex);
        if (pileIndex == 0) {
            // Drawing from DECK
            Card c = deck.remove(deck.size()-1);
            p.getHand()[handIndex] = c;
            if (!isStarting) {
                p.setLastDrawnCard(c);
                cPlayer.addLastActionString("Drew :card_box:");
            }
            if (deck.size() == 0) {
                // Game over
                gameOver();
                return false;
            }
            return true;
        } else if (pileIndex <= 5){
            // Drawing from PILE
            pileIndex--;
            // Cannot draw from empty or just discarded
            if (discardPiles[pileIndex].getSize() > 0 && (p.getLastActionString().startsWith("Play") || p.getLastDropColor() != pileIndex)) {
                Card c = discardPiles[pileIndex].removeTop();
                p.getHand()[handIndex] = c;
                p.setLastDrawnCard(c);
                cPlayer.addLastActionString("Drew "+c.toString());
                return true;
            } else return false;
        }
        return false;
    }

    public void gameOver() {
        // Display everyone's hands? TODO
        if (!cPlayer.getUser().isBot()) {
            displayDiscardPiles(cPlayer);
            displayPlayPiles(cPlayer);
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Game Results");
        Map<Integer, Integer> scores = new HashMap<Integer, Integer>();

        for (int i = 0; i < players.size(); i++) {
            //scores.put(i,players.get(i).calculatePoints());
            scores.put(i,getCurrentScore(players.get(i)));
        }

        List<Entry<Integer, Integer>> sortedList = Utils.sort(scores);

        // Assigns 1st, 2nd, 3rd, etc
        for (int i = 0; i < sortedList.size(); i++) {
            Map.Entry<Integer, Integer> entry = sortedList.get(i);
            String name = players.get(i).getName();
            embed.addField(Utils.endGamePlaces[i],"**"+name+"** - ``"+entry.getValue()+"``",true);
        }
        embed.setFooter("Credit to Lost Cities", null);
        if (spectateMode) gameChannel.sendMessage(embed.build()).queue();
        for (Player p : players) {
            if (!p.getUser().isBot()) {
                Utils.directMessage(p.getUser(), embed);
            }
        }
        GlobalVars.remove(this);
    }

    private void displayDiscardPiles(Player p) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.DARK_GRAY);
        embed.setTitle("Discard Piles");
        String text = "";
        for (DiscardPile dp : discardPiles) {
            text += "\n"+dp.toString(p.getNextPlayer());
        }
        text += "\n**"+deck.size()+" Cards Left**";
        embed.setDescription(text.substring(1));
        if (spectateMode && cPlayer == p) {
            gameChannel.sendMessage(embed.build()).queue();
        }
        Utils.directMessage(p.getUser(), embed);
        //channel.sendMessage(embed).queue(message -> message.addReaction(reaction).queue());
    }

    private void displayPlayPiles(Player p) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.DARK_GRAY);
        embed.setTitle("Play Piles");
        for (Player player : players) {
            String playPiles = "";
            for (PlayPile pp : player.getPlayPiles()) {
                playPiles += "\n"+pp.toString(p.getNextPlayer()); // must be next player to show right asterisk
            }
            playPiles += "\n─────────\n"+player.getLastActionString();
            if (cPlayer == player) {
                embed.addField("***"+player.getName()+"*** ||``"+getCurrentScore(player)+"``||",playPiles.substring(1),true);
            } else {
                embed.addField("**"+player.getName()+"** ||``"+getCurrentScore(player)+"``||",playPiles.substring(1),true);
            }
        }
        if (spectateMode && cPlayer == p) {
            gameChannel.sendMessage(embed.build()).queue();
        }
        Utils.directMessage(p.getUser(), embed);
    }

    void displayHand(Player p) {
        // Sort
        Arrays.sort(p.getHand());

        //embed.setDescription(text.substring(1)+"||");
        //gameChannel.sendMessage(embed.build()).queue();

        EmbedBuilder embed = new EmbedBuilder();
        if (cPlayer == p) {
            embed.setTitle("Your Hand");
        } else embed.setTitle("Opponent is playing...");
        embed.setColor(Color.DARK_GRAY);
        embed.setFooter( "#"+gameChannel.getName()+" ("+turn+")",guild.getIconUrl());
        String text = "\n"+p.getEmoji(0)+"** **";
        int color = 0;
        for (int i = 0; color < 5 || i < p.getHand().length;) {
            if (i < p.getHand().length) {
                Card c = p.getHand()[i];
                if (c.getColor() == color) {
                    if (c == p.getLastDrawnCard()) {
                        text += " "+Utils.numToLetterEmojis[i]+" **"+c.getValueString()+"***";
                    } else text += " "+Utils.numToLetterEmojis[i]+" "+c.getValueString();
                    i++;
                    continue;
                }
            }
            color++;
            if (color < 5) text += "\n"+p.getEmoji(color)+"** **";
        }
        embed.setDescription(text.substring(1));
        Utils.directMessage(p.getUser(), embed);
    }

    void displayCPUHand(Player p) {
        // Sort
        Arrays.sort(p.getHand());

        //embed.setDescription(text.substring(1)+"||");
        //gameChannel.sendMessage(embed.build()).queue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("CPU's Hand");
        embed.setColor(Color.DARK_GRAY);
        embed.setFooter( "#"+gameChannel.getName(),guild.getIconUrl());
        String text = "\n"+p.getEmoji(0)+"** **";
        int color = 0;
        for (int i = 0; color < 5 || i < p.getHand().length;) {
            if (i < p.getHand().length) {
                Card c = p.getHand()[i];
                if (c.getColor() == color) {
                    if (c == p.getLastDrawnCard()) {
                        text += " "+Utils.numToLetterEmojis[i]+" "+c.getValueString()+"*";
                    } else text += " "+Utils.numToLetterEmojis[i]+" "+c.getValueString();
                    i++;
                    continue;
                }
            }
            color++;
            if (color < 5) text += "\n"+p.getEmoji(0)+"** **";
        }
        embed.setDescription(text.substring(1));
        gameChannel.sendMessage(embed.build()).queue();
    }

    public void addDiscardPileReactions(PrivateChannel pmChannel, String messageID) {
        for (int i = 0; i < discardPiles.length; i++) {
            if (discardPiles[i].getSize() > 0) {
                pmChannel.addReactionById(messageID, cPlayer.getEmoji(i)).queue();
            }
        }
        pmChannel.addReactionById(messageID, "🗃️").queue();
    }

    public void addHandReactions(PrivateChannel pmChannel, String messageID) {
        pmChannel.addReactionById(messageID, "👆").queue();
        pmChannel.addReactionById(messageID, "🔥").queue();
        for (int i = 0; i < 8; i++) {
            pmChannel.addReactionById(messageID, Utils.numToLetterEmojis[i]).queue();
        }
        pmChannel.addReactionById(messageID, "⏪").queue();
    }

    private void constructDeck() {
        System.out.println("Constructing Deck...");
        deck = new ArrayList<Card>(60);
        for (int c = 0; c <= 4; c++) { // For each color
            // Add 3 wagers
            deck.add(new Card(0,c));
            deck.add(new Card(0,c));
            deck.add(new Card(0,c));
            for (int v = 2; v <= 10; v++) {
                deck.add(new Card(v,c));
            }
        }
        //Collections.shuffle(deck);
    }

    // Ensures everyone starts with 3 wagers
    private boolean fairStartingHands() {
        for (Player p : players) {
            int wagers = 0;
            for (Card c : p.getHand()) {
                if (c.isWager()) wagers++;
            }
            if (wagers != 3) return false;
        }
        return true;
    }

    // Calculations for Computer

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
            int num = 2;
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

        return 1+c.getValue();
    }

    public int getCardsLeft() {
        return deck.size();
    }


    public int getTurn() {
        return turn;
    }

    public MessageChannel getGameChannel() {
        return gameChannel;
    }

    public Guild getGuild() {
        return guild;
    }

    public Player getCurrentPlayer() {
        return cPlayer;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public transient MessageChannel gameChannel; // transient so need to update upon resuming
    public transient Guild guild; // transient so need to update upon resuming
    public String hostID;

    public int turn = 0;
    public boolean spectateMode = true;
    public boolean computer = false;
    public Player cPlayer;
    public ArrayList<Player> players = new ArrayList<Player>();
    public ArrayList<Card> deck;
    public DiscardPile[] discardPiles = {new DiscardPile(0), new DiscardPile(1), new DiscardPile(2), new DiscardPile(3), new DiscardPile(4)};
    //public State state;
}
