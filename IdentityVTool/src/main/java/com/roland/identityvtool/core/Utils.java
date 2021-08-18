package com.roland.identityvtool.core;

import com.roland.identityvtool.enums.Map;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static boolean has(int[] array, int n) {
        for (int num : array) {
            if (num == n) return true;
        }
        return false;
    }

    public static int indexOf(String[] array, String s) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(s)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean range(int i, int low, int high) {
        return (i >= low && i <= high);
    }

    public static void sendCiphersImage(Guild guild, MessageChannel channel, int map, int group) throws IOException {
        System.out.println("Generating ciphers image");

        BufferedImage mapImage = GlobalVars.images.get(GlobalVars.mapNames[map]);

        BufferedImage combined = new BufferedImage(mapImage.getWidth(), mapImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        // Draws map first
        g.drawImage(mapImage, 0, 0, null);

        g.setColor(Color.MAGENTA);
        g.setFont(new Font("Arial Black", Font.BOLD, 48));
        if (group == -1) {
            // Show all
            for (int i = 0; i < Map.cipherCoordsX[map].length; i++) {
                if (Map.cipherCoordsX[map][i] != 0) { // Skip coordinates 0,0
                    Utils.drawCipher(g, map, i+1);
                }
            }
        } else {
            for (int cipherID : Map.cipherGroups[map][group]) {
                Utils.drawCipher(g, map, cipherID);
            }
        }

        // Writes the combined image into a file
        try {
            ImageIO.write(combined, "PNG", new File("newboard.png"));
        } catch (IOException e) {
            System.out.println("Fail to write file");
            e.printStackTrace();
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Ciphers");
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
    }


    public static BufferedImage scaleImage(BufferedImage img, int width, int height,
                                           Color background) {
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        if (imgWidth*height < imgHeight*width) {
            width = imgWidth*height/imgHeight;
        } else {
            height = imgHeight*width/imgWidth;
        }
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setBackground(background);
            g.clearRect(0, 0, width, height);
            g.drawImage(img, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return newImage;
    }

    public static int getValidCipherID(String message) {
        if (GlobalVars.cipherIDs.keySet().contains(message)) {
            return GlobalVars.cipherIDs.get(message);
        }
        return -1;
    }

    public static void drawCipher(Graphics g, int map, int cipherID) {
        if (cipherID < 11) {
            g.drawString(cipherID + "", Map.cipherCoordsX[map][cipherID - 1], Map.cipherCoordsY[map][cipherID - 1]);
        } else if (cipherID == 11) {
            g.drawString("A", Map.cipherCoordsX[map][cipherID - 1], Map.cipherCoordsY[map][cipherID - 1]);
        } else if (cipherID == 12) {
            g.drawString("B", Map.cipherCoordsX[map][cipherID - 1], Map.cipherCoordsY[map][cipherID - 1]);
        } else if (cipherID == 13) {
            g.drawString("C", Map.cipherCoordsX[map][cipherID - 1], Map.cipherCoordsY[map][cipherID - 1]);
        }
    }

    public static void sendTTS(MessageChannel channel, String text, int delay) {
        if (text == null) text = "Time is up";
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setContent(text);
        messageBuilder.setTTS(true);
        channel.sendMessage(messageBuilder.build()).queueAfter(delay, TimeUnit.MILLISECONDS);
    }
}
