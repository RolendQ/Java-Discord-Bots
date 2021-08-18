package com.roland.wager.bot;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import com.roland.wager.core.Game;
import com.roland.wager.enums.Rarity;
import com.roland.wager.gameelements.Player;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;

public class Commands {
    public Commands(String command, Event event, String[] args) {
        this.command = command;
        this.args = args;
        this.event = event;
        if (event instanceof GuildMessageReactionAddEvent) {
            // Reactions in public server
            this.authorID = ((GuildMessageReactionAddEvent) event).getUser().getId();
            this.user = ((GuildMessageReactionAddEvent) event).getUser();
            this.channel = ((GuildMessageReactionAddEvent) event).getChannel();
            this.guild = ((GuildMessageReactionAddEvent) event).getGuild();
        } else if (event instanceof MessageReceivedEvent) {
            // Messages in public server
            this.authorID = ((MessageReceivedEvent) event).getAuthor().getId();
            this.user = ((MessageReceivedEvent) event).getAuthor();
            this.channel = ((MessageReceivedEvent) event).getChannel();
            this.guild = ((MessageReceivedEvent) event).getGuild();
        } else if (event instanceof GuildMessageReactionRemoveEvent) {
            // Removing reactions in public server
            this.authorID = ((GuildMessageReactionRemoveEvent) event).getUser().getId();
            this.user = ((GuildMessageReactionRemoveEvent) event).getUser();
            this.channel = ((GuildMessageReactionRemoveEvent) event).getChannel();
            this.guild = ((GuildMessageReactionRemoveEvent) event).getGuild();
        } else if (event instanceof PrivateMessageReceivedEvent) {
            // Messages in dm
            this.authorID = ((PrivateMessageReceivedEvent) event).getAuthor().getId();
            this.user = ((PrivateMessageReceivedEvent) event).getAuthor();
            // Find game with matching current player (there could be multiple games)
            this.channel = null;
            this.guild = null;
            for (Game game : GlobalVars.currentGames) {
                if (game.getCurrentPlayer().getPlayerID().contentEquals(authorID)) {
                    this.channel = game.getGameChannel();
                    this.guild = game.getGuild();
                    break;
                }
            }
        } else if (event instanceof PrivateMessageReactionAddEvent) {
            // Reactions in dm
            this.authorID = ((PrivateMessageReactionAddEvent) event).getUser().getId();
            this.user = ((PrivateMessageReactionAddEvent) event).getUser();
            // Find game with matching current player (there could be multiple games)
            this.channel = null;
            this.guild = null;
            for (Game game : GlobalVars.currentGames) {
                if (game.getCurrentPlayer().getPlayerID().contentEquals(authorID)) {
                    this.channel = game.getGameChannel();
                    this.guild = game.getGuild();
                    break;
                }
            }
        }
    }

    public void attempt() {
        // Check if requirements/args are met
        if (channel == null || guild == null) {
            return;
        }

        if (command.contentEquals("cg") || command.equals("create_game")) {
            // Check if there already is a game in this channel
            for (Game g : GlobalVars.currentGames) {
                if (g.getGameChannel().equals(channel)) {
                    return;
                }
            }
        }
//		if (command.contentEquals("eg") || command.equals("end_game") || command.contentEquals("shutdown")) {
//			// Check if this is an whitelisted id
//			if (!Utils.isAdmin(authorID)) return;
//		}
        action();
    }

