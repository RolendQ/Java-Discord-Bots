package com.roland.identityvtool.core;

import com.roland.identityvtool.audio.PlayerManager;
import com.roland.identityvtool.enums.CipherColor;
import com.roland.identityvtool.game.Game;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.log4j.BasicConfigurator;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class App extends ListenerAdapter {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, FileNotFoundException, ClassNotFoundException, IOException {
        // Removes error messages
        BasicConfigurator.configure();

        // Use API to setup bot object and add listeners
        JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("Nzk2ODU4ODIwMjE0OTE1MDgy.X_eCmw.QPGJ_wUv-5xLvHoprk7qeX2mnfo").buildBlocking();
        jdaBot.addEventListener(new App());
        System.out.println("audio: "+jdaBot.isAudioEnabled());
        //jdaBot.getPresence().setGame(Game.of("type ;help"));

        System.out.println(jdaBot.getGuilds().get(0).getTextChannels());
        System.out.println("Setting up Identity V Tool bot");

        GlobalVars.setup();

        //AudioSourceManagers.registerLocalSource(PlayerManager.getInstance());

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        if (e.getAuthor().isBot()) {
//            if (e.getMessage().isTTS()) e.getChannel().deleteMessageById(e.getMessageId()).queueAfter(5000,TimeUnit.MILLISECONDS);
//            if (message.startsWith("~")) {
//                MessageBuilder messageBuilder = new MessageBuilder();
//                messageBuilder.setContent(message.substring(1));
//                messageBuilder.setTTS(true);
//                //e.getChannel().deleteMessageById(e.getMessageId()).queue();
//                e.getChannel().sendMessage(messageBuilder.build()).complete();
//            }
            if (e.getMessage().getEmbeds().size() > 0) {
                for (Game game : GlobalVars.currentGames) {
                    // Detects if embed is for setting id
                    if (game.channel.equals(e.getChannel())) {
                        if (game.previousMapMsgID == null) { // if first map message
                            game.addGroupReactions(e.getMessage());
                        } else {
                            game.addReactions(e.getMessage());
                        }
                        game.previousMapMsgID = e.getMessageId();
                    }
                }
            }
        } else {
            // Bot command symbol
            if (message.startsWith(";")) {
                processCommand(e, message.substring(1));
                //e.getChannel().deleteMessageById(e.getMessageId()).queueAfter(5000,TimeUnit.MILLISECONDS);
            }

            Game game = null;
            for (Game g : GlobalVars.currentGames) {
                if (g.channel.equals(e.getChannel())) {
                    game = g;
                }
            }
            // Didn't find game
            if (game == null) return;

            // Checking current player messages for name input
            if (message.length() < 5) {
                int cipher;
                message = message.toUpperCase();
                if (message.startsWith("-")) {
                    // no cipher
                    cipher = Utils.getValidCipherID(message.substring(1));
                } else if (message.startsWith("R ") || message.startsWith("O ") || message.startsWith("Y ")) {
                    String[] args = message.split(" ");
                    game.setCipherColor(Utils.getValidCipherID(args[1]), CipherColor.getCipherColor(args[0]));

                    //e.getChannel().deleteMessageById(e.getMessageId()).queueAfter(1000, TimeUnit.MILLISECONDS);
                    return;
                } else {
                    // yes cipher
                    cipher = Utils.getValidCipherID(message);
                }


                if (cipher != -1) {
                    if (message.startsWith("-")) {
                        game.checkCipher(cipher, false);
                    } else {
                        if (game.determinedGroup()) {
                            game.popCipher(cipher);
                        } else {
                            game.checkCipher(cipher, true);
                        }
                    }
                }

                //e.getChannel().deleteMessageById(e.getMessageId()).queueAfter(1000, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (!e.getMember().getUser().isBot()) {
            //System.out.println("[DEBUG LOG/App.java] Detected Emoji: "+e.getReaction().getEmote().getName());
            //System.out.print("\"" + e.getReaction().getEmote().getName() + "\"");
            String emoji = e.getReaction().getEmote().getName();
            for (Game game : GlobalVars.currentGames) {
                if (game.channel.equals(e.getChannel())) {
                    int index = Utils.indexOf(GlobalVars.cipherEmojis,emoji);
                    if (index != -1) {
                        System.out.println("Detected cipher emoji: "+emoji);
                        if (game.determinedGroup()) {
                            game.popCipher(index);
                            if (game.ciphersLeft.size() == 2) return;
                        } else {
                            game.setGroup(index-1);
                        }
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
