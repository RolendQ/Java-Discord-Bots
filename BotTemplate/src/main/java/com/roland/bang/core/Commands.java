package com.roland.bang.core;

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
    }

    public String command = "";
    public String authorID = "";
    public String[] args = {};
    public MessageChannel channel;
    public User user;
    public Guild guild;
    Event event;
}
