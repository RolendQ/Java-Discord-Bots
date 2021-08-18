package com.roland.whitehall.core;

import com.roland.whitehall.enums.GameStatusType;
import com.roland.whitehall.game.Game;
import com.roland.whitehall.game.Location;
import com.roland.whitehall.game.Node;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;

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

        if (command.equalsIgnoreCase("adjl")) {
            String s = "Loc ("+args[0]+"):";
            for (Node n : Utils.map.get(Utils.ls[Integer.parseInt(args[0])])) {
                s += " ";
                if (n instanceof Location) {
                    s += "L" + ((Location) n).id;
                } else s += "C";
            }
            channel.sendMessage(s).queue();
        }

        if (command.equalsIgnoreCase("adjc")) {
            String s = "Cro ("+args[0]+"):";
            for (Node n : Utils.map.get(Utils.cs[Integer.parseInt(args[0])])) {
                s += " ";
                if (n instanceof Location) {
                    s += "L" + ((Location) n).id;
                } else s += "C";
            }
            channel.sendMessage(s).queue();
        }

//        if (command.equalsIgnoreCase("movel")) {
//            String s = "Start Loc ("+args[0]+"):";
//            for (Location loc : Utils.getAdjLoc(Utils.ls[Integer.parseInt(args[0])])) {
//                s += " "+loc.id;
//            }
//
//            channel.sendMessage(s).queue();
//        }

//        if (command.equalsIgnoreCase("movec")) {
//            String s = "Start Cro ("+args[0]+")";
//            Crossing start = Utils.cs[Integer.parseInt(args[0])];
//            Crossing finish = null;
//            if (args.length == 2) {
//                finish = start.move(Integer.parseInt(args[1]));
//            } else if (args.length == 3) {
//                finish = start.move(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
//            }
//            if (finish == null) {
//                channel.sendMessage("Error").queue();
//                return;
//            }
//            s += " Finish Cro: "+finish.getTempID();
//            channel.sendMessage(s).queue();
//        }

        if (command.equalsIgnoreCase("create_game") || command.equalsIgnoreCase("cg")) {
            Game newGame = new Game(channel,guild,authorID,user);
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

        if (command.equalsIgnoreCase("spec") || command.equalsIgnoreCase("spectate")) {
            game.toggleSpectate(user);
        }

        if (command.equalsIgnoreCase("j") || command.equalsIgnoreCase("join")) {
            if (game.status == GameStatusType.pregame) {
                game.addPlayer(authorID);
            }
        }

        if (command.equalsIgnoreCase("test") || command.equalsIgnoreCase("teststart")) {
            if (game.status == GameStatusType.pregame) {
                game.testStart();
            }
        }

        if (command.equalsIgnoreCase("st") || command.equalsIgnoreCase("start")) {
            if (game.status == GameStatusType.pregame) {
                game.start();
            }
        }

        if (command.equalsIgnoreCase("c") || command.equalsIgnoreCase("choose")) {
            if (game.status == GameStatusType.startPhase) {
                game.choose(authorID, GlobalVars.letterToInt(args[0]));
            }
        }

        if (command.equalsIgnoreCase("s") || command.equalsIgnoreCase("search")) {
            for (String arg : args) {
                if (arg.equals("all")) {
                    // TODO yellow Why did I comment this out
                    if (game.getDetective(authorID) == game.players.get(1) && game.getDetective(authorID).hasAbility()) {
                        game.searchAll(game.getDetective(authorID));
                    }
                }
                if (game.status == GameStatusType.searchPhase) {
                    game.search(authorID, Integer.parseInt(arg));
                }
            }
        }

        if (command.equalsIgnoreCase("a") || command.equalsIgnoreCase("arrest")) {
            if (game.status == GameStatusType.searchPhase) {
                game.arrest(authorID, Integer.parseInt(args[0]));
            }
        }

        if (command.equalsIgnoreCase("t") || command.equalsIgnoreCase("transfer")) {
            if (event instanceof MessageReceivedEvent) {
                if (((MessageReceivedEvent) event).getMessage().getMentionedMembers().size() > 0) {
                    game.transfer(authorID, ((MessageReceivedEvent) event).getMessage().getMentionedMembers().get(0).getUser().getId());
                }
            }
        }

        if (command.equalsIgnoreCase("e") || command.equalsIgnoreCase("end")) {
            if (game.status == GameStatusType.searchPhase && game.getDetective(authorID) == game.players.get(3)) { // Only red can end
                game.endSearchPhase();
            }
        }




        if (game.currentPlayer == null || !game.currentPlayer.playerID.equals(authorID)) {
            return;
        }

        if (command.equalsIgnoreCase("zoom")) {
            System.out.println("Performing zoom");
            game.drawBoard(Integer.parseInt(args[0]),false);
        }

        if (command.equalsIgnoreCase("m") || command.equalsIgnoreCase("move")) {
            game.move(GlobalVars.letterToInt(args[0]));
        }

        if (command.equalsIgnoreCase("tp")) {
            // Yellow's tp (need player and letter)
            if (game.getDetective(authorID) == game.players.get(1) && game.getDetective(authorID).hasAbility()) {
                game.teleport(((MessageReceivedEvent) event).getMessage().getMentionedMembers().get(0).getUser().getId(),GlobalVars.letterToInt(args[0]));
            }
        }

        if (command.equalsIgnoreCase("debugmove")) {
            game.move(Utils.cs[Integer.parseInt(args[0])]);
            channel.sendMessage("Debug moved").queue();
        }
    }

    public String command = "";
    public String authorID = "";
    public String[] args = {};
    public MessageChannel channel;
    public User user;
    public Guild guild;
    Event event;
}
