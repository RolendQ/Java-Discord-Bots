package com.roland.identityvtool.core;

import com.roland.identityvtool.audio.PlayerManager;
import com.roland.identityvtool.audio.VoiceChannelManager;
import com.roland.identityvtool.enums.Map;
import com.roland.identityvtool.game.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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

        if (command.equalsIgnoreCase("t")) {
            String text = "Teleport is up";
            if (args.length > 0) {
                text = args[0];
            }
            int delay = 97000;
            if (args.length > 1) {
                delay = Integer.parseInt(args[1]);
            }
            Utils.sendTTS(channel,text,delay);
        }

        if (command.equalsIgnoreCase("b")) {
            String text = "Blink is up";
            int delay = 147000;
            Utils.sendTTS(channel,text,delay);
        }

        if (command.equalsIgnoreCase("spawn")) {
            channel.sendMessage(GlobalVars.getSpawnEmbed(Map.getMap(args[0])).build()).queue();
        }

        if (command.equalsIgnoreCase("map")) {
            try {
                Utils.sendCiphersImage(guild,channel,Map.getMap(args[0]),-1);
                System.out.println("Sent image");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (command.equalsIgnoreCase("cipher")) {
            try {
                Utils.sendCiphersImage(guild,channel,Map.getMap(args[0]),(Integer.parseInt(args[1])-1));
                System.out.println("Sent image");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (command.equalsIgnoreCase("playsong")) {
            VoiceChannelManager.joinChannel((MessageReceivedEvent) event);

            PlayerManager playerManager = PlayerManager.getInstance();

//            if (args[0].equalsIgnoreCase("local")) {
//                playerManager.playLocal((TextChannel) channel);
//            }
//            else {
                playerManager.loadAndPlay((TextChannel) channel,args[0]);
//            }

            playerManager.getGuildMusicManager(guild).player.setVolume(10);

            System.out.println("Playing: "+args[0]);
        }

        if (command.equalsIgnoreCase("leave")) {
            VoiceChannelManager.leaveChannel((MessageReceivedEvent) event);
        }

        if (command.equalsIgnoreCase("game")) {
            int map = 0;
            if (args.length > 0) {
                map = Map.getMap(args[0]); // get map using string
            }
            Game newGame = new Game(channel,guild,authorID, map);
            GlobalVars.add(newGame);
            System.out.println("[DEBUG LOG/Commands.java] Created game");
            return;
        }

        Game game = null;
        for (Game g : GlobalVars.currentGames) {
            if (g.channel.equals(channel)) {
                game = g;
            }
        }

        // Didn't find game
        if (game == null) return;

        if (command.equalsIgnoreCase("end")) {
            game.end();
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