    public void action() {
        //System.out.println("Command is: "+command);
        //System.out.println("Args is: "+Arrays.toString(args));

        // Debug commands
        if (command.equals("testmojis")) {
            String result = "";
            String result2 = "";
            String result3 = "";
            int count = 0;
            for (String key : GlobalVars.colorEmojis.keySet()) {
                if (count < 10) {
                    result += key + ": " + Arrays.toString(GlobalVars.colorEmojis.get(key)) + "\n";
                } else if (count < 20) {
                    result2 += key + ": " + Arrays.toString(GlobalVars.colorEmojis.get(key)) + "\n";
                } else {
                    result3 += key + ": " + Arrays.toString(GlobalVars.colorEmojis.get(key)) + "\n";
                }
                count++;
            }
            channel.sendMessage(result).queue();
            channel.sendMessage(result2).queue();
            channel.sendMessage(result3).queue();
        }

        if (command.equals("gachatest")) {
            String msg = "Congratulations, you pulled a ";

            Random r = new Random();
            double rng = r.nextDouble();
            System.out.println("Generated: "+rng);
            double[] chances = new double[]{Rarity.common_chance,Rarity.rare_chance,Rarity.epic_chance,Rarity.legendary_chance,Rarity.mythic_chance};
            String[] names = new String[] {"common","rare","epic","legendary","mythic"};
            int rarity = 0;
            for (int i = chances.length - 1; i >= 0; i--) {
                if (rng < chances[i]) {
                    System.out.println("Found rarity: "+names[i]);
                    rarity = i;
                    break;
                }
            }

            msg += names[rarity].toUpperCase() + " ";
            int reward = r.nextInt(GlobalVars.rarities.get(rarity).length);
            if (rarity == 4) {
                msg += GlobalVars.gambleEmojis.get(GlobalVars.rarities.get(rarity)[reward]);
            } else {
                msg += GlobalVars.colorEmojis.get(GlobalVars.rarities.get(rarity)[reward])[r.nextInt(5)];
            }
            channel.sendMessage(msg).queue();
        }

        if (command.contentEquals("tutorial")) {
            user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
                public void accept(PrivateChannel channel) {
                    Tutorial t = new Tutorial(authorID,channel);
                    GlobalVars.add(t);
                }
            });
        }

        if (command.equals("wager")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.MAGENTA);
            embed.setTitle("Help Menu");
            String text = "**__How to play Wager__**\r\n" +
                    "Wager is a number based card game that centers around making gambles to score more points. Players will take turns playing cards and increasing their point total of their Play Area. Each card has a color and value. "
                    + "The five colors are RED, BLUE, GREEN, YELLOW, and PURPLE. The number values range from 2 to 10 inclusive. There is only one RED 2, one BLUE 2, etc... "
                    + "However there are three **Wager** cards of each color. These behave as if they were a value of 0 however their special effect is that they increase the point multiplier of that color. "
                    + "With no **Wager** cards, a color has a default multiplier of 1x. For every **Wager** card a player owns, their color multiplier is increased by one, up to 4x with all three. "
                    + "Additionally, as soon as you place a card in a color, that color starts off with a flat negative 20 points.\r\n" +
                    "\r\n" +
                    "**__How to take a turn__**\r\n" +
                    "Each turn, you will either play a card or discard a card from your hand. You can discard any card but you can only play cards in increasing order for that color. Meaning you should start with lower value cards, "
                    + "and the last card you can play in any color is a 10 because it is the largest. Remember that **Wager** cards act as 0 and must be played first. "
                    + "After you play or discard a card, you must draw a card. You can draw from the top of any of the discard piles OR from the deck. \r\n" +
                    "\r\n" +
                    "**The game immediately ends as soon as the last card in the deck is drawn**\n"+
                    "\r\n" +
                    "**__How to count points__**\r\n" +
                    "For each color with at least one card in it, add up all the numbers of each card played and subtract a base 20."
                    + "Then, multiply that sum by the player's color multiplier (1-4x). Finally, add 20 if they have at least 8 cards (including wagers) in that color. \r\n" +
                    "\r\n";
            embed.setDescription(text);
            channel.sendMessage(embed.build()).queue();
        }


