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
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Elimination extends Game {
	public Elimination(MessageChannel channel,Guild guild) {
		this.gameChannel = channel;
		this.guild = guild;
		this.roundsPerPhase = 3;
		this.startingLives = 3;
		this.status = "starting";
	}
	public void addPlayer(String id) {
		players.add(id);
		playerStatuses.put(id,":warning:");
		gameChannel.sendMessage("`` "+Utils.getEffectiveName(id, guild)+" `` joined pregame! (**"+players.size()+"**)").queue();
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
				totalScores.put(playerID, totalScores.get(playerID) + earnedPoints);
			} 
			// Highest point in round
			if (earnedPoints > highestPointsThatRound) {
				highestPointsThatRound = earnedPoints;
				System.out.println("New highest points earned that round: "+earnedPoints);
				highestPointsThatRoundUser = playerID;
			}
			// Most points in word
			int currentMost = 0;
			if (Stats.playerRecords.get(2).containsKey(playerID)) {
				currentMost = Stats.playerRecords.get(2).get(playerID).get(4);
			}
			if (earnedPoints > currentMost) {
				Stats.addRecord(playerID, 2, 4, earnedPoints);
				System.out.println(Utils.getEffectiveName(playerID, guild)+" got a new most points in word of: "+earnedPoints);
			}
		
			pointsAwardedThatRound += earnedPoints;
			
			// Penalties don't lower total points
			if (earnedPoints > 0) {
				Stats.addStat(playerID, 2, 0, earnedPoints);
			}
			
			// Check for win condition
//    		int points = scores.get(playerID);
//    		if (points >= requiredPoints) {
//    			String winnerEffectName = Utils.getEffectiveName(playerID, guild);
//    			gameChannel.sendMessage("══════ **[GAME OVER]** ══════ \n:crown: Winner: "+ "``"+winnerEffectName+"`` \n``"+winnerEffectName+"``  reached **" + points + "**/" + requiredPoints + "!").queueAfter(250,TimeUnit.MILLISECONDS);
//				calculateResult(playerID);
//    			endGame();
//    		}
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
		gameChannel.sendMessage("══════ **[NEW GAME]** ══════\nMode: **Elimination** Channel: **"+gameChannel.getName()+"**").queue();
		for (String playerID : players) {
			scores.put(playerID, 0);
			totalScores.put(playerID, 0);
			lives.put(playerID, startingLives);
			// Reset round streak
			Stats.modifyStat(playerID, 2, 14, 0);
		}
		System.out.println("Started everyone with "+startingLives+" lives");
		newRound();
	}
	public void newRound() {
		round += 1;
		gameChannel.sendMessage("══════ **[NEW ROUND]** (**"+round+"**/"+roundsPerPhase+") ══════").queue();
		playersAnswered.clear();
		playersTrapped.clear();
		firstAnswerer = "";
		lastAnswerer = "";
		pointsAwardedThatRound = 0;
		for (String user : players) {
			Stats.addStat(user, 2, 8, 1);
		}
		System.out.println("Added 1 to total round");
		super.generateRandomTarget();
		super.generateRandomBonuses();
		Random rand = new Random();
		String bonuses = "**" + bonus1 + "** (" + Integer.toString(GlobalVars.pointValues.get(bonus1)) + ") **" + bonus2 + "** (" + GlobalVars.pointValues.get(bonus2) + ") **" + bonus3 + "** (" + GlobalVars.pointValues.get(bonus3) + ") "; 
		
		String message = "Target is: **"+target+"** Bonuses are: "+bonuses;
		trap = ".";
		if (rand.nextInt(3) == 0) {
			super.generateRandomTrap();
			message += "\n:bomb: __Trap__ is: **"+trap+"**";
		}
		gameChannel.sendMessage(message).queue();
		status = "takingAnswers";
		Timer timer1 = new Timer(9000, new ActionListener() {
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
	public void newPhase() {
		gameChannel.sendMessage("══════ **[NEW PHASE]** ══════").queue();
		phase += 1;
		round = 0;
		handicapped = "";
		firstTriple = true;
		
		int lowestScore = 9999;
		String lowestScorePlayer = "";
		boolean isTied = false;
		// ArrayList String playerIDs of tied
		for (String playerID : scores.keySet()) {
			if (scores.get(playerID) == lowestScore) {
				isTied = true;
			}
			if (scores.get(playerID) < lowestScore) {
				lowestScore = scores.get(playerID);
				lowestScorePlayer = playerID;
				isTied = false;
			}
			scores.put(playerID, 0);
		}
		if (!isTied) {
			lives.put(lowestScorePlayer, lives.get(lowestScorePlayer)-1);
			if (lives.get(lowestScorePlayer) <= 0) {
				players.remove(lowestScorePlayer);
				scores.remove(lowestScorePlayer);
				gameChannel.sendMessage(":no_entry_sign: ``"+Utils.getEffectiveName(lowestScorePlayer, guild)+"`` ran out of lives and was *eliminated!*").queue();
				Stats.addStat(lowestScorePlayer, 2, 2, 1);
				Stats.modifyStat(lowestScorePlayer, 2, 13, 0);
				// Last one remaining
				if (players.size() == 1) {
					String winnerEffectName = Utils.getEffectiveName(players.get(0), guild);
					gameChannel.sendMessage("``"+winnerEffectName+"`` is the only one remaining!\n══════ **[GAME OVER]** ══════ \n:crown: Winner: "+ "``"+winnerEffectName+"``!").queueAfter(250,TimeUnit.MILLISECONDS);
					calculateResult(players.get(0));
					endGame();
					return;
				} else {
					gameChannel.sendMessage("There are **"+players.size()+"** remaining").queue();
				}
			} else {
				gameChannel.sendMessage(":broken_heart: ``"+Utils.getEffectiveName(lowestScorePlayer, guild)+"`` lost a life. **"+lives.get(lowestScorePlayer)+"** left.").queue();
			}
			// Edit the scoreboard
			gameChannel.editMessageById(scoreboardMessageID, createScoresEmbed().build()).queue();
			System.out.println("Edited scoreboard");
		} else {
			gameChannel.sendMessage("There was a tie for the lowest scores. No lives were lost").queue();
		}
		Timer timer2 = new Timer(3000, new ActionListener() {
		  	public void actionPerformed(ActionEvent arg0) {
				if (status != "stopped") {
					newRound();
				}
		  	}
			});
		timer2.setRepeats(false);
		timer2.start();
	}
	public void endRound() {
		gameChannel.sendMessage("══════ **[END OF ROUND]** (**"+round+"**/"+roundsPerPhase+") ══════").queue();
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
			Stats.addStat(loneAnswerer, 2, 6, 1);
		}
		if (status.equals("stopped")) {
			return;
		}
		
		// Highest points earner
		if (!highestPointsThatRoundUser.equals("")) {
			Stats.addStat(highestPointsThatRoundUser, 2, 7, 1);
		}
		
		// New Round
		if (pointsAwardedThatRound > 1) {
			gameChannel.sendMessage("**★** A total of **"+Integer.toString(pointsAwardedThatRound)+"** points was awarded last round!").queue();
		}
		
		// -10 penalty
		if (pointsAwardedThatRound == 0) {
			if (!lastAnswerer.equals("")) {
				addPoints(lastAnswerer, -10);
				Stats.addStat(lastAnswerer, 2, 9, 1);
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
								  if (round % roundsPerPhase == 0) {
									  newPhase();
								  } else {
									  newRound();
								  }
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
		phase = 0;
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
		embed.setAuthor("Player List");
		embed.addField("PLAYERS",names,true);
		embed.addField("STATUS",statuses,true);
		gameChannel.sendMessage(embed.build()).queue();
	}
	public EmbedBuilder createScoresEmbed() {
		List<Entry<String, Integer>> list = sortScores();
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(Color.YELLOW);
		String names = "";
		String scores = "";
		String livesLeft = "";
		for (Map.Entry<String, Integer> entry : list) {
			String player;
			if (entry.getKey().equals(handicapped)) {
				player = "[H] ``"+Utils.getEffectiveName(entry.getKey(), guild)+"``";
			} else {
				player = "``"+Utils.getEffectiveName(entry.getKey(), guild)+"``";
			}
			names += player + "\n";
			scores += "**" + entry.getValue() + "**\n";
			//livesLeft += lives.get(entry.getKey()) + "\n";
			String hearts = "";
			for (int i = 1; i <= startingLives; i++) {
				if (i <= lives.get(entry.getKey())) {
					hearts += ":heart:";
				} else {
					hearts += ":black_heart:";
				}
			}
			livesLeft += hearts + "\n";
		}
		embed.setTitle("Round #"+round+" of Phase "+phase);
		embed.addField("PLAYERS",names,true);
		embed.addField("SCORES",scores,true);
		embed.addField("LIVES",livesLeft,true);
		return embed;
	}
	public void displayScores() {
		gameChannel.sendMessage("══════ **[SCOREBOARD]** ══════").queue();
		if (scores.size() > 0) {
			gameChannel.sendMessage(createScoresEmbed().build()).queue();
		} else {
			gameChannel.sendMessage("Nobody has scored yet").queue();
		}
	}
	public void calculateResult(String winner) {
		if (!winner.equals("No winner")) {
			Stats.addStat(winner, 2, 1, 1);
			// Win streak
			Stats.addStat(winner, 2, 13, 1);
			if (Stats.playerStats.get(2).get(winner).get(13) > Stats.playerRecords.get(2).get(winner).get(0)) {
				Stats.modifyRecord(winner, 2, 0, Stats.playerStats.get(2).get(winner).get(13));
			}
		}
		// Should be one player but need to update other players in case game quit early
		for (String user : players) {
			// New shortest and longest game
			if (round < Stats.playerRecords.get(2).get(user).get(2)) {
				Stats.modifyRecord(user, 2, 2, round);
			}
			if (round > Stats.playerRecords.get(2).get(user).get(3)) {
				Stats.modifyRecord(user, 2, 3, round);
			}
			// New most points in game
			if (totalScores.get(user) > Stats.playerRecords.get(2).get(user).get(5)) {
				Stats.modifyRecord(user, 2, 5, totalScores.get(user));
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
				Stats.addStat(playerID, 2, 4, 1);
				System.out.println(playerID+" missed the round");
				// Remove round streak
				Stats.modifyStat(playerID, 2, 14, 0);
				
				// Check for 5 missed rounds
				playersMissedRound.add(playerID);
			} else {
				while (playersMissedRound.contains(playerID)) {
					playersMissedRound.remove(playerID);
				}
				if (Stats.playerStats.get(2).get(playerID).get(14) > Stats.playerRecords.get(2).get(playerID).get(1)) {
					Stats.modifyRecord(playerID, 2, 1, Stats.playerStats.get(2).get(playerID).get(14));
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
		System.out.println("[ELI] Trying to verify: "+answer);
		if ((answer.contains(target)) && (!playersTrapped.contains(playerID)) &&(!playersAnswered.contains(playerID)) && players.contains(playerID) && (Utils.dictionarySearch(answer))) {
			System.out.println("Beginning of verified");
			if (!trap.equals(".") && answer.contains(trap)) {
				lastAnswerer = playerID;
				// Trap skips your answers for that round
				playersTrapped.add(playerID);
				gameChannel.sendMessage("<:incorrect:404126060117229578> " + answer + " triggers the trap letter. ``"+Utils.getEffectiveName(playerID, guild)+"`` can no longer answer this round").queue();
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
    			Stats.addStat(playerID, 2, 10, 1);
    		}
    		if (answer.contains(bonus2)) {
    			earnedPoints += GlobalVars.pointValues.get(bonus2);
    			Stats.addStat(playerID, 2, 10, 1);
    		}
    		if (answer.contains(bonus3)) {
    			earnedPoints += GlobalVars.pointValues.get(bonus3);
    			Stats.addStat(playerID, 2, 10, 1);
    			// All 3 bonuses statistic
    			if (answer.contains(bonus1) && answer.contains(bonus2)) {
    				Stats.addStat(playerID, 2, 11, 1);
    				gotTriple = true;
    			}
    		}
    		String note = " ";
    		Stats.addStat(playerID, 2, 3, 1);
    		
    		if (firstAnswerer == "") {
    			if (!playerID.equals(handicapped)) {
    				// Half the length rounded down
    				earnedPoints += answer.length()/2;
    				earnedPoints += 2;
    				
    				// Extra points for three letter target
    				if (target.length() == 3) {
        				earnedPoints += 3;       					
    				}
    				
    				handicapped = playerID;
    				firstAnswerer = playerID;
    				note = " [1st] ";
    				Stats.addStat(playerID, 2, 5, 1);
    			} else {
    				// No +2 or +3 base?
    				earnedPoints += 3;
    				
    				// Extra points for three letter target
    				if (target.length() == 3) {
        				earnedPoints += 3;       					
    				}
    				
    				note = " [H] ";
    				Stats.addStat(playerID, 2, 12, 1);
    			}
				addAnswer(answer, playerID);
    			if (gotTriple) {
    				if (firstTriple) {
    					gameChannel.sendMessage(":white_check_mark: " + answer + "! " + note + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints+15)+"** (**"+earnedPoints+"**+**15**) points for getting the *FIRST TRIPLE!*").queue();
    					earnedPoints += 15;
    					firstTriple = false;
    				} else {
    					gameChannel.sendMessage(":white_check_mark: " + answer + "! " + note + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints+3)+"** (**"+earnedPoints+"**+**3**) points for getting a *TRIPLE!*").queue();
    					earnedPoints += 3;
    				}
    			} else {
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + note + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints)+"** points!").queue();
    			}
    			addPoints(playerID, earnedPoints);
    		} else {
    			earnedPoints += 3;
    			
				// Extra points for three letter target
				if (target.length() == 3) {
    				earnedPoints += 3;       					
				}
				addAnswer(answer, playerID);
    			if (gotTriple) {
    				if (firstTriple) {
    					gameChannel.sendMessage(":white_check_mark: " + answer + "! " + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints+15)+"** (**"+earnedPoints+"**+**15**) points for getting the *FIRST TRIPLE!*").queue();
    					earnedPoints += 15;
    					firstTriple = false;
    				} else {
    					gameChannel.sendMessage(":white_check_mark: " + answer + "! " + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints+3)+"** (**"+earnedPoints+"**+**3**) points for getting a *TRIPLE!*").queue();
    					earnedPoints += 3;
    				}
    			} else {
    				gameChannel.sendMessage(":white_check_mark: " + answer + "! " + "``"+Utils.getEffectiveName(playerID, guild)+"`` earned **"+Integer.toString(earnedPoints)+"** points!").queue();
    			}
    			addPoints(playerID, earnedPoints);
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
		return "elimination";
	}
	public ArrayList<String> getPlayers() {
		return players;
	}
	public void setLastAnswerer(String lastAnswerer) {
		this.lastAnswerer = lastAnswerer;
	}
	public void setScoreboardMessageID(String messageID) {
		scoreboardMessageID = messageID;
	}
	
	public MessageChannel gameChannel;
	public int roundsPerPhase;
	public int startingLives;
	public String scoreboardMessageID;
	public ArrayList<String> players = new ArrayList<String>();
	public Map<String, String> playerStatuses = new HashMap<String, String>();
	public int round = 0;
	public int phase = 1;
	public boolean firstTriple = true;
	public Guild guild;
	public String status = "";
	public ArrayList<String> answers = new ArrayList<String>();
	public Map<String, Integer> scores = new HashMap<String, Integer>();
	public Map<String, Integer> lives = new HashMap<String, Integer>();
	public Map<String, Integer> totalScores = new HashMap<String, Integer>();
	public String firstAnswerer = "";
	public String lastAnswerer = "";
	public String handicapped = "";
	public int highestPointsThatRound = 0;
	public int pointsAwardedThatRound = 0;
	public String highestPointsThatRoundUser = "";
	
	public static ArrayList<String> playersAnswered = new ArrayList<String>();
	public static ArrayList<String> playersTrapped = new ArrayList<String>();
	public static ArrayList<String> playersMissedRound = new ArrayList<String>();
	public static int answerlessRounds;
}
