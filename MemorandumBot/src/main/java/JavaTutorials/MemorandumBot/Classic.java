package JavaTutorials.MemorandumBot;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Classic extends Game {
	public Classic(MessageChannel channel,Guild guild) {
		this.gameChannel = channel;
		this.requiredPoints = 100;
		this.guild = guild;
		this.status = "starting";
	}
	public void addPlayer(String id) {
		players.add(id);
		playerStatuses.put(id,":warning:");
		gameChannel.sendMessage("`` "+Utils.getEffectiveName(id, guild)+" `` joined the game! (**"+players.size()+"**)").queue();
		System.out.println("Added player with id: "+id);
	}
	public void removePlayer(String id) {
		players.remove(id);
		playerStatuses.remove(id);
		gameChannel.sendMessage("`` "+Utils.getEffectiveName(id, guild)+" `` left pregame (**"+players.size()+"**)").queue();
		System.out.println("Removed player with id: "+id);		
	}
	public void readyPlayer(String id) {
		if (playerStatuses.get(id).equals(":warning:")) {
			playerStatuses.put(id, ":ballot_box_with_check:");
			System.out.println(id+" is now ready");
			// Check if all are ready
			for (String status : playerStatuses.values()) {
				if (status.equals(":warning:")) {
					return;
				}
			}
			gameChannel.sendMessage("All players are ready. Starting in 3 seconds").queue();
			Timer timer = new Timer(3000, new ActionListener() {
				  public void actionPerformed(ActionEvent e) {
					  startGame();
				  }
				});
			timer.setRepeats(false);
			timer.start();
		}
	}
	public void addPoints(String playerID,Integer earnedPoints) {
		// Change
		if (status != "stopped") {
			if (scores.containsKey(playerID)) {
				scores.put(playerID, scores.get(playerID) + earnedPoints);
			} else {
				// Resets round streak at the start of the game
				Stats.modifyStat(playerID, 0, 14, 0);
				
				scores.put(playerID, earnedPoints);
			}
			// Highest point in round
			if (earnedPoints > highestPointsThatRound) {
				highestPointsThatRound = earnedPoints;
				System.out.println("New highest points earned that round: "+earnedPoints);
				highestPointsThatRoundUser = playerID;
			}
			// Most points in word
			int currentMost = 0;
			if (Stats.playerRecords.get(0).containsKey(playerID)) {
				currentMost = Stats.playerRecords.get(0).get(playerID).get(4);
			}
			if (earnedPoints > currentMost) {
				Stats.addRecord(playerID, 0, 4, earnedPoints);
				System.out.println(Utils.getEffectiveName(playerID, guild)+" got a new most points in word of: "+earnedPoints);
			}
		
			pointsAwardedThatRound += earnedPoints;
			
			// Penalties don't lower total points
			if (earnedPoints > 0) {
				Stats.addStat(playerID, 0, 0, earnedPoints);
			}
			
			// Check for win condition
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
	public void pregame() {
		status = "pregame";
		gameChannel.sendMessage("Type ``;join``. Game will start when all joined players type ``;ready``").queue();
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
		gameChannel.sendMessage("══════ **[NEW GAME]** ══════\nMode: **Classic** Channel: **"+gameChannel.getName()+"**").queue();
		newRound();
	}
	public void newRound() {
		gameChannel.sendMessage("══════ **[NEW ROUND]** ══════").queue();
		playersAnswered.clear();
		firstAnswerer = "";
		lastAnswerer = "";
		pointsAwardedThatRound = 0;
		round += 1;
		for (String user : players) {
			Stats.addStat(user, 0, 8, 1);
		}
		System.out.println("Added 1 to total round");
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
		if (playersAnswered.size() == 1) {
			String loneAnswerer = playersAnswered.get(0);
			addPoints(loneAnswerer, 10);
			gameChannel.sendMessage("**»** ``" + Utils.getEffectiveName(loneAnswerer, guild) + "`` was the only person to answer correctly! **+10**").queue();
			Stats.addStat(loneAnswerer, 0, 6, 1);
		}
		if (status.equals("stopped")) {
			return;
		}
		
		// Highest points earner
		if (!highestPointsThatRoundUser.equals("")) {
			Stats.addStat(highestPointsThatRoundUser, 0, 7, 1);
		}
		
		// New Round
		if (pointsAwardedThatRound > 1) {
			gameChannel.sendMessage("**★** A total of **"+Integer.toString(pointsAwardedThatRound)+"** points was awarded last round!").queue();
		}
		
		// -10 penalty
		if (pointsAwardedThatRound == 0) {
			if (!lastAnswerer.equals("")) {
				addPoints(lastAnswerer, -10);
				Stats.addStat(lastAnswerer, 0, 9, 1);
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
		playerStatuses.clear();
		scores.clear();
		round = 0;
		answerlessRounds = 0;
		GlobalVars.removeGame(this);
		System.out.println("Everything reset");
	}
	public void displayPlayers() {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(Color.BLUE);
		String names = "";
		String statuses = "";
		for (String playerID : players) {
			names += "``"+Utils.getEffectiveName(playerID, guild)+"``" + "\n";
			statuses += playerStatuses.get(playerID) + "\n";
		}
		embed.setTitle("Player List");
		embed.addField("PLAYERS",names,true);
		embed.addField("STATUS",statuses,true);
		gameChannel.sendMessage(embed.build()).queue();
	}
	public void displayScores() {
		gameChannel.sendMessage("══════ **[SCOREBOARD]** ══════").queue();
		if (scores.size() > 0) {
			// Sort
			List<Entry<String, Integer>> list = sortScores();
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.YELLOW);
			String names = "";
			String scores = "";
			for (Map.Entry<String, Integer> entry : list) {
				String player;
				if (entry.getKey().equals(handicapped)) {
					player = "[H] ``"+Utils.getEffectiveName(entry.getKey(), guild)+"``";
				} else {
					player = "``"+Utils.getEffectiveName(entry.getKey(), guild)+"``";
				}
				names += player + "\n";
				scores += "**" + entry.getValue() + "**\n";
			}
			embed.setTitle("Round #"+round+" | First to "+requiredPoints+" Points");
			embed.addField("PLAYERS",names,true);
			embed.addField("SCORES",scores,true);
			gameChannel.sendMessage(embed.build()).queue();
		} else {
			gameChannel.sendMessage("Nobody has scored yet").queue();
		}
	}
	public void calculateResult(String winner) {
		if (!winner.equals("No winner")) {
			Stats.addStat(winner, 0, 1, 1);
			// Win streak
			Stats.addStat(winner, 0, 13, 1);
			if (Stats.playerStats.get(0).get(winner).get(13) > Stats.playerRecords.get(0).get(winner).get(0)) {
				Stats.modifyRecord(winner, 0, 0, Stats.playerStats.get(0).get(winner).get(13));
			}
		}
		for (String user : players) {
			// New shortest and longest game
			if (round < Stats.playerRecords.get(0).get(user).get(2)) {
				Stats.modifyRecord(user, 0, 2, round);
			}
			if (round > Stats.playerRecords.get(0).get(user).get(3)) {
				Stats.modifyRecord(user, 0, 3, round);
			}
			// New most points in game
			if (scores.get(user) > Stats.playerRecords.get(0).get(user).get(5)) {
				Stats.modifyRecord(user, 0, 5, scores.get(user));
			}
			if (!user.equals(winner)) {
				Stats.addStat(user, 0, 2, 1);
				// Reset win streak
				Stats.modifyStat(user, 0, 13, 0);
			}
		}
		// Save
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
	}
	public void checkForRoundsMissed() {
		for (String playerID : players) {
			if (!playersAnswered.contains(playerID)) {
				Stats.addStat(playerID, 0, 4, 1);
				System.out.println(playerID+" missed the round");
				// Remove round streak
				Stats.modifyStat(playerID, 0, 14, 0);
				
				// Check for 5 missed rounds
				playersMissedRound.add(playerID);
			} else {
				while (playersMissedRound.contains(playerID)) {
					playersMissedRound.remove(playerID);
				}
				if (Stats.playerStats.get(0).get(playerID).get(14) > Stats.playerRecords.get(0).get(playerID).get(1)) {
					Stats.modifyRecord(playerID, 0, 1, Stats.playerStats.get(0).get(playerID).get(14));
				}
			}
		}
	}
	public String verifyAnswer(String answer,String playerID,boolean isCheck) {
		if (isCheck) {
			if (answer.contains(target) && (!playersAnswered.contains(playerID)) && players.contains(playerID)) {
				return "wrong";
			}
			return "ignore";
		}
		System.out.println("[PER] Trying to verify: "+answer);
		if ((answer.contains(target)) && (!playersAnswered.contains(playerID)) && players.contains(playerID) && (Utils.dictionarySearch(answer))) {
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
    			Stats.addStat(playerID, 0, 10, 1);
    		}
    		if (answer.contains(bonus2)) {
    			earnedPoints += GlobalVars.pointValues.get(bonus2);
    			Stats.addStat(playerID, 0, 10, 1);
    		}
    		if (answer.contains(bonus3)) {
    			earnedPoints += GlobalVars.pointValues.get(bonus3);
    			Stats.addStat(playerID, 0, 10, 1);
    			// All 3 bonuses statistic
    			if (answer.contains(bonus1) && answer.contains(bonus2)) {
    				Stats.addStat(playerID, 0, 11, 1);
    				gotTriple = true;
    			}
    		}
    		String note = " ";
    		Stats.addStat(playerID, 0, 3, 1);
    		
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
    				Stats.addStat(playerID, 0, 5, 1);
    			} else {
    				// No +2 or +3 base?
    				earnedPoints += 3;
    				
    				// Extra points for three letter target
    				if (target.length() == 3) {
        				earnedPoints += 3;       					
    				}
    				
    				note = " [H] ";
    				Stats.addStat(playerID, 0, 12, 1);
    			}
				addAnswer(answer, playerID);
				addPoints(playerID, earnedPoints);
    			if (gotTriple) {
    				// No bonus for triple
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + note + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints)+"** points! *TRIPLE!*").queue();
    			} else {
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + note + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints)+"** points!").queue();
    			}
    		} else {
    			earnedPoints += 3;
    			
				// Extra points for three letter target
				if (target.length() == 3) {
    				earnedPoints += 3;       					
				}
				addAnswer(answer, playerID);
				addPoints(playerID, earnedPoints);
    			if (gotTriple) {
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints)+"** points! *TRIPLE!*").queue();
    			} else {
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints)+"** points!").queue();
    			}
    			return "valid";
    		}
		}
		if (answer.contains(target) && !playersAnswered.contains(playerID) && players.contains(playerID)) {
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
		return "classic";
	}
	public ArrayList<String> getPlayers() {
		return players;
	}
	public void setLastAnswerer(String lastAnswerer) {
		this.lastAnswerer = lastAnswerer;
	}
	public MessageChannel gameChannel;
	public ArrayList<String> players = new ArrayList<String>();
	public Map<String, String> playerStatuses = new HashMap<String, String>();
	public int round;
	public int requiredPoints;
	public Guild guild;
	public String status = "";
	public ArrayList<String> answers = new ArrayList<String>();
	public Map<String, Integer> scores = new HashMap<String, Integer>();
	public String firstAnswerer = "";
	public String lastAnswerer = "";
	public String handicapped = "";
	public int highestPointsThatRound = 0;
	public int pointsAwardedThatRound = 0;
	public String highestPointsThatRoundUser = "";
	
	public static ArrayList<String> playersAnswered = new ArrayList<String>();
	public static ArrayList<String> playersMissedRound = new ArrayList<String>();
	public static int answerlessRounds;
}
