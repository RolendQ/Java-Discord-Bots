package BoardGames.ClankBot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;


public class Game {
	
	// [COMM] Initiates Game Object
	public Game(MessageChannel channel, Guild guild,String hostID) {
		this.guild = guild;
		this.gameChannel = channel;
		this.hostID = hostID;
		
		// Add Player One
		playerCount++;
		p1 = new Player(0,hostID,GlobalVars.pieces.get("red"));
		p1.getPiece().setX(GlobalVars.playerCoordsPerRoom[0][0]+GlobalVars.playersOffset[0][0]);
		p1.getPiece().setY(GlobalVars.playerCoordsPerRoom[0][1]+GlobalVars.playersOffset[0][1]);
		players.add(p1);
		p1.setNextPlayer(p1);
		gameChannel.sendMessage("**"+guild.getMemberById(hostID).getEffectiveName()+"** has reincarnated as the RED adventurer").queue();
		
		// 1:Healing 2:Gold 3:Skill 4:Egg 5:Strength 6:Swiftness 7:Magic Spring
		minorSecrets.add(1); minorSecrets.add(1); minorSecrets.add(1);
		minorSecrets.add(2); minorSecrets.add(2); minorSecrets.add(2);
		minorSecrets.add(3); minorSecrets.add(3); minorSecrets.add(3);
		minorSecrets.add(4); minorSecrets.add(4); minorSecrets.add(4);
		minorSecrets.add(5); minorSecrets.add(5);
		minorSecrets.add(6); minorSecrets.add(6);
		minorSecrets.add(7); minorSecrets.add(7);
		
		Collections.shuffle(minorSecrets);
		// 1:Healing 2:Gold 3:Skill 4:Chalice 5:Draw
		majorSecrets.add(1); majorSecrets.add(1);
		majorSecrets.add(2); majorSecrets.add(2);
		majorSecrets.add(3); majorSecrets.add(3);
		majorSecrets.add(4); majorSecrets.add(4); minorSecrets.add(4);
		majorSecrets.add(5); majorSecrets.add(5);
		Collections.shuffle(majorSecrets);
		
	}

	// [COMM] Add a player to game
	public void addPlayer(String playerID) {
		playerCount++;
		String name = guild.getMemberById(playerID).getEffectiveName();
		// Set up each player uniquely
		if (p2 == null) {
			p2 = new Player(1,playerID,GlobalVars.pieces.get("blue"));
			p2.getPiece().setX(GlobalVars.playerCoordsPerRoom[0][0]+GlobalVars.playersOffset[1][0]);
			p2.getPiece().setY(GlobalVars.playerCoordsPerRoom[0][1]+GlobalVars.playersOffset[1][1]);
			players.add(p2);
			p1.setNextPlayer(p2);
			p2.setNextPlayer(p1);
			gameChannel.sendMessage("**"+name+"** has reincarnated as the BLUE adventurer").queue();
		} else if (p3 == null) {
			p3 = new Player(2,playerID,GlobalVars.pieces.get("yellow"));
			p3.getPiece().setX(GlobalVars.playerCoordsPerRoom[0][0]+GlobalVars.playersOffset[2][0]);
			p3.getPiece().setY(GlobalVars.playerCoordsPerRoom[0][1]+GlobalVars.playersOffset[2][1]);
			players.add(p3);
			p2.setNextPlayer(p3);
			p3.setNextPlayer(p1);
			gameChannel.sendMessage("**"+name+"** has reincarnated as the YELLOW adventurer").queue();
		} else if (p4 == null) {
			p4 = new Player(3,playerID,GlobalVars.pieces.get("green"));
			p4.getPiece().setX(GlobalVars.playerCoordsPerRoom[0][0]+GlobalVars.playersOffset[3][0]);
			p4.getPiece().setY(GlobalVars.playerCoordsPerRoom[0][1]+GlobalVars.playersOffset[3][1]);
			players.add(p4);
			p3.setNextPlayer(p4);
			p4.setNextPlayer(p1);
			gameChannel.sendMessage("**"+name+"** has reincarnated as the GREEN adventurer").queue();
		}
	}
	
	// [COMM] Quit the game (any turn)
	public void quitGame(String playerID) {
		// Cannot quit a game by yourself
		if (playerCount > 1) {
			for (int i = 0; i < players.size(); i++) {
				Player p = players.get(i);
				if (p.getPlayerID().contentEquals(playerID) && !p.hasQuit()) {
					p.setHasQuit(true);
					gameChannel.sendMessage("**"+Utils.getName(playerID, guild)+"** has quit the game").queue();
					if (!checkIfGameIsOver()) {
						// If current player, end their turn
						if (currentPlayer.getPlayerID().contentEquals(playerID) && !turnIsEnding) {
							currentPlayer.discardAllCardsNotPlayed();
							endTurn();
						}
					}
				}
			}
		}
	}
	
