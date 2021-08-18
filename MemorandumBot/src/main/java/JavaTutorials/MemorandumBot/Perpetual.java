package JavaTutorials.MemorandumBot;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.apache.commons.collections4.MapUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Perpetual extends Game {
	public Perpetual(MessageChannel channel,Guild guild) {
		this.gameChannel = channel;
		this.guild = guild;
		this.status = "starting";
	}
	public void addPoints(String playerID,Integer earnedPoints) {
		if (playersDroppedOut.contains(playerID)) {
			System.out.println("Cannot add points to "+playerID+" since they dropped out");
			return;
		}
		// Change
		if (status != "stopped") {
			if (scores.containsKey(playerID)) {
				scores.put(playerID, scores.get(playerID) + earnedPoints);
			} else {
				// Resets round streak at the start of the game
				Stats.modifyStat(playerID, 1, 14, 0);
				
				players.add(playerID);
				scores.put(playerID, earnedPoints);
				roundsCounter.put(playerID, 1);
			}
			// Highest point in round
			if (earnedPoints > highestPointsThatRound) {
				highestPointsThatRound = earnedPoints;
				System.out.println("New highest points earned that round: "+earnedPoints);
				highestPointsThatRoundUser = playerID;
			}
			// Most points in word
			int currentMost = 0;
			if (Stats.playerRecords.get(1).containsKey(playerID)) {
				currentMost = Stats.playerRecords.get(1).get(playerID).get(4);
			}
			if (earnedPoints > currentMost) {
				Stats.addRecord(playerID, 1, 4, earnedPoints);
				System.out.println(Utils.getEffectiveName(playerID, guild)+" got a new most points in word of: "+earnedPoints);
			}
		
			pointsAwardedThatRound += earnedPoints;
			
			// Penalties don't lower total points
			if (earnedPoints > 0) {
				Stats.addStat(playerID, 1, 0, earnedPoints);
			}
			
			// Check for win condition
			int roundsPlayed = roundsCounter.get(playerID);
    		int requiredPoints = pointsToWinEquation(roundsPlayed);
    		int points = scores.get(playerID);
    		if (points >= requiredPoints) {
    			String winnerEffectName = Utils.getEffectiveName(playerID, guild);
    			gameChannel.sendMessage("══════ **[GAME OVER]** ══════ \n:crown: Winner: "+ "``"+winnerEffectName+"`` \n``"+winnerEffectName+"``  reached **" + points + "**/" + requiredPoints + "!").queueAfter(250,TimeUnit.MILLISECONDS);
				calculateResult(playerID);
    			endGame();
    		}
		}
	}
	
	public void addAnswer(String answer,String playerID) {
		answers.add(answer);
		System.out.println("Added answer: "+answer);
		playersAnswered.add(playerID);
		System.out.println("Added player: "+playerID);
	}
	public void countdown() {
		gameChannel.sendMessage("Starting in 5 seconds").queue();
		Timer timer = new Timer(5000, new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  startGame();
			  }
			});
		timer.setRepeats(false);
		timer.start();
	}
	public void startGame() {
		try {
			Stats.saveStats();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Stats.saveRecords();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Put reset stuff here?
		gameChannel.sendMessage("══════ **[NEW GAME]** ══════\nMode: **Perpetual** Channel: **"+gameChannel.getName()+"**").queue();
		newRound();
	}
	public void newRound() {
		gameChannel.sendMessage("══════ **[NEW ROUND]** ══════").queue();
		answers.clear();
		playersAnswered.clear();
		firstAnswerer = "";
		lastAnswerer = "";
		pointsAwardedThatRound = 0;
		for (String user : roundsCounter.keySet()) {
			roundsCounter.put(user, roundsCounter.get(user)+1);
			Stats.addStat(user, 1, 8, 1);
		}
		System.out.println("Added 1 to everyone's rounds");
		super.generateRandomTarget();
		super.generateRandomBonuses();
		Random rand = new Random();
		String bonuses = "**" + bonus1 + "** (" + Integer.toString(GlobalVars.pointValues.get(bonus1)) + ") **" + bonus2 + "** (" + GlobalVars.pointValues.get(bonus2) + ") **" + bonus3 + "** (" + GlobalVars.pointValues.get(bonus3) + ") "; 
		String message = "Target is: **"+target+"** Bonuses are: "+bonuses;
		trap = ".";
		if (rand.nextInt(4) == 0) {
			super.generateRandomTrap();
			message += "\n:bomb: __Trap__ is: **"+trap+"**";
		}
		gameChannel.sendMessage(message).queue();
		status = "takingAnswers";
		// Timer or queueafter?
		Timer timer1 = new Timer(12000, new ActionListener() {
			  public void actionPerformed(ActionEvent arg0) {
				if (status != "stopped") {
					gameChannel.sendMessage("*Round ending in 3 seconds*").queue();
					Timer timer2 = new Timer(3000, new ActionListener() {
	  				  	public void actionPerformed(ActionEvent arg0) {
	  						if (status != "stopped") {
	  							endRound();
	  						}
	  				  	}
	  					});
					timer2.setRepeats(false);
					timer2.start();
				}
			  }
			});
		timer1.setRepeats(false);
		timer1.start();
		
		
	}
	public void endRound() {
		gameChannel.sendMessage("══════ **[END OF ROUND]** ══════").queue();
		status = "inbetweenRounds";
		if (playersAnswered.size() == 0) {
			answerlessRounds += 1;
			if (answerlessRounds == 5) {
				gameChannel.sendMessage("══════ **[GAME OVER]** ══════").queue();
				calculateResult("No winner");
				endGame();
				gameChannel.sendMessage("All players have quit so ending game with no winner").queue();
			}
		} else {
			answerlessRounds = 0;
		}
		// Update rounds stuff including streaks
		checkForRoundsMissed();
		
		if (status.equals("stopped")) {
			return;
		}
		
		// Special Lone Answerer
		if (playersAnswered.size() == 1 && !playersDroppedOut.contains(playersAnswered.get(0))) {
			String loneAnswerer = playersAnswered.get(0);
			addPoints(loneAnswerer, 10);
			gameChannel.sendMessage("**»** ``" + Utils.getEffectiveName(loneAnswerer, guild) + "`` was the only person to answer correctly! **+10**").queue();
			Stats.addStat(loneAnswerer, 1, 6, 1);
		}
		if (status.equals("stopped")) {
			return;
		}
		
		// Highest points earner
		if (!highestPointsThatRoundUser.equals("")) {
			Stats.addStat(highestPointsThatRoundUser, 1, 7, 1);
		}
		
		// New Round
		if (pointsAwardedThatRound > 1) {
			gameChannel.sendMessage("**★** A total of **"+Integer.toString(pointsAwardedThatRound)+"** points was awarded last round!").queue();
		}
		// -10 penalty
		if (pointsAwardedThatRound == 0) {
			// Have to be careful that players can't drop out and escape -10
			if (!lastAnswerer.equals("") && !playersDroppedOut.contains(lastAnswerer)) {
				addPoints(lastAnswerer, -10);
				Stats.addStat(lastAnswerer, 1, 9, 1);
				gameChannel.sendMessage("**»** Nobody got it! ``"+Utils.getEffectiveName(lastAnswerer, guild)+"`` answered incorrectly last and got **-10** points!").queue();
			} else {
				gameChannel.sendMessage("**»** Nobody got it!").queue();
			}
		}
		Timer timer1 = new Timer(3000, new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  if (status != "stopped") {
					  displayScores();
					  Timer timer2 = new Timer(5000, new ActionListener() {
						  public void actionPerformed(ActionEvent e) {
							  if (status != "stopped") {
								  newRound();
							  }
						  }
					  });
					  timer2.setRepeats(false);
					  timer2.start();
				  }
			  }
			});
		timer1.setRepeats(false);
		timer1.start();
	}
	public void endGame() {
		status = "stopped";
		players.clear();
		playersDroppedOut.clear();
		playersDroppedOutPermanent.clear();
		scores.clear();
		roundsCounter.clear();
		answerlessRounds = 0;
		GlobalVars.removeGame(this);
		System.out.println("Everything reset");
	}
	public void displayScores() {
		gameChannel.sendMessage("══════ **[SCOREBOARD]** ══════").queue();
		if (scores.size() > 0) {
			// Sort
			List<Entry<String, Integer>> list = sortScores();
			// First Place
			firstPlace = list.get(0).getKey();
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.YELLOW);
			String names = "";
			String scores = "";
			String rounds = "";
			for (Map.Entry<String, Integer> entry : list) {
				int roundsPlayed = roundsCounter.get(entry.getKey());
				// Added 1 to roundsPlayed to show next round requirement
				int requiredPoints = pointsToWinEquation(roundsPlayed+1);
				String player;
				if (entry.getKey().equals(handicapped)) {
					player = "[H] ``"+Utils.getEffectiveName(entry.getKey(), guild)+"``";
				} else {
					player = "``"+Utils.getEffectiveName(entry.getKey(), guild)+"``";
				}
				names += player + "\n";
				scores += "**" + entry.getValue() + "**/" + requiredPoints + "\n";
				rounds += roundsPlayed + "\n";
			}
			embed.addField("PLAYERS",names,true);
			embed.addField("SCORES",scores,true);
			embed.addField("ROUNDS",rounds,true);
			gameChannel.sendMessage(embed.build()).queue();
		} else {
			gameChannel.sendMessage("Nobody has scored yet").queue();
		}
	}
	public void calculateResult(String winner) {
		if (!winner.equals("No winner")) {
			Stats.addStat(winner, 1, 1, 1);
			// Win streak
			Stats.addStat(winner, 1, 13, 1);
			if (Stats.playerStats.get(1).get(winner).get(13) > Stats.playerRecords.get(1).get(winner).get(0)) {
				Stats.modifyRecord(winner, 1, 0, Stats.playerStats.get(1).get(winner).get(13));
			}
		}
		for (String user : roundsCounter.keySet()) {
			// New shortest and longest game
			if (roundsCounter.get(user) < Stats.playerRecords.get(1).get(user).get(2)) {
				Stats.modifyRecord(user, 1, 2, roundsCounter.get(user));
			}
			if (roundsCounter.get(user) > Stats.playerRecords.get(1).get(user).get(3)) {
				Stats.modifyRecord(user, 1, 3, roundsCounter.get(user));
			}
			// New most points in game
			if (scores.get(user) > Stats.playerRecords.get(1).get(user).get(5)) {
				Stats.modifyRecord(user, 1, 5, scores.get(user));
			}
			if (!user.equals(winner)) {
				Stats.addStat(user, 1, 2, 1);
				// Reset win streak
				Stats.modifyStat(user, 1, 13, 0);
			}
		}
		// Save
		try {
			Stats.saveStats();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void checkForRoundsMissed() {
		ArrayList<String> dropOuts = new ArrayList<String>();
		Map<Integer, ArrayList<String>> roundStreaks = new HashMap<Integer, ArrayList<String>>();
		for (String playerID : roundsCounter.keySet()) {
			if (!playersAnswered.contains(playerID)) {
				Stats.addStat(playerID, 1, 4, 1);
				System.out.println(playerID+" missed the round");
				// Remove round streak
				Stats.modifyStat(playerID, 1, 14, 0);
				
				// Check for 5 missed rounds
				playersMissedRound.add(playerID);
				int count = 0;
				for (String player : playersMissedRound) {
					if (player.equals(playerID)) {
						count++;
						if (count == 5) {
							dropOuts.add(playerID);
							//dropOut(playerID);
							System.out.println("Automatically removed "+playerID);
							break;
						}
					}
				}
			} else {
				// Round streak
				Stats.addStat(playerID, 1, 14, 1);
				int roundStreak = Stats.playerStats.get(1).get(playerID).get(14);
				if (roundStreak >= 3) {
					if (roundStreaks.keySet().contains(roundStreak)) {
						roundStreaks.get(roundStreak).add(playerID);
					} else {
						ArrayList<String> newList = new ArrayList<String>();
						newList.add(playerID);
						roundStreaks.put(roundStreak, newList);
					}
				}
				while (playersMissedRound.contains(playerID)) {
					playersMissedRound.remove(playerID);
				}
				if (Stats.playerStats.get(1).get(playerID).get(14) > Stats.playerRecords.get(1).get(playerID).get(1)) {
					Stats.modifyRecord(playerID, 1, 1, Stats.playerStats.get(1).get(playerID).get(14));
				}
			}
		}
		for (String playerID : dropOuts) {
			dropOut(playerID);
		}

		// Sort roundStreaks 
		Map<Integer, ArrayList<String>> sortedRoundStreaks = new TreeMap <Integer,ArrayList<String>>(roundStreaks);
		boolean isFirst = true;
	    // Display elements (in reverse)
		ArrayList<Integer> keys = new ArrayList<Integer>(sortedRoundStreaks.keySet());
		for(int i = keys.size()-1; i >= 0; i--){
			// streak is at least 3
			int extraPoints = 1;
			if (keys.get(i) >= 10) {
				extraPoints = 3;
			} else if (keys.get(i) >= 5) {
				extraPoints = 2;
			}
			String names = "";
			int count = 0;
			for (String playerID : sortedRoundStreaks.get(keys.get(i))) {
				names += "``"+Utils.getEffectiveName(playerID, guild)+"``, ";
				addPoints(playerID,extraPoints);
				// Streaks
//				if (keys.get(i) == 3) {
//					Stats.addStat(playerID, 1, 15, 1);
//				}
//				if (keys.get(i) == 5) {
//					Stats.addStat(playerID, 1, 16, 1);
//				}
//				if (keys.get(i) == 10) {
//					Stats.addStat(playerID, 1, 17, 1);
//				}
				count++;
			}
			String emote = "";
			if (isFirst) {
				emote = ":fire: ";
				isFirst = false;
			}
			if (count > 1) {
				gameChannel.sendMessage(emote + names.substring(0,names.length()-2) +" are on a __"+keys.get(i)+"__ round streak! **+"+extraPoints+"**").queue();
			} else {
				gameChannel.sendMessage(emote + names.substring(0,names.length()-2) +" is on a __"+keys.get(i)+"__ round streak! **+"+extraPoints+"**").queue();
			}
		}
	}
	public void dropOut(String playerID) {
		scores.remove(playerID);
		players.remove(playerID);
		roundsCounter.remove(playerID);
		playersDroppedOut.add(playerID);
		// Prevents losing more than once from dropping out multiple times in single game
		if (!playersDroppedOutPermanent.contains(playerID)) {
			playersDroppedOutPermanent.add(playerID);
		}
		// Counts as loss and round miss
		Stats.addStat(playerID, 1, 2, 1);
		Stats.addStat(playerID, 1, 4, 1);
		gameChannel.sendMessage("*``"+Utils.getEffectiveName(playerID, guild)+"`` dropped out of the game.* Use ``?rejoin`` to earn points again").queue();
		System.out.println("Removed "+playerID+" since they dropped out");
	}
	public void rejoin(String playerID) {
		playersDroppedOut.remove(playerID);
		players.add(playerID);
		gameChannel.sendMessage("*``"+Utils.getEffectiveName(playerID, guild)+"`` rejoined the game*").queue();
		System.out.println(playerID+" rejoined");
	}
	public static int pointsToWinEquation(Integer rounds) {
		int points = 45 + 5*rounds;
		return points;
	}
	public String verifyAnswer(String answer,String playerID,boolean isCheck) {
		if (isCheck) {
			if (answer.contains(target) && (!playersAnswered.contains(playerID)) && !playersDroppedOut.contains(playerID)) {
				return "wrong";
			}
			return "ignore";
		}
		System.out.println("[PER] Trying to verify: "+answer);
		if ((answer.contains(target)) && (!playersAnswered.contains(playerID)) && !playersDroppedOut.contains(playerID) && (Utils.dictionarySearch(answer))) {
			System.out.println("Beginning of verified");
			if (!trap.equals(".") && answer.contains(trap)) {
				lastAnswerer = playerID;
				gameChannel.sendMessage("<:incorrect:404126060117229578> " + answer + " triggers the trap letter. No points for ``"+Utils.getEffectiveName(playerID, guild)+"``").queue();
				return "trap";
			}
			if (answers.contains(answer)) {
				return "duplicate";
			}
			// Reward points
    		// check first answer
    		int earnedPoints = 0;
    		boolean gotTriple = false;
    		if (answer.contains(bonus1)) {
    			earnedPoints += GlobalVars.pointValues.get(bonus1);
    			Stats.addStat(playerID, 1, 10, 1);
    		}
    		if (answer.contains(bonus2)) {
    			earnedPoints += GlobalVars.pointValues.get(bonus2);
    			Stats.addStat(playerID, 1, 10, 1);
    		}
    		if (answer.contains(bonus3)) {
    			earnedPoints += GlobalVars.pointValues.get(bonus3);
    			Stats.addStat(playerID, 1, 10, 1);
    			// All 3 bonuses statistic
    			if (answer.contains(bonus1) && answer.contains(bonus2)) {
    				Stats.addStat(playerID, 1, 11, 1);
    				gotTriple = true;
    			}
    		}
    		String note = " ";
    		Stats.addStat(playerID, 1, 3, 1);
    		
    		if (firstAnswerer == "") {
    			if (!playerID.equals(handicapped)) {
    				earnedPoints += answer.length();
    				earnedPoints += 2;
    				
    				// Extra points for three letter target
    				if (target.length() == 3) {
        				earnedPoints += 3;       					
    				}
    				
    				handicapped = playerID;
    				firstAnswerer = playerID;
    				note = " [1st] ";
    				Stats.addStat(playerID, 1, 5, 1);
    			} else {
    				// No +2 or +3 base?
    				earnedPoints += 3;
    				
    				// Extra points for three letter target
    				if (target.length() == 3) {
        				earnedPoints += 3;       					
    				}
    				
    				note = " [H] ";
    				Stats.addStat(playerID, 1, 12, 1);
    			}
    			if (gotTriple) {
    				// In case there is no first place
    				int firstPlacePoints = 0;
    				if (!firstPlace.equals("")) {
    					firstPlacePoints = scores.get(firstPlace);
    				}
    				if (firstPlacePoints < 0) {
    					firstPlacePoints = 0;
    				}
    				// In case playerID has not scored yet
    				int userPoints = 0;
    				if (scores.containsKey(playerID)) {
    					userPoints = scores.get(playerID);
    				}
					
					int requiredPoints = 50;
					if (roundsCounter.containsKey(playerID)) {
						requiredPoints = pointsToWinEquation(roundsCounter.get(playerID));
					}
					int bonus = (requiredPoints - userPoints) / 2;
					
					if (playerID.equals(firstPlace)) {
						bonus = 0;
					}
    				int total = earnedPoints + bonus;
    				addAnswer(answer, playerID);
    				addPoints(playerID, total);
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + note + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(total)+"** (**"+Integer.toString(earnedPoints)+"**+**"+Integer.toString(bonus)+"**) points for getting a *TRIPLE!*").queue();
    			} else {
    				addAnswer(answer, playerID);
    				addPoints(playerID, earnedPoints);
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + note + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints)+"** points!").queue();
    			}
    		} else {
    			earnedPoints += 3;
    			
				// Extra points for three letter target
				if (target.length() == 3) {
    				earnedPoints += 3;       					
				}
				
    			if (gotTriple) {
    				// In case there is no first place
    				int firstPlacePoints = 0;
    				if (!firstPlace.equals("")) {
    					firstPlacePoints = scores.get(firstPlace);
    				}
    				if (firstPlacePoints < 0) {
    					firstPlacePoints = 0;
    				}
    				// In case user has not scored yet
    				int userPoints = 0;
    				if (scores.containsKey(playerID)) {
    					userPoints = scores.get(playerID);
    				}
					int requiredPoints = 50;
					if (roundsCounter.containsKey(playerID)) {
						requiredPoints = pointsToWinEquation(roundsCounter.get(playerID));
					}
					int bonus = (requiredPoints - userPoints) / 2;
					
					if (playerID.equals(firstPlace)) {
						bonus = 0;
					}
    				int total = earnedPoints + bonus;
    				addAnswer(answer, playerID);
    				addPoints(playerID, total);
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(total)+"** (**"+Integer.toString(earnedPoints)+"**+**"+Integer.toString(bonus)+"**) points for getting a *TRIPLE!*").queue();
    			} else {
    				addAnswer(answer, playerID);
    				addPoints(playerID, earnedPoints);
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints)+"** points!").queue();
    			}
    			return "valid";
    		}
		}
		if (answer.contains(target) && !playersAnswered.contains(playerID) && !playersDroppedOut.contains(playerID)) {
			lastAnswerer = playerID;
			System.out.println("Wrong guess");
			return "wrong";
		}
		return "invalid";
	}
	public List<Entry<String, Integer>> sortScores() {
		Set<Entry<String, Integer>> set = scores.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		} );
		return list;
	}
	@Override
	public MessageChannel getGameChannel() {
		return gameChannel;
	}
	@Override
	public String getStatus() {
		return status;
	}
	public String getMode() {
		return "perpetual";
	}
	public ArrayList<String> getPlayers() {
		return players;
	}
	public ArrayList<String> getDroppedOuts() {
		return playersDroppedOut;
	}
	public void setLastAnswerer(String lastAnswerer) {
		this.lastAnswerer = lastAnswerer;
	}
	public Guild guild;
	public MessageChannel gameChannel;
	public String status = "";
	public ArrayList<String> players = new ArrayList<String>();
	public static Map<String, Integer> roundsCounter = new HashMap<String, Integer>();
	public ArrayList<String> answers = new ArrayList<String>();
	public Map<String, Integer> scores = new HashMap<String, Integer>();
	public String firstAnswerer = "";
	public String lastAnswerer = "";
	public String handicapped = "";
	public String firstPlace = "";
	public int highestPointsThatRound = 0;
	public int pointsAwardedThatRound = 0;
	public String highestPointsThatRoundUser = "";
	
	public static ArrayList<String> playersAnswered = new ArrayList<String>();
	// per game
	public static ArrayList<String> playersDroppedOut = new ArrayList<String>();
	public static ArrayList<String> playersDroppedOutPermanent = new ArrayList<String>();
	public static ArrayList<String> playersMissedRound = new ArrayList<String>();
	public static int answerlessRounds;
}