//        if (command.equals("embedtest")) {
//            EmbedBuilder embed = new EmbedBuilder();
//            embed.setColor(Color.YELLOW);
//            embed.setTitle("Play Piles");
//            embed.addField("``Rolend``",":heart: **5** **10**\n:heart:\n:heart:\n:heart:\n:heart:", true);
//            //embed.addField("``Rolend`` **5x**:moneybag:",":key:**5** :wine_glass:**7**", false);
//            embed.addField("``Aero``",":heart: **5** **10**\n:heart:\n:heart:\n:heart:\n:heart:", true);
//            embed.addField("``Ray``",":heart: **5** **10**\n:heart:\n:heart:\n:heart:\n:heart:", true);
//            channel.sendMessage(embed.build()).queue();
//        }

        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent msgEvent = (MessageReceivedEvent) event;
            if (command.equals("challenge")) {
                Challenge newChallenge;
                if (msgEvent.getMessage().getMentionedMembers().size() > 0) {
                    if (msgEvent.getMessage().getMentionedMembers().get(0).getUser().getId().equals("659047099530346556")) {
                        Game newGame = new Game(msgEvent.getChannel(), msgEvent.getGuild(), msgEvent.getAuthor(), msgEvent.getMessage().getMentionedMembers().get(0).getUser(), msgEvent.getAuthor().getId(), "659047099530346556");
                        GlobalVars.add(newGame);
                        System.out.println("Created game with computer");
                        newGame.start();
                        return;
                    } else {
                        newChallenge = new Challenge(msgEvent.getAuthor().getId(), msgEvent.getAuthor(), msgEvent.getMessage().getMentionedMembers().get(0).getUser().getId());
                    }
                } else {
                    newChallenge = new Challenge(msgEvent.getAuthor().getId(), msgEvent.getAuthor());
                }
                GlobalVars.add(newChallenge);
                channel.addReactionById(msgEvent.getMessageId(), "✅").queue();
                channel.addReactionById(msgEvent.getMessageId(), "\uD83D\uDEAB").queue();
            }

            if (command.equals("accept")) {
                if (msgEvent.getMessage().getMentionedMembers().size() > 0) {
                    for (Challenge c : GlobalVars.pendingChallenges) {
                        if (c.getChallengerID().equals(msgEvent.getMessage().getMentionedMembers().get(0).getUser().getId()) && (c.getChallengedID() == null || c.getChallengedID().equals(msgEvent.getAuthor().getId()))) {
                            Game newGame = new Game(msgEvent.getChannel(), msgEvent.getGuild(), c.getChallengerUser(),msgEvent.getAuthor(), c.getChallengerID(), msgEvent.getAuthor().getId());
                            GlobalVars.add(newGame);
                            System.out.println("Created game from challenge");
                            newGame.start();
                            //channel.addReactionById(msgEvent.getMessageId(), "✅").queue();
                            break;
                        }
                    }
                }
            }
        }

        if (command.equals("create_game") || command.contentEquals("cg")) {
            Game newGame = new Game(channel,guild,user,authorID);
            GlobalVars.add(newGame);
            System.out.println("[DEBUG LOG/Commands.java] Created game");
            return;
        }

        if (command.equals("listchallenges")) {
            for (Challenge c : GlobalVars.pendingChallenges) {
                if (c.getChallengedID() == null) {
                    System.out.println("Challenge: "+c.getChallengerID());
                } else {
                    System.out.println("Challenge: "+c.getChallengerID()+" > "+c.getChallengedID());
                }
            }
        }

        // Commands that require a game (any status or player)
        Game game = null;
        for (Game g : GlobalVars.currentGames) {
            if (g.getGameChannel().equals(channel)) {
                game = g;
            }
        }
        // Didn't find game
        if (game == null) return;

        if (command.equals("j") || command.contentEquals("join")) {
            game.addPlayer(user, authorID, "circles", true);
        }

        // Commands that require the command user to be the current player or admin
        if (game.getCurrentPlayer() != null && !game.getCurrentPlayer().getPlayerID().equals(authorID)) {
            return;
        }

//		if (command.contentEquals("hand")) {
//			game.displayHand();
//		}

        if (command.equals("st") || command.contentEquals("start")) {
            if (game.getCurrentPlayer() == null) {
                game.start();
            }
        }

        if (command.equals("p") || command.contentEquals("play")) {
            Player cPlayer = game.getCurrentPlayer();
            if (!cPlayer.hasDropped()) {
                game.playCard(Integer.parseInt(args[0]));
            }
        }

        if (command.equals("d") || command.contentEquals("discard")) {
            Player cPlayer = game.getCurrentPlayer();
            if (!cPlayer.hasDropped()) {
                game.discardCard(Integer.parseInt(args[0]));
            }
        }

        if (command.equals("r") || command.contentEquals("draw")) {
            Player cPlayer = game.getCurrentPlayer();
            if (cPlayer.hasDropped() && game.drawCard(cPlayer, Integer.parseInt(args[0]), cPlayer.getEmptyIndex(), false)) {
                game.endTurn();
            }
        }

        if (command.equalsIgnoreCase("u") || command.equalsIgnoreCase("undo")) {
            // TODO only reactions
            System.out.println("Undoing...");
            game.undo();
        }
    }


    public String command = "";
    public String authorID = "";
    public String[] args = {};
    public MessageChannel channel;
    public User user;
    public Guild guild;
    //MessageReceivedEvent event;
    Event event;
}
