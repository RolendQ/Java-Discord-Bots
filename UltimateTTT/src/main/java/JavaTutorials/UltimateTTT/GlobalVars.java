package JavaTutorials.UltimateTTT;

import java.util.ArrayList;

public class GlobalVars {
	public static ArrayList<Game> currentGames = new ArrayList<Game>();
	public static ArrayList<Challenge> pendingChallenges = new ArrayList<Challenge>();
	
	public static void add(Game game) {
		currentGames.add(game);
		System.out.println("[VARS] Added a game");
	}
	
	public static void add(Challenge challenge) {
		pendingChallenges.add(challenge);
		System.out.println("[VARS] Added a challenge");
	}
	
	public static void remove(Game game) {
		currentGames.remove(game);
		System.out.println("[VARS] Removed a game");
	}
	
	public static void remove(Challenge challenge) {
		pendingChallenges.remove(challenge);
		System.out.println("[VARS] Removed a challenge");
	}
}
