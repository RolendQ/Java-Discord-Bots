package com.roland.bang.core;

import com.roland.bang.enums.Role;
import com.roland.bang.game.Game;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GlobalVars {
    public static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
    public static String ppath = "C:\\Users\\rolan\\Desktop\\Bang\\images\\";
    public static ArrayList<Game> currentGames = new ArrayList<Game>();
    public static MessageChannel filesChannel;
    public static String[] characters = {"Bart","Jack","Janet","Ringo","Jessie","Jordan","Carl","Luke",
                                        "Paul","Pedro","Rose","Sid","Kain","Suzy","Sam","Will"};
    public static int[] healths = {8,8,8,7,9,7,7,8,9,8,9,8,8,8,9,8};
    public static String[] diceEmoji = {":bow_and_arrow:",":boom:",":one:",":two:",":gear:",":beer:"};
    public static String[] roleIndicators = {"S","D","O","R"};

    public static ArrayList<Integer> availableRoles = new ArrayList<Integer>();

    public static void setup() {
        availableRoles.add(Role.SHERIFF);
        availableRoles.add(Role.OUTLAW);
        availableRoles.add(Role.OUTLAW);
        availableRoles.add(Role.RENEGADE);
        availableRoles.add(Role.DEPUTY);
        availableRoles.add(Role.OUTLAW);
        availableRoles.add(Role.DEPUTY);
        availableRoles.add(Role.RENEGADE);
        try {
            images.put("table", ImageIO.read(new File(ppath + "table.png")));
            images.put("heart", ImageIO.read(new File(ppath + "heart.png")));
            images.put("arrows", ImageIO.read(new File(ppath + "arrows.png")));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Image could not be loaded");
        }
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
