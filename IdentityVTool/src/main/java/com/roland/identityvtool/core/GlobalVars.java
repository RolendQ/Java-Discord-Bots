package com.roland.identityvtool.core;

import com.roland.identityvtool.enums.Map;
import com.roland.identityvtool.game.Game;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GlobalVars {
    public static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
    public static String ppath = "C:\\Users\\rolan\\Desktop\\IdentityVTool\\";
    public static ArrayList<Game> currentGames = new ArrayList<Game>();
    //public static BufferedImage[] maps = new BufferedImage[7];
    public static String[] mapNames = {"arms_factory","red_church","sacred_heart_hospital","lakeside_village","moonlit_river_park",
                                        "leos_memory","eversleeping"};
    public static MessageChannel filesChannel;
    public static HashMap<String, Integer> cipherIDs = new HashMap<>();
    public static String[] cipherEmojis = {"null","1⃣","2⃣","3⃣","4⃣","5⃣","6⃣","7⃣","8⃣","9⃣","0⃣","🇦","🇧","🇨"};

    public static void setup() {
        try {
            // Load maps into memory
            for (int i = 0; i < mapNames.length; i++) {
                images.put(mapNames[i], ImageIO.read(new File(ppath + mapNames[i] +".png")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Image could not be loaded");
        }

        for (int i = 1; i < 11; i++) {
            cipherIDs.put(i+"",i);
        }
        cipherIDs.put("A",11);
        cipherIDs.put("B",12);
        cipherIDs.put("C",13);
    }

    public static EmbedBuilder getSpawnEmbed(int map) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle("Spawns for "+mapNames[map].replaceAll("_"," ").toUpperCase());
        String survivorsText = "";
        for (String survivorSpawn : Map.survivorSpawns[map]) {
            survivorsText += "\n"+survivorSpawn;
        }
        String huntersText = "";
        for (String hunterSpawn : Map.hunterSpawns[map]) {
            huntersText += "\n"+hunterSpawn;
        }
        embed.addField("Survivor",survivorsText, true);
        embed.addField("Hunter",huntersText, true);
        //embed.setFooter("12345678901234567890123456789012345678901234567890123456789012345678901234567890", null);
        return embed;
    }

    public static void add(Game game) {
        currentGames.add(game);
        System.out.println("[DEBUG LOG/GlobalVars.java] Added a game");
    }

    public static void remove(Game game) {
        currentGames.remove(game);
        System.out.println("[DEBUG LOG/GlobalVars.java] Removed a game");
    }
}
