package com.roland.wager.bot;

import com.roland.wager.core.Game;
import com.roland.wager.enums.Rarity;

import java.util.ArrayList;
import java.util.HashMap;


public class GlobalVars {

    //public static HashMap<String, String[]> colorStrings = new HashMap<String, String[]>();
    public static HashMap<String, String[]> colorEmojis = new HashMap<String, String[]>();
    public static HashMap<String, String> gambleEmojis = new HashMap<String, String>();
    public static HashMap<Integer, String[]> rarities = new HashMap<Integer, String[]>();

    public static void add(Game game) {
        currentGames.add(game);
        System.out.println("[DEBUG LOG/GlobalVars.java] Added a game");
    }

    public static void add(Challenge challenge) {
        pendingChallenges.add(challenge);
        System.out.println("[VARS] Added a challenge");
    }

    public static void remove(Game game) {
        currentGames.remove(game);
        System.out.println("[DEBUG LOG/GlobalVars.java] Removed a game");
    }

    public static void remove(Challenge challenge) {
        pendingChallenges.remove(challenge);
        System.out.println("[VARS] Removed a challenge");
    }

    public static void add(Tutorial t) {
        currentTutorials.add(t);
        System.out.println("[DEBUG LOG/GlobalVars.java] Added a tutorial");
    }

    public static void remove(Tutorial t) {
        currentTutorials.remove(t);
        System.out.println("[DEBUG LOG/GlobalVars.java] Removed a tutorial");
    }

