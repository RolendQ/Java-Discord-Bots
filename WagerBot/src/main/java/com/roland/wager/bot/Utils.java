package com.roland.wager.bot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
//import java.util.function.Consumer;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class Utils {
    public static String[] numToEmoji = {":zero:",":one:",":two:",":three:",":four:",":five:",":six:",":seven:",":eight:",":nine:",
            ":one::zero:",":one::two:",":one::three:",":one::four:",":one::five:",":one::six:",":one::seven:",
            ":one::eight:",":one::nine:",":two::zero:"};
    public static String[] endGamePlaces = {"First Place :first_place:","Second Place :second_place:","Third :third_place:","Last Place :medal:"};
    public static String[] numToNumEmoji = {"0⃣","1⃣","2⃣","3⃣","4⃣","5⃣","6⃣","7⃣","8⃣","9⃣"};
    //                              \u0030\u20e3 to \u0039\u20e3
    public static String[] numToLetterEmojis = {"🇦","🇧","🇨","🇩","🇪","🇫","🇬","🇭","🇮","🇯","🇰","🇱","🇲","🇳","🇴","🇵","🇶","🇷","🇸","🇹","🇺","🇻","🇼","🇽","🇾","🇿"};
    //         1                     \ud83c\udde6-9 (D)      \ud83c\uddea-f (J)       \ud83c\uddf0-9 (T)      \ud83c\uddfa-f (Z)
    //public static String[] numToColorString = {":heart:",":blue_heart:",":green_heart:",":yellow_heart:",":purple_heart:"};
    //public static String[] numToColorString = {":red_square:",":blue_square:",":green_square:",":yellow_square:",":purple_square:"};
    //public static String[] numToColorEmoji = {"❤️","💙","💚","💛","💜"};
    //public static String[] numToColorEmoji = {"\uD83D\uDFE5","\uD83D\uDFE6","\uD83D\uDFE9","\uD83D\uDFE8","\uD83D\uDFEA"};

    public static boolean isLetterEmoji(String s) {
        // flag ends with \udfc1
        return s.startsWith("\ud83c") && !s.endsWith("\udfc1");
    }

    public static boolean isNumEmoji(String s) {
        return s.endsWith("\u20e3");
    }
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean isAdmin(String id) {
        for (int i = 0; i < GlobalVars.whitelistedIDs.length; i++) {
            if (GlobalVars.whitelistedIDs[i].contentEquals(id)) return true;
        }
        return false;
    }

    // Get id using effectiveName and guild (returns first)
    public static String getID(String effectiveName,Guild guild) {
        List<Member> members = guild.getMembersByEffectiveName(effectiveName, false);
        for (Member searchedMember : members) {
            //System.out.println(searchedMember.getNickname() + " " + searchedMember.getUser().getId());
        }
        return members.get(0).getUser().getId();
    }

    // Get name using guild and ID
    public static String getName(String id,Guild guild) {
        return guild.getMemberById(id).getEffectiveName();
    }

    public static boolean has(int[] set, int value) {
        for (int item : set) {
            if (item == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean has(String[] set, String value) {
        for (String item : set) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static int indexOf(String[] set, String value) {
        int i = 0;
        for (String item : set) {
            if (item.contentEquals(value)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static List<Entry<Integer, Integer>> sort(Map<Integer, Integer> scores) {
        Set<Entry<Integer, Integer>> set = scores.entrySet();
        List<Entry<Integer, Integer>> sortedList = new ArrayList<Entry<Integer, Integer>>(set);
        Collections.sort(sortedList, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2 ) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        } );
        return sortedList;
    }

//	public static void directMessage(User user, final EmbedBuilder embed) {
//		directMessage(user, embed, 0);
//	}

    public static void directMessage(User user, final EmbedBuilder embed) {
        user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
            public void accept(PrivateChannel channel) {
                channel.sendMessage(embed.build()).queue();
            }
        });
    }

//	public static void addDiscardPileReactions(PrivateChannel pmChannel, String messageID) {
//		pmChannel.addReactionById(messageID, "❤️").queue();
//		pmChannel.addReactionById(messageID, "💙").queue();
//		pmChannel.addReactionById(messageID, "💚").queue();
//		pmChannel.addReactionById(messageID, "💛").queue();
//		pmChannel.addReactionById(messageID, "💜").queue();
//		pmChannel.addReactionById(messageID, "🗃️").queue();
//	}
//
//	public static void addHandReactions(PrivateChannel pmChannel, String messageID) {
//		pmChannel.addReactionById(messageID, "👆").queue();
//		pmChannel.addReactionById(messageID, "🔥").queue();
//		for (int i = 0; i < 8; i++) {
//			pmChannel.addReactionById(messageID, numToLetterEmojis[i]).queue();
//		}
//	}
}

