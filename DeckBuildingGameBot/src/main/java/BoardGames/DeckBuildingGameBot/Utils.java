package BoardGames.DeckBuildingGameBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;

public class Utils {
	public static String[] colors = {"RED","BLUE","GREEN","ORANGE"};
	public static String[] colorBooks = {":closed_book:",":blue_book:",":green_book:",":orange_book:",":notebook_with_decorative_cover:"}; // change Card string later
	public static String[] resourceEmojis = {":diamond_shape_with_a_dot_inside:",":star:",":green_heart:",":boom:",":shield",":book:",":wastebasket:"};
	public static String[] numToEmoji = {":zero:",":one:",":two:",":three:",":four:",":five:",":six:",":seven:",":eight:",":nine:",
										":one::zero:",":one::two:",":one::three:",":one::four:",":one::five:",":one::six:",":one::seven:",
										":one::eight:",":one::nine:",":two::zero:",":two::zero:",":two::one:",":two::two:",":two::three:",
										":two::four:",":two::five:",":two::six:",":two::seven:",":two::eight:",":two::nine:",
										":three::zero:",":three::two:",":three::three:",":three::four:",":three::five:",":three::six:",":three::seven:",
										":three::eight:",":three::nine:",":four::zero:"};
	public static String[] endGamePlaces = {"First Place :first_place:","Second Place :second_place:","Third :third_place:","Last Place :medal:"};
	public static String[] numToNumEmoji = {"0⃣","1⃣","2⃣","3⃣","4⃣","5⃣","6⃣","7⃣","8⃣","9⃣"};
	//                              \u0030\u20e3 to \u0039\u20e3
	public static String[] numToLetterEmojis = {"🇦","🇧","🇨","🇩","🇪","🇫","🇬","🇭","🇮","🇯","🇰","🇱","🇲","🇳","🇴","🇵","🇶","🇷","🇸","🇹","🇺","🇻","🇼","🇽","🇾","🇿"};
	//         1                     \ud83c\udde6-9 (D)      \ud83c\uddea-f (J)       \ud83c\uddf0-9 (T)      \ud83c\uddfa-f (Z)
	
	public static String[] letters = {"A","B","C","D"};
	public static User botUser;
	
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
	public static void setBotUser(User user) {
		botUser = user;
	}
	
	public static void removeEmoji(MessageChannel mc, String messageID, User user, String emoji) {
		//List<MessageReaction> reactions = gameChannel.getMessageById(embedIDs[0]).complete().getReactions();
		List<MessageReaction> reactions = mc.getMessageById(messageID).complete().getReactions();
		System.out.println(reactions.size());
		for (int i = 0; i < reactions.size(); i++) {
			String emoj = reactions.get(i).getReactionEmote().getName();
			if (emoj.contentEquals(emoji)) {
				reactions.get(i).removeReaction().complete();
				reactions.get(i).removeReaction(user).complete();
				System.out.println("removed");
			}
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
			if (item != null && item.contentEquals(value)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean has(ArrayList<String> set, String value) {
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
	
	public static String arrayToString(int[] array) {
		if (array.length == 0) return "";
		String result = "";
		for (int num : array) {
			result += num + ", ";
		}
		return result.substring(0,result.length()-2);
	}
	
	public static String arrayToEmojiString(int[] array) {
		return arrayToEmojiString(array, 0);
	}
	
	public static String arrayToEmojiString(int[] array, int startingNum) {
		if (array.length == 0) return "";
		String result = "";
		for (int i = 0; i < array.length; i++) {
			result += ":regional_indicator_"+((char)(startingNum+i+97))+": **"+array[i]+"** ";
		}
		return result.substring(0,result.length()-1);
	}
	
	public static int getColorFromString(String s) {
		s = s.toUpperCase();
		int color = -1;
		if (s.startsWith("R")) color = 0;
		else if (s.startsWith("B")) color = 1;
		else if (s.startsWith("G")) color = 2;
		else if (s.startsWith("O")) color = 3;
		return color;
	}
	
	public static String shorten(String s, int amount) {
		if (s.length() > amount-1) {
			return s.substring(0,amount);
		}
		return s;
	}	
	
	public static String getGameID() {
		//String filePath = GlobalVars.gpath+"Game#";
		String filePath = "Games\\"+"Game";
		File file;
		for (int i = 1; i < 1000; i++) {
			file = new File(filePath+i+".txt");
			if (!file.isFile()) return i+"";
        } 
		return "1001";
	}
}