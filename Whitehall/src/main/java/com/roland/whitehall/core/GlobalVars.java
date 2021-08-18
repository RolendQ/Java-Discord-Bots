package com.roland.whitehall.core;


import com.roland.whitehall.game.Game;
import com.roland.whitehall.game.Piece;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GlobalVars {

    public static HashMap<String,BufferedImage> images = new HashMap<String, BufferedImage>();
    public static String ppath = "C:\\Users\\rolan\\Desktop\\Whitehall\\images\\";
    public static ArrayList<Game> currentGames = new ArrayList<Game>();
    public static HashMap<String, Piece> pieces = new HashMap<String, Piece>();
    public static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    public static MessageChannel filesChannel;

    public static void setup() {
        try {
            images.put("board", ImageIO.read(new File(ppath + "whitehall_board.png")));
            images.put("red_char", ImageIO.read(new File(ppath + "RedChar.png")));
            images.put("yellow_char", ImageIO.read(new File(ppath + "YellowChar.png")));
            images.put("blue_char", ImageIO.read(new File(ppath + "BlueChar.png")));
            images.put("green_char", ImageIO.read(new File(ppath + "GreenChar.png")));
            images.put("red_marker", ImageIO.read(new File(ppath + "RedMarker.png")));
            images.put("gold_marker", ImageIO.read(new File(ppath + "GoldMarker.png")));
            images.put("green_marker", ImageIO.read(new File(ppath + "GreenMarker.png")));
            images.put("cyan_marker", ImageIO.read(new File(ppath + "CyanMarker.png")));
            for (String letter : letters) {
                images.put("letter_"+letter, ImageIO.read(new File(ppath + "letter_"+letter+".png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Image could not be loaded");
        }
        pieces.put("red",new Piece(new File(ppath+"RedChar.png"),false,0,0));
        pieces.put("blue",new Piece(new File(ppath+"BlueChar.png"),false,0,0));
        pieces.put("yellow",new Piece(new File(ppath+"YellowChar.png"),false,0,0));
        pieces.put("green",new Piece(new File(ppath+"GreenChar.png"),true,0,0));



        System.out.println("Crossings: "+ Utils.crossingCoords.length);
        System.out.println("Locations: "+Utils.locationCoords.length);
    }

    public static void add(Game game) {
        currentGames.add(game);
        System.out.println("[DEBUG LOG/GlobalVars.java] Added a game");
    }

    public static void remove(Game game) {
        currentGames.remove(game);
        System.out.println("[DEBUG LOG/GlobalVars.java] Removed a game");
    }

    public static int letterToInt(String letter) {
        int i = 0;
        for (String l : letters) {
            if (l.equalsIgnoreCase(letter)) return i;
            i++;
        }
        return -1;
    }
}
