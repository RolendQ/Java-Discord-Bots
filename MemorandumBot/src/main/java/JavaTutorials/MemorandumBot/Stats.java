package JavaTutorials.MemorandumBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Stats {
	public static void saveStats() throws IOException {
		File myfile = new File("stats");
		FileOutputStream f = new FileOutputStream(myfile);
		ObjectOutputStream s = new ObjectOutputStream(f);
		s.writeObject(playerStats);
		s.close();
		System.out.println("[STATS] Stats saved");
	}
	public static void saveRecords() throws IOException {
		File myfile = new File("records");
		FileOutputStream f = new FileOutputStream(myfile);
		ObjectOutputStream s = new ObjectOutputStream(f);
		s.writeObject(playerRecords);
		s.close();
		System.out.println("[STATS] Records saved");		
	}
	@SuppressWarnings("unchecked")
	public static void loadStats() throws FileNotFoundException, IOException, ClassNotFoundException {
		playerStats.clear();
		File myfile = new File("stats");
		FileInputStream f = new FileInputStream(myfile);
		ObjectInputStream s = new ObjectInputStream(f);
		//playerStats = (HashMap<String, ArrayList<Integer>>) s.readObject();
		playerStats = (ArrayList<Map<String, ArrayList<Integer>>>) s.readObject();
		s.close();
		System.out.println("[STATS] Stats loaded");
	}
	@SuppressWarnings("unchecked")
	public static void loadRecords() throws FileNotFoundException, IOException, ClassNotFoundException {
		playerRecords.clear();
		File myfile = new File("records");
		FileInputStream f = new FileInputStream(myfile);
		ObjectInputStream s = new ObjectInputStream(f);
		//playerStats = (HashMap<String, ArrayList<Integer>>) s.readObject();
		playerRecords = (ArrayList<Map<String, ArrayList<Integer>>>) s.readObject();
		s.close();
		System.out.println("[STATS] Records loaded");
	}
	public static void createStatsProfile(String playerID) {
		for (int eachMode = 0; eachMode < 4; eachMode++) {
			ArrayList<Integer> blankStatsProfile = new ArrayList<Integer>(); 
			for (int i = 0; i < 15; i++) {
				blankStatsProfile.add(0);
			}
			playerStats.get(eachMode).put(playerID, blankStatsProfile);
		}
		System.out.println("[STATS] Generated new stats profile for "+playerID);
	}
	public static void addStat(String playerID,Integer mode,Integer type,Integer value) {
		if (!playerStats.get(mode).containsKey(playerID)) {
			createStatsProfile(playerID);
		}
		int oldValue = playerStats.get(mode).get(playerID).get(type);
		playerStats.get(mode).get(playerID).set(type, oldValue + value);
		System.out.println("[STATS] Added "+value+" of type "+type+" stat to "+playerID);
	}
	public static void modifyStat(String playerID,Integer mode,Integer type,Integer value) {
		playerStats.get(mode).get(playerID).set(type, value);
		System.out.println("[STATS] Set stat type "+type+" to "+value+" for "+playerID);	
	}
	public static void deleteStats(String playerID,Integer mode) {
		playerStats.get(mode).remove(playerID);
		System.out.println("[STATS] Removed stats for " + playerID);
	}
	public static void clearStats() {
		playerStats.clear();
		playerStats.add(new HashMap<String, ArrayList<Integer>>());
		playerStats.add(new HashMap<String, ArrayList<Integer>>());
		playerStats.add(new HashMap<String, ArrayList<Integer>>());
		playerStats.add(new HashMap<String, ArrayList<Integer>>());
		System.out.println("[STATS] All stats cleared");
	}
	public static void addRecord(String playerID,Integer mode,Integer type,Integer value) {
			// Will change for mode
		if (!playerRecords.get(mode).containsKey(playerID)) {
			for (int eachMode = 0; eachMode < 4; eachMode++) {
				ArrayList<Integer> blankStatsProfile = new ArrayList<Integer>(); 
				blankStatsProfile.add(0);
				blankStatsProfile.add(0);
				blankStatsProfile.add(999);
				blankStatsProfile.add(0);
				blankStatsProfile.add(0);
				blankStatsProfile.add(0);
				playerRecords.get(eachMode).put(playerID, blankStatsProfile);
			}
			System.out.println("[STATS] Generated new records profile for "+playerID);
		}
		int oldValue = playerRecords.get(mode).get(playerID).get(type);
		playerRecords.get(mode).get(playerID).set(type, oldValue + value);
		System.out.println("[STATS] Added "+value+" of type "+type+" record to "+playerID);
	}
	public static void modifyRecord(String playerID,Integer mode,Integer type,Integer value) {
		playerRecords.get(mode).get(playerID).set(type, value);
		System.out.println("[STATS] Set record type "+type+" to "+value+" for "+playerID);	
	}
	public static void deleteRecords(String playerID,Integer mode) {
		playerRecords.get(mode).remove(playerID);
		System.out.println("[STATS] Removed records for " + playerID);
	}
	public static void clearRecords() {
		playerRecords.clear();
		playerRecords.add(new HashMap<String, ArrayList<Integer>>());
		playerRecords.add(new HashMap<String, ArrayList<Integer>>());
		playerRecords.add(new HashMap<String, ArrayList<Integer>>());
		playerRecords.add(new HashMap<String, ArrayList<Integer>>());
		System.out.println("[STATS] All records cleared");
	}
	@SuppressWarnings("serial")
	public static ArrayList<Map<String, ArrayList<Integer>>> playerStats = new ArrayList<Map<String, ArrayList<Integer>>>() {{
		add(new HashMap<String, ArrayList<Integer>>());
		add(new HashMap<String, ArrayList<Integer>>());
		add(new HashMap<String, ArrayList<Integer>>());
		add(new HashMap<String, ArrayList<Integer>>());
	}};
	@SuppressWarnings("serial")
	public static ArrayList<Map<String, ArrayList<Integer>>> playerRecords = new ArrayList<Map<String, ArrayList<Integer>>>() {{
		add(new HashMap<String, ArrayList<Integer>>());
		add(new HashMap<String, ArrayList<Integer>>());
		add(new HashMap<String, ArrayList<Integer>>());
		add(new HashMap<String, ArrayList<Integer>>());
	}};
	@SuppressWarnings("serial")
	public static ArrayList<String> modes = new ArrayList<String>() {{
		add("c"); // Classic
		add("p"); // Perpetual
		add("e"); // Elimination
		add("s"); // Survival
	}};
	public static String[][] statsTypes = {
			{"POINTS","WINS","LOSSES","CORRECTS","MISSES","FIRSTS","LONES","HIGHESTS","ROUNDS","PENALTIES","BONUSES","TRIPLES","HANDICAPS","WINSTREAK","ROUNDSTREAK"},
			{"POINTS","WINS","LOSSES","CORRECTS","MISSES","FIRSTS","LONES","HIGHESTS","ROUNDS","PENALTIES","BONUSES","TRIPLES","HANDICAPS","WINSTREAK","ROUNDSTREAK"},
			{"POINTS","WINS","LOSSES","CORRECTS","MISSES","FIRSTS","LONES","HIGHESTS","ROUNDS","PENALTIES","BONUSES","TRIPLES","HANDICAPS","WINSTREAK","ROUNDSTREAK"},
			{}
	};
	public static String[][] recordsTypes = {
			{"WINSTREAK","ROUNDSTREAK","SHORTESTGAME","LONGESTGAME","POINTSWORD","POINTSGAME"},
			{"WINSTREAK","ROUNDSTREAK","SHORTESTGAME","LONGESTGAME","POINTSWORD","POINTSGAME"},
			{"WINSTREAK","ROUNDSTREAK","SHORTESTGAME","LONGESTGAME","POINTSWORD","POINTSGAME"},
			{}
	};	
	public static List<Entry<String, Integer>> sortData(ArrayList<Map<String, ArrayList<Integer>>> data, int mode, int index) {
		Map<String, Integer> typeStats = new HashMap<String, Integer>();
		for (String player : data.get(mode).keySet()) {
			typeStats.put(player,data.get(mode).get(player).get(index));
		}
		Set<Entry<String, Integer>> set = typeStats.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		} );
		return list;
	}
}