    public static void createFiles() {
//        emojiCommands.put("❤️","r 1");
//        emojiCommands.put("💙","r 2");
//        emojiCommands.put("💚","r 3");
//        emojiCommands.put("💛","r 4");
//        emojiCommands.put("💜","r 5");
//        emojiCommands.put("\uD83D\uDFE5","r 1");
//        emojiCommands.put("\uD83D\uDFE6","r 2");
//        emojiCommands.put("\uD83D\uDFE9","r 3");
//        emojiCommands.put("\uD83D\uDFE8","r 4");
//        emojiCommands.put("\uD83D\uDFEA","r 5");
        emojiCommands.put("🗃️","r 0");
        emojiCommands.put("⏪","u");

//        ArrayList<String> squares = new ArrayList<String>();
//        squares.add(":red_square:");
//        squares.add(":blue_square:");
//        squares.add(":green_square:");
//        squares.add(":yellow_square:");
//        squares.add(":purple_square:");

        colorEmojis.put("squares", new String[]{"\uD83D\uDFE5","\uD83D\uDFE6","\uD83D\uDFE9","\uD83D\uDFE8","\uD83D\uDFEA"});
        colorEmojis.put("hearts", new String[]{"❤️","\uD83D\uDC99","\uD83D\uDC9A","\uD83D\uDC9B","\uD83D\uDC9C"});
        colorEmojis.put("circles", new String[]{"\uD83D\uDD34","\uD83D\uDD35","\uD83D\uDFE2","\uD83D\uDFE1","\uD83D\uDFE3"});
        colorEmojis.put("books", new String[]{"\uD83D\uDCD5","\uD83D\uDCD8","\uD83D\uDCD7","\uD83D\uDCD9","\uD83D\uDCD3"});
        colorEmojis.put("sea_creatures", new String[]{"\uD83E\uDD80","\uD83D\uDC2C","\uD83D\uDC22","\uD83D\uDC20","\uD83D\uDC19"});
        colorEmojis.put("faces", new String[]{"\uD83D\uDE21","\uD83E\uDD76","\uD83E\uDD22","\uD83E\uDD7A","\uD83D\uDE08"});
        colorEmojis.put("people", new String[]{"\uD83E\uDDDB\u200D♂️","\uD83E\uDDD9\u200D♂️","\uD83E\uDDDD\u200D♀️","\uD83D\uDC77","\uD83D\uDD75️\u200D♂️"});
        colorEmojis.put("clothing", new String[]{"\uD83D\uDC5F","\uD83D\uDC55","\uD83E\uDE73","\uD83E\uDDBA","\uD83D\uDC5A"});
        colorEmojis.put("birds", new String[]{"\uD83D\uDC26","\uD83D\uDC27","\uD83E\uDD9C","\uD83D\uDC24","\uD83D\uDD4A️"});
        colorEmojis.put("payment", new String[]{"\uD83E\uDDE7","\uD83D\uDC8E","\uD83D\uDCB6","\uD83D\uDCB0","\uD83E\uDDFE"});
        colorEmojis.put("vehicles", new String[]{"\uD83D\uDE97","\uD83D\uDE99","\uD83D\uDE9C","\uD83D\uDE95","\uD83D\uDE8C"});
        colorEmojis.put("insects", new String[]{"\uD83D\uDC1E","\uD83E\uDD8B","\uD83E\uDDA0","\uD83D\uDC1D","\uD83D\uDC1B"});
        colorEmojis.put("toys", new String[]{"\uD83E\uDE80","\uD83E\uDDE9","\uD83D\uDD2B","\uD83E\uDD4F","\uD83C\uDFAE"});
        colorEmojis.put("office_supplies", new String[]{"✂️","\uD83D\uDCC1","\uD83D\uDD0B","\uD83D\uDCCF","\uD83D\uDCCE"});
        colorEmojis.put("vegetables", new String[]{"\uD83C\uDF45","\uD83E\uDD54","\uD83E\uDD66","\uD83E\uDD55","\uD83C\uDF46"});
        colorEmojis.put("headwear", new String[]{"⛑️","\uD83E\uDDE2","\uD83C\uDFAD","\uD83D\uDC52","\uD83C\uDFA9"});
        colorEmojis.put("electronics", new String[]{"☎️ ","\uD83D\uDCBB","\uD83D\uDCDF","\uD83D\uDCA1","\uD83C\uDF99️"});
        colorEmojis.put("space", new String[]{"\uD83D\uDCA5","\uD83D\uDE80","\uD83C\uDF0E","\uD83E\uDE90 ","\uD83C\uDF0C"});
        colorEmojis.put("hobbies", new String[]{"\uD83E\uDD41","\uD83C\uDFF8","\uD83C\uDFBE","\uD83C\uDFB7","\uD83C\uDFD0"});
        colorEmojis.put("drinks", new String[]{"\uD83C\uDF77","\uD83E\uDD64","\uD83C\uDF75","\uD83C\uDF7A","\uD83C\uDF78"});
        colorEmojis.put("masks", new String[]{"\uD83D\uDC79","\uD83E\uDD16","\uD83D\uDC38","\uD83E\uDD21","\uD83D\uDC7D"});
        colorEmojis.put("floating_objects", new String[]{"\uD83C\uDF88","\uD83E\uDE81","\uD83C\uDF43","\uD83D\uDE81","☂️"});
        colorEmojis.put("asian_food", new String[]{"\uD83C\uDF63","\uD83C\uDF76","\uD83C\uDF61","\uD83E\uDD60","\uD83C\uDF59"});
        colorEmojis.put("nature", new String[]{"\uD83C\uDF4E","\uD83C\uDF0A","\uD83C\uDF3F","\uD83C\uDF29️","⛰️"});
        colorEmojis.put("mythical_beings", new String[]{"\uD83D\uDC98","☃️","\uD83D\uDC09","\uD83E\uDDA7","\uD83D\uDC7B"});
        colorEmojis.put("japanese", new String[]{"⛩️","\uD83D\uDC58","\uD83C\uDF90","\uD83C\uDF5C","\uD83C\uDFEF"});
        colorEmojis.put("holidays", new String[]{"\uD83D\uDC95","❄️","☘️","\uD83D\uDC23","\uD83E\uDD83"});

        gambleEmojis.put("die","\uD83C\uDFB2");
        gambleEmojis.put("crown","\uD83D\uDC51");
        gambleEmojis.put("ticket","\uD83C\uDF9F️");
        gambleEmojis.put("slots","\uD83C\uDFB0");
        gambleEmojis.put("stars","✨");
        gambleEmojis.put("clover","\uD83C\uDF40");

        rarities.put(Rarity.common, new String[]{"books","sea_creatures","faces","people","clothing","vehicles"});
        rarities.put(Rarity.rare, new String[]{"birds","payment","insects","toys","office_supplies","vegetables","headwear"});
        rarities.put(Rarity.epic, new String[]{"electronics","space","hobbies","drinks","masks","floating_objects","asian_food"});
        rarities.put(Rarity.legendary, new String[]{"nature","mythical_beings","japanese","holidays"});
        rarities.put(Rarity.mythic, new String[]{"crown","ticket","slots","stars","clover"});
        //colorEmojis.put("", new String[]{""});


        for (String[] set : colorEmojis.values()) {
            for (int i = 0; i < set.length; i++) {
                emojiCommands.put(set[i],"r "+(i+1));
            }
        }

        System.out.println("Loaded " + colorEmojis.size() + " emoji sets");
    }
    public static HashMap<String, String> emojiCommands = new HashMap<String, String>();
    public static ArrayList<Game> currentGames = new ArrayList<Game>(); // existing games in memory
    public static ArrayList<Challenge> pendingChallenges = new ArrayList<Challenge>();
    public static ArrayList<Tutorial> currentTutorials = new ArrayList<Tutorial>(); // existing tutorials in memory
    public static String[] whitelistedIDs = {"146732744070791168","287334685292625920"};
}
