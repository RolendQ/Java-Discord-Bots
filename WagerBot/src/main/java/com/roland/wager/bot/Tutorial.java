package com.roland.wager.bot;

import java.awt.Color;
import java.io.File;
import java.util.function.Consumer;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

public class Tutorial {
    public String userID; // id of user doing tutorial
    public PrivateChannel pchannel; // channel
    public String[] embedIDs; // id of embed demonstrations
    public int page;
    public String lastReaction; // For mercenary page
    // all reactions to add per pages
    public static String[][] reactions =
            {{"✅"},{"✅"},{"✅"},{"👆","🇦","🇧","\uD83C\uDDE8"},{"✅"},{"\uD83D\uDDC3️"},{"✅"},{"👆","\uD83D\uDD25","🇦","🇧","\uD83C\uDDE8"},{"\uD83D\uDDC3️"},{"✅"},{"👆","\uD83D\uDD25","🇦","🇧","\uD83C\uDDE8"},{"\uD83D\uDFE5","\uD83D\uDFE6","\uD83D\uDDC3️"},{"✅"},{"✅"},{"\uD83C\uDFAE"}};

    public Tutorial(String userID, PrivateChannel pchannel) {
        this.userID = userID;
        this.pchannel = pchannel;
        startTutorial();
        System.out.println("pm embed id: "+embedIDs[0]);
        page = 0;
    }

    public void startTutorial() {
        embedIDs = new String[2];
        EmbedBuilder embed1 = new EmbedBuilder();
        embed1.setColor(Color.GRAY);
        //embed1.setTitle("Shards of Infinity Tutorial");
        embed1.setDescription("Welcome to **Wager**! This bot was created by Rolend#1816. This tutorial will let you learn the game at your own pace. "
                + "Just click confirm :white_check_mark: to continue. If you ever need to go back, click rewind :rewind:");
        //embed1.setFooter("1", null);
        embedIDs[0] = pchannel.sendMessage(embed1.build()).complete().getId();

        EmbedBuilder embed2 = new EmbedBuilder();
        embed2.setColor(Color.GRAY);
        //embed2.setTitle("Board Game");

        embed2.setImage("https://www.pojo.com/wp-content/uploads/2019/10/Lost-Cities-Card-Game-Contents.jpg");
        //File image = new File("game_cover.png");
        embedIDs[1] = pchannel.sendMessage(embed2.build()).complete().getId();
        addReactions();
    }

    public void addReactions() {
        if (reactions[page] == null) return;
        pchannel.addReactionById(embedIDs[0],"⏪").queue(); // Rewind
        for (String r : reactions[page]) {
            pchannel.addReactionById(embedIDs[1],r).queue(); // only add to second
        }
    }

    public void showPage(int change) {
        page += change;
        System.out.println("Showing page: "+page);
        if (page >= TextDatabase.embedData.length) return;
        EmbedBuilder[] embeds = TextDatabase.getEmbeds(page);
        pchannel.editMessageById(embedIDs[0],embeds[0].build()).queue();
        //pchannel.editMessageById(embedIDs[1],embeds[1].build()).queue(); Can't clear reactions
        pchannel.deleteMessageById(embedIDs[1]).queue();
        embedIDs[1] = pchannel.sendMessage(embeds[1].build()).complete().getId();
        addReactions();
    }

    public void setLastReaction(String r) {
        this.lastReaction = r;
    }

    public String getLastReaction() {
        return lastReaction;
    }

    public String getUserID() {
        return userID;
    }

    public int getPage() {
        return page;
    }

    public String[] getEmbedIDs() {
        return embedIDs;
    }
}
