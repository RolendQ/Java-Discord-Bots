package com.roland.bang.core;

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

    public static void setup() {
        try {
            images.put("board", ImageIO.read(new File(ppath + "whitehall_board.png")));
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
