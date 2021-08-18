package com.roland.bang.core;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Utils {

    public static int[][] avatarCoordsX = {{},{50},{50,355},{50,355,203},{50,355,503,203},{50,203,355,503,203},{50,203,355,503,355,203},{50,203,355,508,503,355,203},{50,203,355,508,503,355,203,54}};
    public static int[][] avatarCoordsY = {{},{90},{90,7},{90,7,343},{90,7,238,343},{90,7,7,238,343},{90,7,7,238,348,348},{90,7,7,90,238,348,348},{90,7,7,90,238,348,348,238}};
    public static int[] arrowCoords = {327,189};

    public static boolean has(int[] array, int n) {
        for (int num : array) {
            if (num == n) return true;
        }
        return false;
    }

    public static boolean range(int i, int low, int high) {
        return (i >= low && i <= high);
    }

    public static void saveImage(String imageUrl, String userID) throws IOException {
        System.setProperty("http.agent", "Chrome");
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(GlobalVars.ppath+userID+".png");

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();

        System.out.println("Saved avatar image: "+userID);
    }

    public static void sendSeatsImage(Guild guild, MessageChannel channel, String[] ids) throws IOException {
        System.out.println("Generating seats");
        BufferedImage combined = new BufferedImage(GlobalVars.images.get("table").getWidth(), GlobalVars.images.get("table").getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        // Draws table first
        g.drawImage(GlobalVars.images.get("table"), 0, 0, null);

        int[] coordsX = avatarCoordsX[ids.length];
        int[] coordsY = avatarCoordsY[ids.length];

        // Draw avatars
        for (int i = 0; i < ids.length; i++) {
            File avatar = new File(GlobalVars.ppath+ids[i]+".png");
            if (!avatar.exists()) {
                String avatarUrl = guild.getMemberById(ids[i]).getUser().getAvatarUrl();
                System.out.println("Avatar url: "+avatarUrl);
                saveImage(avatarUrl, ids[i]);
                avatar = new File(GlobalVars.ppath+ids[i]+".png");
            }
            g.drawImage(ImageIO.read(avatar),coordsX[i],coordsY[i], null);

            // name
            Random r = new Random();
            String characterName = GlobalVars.characters[r.nextInt(GlobalVars.characters.length)];
            g.setColor(Color.WHITE);
            g.fillRect(coordsX[i], coordsY[i], 128, 22);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial Black", Font.BOLD, 20));
            g.drawString(characterName, coordsX[i]+4, coordsY[i]+18);

            g.setColor(Color.MAGENTA);
            g.setFont(new Font("Arial Black", Font.BOLD, 24));
            g.drawString("S", coordsX[i]+108, coordsY[i]+20);

            // icons
            g.drawImage(GlobalVars.images.get("heart"),coordsX[i],coordsY[i]+96,null);
            g.drawImage(GlobalVars.images.get("arrows"),coordsX[i]+96,coordsY[i]+96,null);

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial Black", Font.BOLD, 28));
            g.drawString("8", coordsX[i]+6, coordsY[i]+120);
            g.drawString("0", coordsX[i]+102, coordsY[i]+120);
        }

        // Writes the combined image into a file
        try {
            ImageIO.write(combined, "PNG", new File("newboard.png"));
        } catch (IOException e) {
            System.out.println("Fail to write file");
            e.printStackTrace();
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Table");
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
}
