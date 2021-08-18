package JavaTutorials.MemorandumBot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class Utils {
	public static boolean isInt(String s) {
	    try {
	        Integer.parseInt(s);
	        return true;
	    } catch (NumberFormatException ex) {
	        return false;
	    }
	}
	public static String getID(String effectiveName,Guild guild) {
		List<Member> members = guild.getMembersByEffectiveName(effectiveName, false);
		for (Member searchedMember : members) {
			System.out.println(searchedMember.getNickname() + " " + searchedMember.getUser().getId());
		}
		return members.get(0).getUser().getId();
	}
	public static String getEffectiveName(String id,Guild guild) {
		return guild.getMemberById(id).getEffectiveName();
	}
	public static boolean dictionarySearch(String targetValue) {	
		int a =  Arrays.binarySearch(GlobalVars.dictionary, targetValue);
		if(a > 0)
			return true;
		else
			return false;
	}
//	public static boolean isGamemode(String input) {
//		for (String mode : currentModes) {
//			if (input.equals(mode)) {
//				return true;
//			}
//		}
//		return false;
//	}
	public static boolean isDev(Member member) {
		for (Role role : member.getRoles()) {
			if (role.getName().equals("Bot Developer")) {
				return true;
			}
		}
		return false;
	}
	public static boolean isManager(Member member) {
		for (Role role : member.getRoles()) {
			if (role.getName().equals("Bot Manager")) {
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
		for (int i = 0; i < set.length; i++) {
			if (set[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}
	//public static String[] currentModes = {"C","P","E","S","CLASSIC","PERPETUAL","ELIMINATION","SURVIVAL"};
	@SuppressWarnings("serial")
	public static ArrayList<String> currentModes = new ArrayList<String>() {{
	    add("C");
	    add("P");
	    add("E");
	    add("S");
	    add("CLASSIC");
	    add("PERPETUAL");
	    add("ELIMINATION");
	    add("SURVIVAL");
	}};
    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            // Magic
            System.setProperty("http.agent", "Chrome");
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
            	buffer.append(chars, 0, read); 
            System.out.println(buffer.toString());
            return buffer.toString();
            //return buffer.toString().substring(1,buffer.length()-1);
            
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}
