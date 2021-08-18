package JavaTutorials.UltimateTTT;

import java.util.ArrayList;

public class Utils {
	public static int indexOf(char[] set, char value) {
		for (int i = 0; i < set.length; i++) {
			if (set[i] == value) {
				return i;
			}
		}
		return -1;
	}
	
	public static int indexOf(String[] set, String value) {
		for (int i = 0; i < set.length; i++) {
			if (set[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}
	
	public static boolean has(char[] set, char value) {
		for (int i = 0; i < set.length; i++) {
			if (set[i] == value) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean has(String[] set, String value) {
		for (int i = 0; i < set.length; i++) {
			if (set[i].equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isInt(String s) {
	    try {
	        Integer.parseInt(s);
	        return true;
	    } catch (NumberFormatException ex) {
	        return false;
	    }
	}
	
	public static char[] boardLetters = {'A','B','C','D','E','F','G','H','I'};
	public static String[] boardLettersEmoji = {"🇦","🇧","🇨","🇩","🇪","🇫","🇬","🇭","🇮"};
	public static String[] numbersEmoji = {"1⃣","2⃣","3⃣","4⃣","5⃣","6⃣","7⃣","8⃣","9⃣"};
}
