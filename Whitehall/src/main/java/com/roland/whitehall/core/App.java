package com.roland.whitehall.core;

import com.roland.whitehall.enums.GameStatusType;
import com.roland.whitehall.enums.SpecialMoveType;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.log4j.BasicConfigurator;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class App extends ListenerAdapter {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, FileNotFoundException, ClassNotFoundException, IOException {
        // Removes error messages
        //BasicConfigurator.configure();

        // Use API to setup bot object and add listeners
        JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("NzIwMDA3NTAwMDM1Nzg0ODc0.Xt_tYA.IJT5C7Z4hFuGCuF5Qh97NJ72IlM").buildBlocking();
        jdaBot.addEventListener(new App());
        jdaBot.getPresence().setGame(Game.of("type ;help"));

        //System.out.println(jdaBot.getGuilds().get(0).getTextChannels());
        System.out.println("Setting up Whitehall bot");

        Utils.createGraph();

        GlobalVars.setup();


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

            com.roland.whitehall.game.Game game = null;
            for (com.roland.whitehall.game.Game g : GlobalVars.currentGames) {
                if (g.channel.equals(e.getChannel())) {
                    game = g;
                }
            }
            // Didn't find game
            if (game == null) return;

            // Detecting moves through messages
            if (Utils.isCommand(message)) { // spaces is fine
                if (game.status == GameStatusType.startPhase) {
                    processCommand(e, "c " + message);
                } else if (game.status == GameStatusType.movePhase) {
                    processCommand(e, "m " + message);
                } else if (game.status == GameStatusType.searchPhase) {
                    processCommand(e, "s " + message);
                }
            }
        }
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        for (com.roland.whitehall.game.Game g : GlobalVars.currentGames) {
            if (g.currentPlayer.playerID.equals(e.getAuthor().getId())) {
                if (g.status == GameStatusType.movePhase) {
                    // Jack's moves
                    if (message.length() < 4) {
                        g.walk(Integer.parseInt(message), false);
                    } else if (message.startsWith("zoom")) {
                        g.drawBoard(Integer.parseInt(message.split(" ")[1]),true);
                    } else if (message.startsWith("alley")) {
                        g.useSpecial(SpecialMoveType.ALLEY,Integer.parseInt(message.split(" ")[1]));
                    } else if (message.startsWith("boat")) {
                        g.useSpecial(SpecialMoveType.BOAT,Integer.parseInt(message.split(" ")[1]));
                    } else if (message.startsWith("coach")) {
                        String[] args = message.split(" ");
                        g.useSpecial(SpecialMoveType.COACH,Integer.parseInt(args[2]),Integer.parseInt(args[1]));

                    } else if (message.startsWith("debug")) {
                        g.walk(Integer.parseInt(message.split(" ")[1]), true);
                    }

//                    else if (message.startsWith("visit")) {
//                        g.addVisit(Integer.parseInt(message.substring(6)));
//                    }
                } else if (g.status == GameStatusType.startPhase) {
                    if (message.length() < 4) {
                        // Choosing where to start
                        g.chooseJack(Integer.parseInt(message));
                    } else {
                        // Choosing body part locations
                        g.setupJack(message.split(" "));
                    }
                }
            }
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