package com.roland.identityvtool.game;

import com.roland.identityvtool.core.GlobalVars;
import com.roland.identityvtool.core.Utils;
import com.roland.identityvtool.enums.CipherColor;
import com.roland.identityvtool.enums.Map;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Game {
    public Game(MessageChannel channel, Guild guild, String authorID, int map) {
        this.channel = channel;
        this.guild = guild;
        this.map = map;
        cipherGroup = -1;
        cipherStatus = new CipherStatus(map);
        ciphersLeft = new ArrayList<>();
        cipherColors = new HashMap<>();
        previousMapMsgID = null;

        channel.sendMessage(GlobalVars.getSpawnEmbed(map).build()).queue();

        //channel.sendMessage("~Teleport is up").queueAfter(4000,TimeUnit.MILLISECONDS);
        Utils.sendTTS(channel, "Teleport is up",42000);
        Utils.sendTTS(channel, "Blink is up",57000);

        drawMap(true);
    }


    public void checkCipher(int cipher, boolean exists) {
        System.out.println("Checking cipher: "+cipher+" with: "+exists);
        int group = cipherStatus.checkCipher(cipher, exists);
        if (group != -1) {
            setGroup(group);
        }
    }

    public void setGroup(int group) {
        cipherGroup = group;
        System.out.println("Found cipher group: "+group);
        // Set up ciphers left
        for (int cipherID : Map.cipherGroups[map][cipherGroup]) {
            ciphersLeft.add(cipherID);
        }
        // Send picture
        drawMap(false);
    }

    public void popCipher(int cipher) {
        System.out.println("Popping cipher: "+cipher);
        if (ciphersLeft.contains(cipher)) {
            ciphersLeft.remove(Integer.valueOf(cipher));
            if (ciphersLeft.size() == 2) { // 5 ciphers popped
                end();
            } else {
                drawMap(false);
            }
        }
    }

    public void drawMap(boolean isFirst) {
        BufferedImage mapImage = GlobalVars.images.get(GlobalVars.mapNames[map]);
        BufferedImage combined = new BufferedImage(mapImage.getWidth(),mapImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();

        // Draws map first
        g.drawImage(mapImage, 0, 0, null);

        // Draw all cipher markers
        g.setFont(new Font("Arial Black", Font.BOLD, 48));
        if (isFirst) {
//            g.setColor(Color.MAGENTA);
//            for (int cipherID : CipherStatus.keyCipherIDs[map]) {
//                Utils.drawCipher(g, map, cipherID);
//            }
            for (int i = 0; i < CipherStatus.decisiveCiphers[map].length; i++) {
                int cipher = CipherStatus.decisiveCiphers[map][i];
                int group = i + 1;
                g.setColor(CipherStatus.decisiveCiphersColor[map][i]);
                if (cipher == 0) {
                    g.drawString(group + "", 25, 65);
                } else {
                    g.drawString(group + "", Map.cipherCoordsX[map][cipher-1], Map.cipherCoordsY[map][cipher-1]);
                }
            }
        } else {
            for (int cipherID : ciphersLeft) {
                if (cipherColors.get(cipherID) == null) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(CipherColor.colors[cipherColors.get(cipherID)]);
                }
                Utils.drawCipher(g, map, cipherID);
            }
        }

        System.out.println("Finished drawing map");

        // Writes the combined image into a file
        try {
            ImageIO.write(combined, "PNG", new File("newboard.png"));
        } catch (IOException e) {
            System.out.println("Fail to write file");
            e.printStackTrace();
        }

        EmbedBuilder embed = new EmbedBuilder();
        if (isFirst) embed.setTitle("Need to determine the cipher group");
        else embed.setTitle("Group #"+(cipherGroup+1)+": "+(ciphersLeft.size() - 2) + " ciphers left");
        embed.setColor(Color.GRAY);
        InputStream test = null;
        try {
            test = new FileInputStream("newboard.png");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        embed.setImage("attachment://newboard.png");
        MessageBuilder m = new MessageBuilder();
        m.setEmbed(embed.build());
        channel.sendFile(test, "newboard.png", m.build()).queue();
        if (!isFirst) {
            ((TextChannel) channel).clearReactionsById(previousMapMsgID).complete();
            //channel.deleteMessageById(previousMapMsgID).queueAfter(1000, TimeUnit.MILLISECONDS);
        }
    }

    public void addReactions(Message message) {
        for (int cipherID : ciphersLeft) {
            message.addReaction(GlobalVars.cipherEmojis[cipherID]).queue();
        }
    }

    public void addGroupReactions(Message message) {
        for (int i = 1; i <= 5; i++) {
            message.addReaction(GlobalVars.cipherEmojis[i]).queue();
        }
    }

    public void setCipherColor(int cipher, int color) {
        System.out.println("Set color of: "+cipher+" to "+color);
        cipherColors.put(cipher,color);
        drawMap(false);
    }

    public void end() {
        //if (previousMapMsgID != null) channel.deleteMessageById(previousMapMsgID).queue();
        System.out.println("Removing game");
        GlobalVars.remove(this);
    }

    public boolean determinedGroup() {
        return cipherGroup != -1;
    }

    public String previousMapMsgID;
    public HashMap<Integer,Integer> cipherColors;
    public int map;
    public int cipherGroup;
    public ArrayList<Integer> ciphersLeft;
    public CipherStatus cipherStatus;
    public Guild guild;
    public MessageChannel channel;
}