	// [COMM] Transfer your adventurer to another player (any turn)
	public void transfer(String oldPlayerID, String newPlayerID) {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p.getPlayerID().contentEquals(oldPlayerID) && !p.hasQuit()) {
				String color = "";
				if (i == 0) color = "RED";
				else if (i == 1) color = "BLUE";
				else if (i == 2) color = "YELLOW";
				else if (i == 3) color = "GREEN";
				p.setPlayerID(newPlayerID);
				gameChannel.sendMessage("**"+Utils.getName(oldPlayerID, guild)+"** transferred the "+color+" adventurer to "+Utils.getName(newPlayerID, guild)).queue();
			}
		}
	}
	
	// [COMM] Vote for end of game
	public void voteEnd(String playerID) {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p.getPlayerID().contentEquals(playerID) && !p.votedForEnd()) {
				p.setVotedForEnd(true);
				votedForEnd++;
				gameChannel.sendMessage("**"+Utils.getName(playerID, guild)+"** voted to end the game prematurely").queue();
				// Needs one less than playerCount
				if (votedForEnd >= (playerCount-1)) gameOver();
			}
		}
	}
	
	// [COMM] Unvote for end of game
	public void unvoteEnd(String playerID) {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p.getPlayerID().contentEquals(playerID) && p.votedForEnd()) {
				p.setVotedForEnd(false);
				votedForEnd--;
				gameChannel.sendMessage("**"+Utils.getName(playerID, guild)+"** retracted their vote to end the game prematurely").queue();
			}
		}
	}
	
	// Start the game
	public void start() {
		currentPlayer = p1;
		currentName = Utils.getName(currentPlayer.getPlayerID(),guild);
		status = "ingame";
		for (int i = 0; i < 6; i++) {
			dungeonRow[i] = mainDeck.getNext();
			mainDeck.removeTop();
			// Don't arrive for start
			//if (dungeonRow[i].isHasArrive()) doArriveEffect(dungeonRow[i]);
		}
		
		// Setup attackLevel
		attackLevel = 2;
		if (playerCount >= 3) {
			attackLevel--;
			if (playerCount >= 4) {
				attackLevel--;
			}
		}
		
		// Spawn starting clank
		int clank = 3;
		for (int i = 0; i < players.size(); i++) {
			players.get(i).updateClankOnBoard(clank);
			clank--;
		}
		
		// Remove artifacts based on numbers of players
		if (playerCount < 4) {
			removeArtifact();
			if (playerCount < 3) {
				removeArtifact();
				// Not actually in board game
				if (playerCount < 2) {
					removeArtifact();
				}
			}
		}
		
		currentName = Utils.getName(currentPlayer.getPlayerID(),guild);
		
		isFirstBoard = true;
		try {
			updateBoard();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Special order of embeds (may change with lag?)
		updateMarketAndTreasures(true);
		updateEvents(true);
		updateDungeonRow(currentPlayer,true);
		updateInfo(currentPlayer, true);
		currentPlayer.draw(5);
		updatePlayArea(currentPlayer, true);
	}
	
	//----------------------------------------------------------------------------------------------
	// In Game Methods
	
	// Starts a new turn {simple, set currentName, update displays and draw 5}
	private void newTurn() {
		System.out.println("[DEBUG LOG/Game.java] Starting new turn of player "+currentPlayer.getNumber());
		currentName = Utils.getName(currentPlayer.getPlayerID(),guild);
		setLinkedCommand(null);
		
		// CHEATS Testing
		currentPlayer.setBoots(99);
		currentPlayer.setSkill(99);
		currentPlayer.setSwords(99);
		currentPlayer.setGold(99);
	
		updateReactionsMarketAndTreasures(); // New current player with different gold
		updateInfo(currentPlayer, false);
		updateDungeonRow(currentPlayer, false);
		currentPlayer.draw(5);
		updatePlayArea(currentPlayer, false);
	}
	
	// [COMM] Player ends their turn
	public void endTurn() {
		System.out.println("[DEBUG LOG/Game.java] Started endTurn() for player "+currentPlayer.getNumber());
		turnIsEnding = true;

		// End turn restrictions
		if (!currentPlayer.isDead() && !currentPlayer.isFree() && !currentPlayer.hasQuit()) {
			if (currentPlayer.getPlayArea().getNonPlayedSize() > 0) {
				gameChannel.sendMessage("**[ERROR]** Cannot end turn until all cards in play area are played").queue();
				return;
			}
			if (mustChoose.size() > 0) {
				gameChannel.sendMessage("**[ERROR]** Cannot end turn until all choices are made").queue();
				return;
			}
			if (mustTrash.size() > 0) {
				gameChannel.sendMessage("**[ERROR]** Cannot end turn until all trashes are made").queue();
				return;
			}
			// Don't need to discard
		}
		
		// Mass deletes
		clearMessages();
		
		// If just escaped, send message after clearing
		if (currentPlayer.getCurrentRoom() == 0 && currentPlayer.getPiece().getX() == GlobalVars.playerCoordsPerRoom[39][0]) {
			// Update room
			currentPlayer.setCurrentRoom(39);
			if (firstEscapee == currentPlayer) {
				gameChannel.sendMessage(":helicopter: **"+currentName+"** was the first to escape! They received a **20** :star: **Mastery Token**\n:skull: Only **4** turn(s) left!").queue();
			} else {
				gameChannel.sendMessage(":helicopter: **"+currentName+"** escaped in time! They received a **20** :star: **Mastery Token**");
			}
		}
		
		// Set cards in PlayArea to not played for next time, Undos the Discarded tag
		for (int i = 0; i < currentPlayer.getPlayArea().getSize(); i++) {
			currentPlayer.getPlayArea().getCard(i).setPlayed(false);
			if (currentPlayer.getPlayArea().getCard(i).toStringHand().contentEquals("*[Discarded]*")) currentPlayer.getPlayArea().getCard(i).setStringInHand();
		}
		
		// Clear swords, skill, boots, etc
		currentPlayer.endOfTurnReset();
		mustDiscard.clear();
		
		// Replaces cards in dungeon row. Only one attack per turn
		if (!status.contentEquals("over")) {
			boolean hasAttackedThisTurn = false;
			for (int i = 0; i < 6; i++) {
				// Checks to see if it should replace card
				if (dungeonRow[i].isBought()) {
					dungeonRow[i] = mainDeck.getNext();
					if (dungeonRow[i].isDragonAttack() && !hasAttackedThisTurn) {
						dragonAttack(cubesPerLevel[attackLevel]);
						hasAttackedThisTurn = true;
					}
					if (dungeonRow[i].isHasArrive()) {
						doArriveEffect(dungeonRow[i]);
					}
					mainDeck.removeTop();
				}
			}
			// Instantly go on to next player if no dragon attack
			if (!hasAttackedThisTurn) {
				determineNextPlayer();
			}
		}

	}
	
	// Gets next player, runs after endTurn or dragon attack finishes
	public void determineNextPlayer() {
		// Calculate next player
		System.out.println("[DEBUG LOG/Game.java] Current player number before getting next: "+currentPlayer.getNumber());
//		if (currentPlayer.getNumber() == 0) {
//			if (p2 == null) {currentPlayer = p1;} else {currentPlayer = p2;}
//		} else if (currentPlayer.getNumber() == 1) {
//			if (p3 == null) {currentPlayer = p1;} else {currentPlayer = p3;}
//		} else if (currentPlayer.getNumber() == 2) {
//			if (p4 == null) {currentPlayer = p1;} else {currentPlayer = p4;}
//		} else {
//			currentPlayer = p1;
//		}
		currentPlayer = currentPlayer.getNextPlayer(); // Method already deals with quit
		
		// Player is alive and not free
		if (!currentPlayer.isFree() && !currentPlayer.isDead()) { 
			System.out.println("[DEBUG LOG/Game.java] Next player is not dead and not free: "+currentPlayer.getNumber());
			turnIsEnding = false;
			newTurn();
		// Player is dead
		} else if (currentPlayer.isDead()) {
			if (!checkIfGameIsOver()) {
				System.out.println("[DEBUG LOG/Game.java] Player is dead, game is not over so skipping");
				endTurn(); // DEATH CONDITION 1.5 & 2 & 2.5
			}
		// Player is free
		} else {
			// Check if game is over
			if (!checkIfGameIsOver()) {
				// End Game Dragon
				System.out.println("[DEBUG LOG/Game.java] Player is free, game is not over");
				// Check if this is the first escaped player
				if (currentPlayer.getPlayerID().contentEquals(firstEscapee.getPlayerID())) {
					// Advance the room and draw next sprite
					currentPlayer.setCurrentRoom(currentPlayer.getCurrentRoom()+1);
					int room = currentPlayer.getCurrentRoom();
					currentPlayer.getPiece().setX(GlobalVars.playerCoordsPerRoom[room][0]+GlobalVars.playersOffset[currentPlayer.getNumber()][0]);
					currentPlayer.getPiece().setY(GlobalVars.playerCoordsPerRoom[room][1]+GlobalVars.playersOffset[currentPlayer.getNumber()][1]);
					// If last room, kill the rest of the players
					if (room == 43) {
						System.out.println("[DEBUG LOG/Game.java] First escapee has reached last room. Killing all other players");
						for (int i = 0; i < players.size(); i++) {
							if (!players.get(i).isDead() && !players.get(i).isFree()) {
								death(players.get(i)); // DEATH CONDITION 3.5
							}
						}
						// Not run through death because turnIsEnding = true
						if (!status.contentEquals("over")) {
							gameOver();
						}
					} else {
						// Not last stage so keep going
						System.out.println("[DEBUG LOG/Game.java] Advanced room in end sequence");
						gameChannel.sendMessage(":skull: Only **"+(43-room)+"** turn(s) left!").queueAfter(2000,TimeUnit.MILLISECONDS);
						// Increased cube dragon attack
						dragonAttack(cubesPerLevel[attackLevel]+room-39);
						// Even if the last player died, should pass and then end game
						endTurn();
					}
				} else {
					// Not first escapee but also free
					endTurn();
				}
			}
		}
	}

	// Death of a player
	private void death(Player p) {
		System.out.println("[DEBUG LOG/Game.java] Player "+p.getNumber()+" died");
		p.setDead(true);
		p.discardAllCardsNotPlayed();
		
		// Calculates death message
		String deathMessage = "";
		if (p.has("Artifact")) {
			if (!p.isUnderground()) {
				deathMessage = "Fortunately, they had an artifact above ground so the villagers were able to recover the treasure";
			} else {
				deathMessage = "They perished underground so all treasure was lost";
			}
		} else {
			deathMessage = "They perished without an artifact so all treasure was lost";
		}
		gameChannel.sendMessage(":skull: **"+getName(p)+"** died in the dungeon.\n"+deathMessage).queueAfter(dragonAttackingDelay+2000, TimeUnit.MILLISECONDS);
		
		// Commented out because should be handled by App's detection
		// If this player was currentPlayer, endTurn if not already
//		if (!checkIfGameIsOver() && currentPlayer.isDead() && !turnIsEnding) {
//			endTurn();
//		}
	}
	
	// Runs when game is over
	public void gameOver() {
		System.out.println("[DEBUG LOG/Game.java] Game is over. Calculating points");
		status = "over";
		try {
			updateBoard();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateEvents(false);
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Game Results");
		Map<Integer, Integer> scores = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < players.size(); i++) {
			scores.put(i,players.get(i).calculatePoints());
			// Utils.getName(players.get(i).getPlayerID(),guild)
		}
		
		List<Entry<Integer, Integer>> sortedList = sortScores(scores);
		
		// If tied, add highest artifact values
		if ((playerCount > 1) && (sortedList.get(0).getValue().equals(sortedList.get(1).getValue()))) {
			System.out.println("[DEBUG LOG/Game.java] Scores were tied");
			int highestScore = sortedList.get(0).getValue();
			for (Map.Entry<Integer, Integer> entry : sortedList) {
				// Only add artifacts to highest scores
				if (entry.getValue().equals(highestScore)) {
					scores.put(entry.getKey(),entry.getValue()+players.get(entry.getKey()).getHighestArtifactValue());
					players.get(entry.getKey()).setAddedArtifactValue(true);
				}
			}
		}
		
		// Sort with added artifact values
		sortedList = sortScores(scores);
		
		// Assigns 1st, 2nd, 3rd
		for (int i = 0; i < sortedList.size(); i++) {
			Map.Entry<Integer, Integer> entry = sortedList.get(i);
			// If tie, include artifact
			String name = Utils.getName(players.get(entry.getKey()).getPlayerID(),guild);
			if (players.get(entry.getKey()).getAddedArtifactValue()) {
				int artifactValue = players.get(entry.getKey()).getHighestArtifactValue();
				embed.addField(Utils.endGamePlaces[i],"**"+name+"** - ``"+(entry.getValue()-artifactValue)+" ("+artifactValue+")``",true);
			} else {
				embed.addField(Utils.endGamePlaces[i],"**"+name+"** - ``"+entry.getValue()+"``",true);
			}
		}
		embed.setFooter("Credit to Renegade Game Studios", null);
		gameChannel.sendMessage(embed.build()).queueAfter(dragonAttackingDelay+3000,TimeUnit.MILLISECONDS);
		
		GlobalVars.remove(this);
	}
	
	// [COMM] Play a card (runs first)
	public void playCard(int cardNumber, boolean isLast) {
		Card c = currentPlayer.getPlayArea().getCard(cardNumber);
		int prevGold = currentPlayer.getGold();
		int prevHealth = currentPlayer.getHealth();
		doCardEffects(c);
		currentPlayer.getPlayArea().getCard(cardNumber).setPlayed(true);
		currentPlayer.getDiscardPile().add(c);
		System.out.println("[DEBUG LOG/Game.java] "+currentName+" played "+c.getName());
		if (isLast) {
			if (prevHealth != currentPlayer.getHealth()) {
				updateBoardNoImageChange();
			}
			updateInfo(currentPlayer, false);
			updatePlayArea(currentPlayer, false);
			if (prevGold != currentPlayer.getGold()) {
				updateMarketAndTreasures(false);
			}
		}
	}
	
	// Do effects of card (runs after)
	private void doCardEffects(Card c) {
		currentPlayer.updateSkill(c.getSkill());
		currentPlayer.updateBoots(c.getBoots());
		currentPlayer.updateSwords(c.getSwords());
		currentPlayer.updateGold(c.getGold());
		currentPlayer.updateClankOnBoard(c.getClank());
		if (c.isTeleport()) {
			currentPlayer.updateTeleports(1);
		}
		
		// Do swag effect
		if (currentPlayer.getSwags() > 0 && c.getClank() > 0) currentPlayer.updateSkill(c.getClank()*currentPlayer.getSwags());
		
		// Draw last (not sure if required)
		currentPlayer.draw(c.getDraw());
		
		// Calculate conditions
		if (c.getCondition() != null) {
			if (c.getCondition()[0].contentEquals("companion")) {
				if (currentPlayer.hasCompanion()) {
					currentPlayer.draw(1);
				}
			} else if (c.getCondition()[0].contentEquals("artifact")) {
				if (currentPlayer.has("Artifact")) {
					if (c.getCondition()[1].contentEquals("teleport")) {
						currentPlayer.updateTeleports(1);
					} else if (c.getCondition()[1].contentEquals("skill")) {
						currentPlayer.updateSkill(2);
					}
				}
			} else if (c.getCondition()[0].contentEquals("crown")) {
				if (currentPlayer.has("Crown")) {
					if (c.getCondition()[1].contentEquals("heart")) {
						updateHealth(currentPlayer,1);
					} else if (c.getCondition()[1].contentEquals("swordboot")) {
						currentPlayer.updateSwords(1);
						currentPlayer.updateBoots(1);
					}
				}
			} else if (c.getCondition()[0].contentEquals("monkeyidol")) {
				if (currentPlayer.has("MonkeyIdol")) {
					currentPlayer.updateSkill(2);
				}
			}
		}
		
		// Unique Cards
		if (c.isUnique()) {
			// Choose cards
			if (c.getName().contentEquals("Shrine") || c.getName().contentEquals("Dragon Shrine") ||
				c.getName().contentEquals("Treasure Hunter") || 
				c.getName().contentEquals("Underworld Dealing") || c.getName().contentEquals("Mister Whiskers") ||
				c.getName().contentEquals("Wand of Wind")) {
				mustChoose.add(c.getName());
				choosePrompt();
			} else if (c.getName().endsWith("Master Burglar")) {
				currentPlayer.trash("Burgle");
			} else if (c.getName().contentEquals("Dead Run")) {
				currentPlayer.setRunning(true);
			} else if (c.getName().contentEquals("Flying Carpet")) {
				currentPlayer.setFlying(true);
			} else if (c.getName().contentEquals("Watcher") || c.getName().contentEquals("Tattle")) {
				giveOthersClank(1);
			} else if (c.getName().contentEquals("Swagger")) {
				currentPlayer.setSwags(currentPlayer.getSwags()+1);
			} else if (c.getName().contentEquals("Search")) {
				currentPlayer.setSearches(currentPlayer.getSearches()+1);
			} else if (c.getName().contentEquals("Sleight of Hand") || c.getName().contentEquals("Apothecary")) {
				// Including itself
				// If there are other cards, add discard
				if (currentPlayer.getPlayArea().getNonPlayedSize() > 1) {
					mustDiscard.add(c.getName());
					discardPrompt();
				}
			}
		}
		
	}
	
	// [COMM] Move (runs first)
	public void move(int room, int swordsUsed, boolean isLast) {
		int oldRoom = currentPlayer.getCurrentRoom();
		
		// Calculate effect for that path
		int effect = GlobalVars.effectsPerPath[oldRoom][room];
		if (GlobalVars.effectsPerPath[room][oldRoom] > 0) {
			effect = GlobalVars.effectsPerPath[room][oldRoom];
		}
		
		// Key, monster, flying/running
		if (currentPlayer.getBoots() > 0 && (effect != 3 || currentPlayer.has("Key")) && (effect == 0 || effect == 2 || effect == 3 || effect == 5 || currentPlayer.getBoots() > 1) && (currentPlayer.isFlying() || currentPlayer.isRunning() || !currentPlayer.isStoppedInCave())) {
			if (Utils.has(GlobalVars.adjacentRoomsPerRoom[currentPlayer.getCurrentRoom()],room)) {
				String eventDesc = "``"+currentName+"`` moved to **Room #"+room+"**";
				if (effect == 1) eventDesc += " and used an extra Boot";
				else if (effect == 2) {
					eventDesc += " and fought a monster";
					if (swordsUsed > 0) eventDesc += " with 1 sword";
					else eventDesc += " taking 1 damage";
				} else if (effect == 4) {
					eventDesc += ", used an extra Boot, and fought a monster";
					if (swordsUsed > 0) eventDesc += " with 1 sword";
					else eventDesc += " taking 1 damage";
				} else if (effect == 5) {
					eventDesc += " and fought 2 monsters";
					if (swordsUsed == 0) eventDesc += " taking 2 damage";
					else if (swordsUsed == 1) eventDesc += " with 1 sword taking 1 damage";
					else eventDesc += " with 2 swords";
				}
				addEvent(eventDesc,false);
				addHistory("ROOM "+room);
				System.out.println("[DEBUG LOG/Game.java] "+currentName+" moved from "+oldRoom+ " to "+room+" with effect number "+effect);
				
				// Left monkey idol room
				if (oldRoom == 28 && currentPlayer.getAlreadyPickedUpMonkeyIdol()) {
					currentPlayer.setAlreadyPickedUpMonkeyIdol(false);
				}
				
				currentPlayer.updateBoots(-1);
				
				//Effects from moving
				int healthChange = 0;
				if (effect > 0) {
					if (effect == 1) {
						currentPlayer.updateBoots(-1);
					} else if (effect == 2) {
						if (!currentPlayer.isFlying()) healthChange = -1;
					} else if (effect == 4) {
						currentPlayer.updateBoots(-1);
						if (!currentPlayer.isFlying()) healthChange = -1;			
					} else if (effect == 5) {
						if (!currentPlayer.isFlying()) healthChange = -2;				
					}
				}
				
				// Can't use more swords than have
				if (swordsUsed > currentPlayer.getSwords()) {
					swordsUsed = currentPlayer.getSwords();
				}
				healthChange += swordsUsed;
				currentPlayer.updateSwords(-1*swordsUsed);
				
				// Return unused swords
				if (healthChange > 0) {
					currentPlayer.updateSwords(healthChange);
				}
				updateHealth(currentPlayer,healthChange);
				
				moveUpdate(room, isLast);
			}
		}
	}
	
	// Move update (runs after)
	private void moveUpdate(int room, boolean isLast) {
		currentPlayer.setCurrentRoom(room);
		// Adjust coords of piece
		currentPlayer.getPiece().setX(GlobalVars.playerCoordsPerRoom[room][0]+GlobalVars.playersOffset[currentPlayer.getNumber()][0]);
		currentPlayer.getPiece().setY(GlobalVars.playerCoordsPerRoom[room][1]+GlobalVars.playersOffset[currentPlayer.getNumber()][1]);
		
		// Crystal Cave Effect
		if (Utils.isCave(room)) {
			currentPlayer.setStoppedInCave(true);
		}
		
		rewardPlayer(room);
		
		if (isLast) {
			updateInfo(currentPlayer, false);
			updatePlayArea(currentPlayer, false);
			updateMarketAndTreasures(false);
			updateEvents(false);
			try {
				updateBoard();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Check if player escaped with artifact
		if (room == 0 && currentPlayer.has("Artifact")) {
			// Discard all cards on escaping
			currentPlayer.discardAllCardsNotPlayed();
			
			currentPlayer.setFree(true);
			// 39 is first stage of end sequence
			if (firstEscapee == null && playerCount > 1) {
				// Used for checking if just escaped
				//currentPlayer.setCurrentRoom(39);
				currentPlayer.getPiece().setX(GlobalVars.playerCoordsPerRoom[39][0]);
				currentPlayer.getPiece().setY(GlobalVars.playerCoordsPerRoom[39][1]);
				firstEscapee = currentPlayer;
				addEvent("``"+currentName+"`` escaped the dungeon",true);
			}
			addHistory("ESCAPED");
			if (!checkIfGameIsOver()) {
				endTurn();
			}
		}
	}
	
	// Reward player with bonus {return false if didn't pick up a treasure/gold/skill}
	private boolean rewardPlayer(int room) {
		String item = mapContents[room];
		// Maximum of 9 consumables
		if (item != null && currentPlayer.getConsumables().size() < 10) {
			if (item.startsWith("Major")) {
				int s = majorSecrets.get(0);
				if (s == 1) {
					currentPlayer.pickupConsum("LargeHealing"); 
					addEvent("``"+currentName+"`` picked up a **Large Health Potion** :gift_heart:",false);
				}
				else if (s == 2) {
					currentPlayer.updateGold(5);
					addEvent("``"+currentName+"`` picked up **5** Gold :moneybag:",false);
				}
				else if (s == 3) {
					currentPlayer.updateSkill(5);
					addEvent("``"+currentName+"`` picked up **5** Skill :diamond_shape_with_a_dot_inside:",false);
				}
				else if (s == 4) {
					currentPlayer.pickup("Chalice");
					addEvent("``"+currentName+"`` picked up a **Chalice** :wine_glass:",false);
				}
				else if (s == 5) {
					currentPlayer.draw(3);
					addEvent("``"+currentName+"`` picked up a Draw **3** :book:",false);
				}
				
				majorSecrets.remove(0);
				mapContents[room] = null;
				return true;
			} else if (item.startsWith("Minor")) {
				int s = minorSecrets.get(0);
				if (s == 1) {
					currentPlayer.pickupConsum("SmallHealing");
					addEvent("``"+currentName+"`` picked up a **Small Health Potion** :heartpulse:",false);
				}
				else if (s == 2) {
					currentPlayer.updateGold(2);
					addEvent("``"+currentName+"`` picked up **2** Gold :moneybag:",false);
				}
				else if (s == 3) {
					currentPlayer.updateSkill(2);
					addEvent("``"+currentName+"`` picked up **2** Skill :diamond_shape_with_a_dot_inside:",false);
				}
				else if (s == 4) {
					currentPlayer.pickup("Egg");
					attackLevel++;
					addEvent("``"+currentName+"`` picked up a **Dragon Egg** :egg:",false);
				}
				else if (s == 5) {
					currentPlayer.pickupConsum("Strength");
					addEvent("``"+currentName+"`` picked up a **Potion of Strength** :muscle:",false);
				}
				else if (s == 6) {
					currentPlayer.pickupConsum("Swift");
					addEvent("``"+currentName+"`` picked up a **Potion of Swiftness** :ice_skate:",false);
				}
				else if (s == 7) {
					// Trash
					addEvent("``"+currentName+"`` picked up a **Magic Spring** :wastebasket:",false);
					mustTrash.add("Magic Spring");
				}
				
				minorSecrets.remove(0);
				if (item.endsWith("1")) {
					mapContents[room] = null;
				} else {
					mapContents[room] = "MinorSecret1";
				}
				return true;
			} else if (item.equals("Heart")) {
				updateHealth(currentPlayer,1);
				return false;
			}
			return false;
		}
		return false;
	}
	
	// [COMM] Select a card from dungeon row
	public void selectCard(int cardNumber, boolean isLast) {
		Card c = null;
		// Selecting from reserve
		int prevGold = currentPlayer.getGold();
		if (cardNumber >= 0 && cardNumber < 4) {
			if (cardNumber == 0) { 
				c = GlobalVars.cardDatabase.get("Goblin");
				if (currentPlayer.getSwords() >= c.getCost()) {
					currentPlayer.updateSwords(-1*c.getCost());
					doCardEffects(c);
				} else return;
			} else if (cardNumber == 1) {
				c = reserve.getExplorers().get(0);
				if (reserve.getExplorers().size() > 0 && currentPlayer.getSkill() >= c.getCost()) {
					currentPlayer.updateSkill(-1*c.getCost());
					currentPlayer.getDiscardPile().add(c);
					reserve.removeExplorer();
				} else return;
			} else if (cardNumber == 2) {
				c = reserve.getMercenaries().get(0);
				if (reserve.getMercenaries().size() > 0 && currentPlayer.getSkill() >= c.getCost()) {
					currentPlayer.updateSkill(-1*c.getCost());
					currentPlayer.getDiscardPile().add(c);
					reserve.removeMercenary();
				} else return;
			} else if (cardNumber == 3) {
				c = reserve.getSecretTomes().get(0);
				if (reserve.getSecretTomes().size() > 0 && currentPlayer.getSkill() >= c.getCost()) {
					currentPlayer.updateSkill(-1*c.getCost());
					currentPlayer.getDiscardPile().add(c);
					reserve.removeSecretTome();
					currentPlayer.updateSecretTomes(1);
				} else return;
			}
			// Add appropriate event description
			if (c.getType().contentEquals("monster")) addEvent("``"+currentName+"`` has slain **"+c.getName()+"**",false);
			else addEvent("``"+currentName+"`` selected **"+c.getName()+"**",false);
			addHistory(c.getName().toUpperCase());
			System.out.println("[DEBUG LOG/Game.java] "+currentName+" selected "+c.getName());
		} else {
			// Check if already bought
			if (dungeonRow[cardNumber-4].isBought()) {
				return;
			}
			
			c = dungeonRow[cardNumber-4];
			// Check restrictions
			if (c.isDeep() && !currentPlayer.isUnderground()) {
				return;
			}
			
			// Crystal Golem
			if (c.getName().contentEquals("Crystal Golem") && !currentPlayer.isInCave()) {
				return;
			}
			
			if (c.getType().contentEquals("basic")) {
				if (currentPlayer.getSkill() >= c.getCost()) {
					currentPlayer.updateSkill(-1*c.getCost());
					currentPlayer.getDiscardPile().add(c);
					//dungeonRow[cardNumber-4] = null;
					dungeonRow[cardNumber-4].setBought(true);
				} else return;
			} else if (c.getType().contentEquals("gem")) {
				int newc = c.getCost();
				if (currentPlayer.getPlayArea().has("Gem Collector")) {
					newc -= 2;
				}
				if (currentPlayer.getSkill() >= newc) {
					currentPlayer.updateSkill(-1*newc);
					currentPlayer.getDiscardPile().add(c);
					dungeonRow[cardNumber-4].setBought(true);
					currentPlayer.updateClankOnBoard(2);
					if (currentPlayer.getSwags() > 0) currentPlayer.updateSkill(2*currentPlayer.getSwags());
					
				} else return;
			} else if (c.getType().contentEquals("device")) {
				if (currentPlayer.getSkill() >= c.getCost()) {
					currentPlayer.updateSkill(-1*c.getCost());
					
					doCardEffects(c);
					
					dungeonRow[cardNumber-4].setBought(true);
				} else return;
			} else if (c.getType().contentEquals("monster")) {
				if (currentPlayer.getSwords()  >= c.getCost()) {
					currentPlayer.updateSwords(-1*c.getCost());

					doCardEffects(c);
					
					dungeonRow[cardNumber-4].setBought(true);
				} else return;
			}
			// Acquire effects
			if (c.getAcquire() != null) {
				if (c.getAcquire().contentEquals("health")) {
					updateHealth(currentPlayer,1);
				} else if (c.getAcquire().contentEquals("swords")) {
					currentPlayer.updateSwords(1);
				} else if (c.getAcquire().contentEquals("boots")) {
					currentPlayer.updateBoots(1);
				}
			}
			// Add appropriate event description
			if (c.getType().contentEquals("monster")) addEvent("``"+currentName+"`` has slain **"+c.getName()+"**",false);
			else addEvent("``"+currentName+"`` selected **"+c.getName()+"**",false);
			addHistory(c.getName().toUpperCase());
			System.out.println("[DEBUG LOG/Game.java] "+currentName+" selected "+c.getName());
		}
		if (isLast) {
			if (prevGold != currentPlayer.getGold()) {
				updateMarketAndTreasures(false);
			}
			updateBoardNoImageChange(); // Consider removing later
			updateInfo(currentPlayer, false);
			updateDungeonRow(currentPlayer, false);
			updateEvents(false);
		}
	}
	
	// [COMM] Grab an artifact/monkey idol
	public void grab() {
		String item = mapContents[currentPlayer.getCurrentRoom()];
		if (item == null) return;
		
		// If artifact, make sure has room or backpack
		if (item.startsWith("Artifact")) {
			if (currentPlayer.count("Artifact") <= currentPlayer.count("Backpack")) {
				currentPlayer.pickup(item);
				attackLevel++;
				addEvent("``"+currentName+"`` picked up an Artifact **"+item.substring(8)+"** :star:",true);
				addHistory("ARTIFACT "+item.substring(8));
				mapContents[currentPlayer.getCurrentRoom()] = null;
			}
		// If monkey idol, make sure didn't already pick one up this turn
		} else if (item.startsWith("MonkeyIdol") && !currentPlayer.getAlreadyPickedUpMonkeyIdol()) {
			currentPlayer.pickup(item);
			currentPlayer.setAlreadyPickedUpMonkeyIdol(true);
			int num = Integer.parseInt(item.substring(10));
			num--;
			if (num == 0) {
				mapContents[currentPlayer.getCurrentRoom()] = null;
			} else {
				mapContents[currentPlayer.getCurrentRoom()] = "MonkeyIdol"+num;
			}
			addEvent("``"+currentName+"`` picked up a Monkey Idol **5** :star:",true);
			addHistory("IDOL "+item.substring(8));
		}
		updateReactionsInfo();
		updatePlayArea(currentPlayer, false);
		updateMarketAndTreasures(false);
		try {
			updateBoard();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// [COMM] Buy from market
	public void buy(int num) {
		// Make sure there is one in stock and enough gold and in market rooms
		if (marketItemCount[num-1] > 0 && currentPlayer.getGold() >= 7 && (currentPlayer.getCurrentRoom() == 18 || currentPlayer.getCurrentRoom() == 19 || currentPlayer.getCurrentRoom() == 24 || currentPlayer.getCurrentRoom() == 25)) {
			// Adjust item index based on how many left
			int itemIndex = 0;
			if (num == 2) itemIndex = 2;
			if (num == 3) itemIndex = 4;
			while (market[itemIndex] == null) {
				itemIndex++;
			}
			currentPlayer.pickup(market[itemIndex]);
			currentPlayer.updateGold(-7);
			addEvent("``"+currentName+"`` bought a "+market[itemIndex].substring(0,market[itemIndex].length()-1).replace("1",""),true);
			addHistory(market[itemIndex].substring(0,market[itemIndex].length()-1).replace("1","").toUpperCase());
			market[itemIndex] = null;
			marketItemCount[num-1] -= 1;
			updateMarketAndTreasures(false);
			updatePlayArea(currentPlayer, false);
			//updateDungeonRow(currentPlayer, false);
			updateInfo(currentPlayer, false);
		}
	}
	
	// [COMM] Use a consumable
	public void useItem(int num) {
		if (num < currentPlayer.getConsumables().size()) {
			String item = currentPlayer.getConsumables().get(num);
			// Find corresponding name
			if (item.contentEquals("LargeHealing")) {
				addEvent("``"+currentName+"`` used a **Large Health Potion** :gift_heart:",true);
				updateHealth(currentPlayer,2);
				currentPlayer.getConsumables().remove(num);
				updateBoardNoImageChange();
			} else if (item.contentEquals("SmallHealing")) {
				addEvent("``"+currentName+"`` used a **Small Health Potion** :heartpulse:",true);
				updateHealth(currentPlayer,1);
				currentPlayer.getConsumables().remove(num);
				updateBoardNoImageChange();
			} else if (item.contentEquals("Swift")) {
				addEvent("``"+currentName+"`` used a **Potion of Swiftness** :ice_skate:",true);
				currentPlayer.updateBoots(1);
				currentPlayer.getConsumables().remove(num);
			} else if (item.contentEquals("Strength")) {
				addEvent("``"+currentName+"`` used a **Potion of Strength** :muscle:",true);
				currentPlayer.updateSwords(2);
				currentPlayer.getConsumables().remove(num);
			}
		}
		updateInfo(currentPlayer, false);
		updatePlayArea(currentPlayer, false);
	}
	
	// [COMM] Teleport
	public void teleport(int room, boolean isLast) {
		if (currentPlayer.getTeleports() > 0) {
			// If it's a valid room to teleport to
			if (Utils.has(GlobalVars.adjacentRoomsPerRoom[currentPlayer.getCurrentRoom()],room) || 
				Utils.has(GlobalVars.teleportRoomsPerRoom[currentPlayer.getCurrentRoom()],room)) {
				System.out.println("[DEBUG LOG/Game.java] "+currentName+" teleported to "+room);
				
				// If it's from a monkey room, allow to pickup another
				if (currentPlayer.getCurrentRoom() == 28 && currentPlayer.getAlreadyPickedUpMonkeyIdol()) {
					currentPlayer.setAlreadyPickedUpMonkeyIdol(false);
				}
				
				currentPlayer.updateTeleports(-1);
				
				moveUpdate(room, isLast);
			}
		}
	}

	// [COMM] Trash a card
	public void trash(String cardName) {
		if (currentPlayer.trash(cardName)) {
			mustTrash.remove(0);
			// Special case of trashing secret tome
			if (cardName.contentEquals("Secret Tome")) currentPlayer.updateSecretTomes(-1);
			updatePlayArea(currentPlayer, false);
		} 
	}
	
	// Send a choose prompt
	private void choosePrompt() {
		for (int i = 0; i < mustChoose.size(); i++) {
			String prompt = "**[Choose]** ";
			// Find the correct text to display
			if (mustChoose.get(i).contentEquals("Shrine")) {
				prompt += "**1** :moneybag: ~OR~ **1** :heart:";
			} else if (mustChoose.get(i).contentEquals("Dragon Shrine")) {
				prompt += "**2** :moneybag: ~OR~ **1** :wastebasket:";
			} else if (mustChoose.get(i).contentEquals("Treasure Hunter")) {
				prompt += "a card to replace in the dungeon row (4-9)";
			} else if (mustChoose.get(i).contentEquals("Underworld Dealing")) {
				prompt += "**1** :moneybag: ~OR~ **BUY 2** __Secret Tome__ **FOR 7** :moneybag:";
			} else if (mustChoose.get(i).contentEquals("Apothecary")) {
				prompt += "**3** :crossed_swords: **~OR~** **2** :moneybag: **~OR~** **1** :heart:";
			} else if (mustChoose.get(i).contentEquals("Mister Whiskers")) {
				prompt += "**Dragon Attack** ~OR~ **-2** :warning:";
			} else if (mustChoose.get(i).contentEquals("Wand of Wind")) {
				if (GlobalVars.teleportRoomsPerRoom.length > 0) {
					prompt += "**1** :crystal_ball: ~OR~ Take a **SECRET** from an adjacent room ``"+
						Utils.arrayToString(GlobalVars.adjacentRoomsPerRoom[currentPlayer.getCurrentRoom()])+" ┃ "+
						Utils.arrayToString(GlobalVars.teleportRoomsPerRoom[currentPlayer.getCurrentRoom()])+"``";
				} else {
					prompt += "**1** :crystal_ball: ~OR~ Take a **SECRET** from an adjacent room ``"+
							Utils.arrayToString(GlobalVars.adjacentRoomsPerRoom[currentPlayer.getCurrentRoom()])+"``";
				}
			}
			gameChannel.sendMessage(prompt).queue();
		}
	}
	
	// [COMM] Choose a prompted option (1-3)
	public void choose(int n) {
		for (int i = 0; i < mustChoose.size(); i++) {
			// Find the specific card
			if (mustChoose.get(i).contentEquals("Shrine")) {
				if (n == 1) {
					currentPlayer.updateGold(1);
				} else if (n == 2) {
					updateHealth(currentPlayer,1);
					updateBoardNoImageChange();
				} else return;
			} else if (mustChoose.get(i).contentEquals("Dragon Shrine")) {
				if (n == 1) {
					currentPlayer.updateGold(2);
				} else if (n == 2) {
					mustTrash.add("Dragon Shrine");
				} else return;
			} else if (mustChoose.get(i).contentEquals("Treasure Hunter")) {
				if (!dungeonRow[n-4].isBought()) {
					dungeonRow[n-4] = mainDeck.getNext();
					mainDeck.removeTop();
					if (dungeonRow[n-4].isHasArrive()) {
						doArriveEffect(dungeonRow[n-4]);
					}
					updateDungeonRow(currentPlayer, false);
				} else return;
			} else if (mustChoose.get(i).contentEquals("Underworld Dealing")) {
				if (n == 1) {
					currentPlayer.updateGold(1);
				} else if (n == 2 && currentPlayer.getGold() >= 7) {
					currentPlayer.updateGold(-7);
					// Tries to buy 2
					if (reserve.getSecretTomes().size() > 0) {
						currentPlayer.getDiscardPile().add(reserve.getSecretTomes().get(0));
						reserve.removeSecretTome();
						currentPlayer.updateSecretTomes(1);
					}
					if (reserve.getSecretTomes().size() > 0) {
						currentPlayer.getDiscardPile().add(reserve.getSecretTomes().get(0));
						reserve.removeSecretTome();
						currentPlayer.updateSecretTomes(1);
					}
					updateDungeonRow(currentPlayer, false);
				} else return;
			} else if (mustChoose.get(i).contentEquals("Apothecary")) {
				if (n == 1) {
					currentPlayer.updateSwords(3);
				} else if (n == 2) {
					currentPlayer.updateGold(2);
				} else if (n == 3) {
					updateHealth(currentPlayer,1);
					updateBoardNoImageChange();
				} else return;
			} else if (mustChoose.get(i).contentEquals("Mister Whiskers")) {
				if (n == 1) {
					// Only dragon attack in the middle of turn
					dragonAttack(cubesPerLevel[attackLevel]);
				} else if (n == 2) {
					currentPlayer.updateClankOnBoard(-2);
					updateBoardNoImageChange();
				} else return;
			} else if (mustChoose.get(i).contentEquals("Wand of Wind")) {
				if (n == 1) {
					currentPlayer.updateTeleports(1);
				} else if (n > 1) {
					// Take a secret from nearby room
					if (Utils.has(GlobalVars.adjacentRoomsPerRoom[currentPlayer.getCurrentRoom()],n) || Utils.has(GlobalVars.teleportRoomsPerRoom[currentPlayer.getCurrentRoom()],n)) {
						if (mapContents[n].startsWith("Minor") || mapContents[n].startsWith("Major")) {
							rewardPlayer(n);
							try {
								updateBoard();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else return;
				} else return;
			}
			mustChoose.remove(i);
		}
		updateMarketAndTreasures(false);
		updateInfo(currentPlayer, false);
		// In case Mister Whiskers ends the game
		if (!checkIfGameIsOver()) {
			updatePlayArea(currentPlayer, false);
		}
	}
	
	// Send a discard prompt
	private void discardPrompt() {
		for (int i = 0; i < mustDiscard.size(); i++) {
			String prompt = "**[Discard]** a card ";
			if (mustDiscard.get(i).contentEquals("Sleight of Hand")) {
				prompt += "to draw 2 cards";
			} else if (mustDiscard.get(i).contentEquals("Apothecary")) {
				prompt += "to choose one: **3** :crossed_swords: **~OR~** **2** :moneybag: **~OR~** **1** :heart:";
			}
			gameChannel.sendMessage(prompt).queue();
		}
	}
	
	// [COMM] Discard a card in hand
	public void discard(int num) {
		for (int i = 0; i < mustDiscard.size(); i++) {
			if (currentPlayer.discard(num)) {
				if (mustDiscard.get(i).contentEquals("Sleight of Hand")) {
					mustDiscard.remove(i);
					currentPlayer.draw(2);
					updatePlayArea(currentPlayer, false);
				} else if (mustDiscard.get(i).contentEquals("Apothecary")) {
					mustDiscard.remove(i);
					mustChoose.add("Apothecary");
					choosePrompt();
					updatePlayArea(currentPlayer, false);
				}
			}
		}
	}
	
	// Dragon attacks, calculate who gets hit
	private void dragonAttack(int cubes) {
		if (status.contentEquals("over")) {
			return;
		}
		// Add additional cubes per danger card
		for (int i = 0; i < 6; i++) {
			if (!dungeonRow[i].isBought() && dungeonRow[i].isHasDanger()) {
				cubes += 1;
			}
		}
		
		int totalClank = dragonClank;
		for (int i = 0; i < players.size(); i++) {
			players.get(i).insertClank();
			totalClank += players.get(i).getClankInBag();
		}
		
		dragonAttackingDelay = 2000;
		gameChannel.sendMessage(":dragon: *Dragon Attack!* Pulling **"+cubes+"** clank...").queueAfter(dragonAttackingDelay, TimeUnit.MILLISECONDS);
		
		// Randomly choose a cube
		dragonAttackEventText = "**[ :dragon: ]** ";
		Random r = new Random();
		for (int i = 0; i < cubes && totalClank > 0 && status.contentEquals("ingame"); i++) {
			boolean isLast = ((i == cubes-1) || totalClank == 1);
			int chance = r.nextInt(totalClank)+1;
			dragonAttackingDelay += 1000;
			if (chance <= dragonClank) {
				dragonClank -= 1;
				//addEvent(":dragon: Pulled Black Clank",false);
				if (isLast) {
					gameChannel.sendMessage("_ _ _ _ :game_die: Black clank was pulled. No damage dealt").queueAfter(dragonAttackingDelay, TimeUnit.MILLISECONDS);
				} else {
					gameChannel.sendMessage("** ** ** ** :game_die: Black clank was pulled. No damage dealt").queueAfter(dragonAttackingDelay, TimeUnit.MILLISECONDS);
				}
			} else if (chance <= dragonClank+p1.getClankInBag()) {
				dragonHit(p1, isLast);
			} else if (chance <= dragonClank+p1.getClankInBag()+p2.getClankInBag()) {
				dragonHit(p2, isLast);
			} else if (chance <= dragonClank+p1.getClankInBag()+p2.getClankInBag()+p3.getClankInBag()) {
				dragonHit(p3, isLast);
			} else if (chance <= dragonClank+p1.getClankInBag()+p2.getClankInBag()+p3.getClankInBag()+p4.getClankInBag()) {
				dragonHit(p4, isLast);
			}
			totalClank--;
		}
		// For deaths outside of dragon attack
		dragonAttackingDelay = 0;
		updateBoardNoImageChange();
		updateInfo(currentPlayer, false);
		updateEvents(false);
	}
	
	// Dragon hits a player
	private void dragonHit(Player p, boolean isLast) {
		p.updateClankInBag(-1);
		//String damageMsgE = ":dragon: Pulled ``"+getName(p)+"``'s clank";
		//**[ :dragon: ]** **-1** :heart: **-0** :blue_heart:
		dragonAttackEventText += "**-1** "+p.getHeartString()+" ";
		String damageMsgT = ":game_die: **"+getName(p)+"**";
		if (isLast) {
			damageMsgT = "_ _ _ _ "+damageMsgT;
		} else {
			damageMsgT = "** ** ** ** "+damageMsgT;
		}
		if (p.isFree()) {
			gameChannel.sendMessage(damageMsgT+"'s clank was pulled, but they already escaped").queueAfter(dragonAttackingDelay, TimeUnit.MILLISECONDS);
			//addEvent(damageMsgE + ", but they already escaped",false);
		} else if (p.isDead()) {
			gameChannel.sendMessage(damageMsgT+"'s clank was pulled, but they already died").queueAfter(dragonAttackingDelay, TimeUnit.MILLISECONDS);
			//addEvent(damageMsgE + ", but they already died",false);
		} else {
			gameChannel.sendMessage(damageMsgT+"'s clank was pulled and got hit by the dragon! **-1** :broken_heart: **"+(p.getHealth()-1)+"** Health Left").queueAfter(dragonAttackingDelay, TimeUnit.MILLISECONDS);
			//addEvent(damageMsgE + " **-1** :broken_heart: **"+(p.getHealth()-1)+"** Health Left",false);
		}
		updateHealth(p,-1);
		if (isLast && !dragonAttackEventText.contentEquals("**[ :dragon: ]** ")) {
			updateBoardNoImageChange();
		}
	}
	
	//----------------------------------------------------------------------------------------------
	// Updates/Displays
	
	// [DISP] Update Player Info Display	
	public void updateInfo(Player p, boolean isFirst) {
		if (p.isDead() || p.isFree()) {
			return;
		}
		System.out.println("[DEBUG LOG/Game.java] Updating player info");
		
		// Build embed with all stats
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(p.getColor());
		embed.setTitle("**"+Utils.getName(p.getPlayerID(),guild)+"**'s Information");
		embed.addField("Health :heart:","``"+p.getHealth()+"``", true);
		embed.addField("Board Clank :warning:","``"+p.getClankOnBoard()+"``", true);
		embed.addField("Bag Clank :briefcase:","``"+p.getClankInBag()+"``", true);
		embed.addField("**Skill** :diamond_shape_with_a_dot_inside:","``"+p.getSkill()+"``",true);
		embed.addField("**Boots** :boot:","``"+p.getBoots()+"``",true);
		embed.addField("**Swords** :crossed_swords:","``"+p.getSwords()+"``",true);
		embed.addField("Gold :moneybag:","``"+p.getGold()+"``",true);
		//embed.addField("Room ["+ p.getCurrentRoom() + "] :door:","``"+Utils.arrayToString(GlobalVars.adjacentRoomsPerRoom[p.getCurrentRoom()])+"``",true);
		embed.addField("Room ["+ p.getCurrentRoom() + "] :door:",Utils.arrayToEmojiString(GlobalVars.adjacentRoomsPerRoom[p.getCurrentRoom()]),true);
		if (p.getTeleports() > 0) {
			if (GlobalVars.teleportRoomsPerRoom[p.getCurrentRoom()].length > 0) {
				//embed.addField(p.getTeleports() + "x **Teleport(s)** :crystal_ball:","``"+
				//		Utils.arrayToString(GlobalVars.adjacentRoomsPerRoom[p.getCurrentRoom()])+" ┃ "+
				//		Utils.arrayToString(GlobalVars.teleportRoomsPerRoom[p.getCurrentRoom()])+"``",true);
				embed.addField(p.getTeleports() + "x **Teleport(s)** :crystal_ball:",
						Utils.arrayToEmojiString(GlobalVars.adjacentRoomsPerRoom[p.getCurrentRoom()])+"┃"+
						Utils.arrayToEmojiString(GlobalVars.teleportRoomsPerRoom[p.getCurrentRoom()],GlobalVars.adjacentRoomsPerRoom[p.getCurrentRoom()].length),true);
			} else {
				//embed.addField(p.getTeleports() + "x **Teleport(s)** :crystal_ball:","``"+
				//		Utils.arrayToString(GlobalVars.adjacentRoomsPerRoom[p.getCurrentRoom()])+"``",true);
				embed.addField(p.getTeleports() + "x **Teleport(s)** :crystal_ball:",
						Utils.arrayToEmojiString(GlobalVars.adjacentRoomsPerRoom[p.getCurrentRoom()]),true);
			}
		} else {
			embed.addField("","",true);
		}
		
		// Set history as footer
		String history = "";
		for (int i = p.getHistory().size()-1; i >= 0; i--) {
			history += p.getHistory().get(i) + " / ";
		}
		embed.setFooter(history, null);
		
		if (isFirst) {
			gameChannel.sendMessage(embed.build()).queueAfter(5000, TimeUnit.MILLISECONDS);
		} else {
			gameChannel.editMessageById(infoID, embed.build()).queue();
			updateReactionsInfo(); // To allow for hiding rooms that can't be moved into
		}
	}
	
	// [REACT] Update Reactions for Player Info
	public void updateReactionsInfo() {
		((TextChannel) gameChannel).clearReactionsById(infoID).queue();
		
		// Grab
		String item = mapContents[currentPlayer.getCurrentRoom()];
		if (item != null) {
			if (item.startsWith("Artifact") && currentPlayer.count("Artifact") <= currentPlayer.count("Backpack")) {
				gameChannel.addReactionById(infoID, GlobalVars.emojis.get("scroll")).queue();

			} else if (item.startsWith("MonkeyIdol") && !currentPlayer.getAlreadyPickedUpMonkeyIdol()) {
				gameChannel.addReactionById(infoID, GlobalVars.emojis.get("monkey_face")).queue();
			}
		}
		
		// Movement
		int totalRooms = GlobalVars.adjacentRoomsPerRoom[currentPlayer.getCurrentRoom()].length;
		if (currentPlayer.getTeleports() > 0 ) {
			gameChannel.addReactionById(infoID, GlobalVars.emojis.get("crystal_ball")).queue();
			if (hasLinkedCommand() && linkedCommand.equals("tp ")) {
				totalRooms += GlobalVars.teleportRoomsPerRoom[currentPlayer.getCurrentRoom()].length;
			}
		}
		gameChannel.addReactionById(infoID, GlobalVars.emojis.get("link"));
		for (int i = 0; i < totalRooms; i++) {
			// Later, don't show rooms that can't be moved to
			gameChannel.addReactionById(infoID, Utils.numToLetterEmojis[i]).queue();
		}
		
//		if (currentPlayer.getSwords() > 0 && ???) {
//			int effect = GlobalVars.effectsPerPath[oldRoom][room];
//			if (GlobalVars.effectsPerPath[room][oldRoom] > 0) {
//				effect = GlobalVars.effectsPerPath[room][oldRoom];
//			}
//			
//		}
	}
	
	// [DISP] Update Dungeon Row Display
	private void updateDungeonRow(Player p, boolean isFirst) {
		System.out.println("[DEBUG LOG/Game.java] Updating dungeon row");
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(Color.GRAY);
		embed.setTitle("Dungeon Row");
		
		// Add reserve and rotation cards
		String reserveCards = "";
		String rotationCards = "";
		reserveCards += "\n:zero: "+GlobalVars.cardDatabase.get("Goblin").toString();
		reserveCards += "\n:one: "+reserve.getExplorers().size()+"x "+GlobalVars.cardDatabase.get("Explorer").toString();
		reserveCards += "\n:two: "+reserve.getMercenaries().size()+"x "+GlobalVars.cardDatabase.get("Mercenary").toString();
		reserveCards += "\n:three: "+reserve.getSecretTomes().size()+"x "+GlobalVars.cardDatabase.get("Secret Tome").toString();
		for (int i = 0; i < 6; i++) {
			if (!dungeonRow[i].isBought()) {
				rotationCards += "\n"+Utils.numToEmoji[i+4]+" "+dungeonRow[i].toString();
			} else {
				rotationCards += "\n:white_check_mark: "+dungeonRow[i].toString();
			}
		}

		//String marketString = "MARKET: [1] "+marketItemCount[0]+"x Key / [2] "+marketItemCount[1]+"x Backpack / [3] "+marketItemCount[2]+"x Crown";

		//embed.setFooter(marketString, null);
		embed.addField("Reserve",reserveCards, true);
		embed.addField("Rotation",rotationCards, true);
		if (isFirst) {
			gameChannel.sendMessage(embed.build()).queueAfter(1000, TimeUnit.MILLISECONDS);
		} else {
			gameChannel.editMessageById(dungeonRowID, embed.build()).queue();
			updateReactionsDungeonRow();
		}
	}
	
	// [REACT] Update Reactions for Dungeon Row
	public void updateReactionsDungeonRow() {
		((TextChannel) gameChannel).clearReactionsById(dungeonRowID).queue();
		gameChannel.addReactionById(dungeonRowID, GlobalVars.emojis.get("mag")).queue();
		gameChannel.addReactionById(dungeonRowID, GlobalVars.emojis.get("link")).queue();
		// Later, only add the ones they can afford/select based on restrictions
		for (int i = 0; i < 10; i++) {
			gameChannel.addReactionById(dungeonRowID, Utils.numToNumEmoji[i]).queue();
		}
	}
	
	// [DISP] Update Play Area Display
	public void updatePlayArea(Player p, boolean isFirst) {
		if (status.contentEquals("over")) {
			return;
		}
		
		System.out.println("[DEBUG LOG/Game.java] Updating play area");
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(p.getColor());
		embed.setTitle("**"+Utils.getName(p.getPlayerID(),guild)+"**'s Play Area");
		
		// Create cards string
		String cards = "";
		for (int i = 0; i < currentPlayer.getPlayArea().getSize(); i++) {
			Card c = currentPlayer.getPlayArea().getCard(i);
			cards += "\n";
			if (c.isPlayed()) {
				cards += ":white_check_mark: ";
			} else if (c.hasPriorityHint()) {
				cards += ":bell: ";
			} else {
				//cards += Utils.numToEmote[i+1]+" ";
				cards += ":regional_indicator_"+((char)(i+97))+": ";
			}
			cards += c.toStringHand();
		}
		
		// Create treasures and consumables string
		String consumables = "";
		int counter = 0;
		for (int i = 0; i < currentPlayer.getConsumables().size(); i++) {
			String item = currentPlayer.getConsumables().get(i);
			if (item.contentEquals("LargeHealing")) {
				consumables += ":gift_heart: ";
			} else if (item.contentEquals("SmallHealing")) {
				consumables += ":heartpulse: ";
			} else if (item.contentEquals("Swift")) {
				consumables += ":ice_skate: ";
			} else if (item.contentEquals("Strength")) {
				consumables += ":muscle: ";
			}
			counter++;
			if (counter % 3 == 0) {
				consumables += "\n";
			}
		}
		embed.addField("Cards",cards, true);
		if (currentPlayer.getInventory().size() > 0) {
			embed.addField("Treasures",currentPlayer.getInventoryAsString(true), true);
		}
		if (currentPlayer.getConsumables().size() > 0) {
			embed.addField("Consumables",consumables, true);
		}

		embed.setFooter("Next Turn: "+getName(currentPlayer.getNextPlayer()), null);
		
		if (isFirst) {
			gameChannel.sendMessage(embed.build()).queueAfter(6000, TimeUnit.MILLISECONDS);
		} else {
			gameChannel.editMessageById(playAreaID, embed.build()).queue();
			updateReactionsPlayArea();
		}
	}
	
	// [REACT] Update Reactions for Play Area
	public void updateReactionsPlayArea() {
		((TextChannel) gameChannel).clearReactionsById(playAreaID).queue();
		if (currentPlayer.getPlayArea().getNonPlayedSize() > 0) {
			gameChannel.addReactionById(playAreaID, GlobalVars.emojis.get("fast_forward")).queue();
			gameChannel.addReactionById(playAreaID, GlobalVars.emojis.get("link")).queue();
			for (int i = 0; i < currentPlayer.getPlayArea().getSize(); i++) {
				if (!currentPlayer.getPlayArea().getCard(i).isPlayed()) {
					gameChannel.addReactionById(playAreaID, Utils.numToLetterEmojis[i]).queue();
				}
			}
		} else {
			gameChannel.addReactionById(playAreaID, GlobalVars.emojis.get("checkered_flag")).queue();
		}
	}

	// [DISP] Updates events embed
	private void updateEvents(boolean isFirst) {
		System.out.println("[DEBUG LOG/Game.java] Updating events");
		EmbedBuilder embed = new EmbedBuilder();
		String eventMsg = "";
		for (int i = 0; i < events.size(); i++) {
			eventMsg += events.get(i) + "\n";
		}
		embed.setColor(Color.GRAY);
		if (isFirst) {
			embed.setTitle("Events");
			gameChannel.sendMessage(embed.build()).queueAfter(100, TimeUnit.MILLISECONDS);
		} else {
			embed.setTitle(null);
			// Game breaks if eventMsg and title is ""
			if (eventMsg.contentEquals("")) {
				embed.setDescription("Empty");
			} else {
				embed.setDescription(eventMsg);
			}
			gameChannel.editMessageById(eventsID, embed.build()).queue();
		}
	}
	
	// [DISP] Updates Markets & Treasures embed
	private void updateMarketAndTreasures(boolean isFirst) {
		System.out.println("[DEBUG LOG/Game.java] Updating market and treasures");
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Market & Treasures");
		embed.setColor(Color.YELLOW);
		
		String marketString = "";
		for (int i = 0; i < market.length; i++) {
			if (market[i] != null) {
				if (market[i].startsWith("Key")) {
					marketString += ":key:**5** ";
				} else if (market[i].startsWith("Backpack")) {
					marketString += ":briefcase:**5** ";
				} else if (market[i].startsWith("Crown")) {
					marketString += ":crown:**"+market[i].substring(5)+"** ";
				}
			}
		}
		
		// Add strings
		embed.addField("In Stock (**7x**:moneybag: each)", marketString, false);
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			embed.addField("``"+getName(p)+"`` **"+p.getGold()+"x**:moneybag:",p.getInventoryAsString(false), false);
		}
		
		if (isFirst) {
			gameChannel.sendMessage(embed.build()).queueAfter(500, TimeUnit.MILLISECONDS);
		} else {
			gameChannel.editMessageById(marketAndTsID, embed.build()).queue();
			updateReactionsMarketAndTreasures();
		}
	}
	
	// [REACT] Update Reactions for Market & Treasures
	public void updateReactionsMarketAndTreasures() {
		((TextChannel) gameChannel).clearReactionsById(marketAndTsID).queue();
		if (currentPlayer.getGold() >= 7) {
			if (marketItemCount[0] > 0) {
				gameChannel.addReactionById(marketAndTsID, GlobalVars.emojis.get("key")).queue();
			}
			if (marketItemCount[1] > 0) {
				gameChannel.addReactionById(marketAndTsID, GlobalVars.emojis.get("briefcase")).queue();
			}
			if (marketItemCount[2] > 0) {
				gameChannel.addReactionById(marketAndTsID, GlobalVars.emojis.get("crown")).queue();
			}
		}
	}
	
	// Helps update board embed with new board
	private void updateBoard() throws FileNotFoundException {
		System.out.println("[DEBUG LOG/Game.java] Creating board image");
		BufferedImage combined = new BufferedImage(GlobalVars.images.get("Board1").getWidth(), GlobalVars.images.get("Board1").getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = combined.getGraphics();
		// Draws board template first
		g.drawImage(GlobalVars.images.get("Board1"), 0, 0, null);
		
		// Loops through contents and adds secrets, artifacts, idols
		for (int i = 0; i < mapContents.length; i++) {
			if (mapContents[i] != null && mapContents[i] != "heart") {
				int offsetX = 4;
				int offsetY = -1;
				if (mapContents[i].startsWith("Minor")) {
					offsetX = 7;
					offsetY = 3;
				}
				// Specific spots to this map
				if (i == 18) {
					g.drawImage(GlobalVars.images.get(mapContents[i]), 206, 286, null);
				} else if (i == 19) {
					g.drawImage(GlobalVars.images.get(mapContents[i]), 299, 287, null);
				} else if (i == 25) {
					g.drawImage(GlobalVars.images.get(mapContents[i]), 297, 352, null);
				} else if (i == 28) {
					int num = Integer.parseInt(mapContents[i].substring(10));
					if (num > 0) {
						g.drawImage(GlobalVars.images.get("MonkeyIdol"), 28, 369, null);
						if (num > 1) {
							g.drawImage(GlobalVars.images.get("MonkeyIdol"), 28, 395, null);
							if (num > 2) {
								g.drawImage(GlobalVars.images.get("MonkeyIdol"), 28, 422, null);
							}
						}
					}
				} else {
					g.drawImage(GlobalVars.images.get(mapContents[i]), GlobalVars.playerCoordsPerRoom[i][0]+offsetX, GlobalVars.playerCoordsPerRoom[i][1]+offsetY, null);
				}
			}
		}
		
		// Draws dragons
		if (attackLevel == 0) g.drawImage(GlobalVars.images.get("Dragon"), 395, 548, null);
		else if (attackLevel == 1) g.drawImage(GlobalVars.images.get("Dragon"), 456, 542, null);
		else if (attackLevel == 2) g.drawImage(GlobalVars.images.get("Dragon"), 513, 510, null);
		else if (attackLevel == 3) g.drawImage(GlobalVars.images.get("Dragon"), 549, 462, null);
		else if (attackLevel == 4) g.drawImage(GlobalVars.images.get("Dragon"), 561, 412, null);
		else if (attackLevel == 5) g.drawImage(GlobalVars.images.get("Dragon"), 551, 349, null);
		else {
			g.drawImage(GlobalVars.images.get("Dragon"), 552, 287, null);
		}
		
		// Draws characters
		g.drawImage(GlobalVars.images.get("RedChar"), p1.getPiece().getX(), p1.getPiece().getY(), null);
		if (playerCount >= 2) {
			g.drawImage(GlobalVars.images.get("BlueChar"), p2.getPiece().getX(), p2.getPiece().getY(), null);
			if (playerCount >= 3) {
				g.drawImage(GlobalVars.images.get("YellowChar"), p3.getPiece().getX(), p3.getPiece().getY(), null);
				if (playerCount >= 4) {
					g.drawImage(GlobalVars.images.get("GreenChar"), p4.getPiece().getX(), p4.getPiece().getY(), null);
				}
			}
		}
		
		// Writes the combined image into a file
		try {
			ImageIO.write(combined, "PNG", new File("newboard.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Builds an embed and sends it to the filesChannel
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(gameChannel.getName());
		embed.setColor(Color.GRAY);
		InputStream test = new FileInputStream("newboard.png");
		embed.setImage("attachment://newboard.png");
		MessageBuilder m = new MessageBuilder();
		m.setEmbed(embed.build());
		GlobalVars.filesChannel.sendFile(test, "newboard.png", m.build()).queue();
	}
	
	// Helps update board embed with same board, just new footer and description
	private void updateBoardNoImageChange() {
		afterReceivedBoardURL(boardImageURL);
	}
	
	// [DISP] Updates board embed
	public void afterReceivedBoardURL(String url) {
    	System.out.println("[DEBUG LOG/Game.java] Updating board...");
		boardImageURL = url;
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Board");
		embed.setColor(Color.GRAY);
		
		// Calculate clank footer
		int totalClank = dragonClank + p1.getClankInBag();
		if (playerCount >= 2) {
			totalClank += p2.getClankInBag();
			if (playerCount >= 3) {
				totalClank += p3.getClankInBag();
				if (playerCount >= 4) {
					totalClank += p4.getClankInBag();
					embed.setFooter(dragonClank+" BLACK / "+p1.getClankInBag()+" ("+p1.getClankOnBoard()+") RED / "+p2.getClankInBag()+" ("+p2.getClankOnBoard()+") BLUE / "+p3.getClankInBag()+" ("+p3.getClankOnBoard()+") YELLOW / "+p4.getClankInBag()+" ("+p4.getClankOnBoard()+") GREEN / "+totalClank+" TOTAL", null);
				} else {
					embed.setFooter(dragonClank+" BLACK / "+p1.getClankInBag()+" ("+p1.getClankOnBoard()+") RED / "+p2.getClankInBag()+" ("+p2.getClankOnBoard()+") BLUE / "+p3.getClankInBag()+" ("+p3.getClankOnBoard()+") YELLOW / "+totalClank+" TOTAL", null);
				}
			} else {
				embed.setFooter(dragonClank+" BLACK / "+p1.getClankInBag()+" ("+p1.getClankOnBoard()+") RED / "+p2.getClankInBag()+" ("+p2.getClankOnBoard()+") BLUE / "+totalClank+" TOTAL", null);
			}
		} else {
			embed.setFooter(dragonClank+" BLACK / "+p1.getClankInBag()+" ("+p1.getClankOnBoard()+") RED / "+totalClank+" TOTAL", null);
		}
		// Dragon and Health indicator
		String healthIndicator = "LEVEL **"+attackLevel+"** :dragon: |";
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (!p.hasQuit()) {
				healthIndicator += " **"+p.getHealth()+"** "+p.getHeartString();
			}
		}
		embed.setDescription(healthIndicator);
		embed.setImage(url);
		if (isFirstBoard) {
			// If first board, set delay so it appears in correct order
			gameChannel.sendMessage(embed.build()).queueAfter(1000, TimeUnit.MILLISECONDS);;
			isFirstBoard = false;
		} else {
			gameChannel.editMessageById(boardID, embed.build()).queue();
		}
		System.out.println("[DEBUG LOG/Game.java] Updated board");
	}
	
	// [COMM] Show deck of player
	public void displayDeck(String playerID) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("**"+Utils.getName(playerID, guild)+"**'s Deck");
		String cardsInDeck = "";
		String cardsInDiscard = "";
		int totalCards = 0;
		
		for (int i = 0; i < players.size(); i++) {
			// Find player and sort a temporary version of their deck/discard
			if (playerID.contentEquals(players.get(i).getPlayerID())) {
				@SuppressWarnings("unchecked")
				ArrayList<Card> tempSortedDeck = (ArrayList<Card>) players.get(i).getDeck().cards.clone();
				Collections.sort(tempSortedDeck);
				
				@SuppressWarnings("unchecked")
				ArrayList<Card> tempSortedDiscard = (ArrayList<Card>) players.get(i).getDiscardPile().cards.clone();
				Collections.sort(tempSortedDiscard);
				
				for (int j = 0; j < tempSortedDeck.size(); j++) {
					cardsInDeck += tempSortedDeck.get(j).getName()+", ";
				}
				for (int j = 0; j < tempSortedDiscard.size(); j++) {
					if (!tempSortedDiscard.get(j).isPlayed()) {
						cardsInDiscard += tempSortedDiscard.get(j).getName()+", ";
					}
				}
				totalCards = tempSortedDeck.size() + tempSortedDiscard.size() + players.get(i).getPlayArea().getNonPlayedSize();
				break;
			}
		}
		
		// String formatting
		if (cardsInDeck.length() >= 2) {
			cardsInDeck = cardsInDeck.substring(0,cardsInDeck.length()-2);
		}
		
		if (cardsInDiscard.length() >= 2) {
			cardsInDiscard = cardsInDiscard.substring(0,cardsInDiscard.length()-2);
			cardsInDiscard = "*"+cardsInDiscard+"*";
		}
		embed.addField("Deck",cardsInDeck,true);
		embed.addField("Discard Pile",cardsInDiscard,true);
		embed.setFooter(totalCards+" TOTAL CARDS", null);
		gameChannel.sendMessage(embed.build()).queue();
	}
	
	// [COMM] Send history in private message
	public void sendHistory(User user, String playerID) {
		for (int i = 0; i < players.size(); i++) {
			final Player p = players.get(i);
			if (p.getPlayerID().contentEquals(playerID)) {
				user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
					public void accept(PrivateChannel channel) {
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle("**"+Utils.getName(p.getPlayerID(),guild)+"**'s History #"+gameChannel.getName());
						embed.setColor(p.getColor());
						embed.setDescription(p.getHistoryString());
					    channel.sendMessage(embed.build()).queue();
					}
				});
			}
		}
	}
	//----------------------------------------------------------------------------------------------
	// Misc Helper methods
	
	// Adds an event
	public void addEvent(String s, boolean isLast) {
		events.add(s);
		if (events.size() > 12) events.remove(0);
		if (isLast) {
			updateEvents(false);
		}
	}
	
	// Adds to history of currentPlayer
	private void addHistory(String s) {
		currentPlayer.getHistory().add(s);
		if (currentPlayer.getHistory().size() > 6) currentPlayer.getHistory().remove(0);
		//updatePlayArea(currentPlayer, false);
	}
	
	// Clear messages
	public void clearMessages() {
		//textChannel.deleteMessagesByIds(messagesToDeleteIDs).queue();
		if (messagesToDeleteIDs.size() != 0) {
			((TextChannel) gameChannel).deleteMessagesByIds(messagesToDeleteIDs).queueAfter(1000, TimeUnit.MILLISECONDS);
			messagesToDeleteIDs.clear();
		}
	}
	
	// Give everyone except currentPlayer clank
	public void giveOthersClank(int clank) {
		for (int i = 0; i < players.size(); i++) {
			if (!(players.get(i) == currentPlayer) && !players.get(i).isDead() && !players.get(i).isFree()) {
				players.get(i).updateClankOnBoard(clank);
			}
		}
	}
	
	// Remove artifact (used at start)
	public void removeArtifact() {
		Random r = new Random();
		int num;
		String artifact;
		// Pick random artifact and remove it
		do {
			num = r.nextInt(artifacts.length);
			artifact = artifacts[num];
		} while (artifact == null);
		for (int i = 0; i < mapContents.length; i++) {
			if (mapContents[i] != null && artifact.contentEquals(mapContents[i])) {
				mapContents[i] = null;
			}
		}
		artifacts[num] = null;
	}
	
	// Performs arrive effects (adding clank)
	public void doArriveEffect(Card c) {
		// May change later
		if (c.getName().contentEquals("Shrine")) {
			dragonClank += 3;
			if (dragonClank > 24) {
				dragonClank = 24;
			}
		// Overlord and Watcher so far
		} else {
			for (int j = 0; j < players.size(); j++) {
				if (!players.get(j).isDead() && !players.get(j).isFree()) {
					players.get(j).updateClankOnBoard(1);
				}
			}
		}
		updateBoardNoImageChange();
	}
	
	// Update the health of player {simple, runs death(p)}
	public void updateHealth(Player p, int n) {
		if (!p.isDead() && !p.isFree()) {
			p.updateHealth(n);
			if (p.getHealth() > 10) {
				p.setHealth(10);
			} else if (p.getHealth() <= 0) {
				death(p);
			}
			//updateBoardNoImageChange();
		}
	}
	
	// Check if over
	public boolean checkIfGameIsOver() {
		System.out.println("[DEBUG LOG/Game.java] Checking if game is over");
		for (int i = 0; i < players.size(); i++) {
			if (!players.get(i).isDead() && !players.get(i).isFree() && !players.get(i).hasQuit()) {
				System.out.println("[DEBUG LOG/Game.java] Game was not found to be over");
				return false;
			}
		}
		// It is over so run gameOver
		if (!status.contentEquals("over")) {
			gameOver();
		}
		return true;
	}
	
	// Return effective name
	private String getName(Player p) {
		return guild.getMemberById(p.getPlayerID()).getEffectiveName();
	}
	
	// Sort scores
	public List<Entry<Integer, Integer>> sortScores(Map<Integer, Integer> scores) {
		Set<Entry<Integer, Integer>> set = scores.entrySet();
		List<Entry<Integer, Integer>> sortedList = new ArrayList<Entry<Integer, Integer>>(set);
		Collections.sort(sortedList, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2 ) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		} );
		return sortedList;
	}
	
	//----------------------------------------------------------------------------------------------
	// Simple getters and setters
	
	public MessageChannel getGameChannel() {
		return gameChannel;
	}
	
	public String getStatus() {
		return status;
	}
	
	public int getPlayerCount() {
		return playerCount;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public String getInfoID() {
		return infoID;
	}

	public void setInfoID(String infoID) {
		this.infoID = infoID;
	}

	public String getPlayAreaID() {
		return playAreaID;
	}

	public void setPlayAreaID(String playAreaID) {
		this.playAreaID = playAreaID;
	}

	public String getDungeonRowID() {
		return dungeonRowID;
	}

	public void setDungeonRowID(String dungeonRowID) {
		this.dungeonRowID = dungeonRowID;
	}

	public String getBoardID() {
		return boardID;
	}

	public void setBoardID(String boardID) {
		this.boardID = boardID;
	}
	
	public String getEventsID() {
		return eventsID;
	}

	public void setEventsID(String eventsID) {
		this.eventsID = eventsID;
	}
	
	public String getMarketAndTsID() {
		return marketAndTsID;
	}
	
	public void setMarketAndTsID(String marketAndTsID) {
		this.marketAndTsID = marketAndTsID;
	}
	
	public boolean isFirstBoard() {
		return isFirstBoard;
	}

	public void setFirstBoard(boolean isFirstBoard) {
		this.isFirstBoard = isFirstBoard;
	}

	public String[] getMapContents() {
		return mapContents;
	}

	public void setMapContents(String[] mapContents) {
		this.mapContents = mapContents;
	}

	public ArrayList<String> getMustChoose() {
		return mustChoose;
	}

	public void setMustChoose(ArrayList<String> mustChoose) {
		this.mustChoose = mustChoose;
	}

	public ArrayList<String> getMustTrash() {
		return mustTrash;
	}

	public void setMustTrash(ArrayList<String> mustTrash) {
		this.mustTrash = mustTrash;
	}

	public ArrayList<String> getMustDiscard() {
		return mustDiscard;
	}

	public void setMustDiscard(ArrayList<String> mustDiscard) {
		this.mustDiscard = mustDiscard;
	}

	public ArrayList<String> getMessagesToDeleteIDs() {
		return messagesToDeleteIDs;
	}

	public void setMessagesToDeleteIDs(ArrayList<String> messagesToDeleteIDs) {
		this.messagesToDeleteIDs = messagesToDeleteIDs;
	}
	
	public String getLinkedCommand() {
		return linkedCommand;
	}
	
	public void addLinkedCommand(String s) {
		linkedCommand += s;
	}
	
	public void setLinkedCommand(String s) {
		linkedCommand = s;
	}
	
	public boolean hasLinkedCommand() {
		return linkedCommand != null;
	}
	
	// Variables
	public HashMap<String,Piece> pieces = new HashMap<String,Piece>();
	public MessageChannel gameChannel;
	public Guild guild;
	public String hostID;
	public String status = "pregame";
	public int playerCount = 0;
	public int votedForEnd = 0;
	public String linkedCommand = null;
	
	public ArrayList<Player> players = new ArrayList<Player>();
	public Deck mainDeck = new Deck(false);
	public Card[] dungeonRow = new Card[6];
	public Reserve reserve = new Reserve();
	public String[] mapContents = {null,null,null,"MinorSecret2","MajorSecret1","MajorSecret1","major",null,"MajorSecret1","MinorSecret2",null,"Heart","MinorSecret2",null,null,
			  "MinorSecret2",null,null,"MinorSecret2","MinorSecret2","Artifact7","MinorSecret2","Artifact15","MajorSecret1",
			  null,"Artifact10","MajorSecret1","MajorSecret1","MonkeyIdol3","MinorSecret2","Artifact20","MinorSecret2","Artifact25",
			  "Heart","Artifact30",null,"MajorSecret1","Heart","MajorSecret1"};
	public String[] market = {"Key5","Key5","Backpack5","Backpack5","Crown10","Crown9","Crown8"};
	public int[] marketItemCount = {2,2,3};
	//public boolean isEndGame = false;
	public Player firstEscapee = null;
	public ArrayList<String> events = new ArrayList<String>();
	public ArrayList<String> messagesToDeleteIDs = new ArrayList<String>();
	
	public ArrayList<Integer> minorSecrets = new ArrayList<Integer>();
	public ArrayList<Integer> majorSecrets = new ArrayList<Integer>();
	public ArrayList<String> mustTrash = new ArrayList<String>();
	public ArrayList<String> mustChoose = new ArrayList<String>();
	public ArrayList<String> mustDiscard = new ArrayList<String>();
	
	public Player p1;
	public Player p2;
	public Player p3;
	public Player p4;
	
	public Player currentPlayer;
	public String currentName;
	public String infoID;
	public String playAreaID;
	public String dungeonRowID;
	public String boardID;
	public String eventsID;
	public String marketAndTsID;
	public boolean isFirstBoard;
	public boolean turnIsEnding;
	public String boardImageURL;
	public int attackLevel;
	public int[] cubesPerLevel = {2,2,3,3,4,4,5,5,5,5,5,5,5,5,5,5,5,5,5};
	String[] artifacts = {"Artifact5","Artifact7","Artifact10","Artifact15","Artifact20","Artifact25","Artifact30"};
	public int dragonClank = 24;
	public int dragonAttackingDelay = 2000;
	public String dragonAttackEventText = "";
}
