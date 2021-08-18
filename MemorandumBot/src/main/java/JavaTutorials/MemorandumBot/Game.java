package JavaTutorials.MemorandumBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Game {
	public Game() {
		
	}
	public void addPoints() {
		
	}
	public void displayScores() {
		
	}
	public void setScoreboardMessageID(String messageID) {
		// Only for Elimination
	}
	public void startGame() {
		
	}
	public void newRound() {
		
	}
	public void dropOut(String id) {
		
	}
	public void rejoin(String id) {
		
	}
	public void calculateResult(String winner) {
		
	}
	public String verifyAnswer(String answer,String playerID,boolean isCheck) {
		return playerID;
		
	}
	public void generateRandomTarget() {
		Random rand = new Random();
		int targetType = rand.nextInt(5) + 1;
		do {
			if (targetType == 1) {
				target = GlobalVars.easyConsonants[rand.nextInt(13)] + GlobalVars.vowels[rand.nextInt(5)] + GlobalVars.easyConsonants2[rand.nextInt(12)];
			} else if (targetType < 4) {
				target = GlobalVars.vowels[rand.nextInt(5)] + GlobalVars.letters[rand.nextInt(26)];
			} else {
				target = GlobalVars.letters[rand.nextInt(26)] + GlobalVars.vowels[rand.nextInt(5)];
			}
		} while (Arrays.asList(GlobalVars.bannedTargets).contains(target));
	}
	public void generateRandomBonuses() {
		Random rand = new Random();
		bonus1 = GlobalVars.letters[rand.nextInt(26)];
		bonus2 = GlobalVars.letters[rand.nextInt(26)];
		bonus3 = GlobalVars.letters[rand.nextInt(26)];
		// Makes sure no duplicates
		while (target.contains(bonus1)) {
			bonus1 = GlobalVars.letters[rand.nextInt(26)];
		}
		while (target.contains(bonus2) || bonus2 == bonus1) {
			bonus2 = GlobalVars.letters[rand.nextInt(26)];
		}
		while (target.contains(bonus3) || bonus3 == bonus1 || bonus3 == bonus2) {
			bonus3 = GlobalVars.letters[rand.nextInt(26)];
		}
	}
	public void generateRandomTrap() {
		Random rand = new Random();
		do {
			trap = GlobalVars.easyConsonants[rand.nextInt(13)];
		} while (target.contains(trap) || bonus1.equals(trap) || bonus2.equals(trap) || bonus3.equals(trap));
	}
	public void endRound() {
		
	}
	public void endGame() {
		
	}
	public void addPlayer(String id) {
		
	}
	public void removePlayer(String id) {
		
	}
	public void readyPlayer(String id) {
		
	}
	public MessageChannel getGameChannel() {
		return gameChannel;
	}
	public String getStatus() {
		return status;
	}
	public String getMode() {
		return "none";
	}
	public ArrayList<String> getPlayers() {
		return players;
	}
	public void displayPlayers() {
		// Should be overrided anyways
	}
	public ArrayList<String> getDroppedOuts() {
		// Should be overrided anyways
		return players;
	}
	public void setLastAnswerer(String lastAnswerer) {
		// Should be overrided anyways
	}
	public ArrayList<String> players;
	public Guild guild;
	public String lastAnswerer;
	public MessageChannel gameChannel;
	public String status;
	public String target;
	public String bonus1;
	public String bonus2;
	public String bonus3;
	public String trap;
}
