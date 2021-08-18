package JavaTutorials.MemorandumBot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GlobalVars {
	public static ArrayList<Game> currentGames = new ArrayList<Game>();
	public static String[] letters = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	public static String[] vowels = new String[] {"A","E","I","O","U"};
	public static String[] easyConsonants = new String[] {"B","C","D","F","G","H","L","M","N","P","R","S","T"};
	public static String[] easyConsonants2 = new String[] {"B","C","D","F","G","L","M","N","P","R","S","T"};
	public static String[] bannedTargets = new String [] {"AA","UU","II","IW","UW","IH","WU","IY","OQ","UQ","IQ","QO","QA","QE","QI","OJ","UJ","UH","UV","IJ","FAF","GEB","CEG","FEB","CIC","GOM","PIF","GEG","GUC","GUF","TEF","CEG","FAF","MIB","FEB","GIP","FUG","FOB","DOB","FIP","BEB","CUM","BEP","DAC","GOP","MIM","BAF","MIP","BOF","DOF","FOF","GOF","HOF","LOF","MOF","MEF","NOF","POF","TOF","RUR","GUD","DUD","FOD","FUF","RIR","TIT","FAP","BIP","LIR"};
	public static Map<String, Integer> pointValues = createPointValues();
	public static String[] dictionary = createDictionary();
	
	public static void addGame(Game game) {
		currentGames.add(game);
		System.out.println("[VARS] Added a game");
	}
	public static void removeGame(Game game) {
		currentGames.remove(game);
		System.out.println("[VARS] Removed a game");
	}
	public static Map<String, Integer> createPointValues() {
		Map<String,Integer> pointValues = new HashMap<String,Integer>();
		pointValues.put("A",1);
		pointValues.put("B",3);
		pointValues.put("C",3);
		pointValues.put("D",2);
		pointValues.put("E",1);
		pointValues.put("F",4);
		pointValues.put("G",2);
		pointValues.put("H",4);
		pointValues.put("I",1);
		pointValues.put("J",8);
		pointValues.put("K",5);
		pointValues.put("L",1);
		pointValues.put("M",3);
		pointValues.put("N",1);
		pointValues.put("O",1);
		pointValues.put("P",3);
		pointValues.put("Q",10);
		pointValues.put("R",1);
		pointValues.put("S",1);
		pointValues.put("T",1);
		pointValues.put("U",1);
		pointValues.put("V",4);
		pointValues.put("W",4);
		pointValues.put("X",8);
		pointValues.put("Y",4);
		pointValues.put("Z",10);
		return pointValues;
	}
	
	public static String[] createDictionary() {
		String filePath = "allWords.txt";
		String tempDictionary = null;
		try {
			tempDictionary = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Created dictionary");
		return tempDictionary.split("\n");
	}
}
