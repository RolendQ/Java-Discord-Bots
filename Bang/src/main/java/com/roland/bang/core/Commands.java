package com.roland.bang.core;

import com.roland.bang.enums.GameStatusType;
import com.roland.bang.game.Die;
import com.roland.bang.game.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;

import java.io.IOException;

public class Commands {
    public Commands(String command, Event event, String[] args) {
        this.command = command;
        this.args = args;
        this.event = event;
        if (event instanceof GuildMessageReactionAddEvent) {
            this.authorID = ((GuildMessageReactionAddEvent) event).getUser().getId();
            this.user = ((GuildMessageReactionAddEvent) event).getUser();
            this.channel = ((GuildMessageReactionAddEvent) event).getChannel();
            this.guild = ((GuildMessageReactionAddEvent) event).getGuild();
        } else if (event instanceof MessageReceivedEvent) {
            this.authorID = ((MessageReceivedEvent) event).getAuthor().getId();
            this.user = ((MessageReceivedEvent) event).getAuthor();
            this.channel = ((MessageReceivedEvent) event).getChannel();
            this.guild = ((MessageReceivedEvent) event).getGuild();
        } else if (event instanceof GuildMessageReactionRemoveEvent) {
            this.authorID = ((GuildMessageReactionRemoveEvent) event).getUser().getId();
            this.user = ((GuildMessageReactionRemoveEvent) event).getUser();
            this.channel = ((GuildMessageReactionRemoveEvent) event).getChannel();
            this.guild = ((GuildMessageReactionRemoveEvent) event).getGuild();
        }
    }

    public void attempt() {
        //
        action();
    }

    public void action() {
        if (command.equalsIgnoreCase("ping")) {
            channel.sendMessage("pong").queue();
        }

        if (command.equalsIgnoreCase("avatar")) {
            channel.sendMessage("``"+guild.getMemberById(authorID).getUser().getAvatarUrl()+"``").queue();
        }

        if (command.equalsIgnoreCase("seat")) {
            if (args.length < 9) { // 4-8
                try {
                    Utils.sendSeatsImage(guild,channel,args);
                    System.out.println("Sent image");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (command.equalsIgnoreCase("testroll")) {
            Die die = new Die(-1);
            int value = die.roll(false);
            channel.sendMessage("Rolled: "+value).queue();
        }

        if (command.equalsIgnoreCase("create_game") || command.equalsIgnoreCase("cg")) {
            Game newGame = new Game(channel,guild,authorID);
            GlobalVars.add(newGame);
            System.out.println("[DEBUG LOG/Commands.java] Created game");
            return;
        }

        // Commands that require a game (any status or player)
        Game game = null;
        for (Game g : GlobalVars.currentGames) {
            if (g.channel.equals(channel)) {
                game = g;
            }
        }
        // Didn't find game
        if (game == null) return;

        if (command.equalsIgnoreCase("j") || command.equalsIgnoreCase("join")) {
            if (game.status == GameStatusType.PREGAME) {
                game.addPlayer(authorID);
            }
        }

        if (command.equalsIgnoreCase("st") || command.equalsIgnoreCase("start")) {
            if (game.status == GameStatusType.PREGAME) {
                game.start();
            }
        }

        if (game.currentPlayer == null || !game.currentPlayer.playerID.equals(authorID)) {
            return;
        }

        // TODO TEMP
        if (command.equalsIgnoreCase("shoot")) {
            System.out.println("Performing shoot "+args[0]+" for: "+args[1]);
            game.shoot(args[1], Integer.parseInt(args[0]));
        }

        if (command.equalsIgnoreCase("heal")) {
            System.out.println("Performing heal: "+args[0]);
            game.heal(args[0]);
        }

        if (command.equalsIgnoreCase("c") || command.equalsIgnoreCase("confirm")) {
            if (game.confirmRolls()) {
                channel.addReactionById(((GenericMessageEvent) event).getMessageId(), "✅").queue();
            }
        }

        if (command.equalsIgnoreCase("r") || command.equalsIgnoreCase("roll")) {
            if (args.length == 0) {
                game.roll(null,true);
                return;
            }
            int[] ids = new int[args.length];
            for (int i = 0; i < args.length; i++) {
                ids[i] = Integer.parseInt(args[i])-1;
            }
            game.roll(ids,true);
        }

        if (command.equalsIgnoreCase("e") || command.equalsIgnoreCase("end")) {
            game.endTurn();
        }

        if (command.equalsIgnoreCase("stats")) {
            game.drawBoard();
            game.displayStats();
        }

        // debug
        if (command.equalsIgnoreCase("skip")) {
            game.skip();
        }
    }

    public String command = "";
    public String authorID = "";
    public String[] args = {};
    public MessageChannel channel;
    public User user;
    public Guild guild;
    public Event event;
}
