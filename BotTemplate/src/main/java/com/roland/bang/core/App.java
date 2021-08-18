package com.roland.bang.core;

import com.roland.bang.game.Game;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class App extends ListenerAdapter {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, FileNotFoundException, ClassNotFoundException, IOException {
        // Removes error messages
        //BasicConfigurator.configure();

        // Use API to setup bot object and add listeners
        JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("Nzg4NDYxNDgzNTU4MDQzNjU4.X9j1-g.bA-ZyQSGx-TyCp0QHG0mRasPg-k").buildBlocking();
        jdaBot.addEventListener(new App());
        //jdaBot.getPresence().setGame(Game.of("type ;help"));

        System.out.println(jdaBot.getGuilds().get(0).getTextChannels());
        System.out.println("Setting up Bang bot");

        //GlobalVars.setup();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        if (e.getAuthor().isBot()) {

        } else {
            // Bot command symbol
            if (message.startsWith(";")) {
                processCommand(e, message.substring(1));
            }

            Game game = null;
            for (Game g : GlobalVars.currentGames) {
                if (g.channel.equals(e.getChannel())) {
                    game = g;
                }
            }
            // Didn't find game
            if (game == null) return;
        }
    }

    public void processCommand(net.dv8tion.jda.core.events.Event e, String text) {
        processCommand(e, text, false);
    }
    public void processCommand(net.dv8tion.jda.core.events.Event e, String text, boolean forced) {
        System.out.println("[DEBUG LOG/Game.java] Processing: "+text);
        String commandName = text.split(" ")[0];
        String[] args = {};
        if (text.split(" ").length > 1) {
            args = text.replace(commandName+" ","").split(" ");
        }
        Commands command = new Commands(commandName,e,args);
        // For forcing a player to do a command
//        if (forced) {
//            command.changeAuthor();
//        }
        command.attempt();
        command = null;
    }

}
