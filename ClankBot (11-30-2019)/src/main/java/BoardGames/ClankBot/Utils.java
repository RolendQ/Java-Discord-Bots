package BoardGames.ClankBot;

import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class Utils {
	public static String[] numToEmoji = {":zero:",":one:",":two:",":three:",":four:",":five:",":six:",":seven:",":eight:",":nine:",
										":one::zero:",":one::two:",":one::three:",":one::four:",":one::five:",":one::six:",":one::seven:",
										":one::eight:",":one::nine:",":two::zero:"};
	public static String[] endGamePlaces = {"First Place :first_place:","Second Place :second_place:","Third :third_place:","Last Place :medal:"};
	public static String[] numToNumEmoji = {"0⃣","1⃣","2⃣","3⃣","4⃣","5⃣","6⃣","7⃣","8⃣","9⃣"};
	public static String[] numToLetterEmojis = {"🇦","🇧","🇨","🇩","🇪","🇫","🇬","🇭","🇮","🇯","🇰","🇱","🇲","🇳","🇴","🇵","🇶","🇷","🇸","🇹","🇺","🇻","🇼","🇽","🇾","🇿"};
	
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
	// Specific rooms are caves
	public static boolean isCave(int room) {
		return (room == 6 || room == 7 || room == 9 || room == 13 || room == 17 || room == 21 || room == 22 || room == 26 || room == 31 || room == 38);
	}
	
	// Calculate points for any card
	public static int calculatePointsForCard(Card c,Player p) {
		int points = c.getPoints();
		if (c.isUnique()) {
			if (c.getName().contentEquals("Wizard")) {
				points += 2*p.getSecretTomes();
			} else if (c.getName().contentEquals("Dragon's Eye")) {
				if (p.isFree()) points += 10;
			} else if (c.getName().contentEquals("The Duke")) {
				points += p.getGold() / 5;
			} else if (c.getName().contentEquals("Dwarven Peddler")) {
				if ((p.has("Chalice") && p.has("Egg")) || 
					(p.has("Chalice") && p.has("MonkeyIdol")) ||
					(p.has("Egg") && p.has("MonkeyIdol"))) {
					points += 4;
				}
			}
		}
		
		return points;
	}
	
}