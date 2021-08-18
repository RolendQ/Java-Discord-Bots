package com.roland.bang.game;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;

public class Game {
    public Game(MessageChannel channel, Guild guild, String authorID) {
        this.channel = channel;
        this.guild = guild;
    }

    public void addPlayer(String playerID) {
        String name = guild.getMemberById(playerID).getEffectiveName();
    }

    public void start() {

    }

    public void end() {

    }

    public Player currentPlayer;
    public ArrayList<Player> players = new ArrayList<Player>();
    public Guild guild;
    public MessageChannel channel;
}
