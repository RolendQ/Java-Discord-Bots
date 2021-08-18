package com.roland.wager.core;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import com.roland.wager.bot.*;
import com.roland.wager.gameelements.Player;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App extends ListenerAdapter {

    static Logger logger = LogManager.getLogger(App.class);

    // Main method that runs the bot
    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, FileNotFoundException, ClassNotFoundException, IOException {
        // Removes error messages
        //BasicConfigurator.configure();

        // Creates bot with token
        JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("NjU5MDQ3MDk5NTMwMzQ2NTU2.XgInwg.aaOlc-R2Rk02hh6TyXN-tVqrgTo").buildBlocking();
        jdaBot.addEventListener(new App());
        jdaBot.getPresence().setGame(Game.of("WIP"));

        // Logs guilds
        for (int i = 0; i < jdaBot.getGuilds().size(); i++) {
            System.out.println(jdaBot.getGuilds().get(i).getName() + ": " + jdaBot.getGuilds().get(i).getId());
        }

        logger.info("\nNew logger initiated");
        System.out.println("Launching Wager Bot...");
        GlobalVars.createFiles();
        System.out.println("Wager Bot successfully launched!");
    }

    @SuppressWarnings("deprecation")
    public void onMessageReceived(MessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        if (e.getAuthor().isBot()) {
            // Detecting bot messages
        } else if (message.startsWith(";")){
            // Commands in public server chat
            processCommand(e, message.substring(1));
        }
    }

    @SuppressWarnings("deprecation")
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        String message = e.getMessage().getContent();
        if (e.getAuthor().isBot()) {
            if (e.getMessage().getEmbeds().size() > 0) {
                String embed = e.getMessage().getEmbeds().get(0).getTitle();
                Player player = null;
                com.roland.wager.core.Game game = null;
                for (com.roland.wager.core.Game g : GlobalVars.currentGames) {
                    // Must be current player to add reactions
                    //System.out.println(e.getChannel().getUser().getName());
                    if (g.getCurrentPlayer().getPlayerID().contentEquals(e.getChannel().getUser().getId())) {
                        player = g.getCurrentPlayer();
                        game = g;
                        break;
                    }
                }
                if (player == null) return;
                // This player is current player so add reactions
                if (embed.startsWith("Discard")) {
                    game.addDiscardPileReactions(e.getChannel(), e.getMessageId());
                } else if (embed.startsWith("Your Hand")) {
                    game.addHandReactions(e.getChannel(), e.getMessageId());
                }
            }
        } else {
            // Process command from player (no command symbol)
            processCommand(e, message);
        }
    }

    @SuppressWarnings("deprecation")
    public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent e) {
        if (!e.getUser().isBot()) {
            String emoji = e.getReaction().getEmote().getName();

            Tutorial tutorial = null;
            for (Tutorial t : GlobalVars.currentTutorials) {
                // Must be this user and fit one of their ids
                if (t.getUserID().contentEquals(e.getUser().getId()) && (e.getMessageId().contentEquals(t.getEmbedIDs()[1]) || e.getMessageId().contentEquals(t.getEmbedIDs()[0]))) {
                    tutorial = t;
                    break;
                }
            }

            if (tutorial == null) { // Not tutorial, so in game

                Player player = null;
                for (com.roland.wager.core.Game g : GlobalVars.currentGames) {
                    for (Player p : g.getPlayers()) {
                        if (p.getPlayerID().contentEquals(e.getUser().getId())) {
                            player = p;
                            break;
                        }
                    }
                }
                if (player == null) return;

                // This player is current player so detect reactions
                if (emoji.equals("👆")) { // Play
                    player.setLinkedCommand("p ");
                } else if (emoji.equals("🔥")) { // Discard
                    player.setLinkedCommand("d ");
                } else if (GlobalVars.emojiCommands.containsKey(emoji)) {
                    // Draw from discard or deck or undo
                    //e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
                    processCommand(e, GlobalVars.emojiCommands.get(emoji));
                } else if (player.isLinking()) { // Process a letter for playing or discarding
                    processCommand(e, player.getLinkedCommand()+(Utils.indexOf(Utils.numToLetterEmojis,emoji)+1));
                    player.setLinkedCommand(null);
                }

            } else { // Found tutorial

                if (emoji.contentEquals("⏪") && tutorial.getPage() != 0) {
                    tutorial.showPage(-1);
                    return;
                }

                String requiredEmoji = Tutorial.reactions[tutorial.getPage()][0];
                System.out.println(requiredEmoji);

                if (tutorial.getPage() == 3) { // Play page
                    requiredEmoji = "🇦";
                    if (!tutorial.getLastReaction().contentEquals("👆")) {
                        requiredEmoji = ""; // fails
                    }
                } else if (tutorial.getPage() == 7) {
                    requiredEmoji = "🇦";
                    if (!tutorial.getLastReaction().contentEquals("\uD83D\uDD25")) {
                        requiredEmoji = "";
                    }
                } else if (tutorial.getPage() == 10) {
                    requiredEmoji = "\uD83C\uDDE7"; // B
                    if (!tutorial.getLastReaction().contentEquals("👆")) {
                        requiredEmoji = "";
                    }
                } else if (tutorial.getPage() == 11) {
                    requiredEmoji = "\uD83D\uDFE6";
                }

                if (requiredEmoji.contentEquals(emoji)) { // Check to see if correct emoji is pressed
                    tutorial.showPage(1);
                }
                tutorial.setLastReaction(emoji);
            }
        }
    }

//    public void onTextChannelCreate(TextChannelCreateEvent e) {
//        TextChannel tc = e.getChannel();
//        if (tc.getName().endsWith("hand")) {
//            tc.sendMessage("Hi "+e.getChannel().getName());
//            //tc.createPermissionOverride(role)
//        }
//    }

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (!e.getMember().getUser().isBot()) {
            String authorID = e.getUser().getId();

            if (e.getReaction().getEmote().getName().equals("✅")) {

                // Accepting a challege
                for (Challenge c : GlobalVars.pendingChallenges) {
                    if (c.getChallengedID() == null || c.getChallengedID().equals(authorID)) {
                        User challengerUser = e.getChannel().getMessageById(e.getMessageId()).complete().getAuthor();
                        com.roland.wager.core.Game newGame = new com.roland.wager.core.Game(e.getChannel(), e.getGuild(), challengerUser, e.getUser(), c.getChallengerID(), authorID);
                        GlobalVars.add(newGame);
                        System.out.println("Created game from challenge");
                        newGame.start();
                        break;
                    }
                }
            }
        }
    }

    public void processCommand(net.dv8tion.jda.core.events.Event e, String text) {
        System.out.println("[DEBUG LOG/Game.java] "+e.toString()+": "+text);
        String commandName = text.split(" ")[0];
        String[] args = {};
        if (text.split(" ").length > 1) {
            args = text.replace(commandName+" ","").split(" ");
        }
        Commands command = new Commands(commandName,e,args);
        command.attempt();
        command = null;
    }
}
