package BoardGames.DeckBuildingGameBot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;


public class Game implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3350814579583386331L;
	/**
	 * 
	 */
	// [COMM] Initiates Game Object
	public Game(MessageChannel channel, Guild guild,String hostID,int firstColor,boolean addReactions) {
		this.guild = guild;
		this.gameChannel = channel;
		this.hostID = hostID;
		availableColors.add(0);
		availableColors.add(1);
		availableColors.add(2);
		availableColors.add(3);
		
		setupNewGame(firstColor, addReactions);
	}
	
    // [HELP] Initiate game elements
	private void setupNewGame(int firstColor, boolean addReactions) {
		// Add Player One
		playerCount++;
		String name = guild.getMemberById(hostID).getEffectiveName();
		players.add(new Player(0,hostID,name,firstColor));
//		p1.setNextPlayer(p1);
		String id;
		if (firstColor == -1) id = gameChannel.sendMessage("**"+name+"** started a game").complete().getId();
		else {
			id = gameChannel.sendMessage("**"+name+"** started a game as "+Utils.colorBooks[firstColor]).complete().getId();
			availableColors.remove((Integer) firstColor);
		}
		
		// Add emojis
		if (addReactions) {
			for (Integer i : availableColors) {
				gameChannel.addReactionById(id, GlobalVars.emojis.get("book "+i)).queue();
			}
			gameChannel.addReactionById(id, GlobalVars.emojis.get("repeat")).queue();
			gameChannel.addReactionById(id, GlobalVars.emojis.get("performing_arts")).queue();
			gameChannel.addReactionById(id, GlobalVars.emojis.get("arrow_forward")).queue();
		}
		
		turnNumber = 1;
//		startingNewTurnPlayer = p1;
//		currentPlayer = p1;
		
		generateDeck(true, addReactions);
	}
	
	public void generateDeck(boolean isFirst, boolean display) {
		mainDeck = new Deck(false);

		for (int i = 0; i < 6; i++) {
			centerRow[i] = mainDeck.getNext();
			mainDeck.removeTop();
		}
		
		if (display) {
			updateCenterRow(currentPlayer, isFirst); // Doesn't add reactions.
		}
	}
	
	public void saveFiles() {
		String id = Utils.getGameID();
		System.out.println("ID: "+id);
		//String fileName = "Game#"+id+".txt";
		String fileName = "Game"+id+".txt";
		try { 
			//File output = null;
            //FileOutputStream file = new FileOutputStream(GlobalVars.gpath+fileName); 
			FileOutputStream file = new FileOutputStream("Games\\"+fileName); 
            ObjectOutputStream out = new ObjectOutputStream(file); 
            out.writeObject(this); 
            out.close(); 
            file.close();
            System.out.println(fileName+" has been serialized");
            GlobalVars.gamesChannel.sendFile(new File("Games\\"+fileName)).queue();
            gameChannel.sendMessage("Game data has been saved. ID: **"+id+"**").queue();
        } catch (IOException ex) { 
            System.out.println("IOException is caught"); 
        } 
	}
	// [HELP] Load files
	public static Game loadFiles(String gameID, MessageChannel channel, Guild guild) {
		String fileName = "Game"+gameID+".txt";
		Game g = null;
		try { 
            //FileInputStream file = new FileInputStream(GlobalVars.gpath+fileName); 
			FileInputStream file = new FileInputStream("Games\\"+fileName); 
            ObjectInputStream in = new ObjectInputStream(file); 
            g = (Game)in.readObject(); 
            g.gameChannel = channel;
            g.guild = guild;
            in.close(); 
            file.close(); 
            System.out.println(fileName+" has been deserialized");
            //
        } catch (IOException ex) { 
            System.out.println("IOException is caught"); 
        } catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException");
		}
		return g;
	}
	
	public static Game loadOnline(String gameID, MessageChannel channel, Guild guild) {
		String fileName = "Game"+gameID+".txt"; // no #
		Game g = null;
		try { 
			InputStream file = null;
			for (Message m : GlobalVars.gamesChannel.getHistory().retrievePast(20).complete()) {
				//System.out.println("content: "+m.getContent());
				//System.out.println("fileName: "+m.getAttachments().get(0).getFileName());
				if (m.getAttachments().size() > 0 && m.getAttachments().get(0).getFileName().contentEquals(fileName)) {
					System.out.println("fileName: "+m.getAttachments().get(0).getFileName());
					//File downloaded = new File(GlobalVars.gpath+fileName);
					file = m.getAttachments().get(0).getInputStream();
					//m.getAttachments().get(0).download(downloaded);
					System.out.println("file updated");
				}
			}
			
           // FileInputStream file = new FileInputStream(GlobalVars.gpath+fileName); 
            ObjectInputStream in = new ObjectInputStream(file); 
            g = (Game)in.readObject(); 
            g.gameChannel = channel;
            g.guild = guild;
            in.close(); 
            file.close(); 
            System.out.println(fileName+" has been deserialized");
            //
        } catch (IOException ex) { 
            System.out.println("IOException is caught"); 
        } catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException");
		}
		return g;
	}
	
	// [COMM] Add a player to game
	public void addPlayer(String playerID, int color) {
		String name = guild.getMemberById(playerID).getEffectiveName();
		
		players.add(new Player(playerCount,playerID,name,color));
		if (color == -1) gameChannel.sendMessage("**"+name+"** joined").queue();
		else {
			gameChannel.sendMessage("**"+name+"** joined as "+Utils.colorBooks[color]).queue();
			availableColors.remove((Integer) color);
		}
		playerCount++;
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
						if (p == startingNewTurnPlayer) {
							startingNewTurnPlayer = p.getNextPlayer();
						}
						// If current player, end their turn
						if (currentPlayer.getPlayerID().contentEquals(playerID) && !turnIsEnding) {
							endTurn();
						}
					}
					return;
				}
			}
		}
	}
	
	public void start(boolean resuming) {
		status = "ingame";
		
		if (!resuming) {
			// Set next players and colors
			for (int i = 0; i < players.size(); i++) {
				players.get(i).setNextPlayer(players.get((i+1) % playerCount));
				if (players.get(i).getColor() == -1) {
					Random random = new Random();
					int index = random.nextInt(availableColors.size());
					players.get(i).setColor(availableColors.get(index));
					availableColors.remove(index);
				}
			}
			
			// Give starting mastery and set first player
			if (mode.contentEquals("2v2") && players.size() > 1) {
				currentPlayer = players.get(1);
				// Give everyone 2 mastery
				for (int i = 0; i < players.size(); i++) {
					updateMastery(players.get(i),2);
				}
			}
			else {
				currentPlayer = players.get(0);
				
				int mastery = 0;
				for (int i = 0; i < players.size(); i++) {
					updateMastery(players.get(i),mastery);
					mastery++;
				}
			}
			startingNewTurnPlayer = currentPlayer;
			currentName = currentPlayer.getEffectiveName();
			
		}
		
		// Special order of embeds (may change with lag?)
		updateEvents(true);
		updateCenterRow(currentPlayer, true);
		updateChampions(true);
		updateInfo(currentPlayer, true);
		if (!resuming) {
			//currentPlayer.draw(5);
			for (Player p : players) {
				p.draw(5);
			}
		}
		updatePlayArea(currentPlayer, true);
		//updateReactionsPlayArea();
	}
	
	// Starts a new turn {simple, set currentName, update displays}
	private void newTurn() {
		System.out.println("[DEBUG LOG/Game.java] Starting new turn of player "+currentPlayer.getNumber());
		currentName = Utils.getName(currentPlayer.getPlayerID(),guild);
		setLinkedCommand(null);
		setLinking(false);
		playAreaPage = 0;
		
		// CHEATS Testing
		//currentPlayer.setShards(99);
		//currentPlayer.setMastery(10);
	
		updateInfo(currentPlayer, false);
		//updateCenterRow(currentPlayer, false); Doesn't need to update every new turn
		updateChampions(false);
		//currentPlayer.draw(5); draw at end instead
		updatePlayArea(currentPlayer, false);
		//updateReactionsInfo();
		//updateReactionsPlayArea();
	}

	public void endTurn() {
		
		// Confirm when cards to play
//		if (currentPlayer.getPlayArea().hasNonPlayed()) {
//			if (!confirmingEnd) {
//				confirmingEnd = true;
//				gameChannel.sendMessage("You have unplayed cards. Are you sure you want to end turn? End again to confirm").queue();
//				return;
//			}
//		}
		
		// Auto focus
		if (currentPlayer.getShards() >= 1 && currentPlayer.canFocus()) useFocus(false);
		
		// Auto exhaust champs
		for (Card champ : currentPlayer.getChampions()) {
			if (!champ.isExhausted()) {
				//champ.setExhausted(true);
				doChampEffect(champ, false);
			}
		}
		
		// Clearing page arrows
		if (currentPlayer.getPlayArea().getSize() > CARDS_PER_PAGE) {
			gameChannel.removeReactionById(embedIDs[1], GlobalVars.emojis.get("arrow_backward")).queue();
			gameChannel.removeReactionById(embedIDs[1], GlobalVars.emojis.get("arrow_forward")).queue();
		}
		
		System.out.println("[DEBUG LOG/Game.java] Auto attacking");
		// Use up remaining power on next player
		if (currentPlayer.getPower() > 0) {
			if (mode.contentEquals("2v2")) {
				int opponentID = (currentPlayer.getNumber()+2) % 4;
				if (!players.get(opponentID).isDead()) attackPlayer(players.get(opponentID),currentPlayer.getPower(), false);
				else attackPlayer(players.get(getTeammate(opponentID)),currentPlayer.getPower(), false);
			}
			else attackPlayer(currentPlayer.getNextPlayer(), currentPlayer.getPower(), false);
		}
		
		System.out.println("[DEBUG LOG/Game.java] Started endTurn for player "+currentPlayer.getNumber());
		turnIsEnding = true;

		// End turn restrictions would go here
		
		System.out.println("[DEBUG LOG/Game.java] Before clearing messages");
		
		// Mass deletes
		clearMessages();
		
		System.out.println("[DEBUG LOG/Game.java] After clearing messages");
		
		int drawCount = 5;
		if (damageDealtThisTurn >= 10 && currentPlayer.getPlayArea().has("The Heart of Nothing")) drawCount += 3;
		damageDealtThisTurn = 0;
		
		// Set cards in PlayArea to not played for next time
		for (int i = 0; i < currentPlayer.getPlayArea().getSize(); i++) {
			Card c = currentPlayer.getPlayArea().getCard(i);
			c.setPlayed(false);
			if (!c.asMerc() && !c.getType().contentEquals("champ") && !c.toStringHand().contentEquals("*[Banished]*")) {
				currentPlayer.getDiscardPile().add(c);
			}
		}
		
		// Reset champions exhaust
		for (Card c : currentPlayer.getChampions()) {
			c.setExhausted(false);
		}
		
		// Clear resources and play area
		currentPlayer.endOfTurnReset();
		chooses.clear();
		revives.clear();
		confirmingEnd = false;
		infoNeedsUpdate = true;
		// Draw at end of turn, then calculate shield
		currentPlayer.draw(drawCount);
		//currentPlayer.calculateShield();
		
		System.out.println("[DEBUG LOG/Game.java] Before next player");
		
		if (!status.contentEquals("over")) {
			determineNextPlayer();
		}
	}
	
	public void determineNextPlayer() {
		// Calculate next player
		System.out.println("[DEBUG LOG/Game.java] Current player number before getting next: "+currentPlayer.getNumber());

		currentPlayer = currentPlayer.getNextPlayer(); // Method already deals with quit
		if (currentPlayer == startingNewTurnPlayer) turnNumber++;
		
		// Player is alive 
		if (!currentPlayer.isDead() && !currentPlayer.hasQuit()) { 
			System.out.println("[DEBUG LOG/Game.java] Next player is not dead and hasn't quit: "+currentPlayer.getNumber());
			turnIsEnding = false;
			newTurn();
		// Player is dead or quit
		} else {
			if (!checkIfGameIsOver()) {
				System.out.println("[DEBUG LOG/Game.java] Player is dead or quit, game is not over so skipping");
				//endTurn();
				determineNextPlayer();
			}
		}
	}

	private void death(Player p) {
		System.out.println("[DEBUG LOG/Game.java] Player "+p.getNumber()+" died");
		gameChannel.sendMessage("**"+p.getEffectiveName()+"** died").queueAfter(1000,TimeUnit.MILLISECONDS);
		p.setDead(true);
		
		checkIfGameIsOver();
		
//		p.discardAllCardsNotPlayed();
//		
//		// Calculates death message
//		String deathMessage = "";
//		if (p.has("Artifact")) {
//			if (!p.isUnderground()) {
//				deathMessage = "Fortunately, they had an artifact above ground so the villagers were able to recover the treasure";
//			} else {
//				deathMessage = "They perished underground so all treasure was lost";
//			}
//		} else {
//			deathMessage = "They perished without an artifact so all treasure was lost";
//		}
//		gameChannel.sendMessage(":skull: **"+getName(p)+"** died in the dungeon.\n"+deathMessage).queueAfter(dragonAttackingDelay+2000, TimeUnit.MILLISECONDS);
	}
	
	public void gameOver(Player winner) {
		System.out.println("[DEBUG LOG/Game.java] Game is over");
		status = "over";
		
		gameChannel.sendMessage("**"+winner.getEffectiveName()+"** won!").queueAfter(2500,TimeUnit.MILLISECONDS);
		GlobalVars.remove(this);
	}
	
	// 2v2
	public void gameOver(Player winner1, Player winner2) {
		System.out.println("[DEBUG LOG/Game.java] Game is over");
		status = "over";
		
		gameChannel.sendMessage("**"+winner1.getEffectiveName()+"** and **"+winner2.getEffectiveName()+"** won!").queueAfter(2000,TimeUnit.MILLISECONDS);
		GlobalVars.remove(this);
	}
	
	public void playCard(int cardNumber, boolean isLast) {
		Card c = currentPlayer.getPlayArea().getCard(cardNumber);
		playCardSimple(c);
		//currentPlayer.getDiscardPile().add(c);
		System.out.println("[DEBUG LOG/Game.java] "+currentName+" played "+c.getName());
		//if (isLast) updateICP();
	}
	
	private void doCardEffects(Card c) {
		if (c == null) return;
		
		updateShards(currentPlayer,c.getShards());
		updateMastery(currentPlayer, c.getMastery());
		updateHealth(currentPlayer, c.getHealth());
		updatePower(currentPlayer, c.getPower());
		currentPlayer.updateBanish(c.getBanish());
		
		// Draw last (not sure if required)
		currentPlayer.draw(c.getDraw());
		
		// Masteries
		if (c.getMasteries() != null) {
			int mLevel = currentPlayer.getMastery();
			for (Mastery m : c.getMasteries()) {
				if (mLevel >= m.getRequirement()) {
					currentPlayer.updateResource(m.getReward(), m.getAmount());
//					if (m.isUnique()) {
//						// Unique reward
//					}
				}
			}
		}
		
		// Champions
		if (c.getType().contentEquals("champ")) {
			currentPlayer.getChampions().add(c);
			infoNeedsUpdate = true;
			// Revive Praetorian-01
			Card prae = currentPlayer.getDiscardPile().remove("Praetorian-01");
			if (prae != null) currentPlayer.getPlayArea().add(prae);
			
			addEvent("``"+currentName+"`` played **"+c.getName()+"** :crossed_swords:",false);
		}
		
		// Perks
		Perk pk = c.getPerk();
		if (pk != null) {
			if (c.getColor() == 0 && currentPlayer.countGroup(0, null, currentPlayer.getDiscardPile().getCards()) >= 1) {
				currentPlayer.updateResource(pk.getReward(),pk.getAmount());
				if (c.isUnique()) {
					if (c.getName().contentEquals("Scion of Nothingness")) {
						currentPlayer.updateResource(3,2*currentPlayer.countGroup(0, null, currentPlayer.getDiscardPile().getCards()));
					}
				}
			}
			if (c.getColor() == 1 && currentPlayer.hasOneFromEach()) {
				currentPlayer.updateResource(pk.getReward(),pk.getAmount());
				if (c.isUnique()) {
					// No cards yet
				}
			}
			if (c.getColor() == 2 && currentPlayer.countGroup(2, currentPlayer.getPlayArea().getCards()) >= 2) {
				currentPlayer.updateResource(pk.getReward(),pk.getAmount());
				if (c.isUnique()) {
					if (c.getName().contentEquals("Thorn Zealot")) {
						chooses.add(c.getName());
						choosePrompt();
					}
				}
			}
			if (c.getColor() == 3 && currentPlayer.countGroup(-1, "champ", currentPlayer.getChampions()) >= 1) {
				currentPlayer.updateResource(pk.getReward(),pk.getAmount());
				if (c.isUnique()) {
					if (c.getName().contentEquals("Venator of the Wastes")) {
						chooses.add(c.getName());
						choosePrompt();
					}
				}
			}
		}
		
		// Unique Cards
		if (c.isUnique()) {
			String cn = c.getName();
			Player cp = currentPlayer;
			if (c.isRelic()) {
				if (cn.contentEquals("The World Piercer")) {
					if (cp.getMastery() >= 20) {
						cp.reviveAll("merc");
					} else addRevive("merc");
				} else if (cn.contentEquals("Terminal Crescents")) {
					if (cp.getMastery() >= 20) updatePower(cp, cp.getMastery());
					else {
						if (cp.getMastery() % 2 == 0) updatePower(cp,cp.getMastery()/2);
						else updatePower(cp,(cp.getMastery()/2)+1);
					}
				} else if (cn.contentEquals("Entropic Talons")) {
					cp.setUsingTalons(true);
					if (cp.getMastery() >= 20) updateHealth(cp, 10);
				} else if (cn.contentEquals("Panconscious Crown")) {
					if (cp.getMastery() >= 20 && 
						currentPlayer.countGroup(2, currentPlayer.getPlayArea().getCards()) >= 2) updateHealth(cp, 50);
				}
				return;
			}
			if (cn.contentEquals("Furrowing Elemental")) {
				if (cp.getHealth() == 50) updatePower(cp,6);
			} else if (cn.contentEquals("The Lost")) {
				if (cp.getColor() == 0) cp.updateBanish(1);
			} else if (cn.contentEquals("Mainframe Abbot")) {
				if (cp.getColor() == 1) updateMastery(cp,1);
			} else if (cn.contentEquals("Hounds of Volos")) {
				if (cp.getColor() == 2) updatePower(cp,5);
			} else if (cn.contentEquals("Cloud Oracles")) {
				if (cp.hasHighestMastery(players)) updateShards(cp,2);
			} else if (cn.contentEquals("Ghostwillow Avenger")) {
				if (cp.getMastery() >= 15) {
					for (Player p : players) {
						if (p != currentPlayer && p.getNumber() != getTeammate(currentPlayer.getNumber())) {
							p.clearChampions(); 
						}
					}
					infoNeedsUpdate = true;
					addEvent("``"+currentName+"`` killed all enemy champions",true);
				}
			} else if (cn.contentEquals("Portal Monk")) {
				cp.updateFreeSelect(1);
			} else if (cn.contentEquals("Korvus Legionnaire")) {
				addRevive("champ");
			} else if (cn.contentEquals("Shadebound Sentry")) {
				addRevive("merc");
			} 
			// Choose cards
			if (cn.contentEquals("Ojas, Genesis Druid")) {
				chooses.add(c.getName());
				choosePrompt();
			}
		}
	}
	
	// Exhaust
	public void doChampEffect(Card c, boolean isLast) {
		if (c.isExhausted() || c.getOwner() != currentPlayer) return;
		
		String cn = c.getName();
		Player cp = currentPlayer;
		int m = cp.getMastery();
		if (cn.contentEquals("Optio Crusher")) {
			updatePower(cp,3);
			if (m >= 10) updatePower(cp,2);
		} else if (cn.contentEquals("Numeri Drones")) {
			updateShards(cp,1);
			cp.updateInstantSelect(1);
		} else if (cn.contentEquals("Systema A.I.")) {
			updateMastery(cp,1);
			if (m >= 19) cp.draw(2);
		} else if (cn.contentEquals("Evokatus")) {
			updatePower(cp,cp.countGroup(3, "champ", cp.getChampions()));
		} else if (cn.contentEquals("Primus Pilus")) {
			if (cp.countGroup(3, "champ", cp.getChampions()) >= 3) cp.draw(2);
		} else if (cn.contentEquals("Additri, Gaiamancer")) {
			updatePower(cp,2 + 2*(cp.countGroup(2, cp.getPlayArea().getCards())));
		} else if (cn.contentEquals("Fao Cu'tul, The Formless")) {
			updatePower(cp,2);
			if (cp.getMastery() >= 20) updatePower(cp,cp.getPower());
		} else if (cn.contentEquals("Drakonarius")) {
			updatePower(cp,6);
		} else if (cn.contentEquals("Li Hin, The Shattered")) {
			updatePower(cp,1);
		} else if (cn.contentEquals("Raidian, Cloud Master")) {
			cp.draw(1);
		} else if (cn.contentEquals("Ru Bo Vai, The Transcendant")) {
			updatePower(cp,4);
		} else if (cn.contentEquals("Ferrata Guard")) {
			updateShards(cp,cp.countGroup(3, "champ", cp.getChampions()));
		} else if (cn.contentEquals("Axia")) {
			updatePower(cp,7);
		} else if (cn.contentEquals("Giga, Source Adept")) {
			if (cp.hasOneFromEach()) updateMastery(cp,3);
		} else if (cn.contentEquals("Taur, Arachpriest")) {
			chooses.add(c.getName());
			choosePrompt();
		} else if (cn.contentEquals("General Decurion")) {
			updateShards(cp,3);
			if (cp.getMastery() >= 20) {
				for (Card card : cp.getPlayArea().getCards()) {
					if (card.getColor() == 3 && !card.getType().contentEquals("champ")) {
						doCardEffects(card);
					}
				}
				cp.setUsingDecurion(true);
			}
		} else if (cn.contentEquals("Zen Chi Set, Godkiller")) {
			updatePower(cp,3);
			addRevive("red");
		}
		c.setExhausted(true);
		
		if (isLast) updateICP(false, false); // don't need to update play area reactions
	}
	
	// Always isLast becuase no chain?
	public void selectCard(int cardNumber, boolean asMerc, boolean isLast) {
		System.out.println("Selecting card num:"+cardNumber);
		Card c = centerRow[cardNumber];
		
		int cost = getCost(c);
		
		if (currentPlayer.getShards() >= cost || (currentPlayer.getFreeSelect() > 0 && cost <= 6)) {
			// Portal Monk
			boolean usedFreeSelect = false;
			if (currentPlayer.getFreeSelect() > 0 && cost <= 6) {
				currentPlayer.updateFreeSelect(-1);
				usedFreeSelect = true;
			} else {
				updateShards(currentPlayer,-1*cost);
			}
			// Numeri Drones *portal monk overrides this NOT *overrides portal monk's put into hand
			if (usedFreeSelect == false && currentPlayer.getInstantSelect() > 0 && c.getColor() == 3 && c.getType().contentEquals("champ")) {
				//currentPlayer.getChampions().add(c);
				currentPlayer.updateInstantSelect(-1);
				//playCardSimple(c); doesn't reset played if it goes directly into champions
				//doCardEffects(c);
				
				currentPlayer.getPlayArea().add(c);
				// Play it immediately
				playCardSimple(c);
			} else {
				if (asMerc && c.getType().contentEquals("merc")) { // Merc
					currentPlayer.getPlayArea().add(c);
					//doCardEffects(c);
					//c.setPlayed(true);	
					c.setAsMerc(true);
					playCardSimple(c);
				} else if (usedFreeSelect && currentPlayer.getMastery() >= 15) { // Portal monk into hand
					currentPlayer.getPlayArea().add(c);
					// Play it immediately
					playCardSimple(c);
				} else {
					currentPlayer.getDiscardPile().add(c); // Regular
				}
			}
			if (c.getType().contentEquals("champ")) c.setOwner(currentPlayer); // Set owner of champ
			centerRow[cardNumber] = mainDeck.getNext(true);
			mainDeck.removeTop();
					
			// Add appropriate event description
			if (asMerc) addEvent("``"+currentName+"`` selected **"+c.getName()+"** as :dagger:",false);
			else addEvent("``"+currentName+"`` selected **"+c.getName()+"**",false);
			addHistory(Utils.shorten(c.getName().toUpperCase(),HISTORY_NAME_LENGTH));
			
			if (usedFreeSelect && currentPlayer.getMastery() >= 15) {
				addEvent("``"+currentName+"`` played **"+c.getName()+"** using Portal Monk",false);
				if (isLast) updatePlayArea(currentPlayer, false); // Update to show that it was played automatically
			}
			
			System.out.println("[DEBUG LOG/Game.java] "+currentName+" selected "+c.getName());
		}
		if (isLast) {
			if (asMerc || c.getColor() == 0) updatePlayArea(currentPlayer, false);
			updateCenterRow(currentPlayer, false);
			updateInfo(currentPlayer, false);
			updateChampions(false);
			updateEvents(false);
		}
	}
	
	public void selectRelic(int choice) {
		System.out.println("[DEBUG LOG/Game.java] Choosing relic "+choice);
		if (!currentPlayer.hasRelic() && currentPlayer.getMastery() >= 10 && choice < 3 && choice > 0) {
			// Different modes?
			int relicNum = (2*currentPlayer.getColor())+choice-1;
			// 0 2 4 6
			Card relic = GlobalVars.relics[relicNum];
			currentPlayer.setRelic(relic); // implicitly changes hasRelic
			// Set owner
			if (relic.getType().contentEquals("champ")) relic.setOwner(currentPlayer);
			addEvent("``"+currentName+"`` chose :trophy: **"+relic.getName()+"**",true);
			addHistory(Utils.shorten(relic.getName().toUpperCase(),HISTORY_NAME_LENGTH));
			currentPlayer.getDiscardPile().add(relic);
		}
	}
	
	public void attackPlayer(int color, boolean update) {
		attackPlayer(getPlayerByColor(color), currentPlayer.getPower(), update);
	}
	
	public void attackPlayer(int color, int power, boolean update) {
		attackPlayer(getPlayerByColor(color), power, update);
	}
	
	public void attackPlayer(Player p, int power) {
		attackPlayer(p, power, true);
	}
	
	public void attackPlayer(Player p, int power, boolean update) {
		if (p.isDead()) return; // can attack quit players?
		Card zetta = p.getZetta();
		if (zetta != null) {
			if (attackChamp(zetta)) { // Tries to kill Zetta with all power first
				power -= zetta.getHealth();
				// Continue to attack player after Zetta dies
			} else return;
		}
		System.out.println("[DEBUG LOG/Game.java] Attacking player: "+p.getEffectiveName()+" with power: "+power);
		damageDealtThisTurn = 0; // To prevent splitting damage and getting draw TODO
		
		if (power > 0 && power <= currentPlayer.getPower() && currentPlayer != p && (!mode.contentEquals("2v2") || checkOpponent(currentPlayer.getNumber(),p.getNumber()))) {
			int totalDamage = power;
			if (!currentPlayer.hasChamp("Ru Bo Vai, The Transcendant") || !(currentPlayer.getMastery() >= 10)) {
				if (p.getShield() == 0) p.calculateShield(); // Calculate shield now in case mastery/shield updated
				totalDamage -= p.getShield();
			}
			if (totalDamage < 0) totalDamage = 0;
			damageDealtThisTurn += totalDamage;
			int excessiveDamage = updateHealth(p,-1*totalDamage);
			
			int powerSpent = (-1*power)+excessiveDamage;
			if (p.getShield() > 0) addEvent("``"+currentName+"`` hit "+p.getEffectiveName()+" for **"+(totalDamage-excessiveDamage)+"** :boom: | **"+p.getShield()+"** :shield:",true);
			else addEvent("``"+currentName+"`` hit "+p.getEffectiveName()+" for **"+(totalDamage-excessiveDamage)+"** :boom:",true);

			updatePower(currentPlayer,powerSpent);
			if (excessiveDamage > 0 && mode.contentEquals("2v2")) {
				System.out.println("[DEBUG LOG/Game.java] Attacking player with excessiveDamage: "+excessiveDamage);
				attackPlayer(players.get(getTeammate(p.getNumber())), excessiveDamage, update);
			}
		}
		
		if (update) {
			updateChampions(false);
			updateInfo(currentPlayer, false);
		}
	}
	
	public void attackChamp(int champ) {
		attackChamp(getChamp(champ));
	}
	
	// Return killed or not
	public boolean attackChamp(Card c) {
		System.out.println("[DEBUG LOG/Game.java] Attacking champ: "+c.getName());
		
		int life = getLife(c);
		
		if (c.getOwner() != currentPlayer && currentPlayer.getPower() >= life) {
			if (c.isUnique()) {
				if (c.getName().contentEquals("Li Hin, The Shattered")) return false; // Can't attack this card
				if (c.getName().contentEquals("Raidian, Cloud Master")) {
					if (currentPlayer.getMastery() < c.getOwner().getMastery()) return false;
				}
				if (c.getName().contentEquals("Drakonarius")) {
					if (c.getOwner().hasChamp("General Decurion")) return false;
				}
			}
			
			if (!c.getName().contentEquals("Zetta, The Encryptor") && c.getOwner().hasChamp("Zetta, The Encryptor")) return false; // Can't atk other champs when Zetta is up
			
			System.out.println("Passed Attacking Restrictions");
			updatePower(currentPlayer,-1*life);
			killChamp(c);
//			c.getOwner().getDiscardPile().add(c);
//			c.getOwner().getChampions().remove(c);
//			c.setExhausted(false);
//			addEvent("``"+currentName+"`` killed "+c.getOwner().getEffectiveName()+"'s **"+c.getName()+"**",true);
//			addHistory("KILL "+Utils.shorten(c.getName().toUpperCase()));
			infoNeedsUpdate = true;
			updateInfo(currentPlayer,false);
			updateChampions(false);
			return true;
		}
		return false;
	}
	
	private void killChamp(Card c) {
		if (c == null) return;
		c.getOwner().getDiscardPile().add(c);
		c.getOwner().getChampions().remove(c);
		infoNeedsUpdate = true;
		c.setExhausted(false);
		addEvent("``"+currentName+"`` killed "+c.getOwner().getEffectiveName()+"'s **"+c.getName()+"**",true);
		addHistory("KILL "+Utils.shorten(c.getName().toUpperCase(),HISTORY_NAME_LENGTH));
	}
	
	private void choosePrompt() {
		for (int i = 0; i < chooses.size(); i++) {
			String prompt = "**[Choose]** ";
			// Find the correct text to display
			if (chooses.get(i).contentEquals("Taur, Arachpriest")) {
				prompt += "a :green_book: non :crossed_swords: to play again";
			} else if (chooses.get(i).contentEquals("Ojas, Genesis Druid")) {
				prompt += "a non :crossed_swords: to play again | **20** :star: = Play it again";
			} else if (chooses.get(i).contentEquals("Thorn Zealot")) {
				prompt += "a :crossed_swords: to destroy";
			} else if (chooses.get(i).contentEquals("Venator of the Wastes")) {
				prompt += "a player to lose **2** :star:";
			} else if (chooses.get(i).contentEquals("RELIC")) {
				prompt += "a relic to recruit (**1** or **2**)";
			} 
			gameChannel.sendMessage(prompt).queue();
		}
	}
	
	public void addChoiceReactions(String id) {
		String choosePrompt = chooses.get(chooses.size()-1);
		if (choosePrompt.contentEquals("Thorn Zealot")) {
			int count = 1;
			for (Player p : players) {
				// Find players champions
				for (@SuppressWarnings("unused") Card champ : p.getChampions()) {
					if (p != currentPlayer) gameChannel.addReactionById(id, Utils.numToNumEmoji[count]).queue();
					count++;
				}
			}
		} else if (choosePrompt.contentEquals("Venator of the Wastes")) {
			for (Player p : players) {
				if (p != currentPlayer) gameChannel.addReactionById(id, GlobalVars.emojis.get("book "+p.getColor())).queue();
			}
		} else if (choosePrompt.contentEquals("RELIC")) {
			gameChannel.addReactionById(id, Utils.numToNumEmoji[1]).queue();
			gameChannel.addReactionById(id, Utils.numToNumEmoji[2]).queue();
		}
	}
	
	public void doChampEffectAll() {
		for (Card c : currentPlayer.getChampions()) {
			doChampEffect(c,false);
		}
		updateICP();
	}
	
	public int updateHealth(Player p, int n) {
		if (!p.isDead()) {
			p.updateHealth(n);
			if (p.getUsingTalons()) updatePower(p,n);
			if (p.getHealth() > 50) {
				p.setHealth(50);
			} else if (p.getHealth() <= 0) {
				int prevHealth = p.getHealth();
				p.setHealth(0);
				death(p);
				return -1*prevHealth;
			}
		}
		return 0;
	}
	
	public void updateMastery(Player p, int n) {
		p.updateMastery(n);
		if (p.getMastery() >= 10 && !p.hasRelic() && !Utils.has(chooses, "RELIC")) {
			promptRelic(p.getColor());
		}
		if (p.getMastery() < 0) p.setMastery(0);
		if (p.getMastery() > 30) p.setMastery(30);
	}
	
	public void updateShards(Player p, int n) {
		int prevShards = p.getShards();
		p.updateShards(n);
		if (prevShards < 1 && n > 0 && p.canFocus() || prevShards > 0 && n < 0 && p.canFocus()) {
			infoNeedsUpdate = true;
		}
	}
	
	public void updatePower(Player p, int n) {
		p.updatePower(n);
		if (n != 0) { // Consider checking if there are champions on the board
			infoNeedsUpdate = true;
		}
	}
	
	public void addRevive(String cardType) {
		if (revives.size() == 0) { // Only display if it is first
			revives.add(cardType);
			displayDeck(currentPlayer);
		} else revives.add(cardType);
	}
	
	public boolean useFocus() {
		return useFocus(true);
	}
	
	public boolean useFocus(boolean update) {
		if (currentPlayer.canFocus() && currentPlayer.getShards() >= 1) {
			updateMastery(currentPlayer,1);
			updateShards(currentPlayer,-1);
			currentPlayer.setFocus(false);
			addEvent("``"+currentName+"`` used Focus to gain **1** :star:",true);
			
			if (update) {
				gameChannel.removeReactionById(embedIDs[0], "⚛️").queue();
				updateChampions(false);
				updateReactionsCenterRow(); // shards changed
				updateInfo(currentPlayer, false);
			}
			return true;
		}
		return false;
	}

	public boolean banish(String cardName) {
		if (currentPlayer.getBanish() > 0) { 
			Card c = currentPlayer.banish(cardName);
			if (c != null) {
				currentPlayer.updateBanish(-1);
				addEvent("``"+currentName+"`` banished **"+c.getName()+"**",true);
				updateInfo(currentPlayer, false);
				updatePlayArea(currentPlayer, false);
				return true;
			}
		} 
		return false;
		
	}
	
	public boolean revive(String cardName) {
		if (revives.size() > 0) { 
			Card c = currentPlayer.revive(cardName,revives); // Removes from revives in Player class
			if (c != null) {
				playCardSimple(c);
				addEvent("``"+currentName+"`` revived and played **"+c.getName()+"**",true);
				updateICP();
				return true;
			}
		}
		return false;
	}

	public void choose(String choice) {
		System.out.println("Choice: "+choice);
		for (int i = 0; i < chooses.size(); i++) {
			if (chooses.get(i).contentEquals("RELIC")) { // 1 or 2
				selectRelic(Integer.parseInt(choice));
				chooses.remove(i);
				break;
			} else if (chooses.get(i).contentEquals("Thorn Zealot")) { // Number
				if (Utils.isInt(choice)) {
					killChamp(getChamp(Integer.parseInt(choice))); // infoNeedsUpdate is programmed
					chooses.remove(i);
					break;
				}
			} else if (chooses.get(i).contentEquals("Venator of the Wastes")) { // R, G, B, O
				for (Player p : players) {
					if (p.getColor() == Utils.getColorFromString(choice)) {
						updateMastery(p,-2);
						chooses.remove(i);
						break;
					}
				}
			} else if (chooses.get(i).contentEquals("Taur, Arachpriest")) { // Card name
				Card c = currentPlayer.getPlayArea().get(choice); // special get
				if (c != null && c.getColor() == 2 && !c.getType().contentEquals("champ")) {
					if (copyEffect(currentPlayer.getPlayArea().get(choice))) {
						chooses.remove(i);
						break;
					}
				}
			} else if (chooses.get(i).contentEquals("Ojas, Genesis Druid")) { 
				Card c = currentPlayer.getPlayArea().get(choice); // special get
				if (c != null && !c.getType().contentEquals("champ")) {
					if (copyEffect(currentPlayer.getPlayArea().get(choice))) {
						if (currentPlayer.getMastery() >= 20) copyEffect(currentPlayer.getPlayArea().get(choice));
						chooses.remove(i);
						break;
					}
				}
			}
		}
		
		updatePlayArea(currentPlayer, false);
		updateChampions(false);
		updateReactionsCenterRow();
		updateInfo(currentPlayer, false);
		
	}
	
	public void updateInfo(Player p, boolean isFirst) {
		if (p.isDead()) {
			return;
		}
		System.out.println("[DEBUG LOG/Game.java] Updating player info");
		
		// Build embed with all stats
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(p.getActualColor());
		embed.setTitle("**"+p.getEffectiveName()+"**'s Information");
//		embed.addField("Health :green_heart:","``"+p.getHealth()+"``", true);
//		if (p.canFocus()) {
//			embed.addField("Mastery(+) :star:","``"+p.getMastery()+"``", true);
//		} else {
//			embed.addField("Mastery :star:","``"+p.getMastery()+"``", true);
//		}
		embed.addField("**Shards** :diamond_shape_with_a_dot_inside:","``"+p.getShards()+"``",true);
		embed.addField("**Power** :boom:","``"+p.getPower()+"``",true);
		if (p.getBanish() > 0) {
			embed.addField("**Banishes** :wastebasket:","``"+p.getBanish()+"``",true);
		}
		
		// Set history as footer
		String history = "";
		int numToDisplay = p.getHistory().size()-1;
		if (numToDisplay > FOOTER_SIZE-1) numToDisplay = FOOTER_SIZE-1;
		for (int i = numToDisplay; i >= 0; i--) {
			int prefixSize = p.getHistory().get(i).split(" ")[0].length();
			history += p.getHistory().get(i).substring(prefixSize) + " / ";
		}
		embed.setFooter(history, null);
		
		if (isFirst) {
			gameChannel.sendMessage(embed.build()).queueAfter(3000, TimeUnit.MILLISECONDS);
		} else {
			gameChannel.editMessageById(embedIDs[0], embed.build()).queue();
			updateReactionsInfo(); 
		}
	}
	
	// [REACT] Update Reactions for Player Info
	public void updateReactionsInfo() {
		// Maybe don't clear all the time
		if (infoNeedsUpdate) {
			//System.out.println("update reactions info");
			infoNeedsUpdate = false;
			((TextChannel) gameChannel).clearReactionsById(embedIDs[0]).complete();
			if (currentPlayer.canFocus() && currentPlayer.getShards() >= 1) {
				gameChannel.addReactionById(embedIDs[0], GlobalVars.emojis.get("atom")).queue();
			}
			
			if (currentPlayer.getPower() > 0) {
				// Books
				for (int i = 0; i < players.size(); i++) {
					if (checkOpponent(currentPlayer.getNumber(),i)) gameChannel.addReactionById(embedIDs[0], GlobalVars.emojis.get("book "+players.get(i).getColor())).queue();
				}
				
				int count = 1;
				for (Player p : players) {
					// Find players champions
					for (Card champ : p.getChampions()) {
						if (p != currentPlayer && (!mode.contentEquals("2v2") || p.getNumber() != getTeammate(currentPlayer.getNumber())) && currentPlayer.getPower() >= getLife(champ)) gameChannel.addReactionById(embedIDs[0], Utils.numToNumEmoji[count]).queue();
						count++;
					}
				}
			}
		} else {
			System.out.println("no need to update reactions info");
		}
	}
	
	private void updateCenterRow(Player p, boolean isFirst) {
		System.out.println("[DEBUG LOG/Game.java] Updating center row");
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(Color.GRAY);
		embed.setTitle("Center Row");
		
		// Add reserve and rotation cards
		String cards = "";
		String extraEffects = "";
		for (int i = 0; i < 6; i++) {
			cards += "\n"+Utils.numToLetterEmojis[i]+" "+centerRow[i].toString();
			extraEffects += "\n"+Utils.numToLetterEmojis[i]+" "+centerRow[i].getText();
		}
		
		embed.addField("Cards",cards, true);
		embed.addField("Extra Effects",extraEffects, true);
		if (isFirst) {
			gameChannel.sendMessage(embed.build()).queueAfter(1000, TimeUnit.MILLISECONDS);
		} else {
			gameChannel.editMessageById(embedIDs[2], embed.build()).queue();
			updateReactionsCenterRow();
		}
	}
	
	// [REACT] Update Reactions for Center Row
	public void updateReactionsCenterRow() {
		((TextChannel) gameChannel).clearReactionsById(embedIDs[2]).complete();
		// Don't clear ?
		if (status.contentEquals("pregame")) return;
		
		if (currentPlayer.getShards() == 0 && currentPlayer.getFreeSelect() == 0) { // Assumes no 0 cost card. Axia may be an exception TODO
			//((TextChannel) gameChannel).clearReactionsById(embedIDs[2]).complete();
			return;
		}
		
		if (currentPlayer.getFreeSelect() == 0) {
			gameChannel.addReactionById(embedIDs[2], Utils.numToNumEmoji[currentPlayer.getShards()]).queue(); // Displays shards. not 0
		} else {
			gameChannel.addReactionById(embedIDs[2], "🌀").queue();
		}
		
		boolean hasMerc = false;
		for (int i = 0; i < centerRow.length; i++) {
			//gameChannel.removeReactionById(embedIDs[2], Utils.numToNumEmoji[i+1]).queue(); // Preserves order REMOVE FOR NOW
			if ((currentPlayer.getShards() >= getCost(centerRow[i])) || currentPlayer.getFreeSelect() > 0) {
				if (centerRow[i].getType().contentEquals("merc")) hasMerc = true;
				gameChannel.addReactionById(embedIDs[2], Utils.numToLetterEmojis[i]).queue();
			} else {
				//gameChannel.removeReactionById(embedIDs[2], Utils.numToLetterEmojis[i]).queue();
			}
		}
		
		//gameChannel.removeReactionById(embedIDs[2], GlobalVars.emojis.get("dagger")).queue(); // REMOVE FOR NOW
		if (hasMerc) gameChannel.addReactionById(embedIDs[2], GlobalVars.emojis.get("dagger")).queue();
		//else gameChannel.removeReactionById(embedIDs[2], GlobalVars.emojis.get("dagger")).queue();
	}
	
	public void updatePlayArea(Player p, boolean isFirst) {
		updatePlayArea(p, isFirst, true);
	}
	
	public void updatePlayArea(Player p, boolean isFirst, boolean updateReactions) {
		if (status.contentEquals("over")) {
			return;
		}
		
		System.out.println("[DEBUG LOG/Game.java] Updating play area");
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(p.getActualColor());
		embed.setTitle("**"+Utils.getName(p.getPlayerID(),guild)+"**'s Play Area");
		
		// Create cards string
		String cards = "";
		String extraEffects = "";
		int start = 0;
		if (playAreaPage > 0) {
			start = playAreaPage * CARDS_PER_PAGE;
		}
		// for (int i = 0; i < currentPlayer.getPlayArea().getSize(); i++) {
		for (int i = start; i < currentPlayer.getPlayArea().getSize() && i < start+CARDS_PER_PAGE; i++) {
			Card c = currentPlayer.getPlayArea().getCard(i);
			cards += "\n";
			if (c.getText() != "") extraEffects += "\n";
			if (c.isPlayed()) {
				cards += ":white_check_mark: ";
				if (c.getText() != "") extraEffects += ":white_check_mark: ";
			} else if (c.hasPriorityHint()) {
				cards += ":bell: ";
				if (c.getText() != "") extraEffects += ":bell: ";
			} else {
				//cards += Utils.numToEmote[i+1]+" ";
				cards += ":regional_indicator_"+((char)(i+97))+": ";
				if (c.getText() != "") extraEffects += ":regional_indicator_"+((char)(i+97))+": ";
			}
			if (c.asMerc()) cards += c.toStringHandMerc();
			else cards += c.toStringHand();
			if (currentPlayer.willCycle(c.getDraw()) && !c.isPlayed()) cards += " :recycle:"; // shows that cards will cycle
			if (c.getText() != "") extraEffects += c.getText();
		}
		
		if (currentPlayer.willCycle(5)) embed.addField("Cards :recycle:",cards, true);
		else embed.addField("Cards",cards, true);
		if (currentPlayer.countGroup(0, currentPlayer.getDiscardPile().getCards()) > 0) embed.addField("Extra Effects :closed_book:",extraEffects, true);
		else embed.addField("Extra Effects",extraEffects, true);

		embed.setFooter("Next Turn: "+currentPlayer.getNextPlayer().getEffectiveName(), null);
		
		if (isFirst) {
			gameChannel.sendMessage(embed.build()).queueAfter(4000, TimeUnit.MILLISECONDS);
			//updateReactionsPlayArea();
		} else {
			gameChannel.editMessageById(embedIDs[1], embed.build()).queue();
			if (updateReactions) updateReactionsPlayArea();
		}
	}
	
	public void clearReactionsPlayArea() {
		((TextChannel) gameChannel).clearReactionsById(embedIDs[1]).complete();
		gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("card_box")).queue();
		gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("checkered_flag")).queue();
		if (currentPlayer.getPlayArea().getSize() > CARDS_PER_PAGE) {
			gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("arrow_backward")).queue();
			gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("arrow_forward")).queue();
		} 
	}
	
	public void updateReactionsPlayArea() {
		boolean addFastForward = true;
		for (int i = 0; i < currentPlayer.getPlayArea().getSize(); i++) {
			if (!currentPlayer.getPlayArea().getCard(i).isPlayed()) {
				if (addFastForward) {
					gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("fast_forward")).queue();
					addFastForward = false;
				}
				gameChannel.addReactionById(embedIDs[1], Utils.numToLetterEmojis[i]).queue();
			} else {
				gameChannel.removeReactionById(embedIDs[1], Utils.numToLetterEmojis[i]).queue();
			}
		}
		if (addFastForward) {
			gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("checkered_flag")).queue();
			gameChannel.removeReactionById(embedIDs[1], GlobalVars.emojis.get("fast_forward")).queue();
		}
		else gameChannel.removeReactionById(embedIDs[1], GlobalVars.emojis.get("checkered_flag")).queue();
		gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("card_box")).queue();
		
		if (currentPlayer.getPlayArea().getSize() > CARDS_PER_PAGE) { 
			gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("arrow_backward")).queue();
			gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("arrow_forward")).queue();
		}
//		((TextChannel) gameChannel).clearReactionsById(embedIDs[1]).complete();
//		if (currentPlayer.getPlayArea().hasNonPlayed()) {
//			gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("fast_forward")).queue();
//			//gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("link")).queue();
//			for (int i = 0; i < currentPlayer.getPlayArea().getSize(); i++) {
//				if (!currentPlayer.getPlayArea().getCard(i).isPlayed()) {
//					gameChannel.addReactionById(embedIDs[1], Utils.numToLetterEmojis[i]).queue();
//				}
//			}
//		} else {
//			gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("checkered_flag")).queue();
//		}
//		gameChannel.addReactionById(embedIDs[1], GlobalVars.emojis.get("card_box")).queue();
	}
	
	public void updateChampions(boolean isFirst) {
		updateChampions(isFirst, true);
	}
	
	public void updateChampions(boolean isFirst, boolean updateReactions) {
		System.out.println("[DEBUG LOG/Game.java] Updating champions");
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Players & Champions");
		embed.setColor(currentPlayer.getActualColor());
		
		// Add strings
		int count = 1;
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			String championsString = "";
			for (Card c : p.getChampions()) {
				championsString += "\n"+Utils.numToEmoji[count]+" "+c.toStringChamp()+" "+c.getText();
				count++;
			}
			String name = "";
			if (!p.isDead()) name = "``"+p.getEffectiveName()+"`` ";
			else name = "~~``"+p.getEffectiveName()+"``~~ ";
			if (mode.contentEquals("2v2")) name += "("+Utils.letters[p.getNumber()]+") ";
			String text = name + "**"+p.getHealth()+"** "+p.getHeartString()+" **"+p.getMastery()+"** :star:";
			
			if (p == currentPlayer) {
				if (p.canFocus()) embed.addField(":atom:"+text,championsString, false);
				else embed.addField(":white_check_mark:"+text,championsString, false);
			}
			else embed.addField(text,championsString, false);
		}
		
		if (isFirst) {
			gameChannel.sendMessage(embed.build()).queueAfter(2000, TimeUnit.MILLISECONDS);
		} else {
			gameChannel.editMessageById(embedIDs[3], embed.build()).queue();
			if (updateReactions) updateReactionsChampions();
		}
	}
	
	// [REACT] Update Reactions for Champions
	public void updateReactionsChampions() {
		((TextChannel) gameChannel).clearReactionsById(embedIDs[3]).complete();

		boolean putExhaustAll = false;
		int count = 1;
		for (Player p : players) {
			// Find players champions
			for (Card champ : p.getChampions()) {
				if (p == currentPlayer) {
					if (!champ.isExhausted()) {
						if (!putExhaustAll) {
							gameChannel.addReactionById(embedIDs[3], GlobalVars.emojis.get("fast_forward")).queue();
							putExhaustAll = true;
						}
						gameChannel.addReactionById(embedIDs[3], Utils.numToNumEmoji[count]).queue();
					}
				}
				count++;
			}
			// Don't need to keep going
			if (p == currentPlayer) break;
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
//		if (isFirst) {
//			embed.setTitle("Events");
//			gameChannel.sendMessage(embed.build()).queueAfter(100, TimeUnit.MILLISECONDS);
//		} else {
//			embed.setTitle(null);
//			// Game breaks if eventMsg and title is ""
//			if (eventMsg.contentEquals("")) {
//				embed.setDescription("Empty");
//			} else {
//				embed.setDescription(eventMsg);
//			}
//			gameChannel.editMessageById(embedIDs[4], embed.build()).queue();
//		}
		embed.setTitle("Events");
		if (!eventMsg.contentEquals("")) {
			embed.setDescription(eventMsg);
		}
		if (isFirst) {
			gameChannel.sendMessage(embed.build()).queueAfter(100, TimeUnit.MILLISECONDS);
		} else {
			gameChannel.editMessageById(embedIDs[4], embed.build()).queue();
		}
	}
	
	public void addEvent(String s, boolean isLast) {
		events.add(s);
		if (events.size() > EVENTS_SIZE) events.remove(0);
		if (isLast) {
			updateEvents(false);
		}
	}
	
	// Adds to history of currentPlayer
	private void addHistory(String s) {
		currentPlayer.getHistory().add("["+turnNumber+"] "+ s);
		//if (currentPlayer.getHistory().size() > 12) currentPlayer.getHistory().remove(0);
		//updatePlayArea(currentPlayer, false);
	}
	
	// For @Player
	public Player getPlayerByID(String playerID) {
		for (int i = 0; i < players.size(); i++) {
			if (playerID.contentEquals(players.get(i).getPlayerID())) {
				return players.get(i);
			}
		}
		return null;
	}
	
	// For color
	public void displayDeck(int color) {
		displayDeck(getPlayerByColor(color));
	}
	
	public void displayDeck(Player p) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("**"+p.getEffectiveName()+"**'s Deck");
		embed.setColor(p.getActualColor());
		String cardsInDeck = "";
		String cardsInDiscard = "";
		String revivesIndicator = "";
		int totalCards = 0;
		
//		for (int i = 0; i < players.size(); i++) {
//			// Find player and sort a temporary version of their deck/discard
//			if (playerID.contentEquals(players.get(i).getPlayerID())) {
				@SuppressWarnings("unchecked")
				ArrayList<Card> tempSortedDeck = (ArrayList<Card>) p.getDeck().getCards().clone();
				// Add cards in hand
				if (currentPlayer != p) {
					for (int j = 0; j < p.getPlayArea().getCards().size(); j++) {
						tempSortedDeck.add(p.getPlayArea().getCard(j));
					}
				}
				Collections.sort(tempSortedDeck);
				
				@SuppressWarnings("unchecked")
				ArrayList<Card> tempSortedDiscard = (ArrayList<Card>) p.getDiscardPile().getCards().clone();
				Collections.sort(tempSortedDiscard);
				
				for (int j = 0; j < tempSortedDeck.size(); j++) {
					cardsInDeck += tempSortedDeck.get(j).getNameInList()+" ";
				}
				for (int j = 0; j < tempSortedDiscard.size(); j++) {
					cardsInDiscard += tempSortedDiscard.get(j).getNameInList()+" ";
				}
				if (currentPlayer == p) totalCards = tempSortedDeck.size() + tempSortedDiscard.size() + p.getPlayArea().getSize();// + players.get(i).getChampions().size();
				else totalCards = tempSortedDeck.size() + tempSortedDiscard.size();// + players.get(i).getChampions().size();
				
				if (revives.size() > 0 && currentPlayer == p) {
					revivesIndicator += " :syringe: any";
					for (String card : revives) {
						revivesIndicator += " "+GlobalVars.reviveEmojis.get(card) + " |";
					}
					revivesIndicator = revivesIndicator.substring(0,revivesIndicator.length()-1);
				}
//					
//				break; 
//			}
//		}
		
		// String formatting
//		if (cardsInDeck.length() >= 2) {
//			cardsInDeck = cardsInDeck.substring(0,cardsInDeck.length()-2);
//		}
//		
//		if (cardsInDiscard.length() >= 2) {
//			cardsInDiscard = cardsInDiscard.substring(0,cardsInDiscard.length()-2);
//			//cardsInDiscard = "*"+cardsInDiscard+"*";
//		}
		
		embed.addField("Deck",cardsInDeck,true);
		embed.addField("Discard Pile"+revivesIndicator,cardsInDiscard,true);
		embed.setFooter(totalCards+" TOTAL CARDS", null);
		gameChannel.sendMessage(embed.build()).queue();
	}
	
	public void sendHistory(User user, String playerID) {
		for (int i = 0; i < players.size(); i++) {
			final Player p = players.get(i);
			if (p.getPlayerID().contentEquals(playerID)) {
				user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
					public void accept(PrivateChannel channel) {
//						EmbedBuilder embed = new EmbedBuilder();
//						embed.setTitle("**"+Utils.getName(p.getPlayerID(),guild)+"**'s History #"+gameChannel.getName());
//						embed.setColor(p.getActualColor());
//						embed.setDescription(p.getHistoryString(0)); // 0 is most recent
					    channel.sendMessage(createHistoryEmbed(p,0).build()).queue(new Consumer<Message>() {
							public void accept(Message message) {
								message.addReaction(GlobalVars.emojis.get("arrow_backward")).queue();
								message.addReaction(GlobalVars.emojis.get("arrow_forward")).queue();
							}
						});
					}
				});
			}
		}
	}
	
	public EmbedBuilder createHistoryEmbed(Player p, int page) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("**"+Utils.getName(p.getPlayerID(),guild)+"**'s History #"+gameChannel.getName()+"\nPage "+(page+1)+"/"+(((p.getHistory().size()-1) / 10) + 1));
		embed.setColor(p.getActualColor());
		embed.setDescription(p.getHistoryString(page)); // 0 is most recent
		embed.setFooter("Player: "+p.getPlayerID()+" Channel: "+gameChannel.getId(), null);
		return embed;
	}
	
	public void setID(String playerID, int id) {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p.getPlayerID().contentEquals(playerID)) {
				System.out.println("Swapping, set id to "+id);
				
				players.get(id).setNumber(p.getNumber());
				p.setNumber(id);
				Player temp = players.get(id);
				players.set(id,p);
				players.set(i, temp);
				return;
			}
		}
	}
	
	public void listPlayers() {
		String playerList = "";
		for (Player p : players) {
			if (mode.contentEquals("2v2")) {
				if (p.getNumber() < 2) playerList += "**[Team 1]** ";
				else playerList += "**[Team 2]** ";
			}
			playerList += Utils.numToLetterEmojis[p.getNumber()] + " ``" + p.getEffectiveName() + "`` " + Utils.colorBooks[p.getColor()] + "\n";
		}
		gameChannel.sendMessage(playerList).queue();
	}
	
	private int getCost(Card c) {
		// Change cost for Axia
		int cost = c.getCost();
		if (c.getName().contentEquals("Axia")) {
			cost -= currentPlayer.countGroup(3, "champ", currentPlayer.getChampions());
		}
		return cost;
	}
	
	private int getLife(Card c) {
		// Change cost for Ferrata
		int life = c.getLife();
		if (c.getOwner().getColor() == 3) {
			life += 2*(c.getOwner().countChamp("Ferrata Guard"));
		}
		return life;
	}
	
	public Player getPlayerByColor(int color) {
		if (color == -1) return null;
		for (Player p : players) {
			if (p.getColor() == color) return p;
		}
		return null;
	}
	
	public int getTeammate(int id) {
		if (id % 2 == 0) return id+1;
		else return id-1;
	}
	
	// opponent = (id+2) % 4;
	// ally = getTeammate(id);
	
	public boolean checkOpponent(int id, int opponentID) {
		//System.out.println("Checking opponent, id: "+id+" opponent id: "+opponentID);
		int actualOpponentID = (id+2) % 4;
		if (mode.contentEquals("2v2")) return (!players.get(opponentID).isDead() && id != opponentID) && ((opponentID == actualOpponentID) || players.get(getTeammate(id)).isDead() || (opponentID == getTeammate(actualOpponentID) && players.get(actualOpponentID).isDead()));
		return (id != opponentID) && !(players.get(opponentID).isDead());
//		if (id == 0) return (opponentID == 2) || players.get(getTeammate(id)).isDead() || (opponentID == 3 && players.get(2).isDead());
//		else if (id == 1) return (opponentID == 3) || players.get(getTeammate(id)).isDead() || (opponentID == 2 && players.get(3).isDead());
//		else if (id == 2) return (opponentID == 0) || players.get(getTeammate(id)).isDead() || (opponentID == 1 && players.get(0).isDead());
//		else if (id == 3) return (opponentID == 1) || players.get(getTeammate(id)).isDead() || (opponentID == 0 && players.get(1).isDead());
//		else return false;
	}
	
	// [COMM] Transfer your adventurer to another player (any turn)
	public void transfer(String oldPlayerID, String newPlayerID) {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p.getPlayerID().contentEquals(oldPlayerID) && !p.hasQuit()) {
				p.setPlayerID(newPlayerID);
				gameChannel.sendMessage("**"+Utils.getName(oldPlayerID, guild)+"** transferred their progress to "+Utils.getName(newPlayerID, guild)).queue();
			}
		}
	}
	
	private void playCardSimple(Card c) {
		doCardEffects(c);
		// Decurion
		if (currentPlayer.getUsingDecurion() && c.getColor() == 3 && !c.getType().contentEquals("champ")) {
			doCardEffects(c);
		}
		c.setPlayed(true);
	}

	private void promptRelic(int color) {
		chooses.add("RELIC");
		choosePrompt();
		String relics = "";
		// int relicNum = (2*currentPlayer.getColor())+choice-1;
		Card r = GlobalVars.relics[2*color];
		relics += "\n:one: "+r.toString()+"\n\t\t\t\t\t"+r.getText();
		r = GlobalVars.relics[(2*color)+1];
		relics += "\n:two: "+r.toString()+"\n\t\t\t\t\t"+r.getText();
		gameChannel.sendMessage(relics).queue();
	}
	
	public void clearMessages() {
		//textChannel.deleteMessagesByIds(messagesToDeleteIDs).queue();
		if (messagesToDeleteIDs.size() != 0) {
			if (messagesToDeleteIDs.size() == 1) {
				gameChannel.deleteMessageById(messagesToDeleteIDs.get(0)).queueAfter(1000, TimeUnit.MILLISECONDS);
			} else ((TextChannel) gameChannel).deleteMessagesByIds(messagesToDeleteIDs).queueAfter(1000, TimeUnit.MILLISECONDS);
			messagesToDeleteIDs.clear();
		}
	}
	
	public void addCard(String name) {
		Card c = GlobalVars.clone(name);
		currentPlayer.getPlayArea().add(c);
		if (c.getType().contentEquals("champ")) c.setOwner(currentPlayer); // for champs
		updatePlayArea(currentPlayer,false);
	}
	
	public Card getChamp(int num) {
		int count = 1;
		for (int i = 0; i < players.size(); i++) {
			for (Card c : players.get(i).getChampions()) {
				if (num == count) return c;
				count++;
			}
		}
		return null;
	}
	
	public boolean checkIfGameIsOver() {
		System.out.println("[DEBUG LOG/Game.java] Checking if game is over");
		if (mode.contentEquals("normal")) {
			int aliveCount = 0;
			Player alivePlayer = null;
			for (int i = 0; i < players.size(); i++) {
				if (!players.get(i).isDead() && !players.get(i).hasQuit()) {
					aliveCount++;
					alivePlayer = players.get(i);
				} 
			}
			if (aliveCount > 1) return false;
			if (!status.contentEquals("over")) {
				gameOver(alivePlayer);
			}
		} else if (mode.contentEquals("2v2")) {
			if ((players.get(0).isDead() || players.get(0).hasQuit()) && (players.get(1).isDead() || players.get(1).hasQuit())) gameOver(players.get(2),players.get(3));
			else if ((players.get(2).isDead() || players.get(2).hasQuit()) && (players.get(3).isDead() || players.get(3).hasQuit())) gameOver(players.get(0),players.get(1));
			return false;
		}
		
		// It is over so run gameOver

		return true;
	}
	
	private boolean copyEffect(Card c) {
		if (c.getName().contentEquals("Taur, Arachpriest") || c.getName().contentEquals("Ojas, Genesis Druid")) return false;
		doCardEffects(c);
		return true;
	}
	
	public void addResource(int type, int amount) {
		currentPlayer.updateResource(type, amount);
	}
	
	public void updateICP() {
		updateICP(true, true);
	}
	
	public void updateICP(boolean updateChampionsReactions, boolean updatePlayAreaReactions) {
		System.out.println("Updating ICP");
		updatePlayArea(currentPlayer, false, updatePlayAreaReactions);
		updateReactionsCenterRow(); // try this 2nd
		updateChampions(false,updateChampionsReactions);
		updateInfo(currentPlayer, false);
		//updateReactionsCenterRow();
	}
	
	public int getPlayerCount() {
		return playerCount;
	}
		
	public MessageChannel getGameChannel() {
		return gameChannel;
	}

//	private String getName(Player p) {
//		return guild.getMemberById(p.getPlayerID()).getEffectiveName();
//	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public String getStatus() {
		return status;
	}

	public boolean isFirstBoard() {
		return isFirstBoard;
	}

	public void setFirstBoard(boolean isFirstBoard) {
		this.isFirstBoard = isFirstBoard;
	}

	public ArrayList<String> getChooses() {
		return chooses;
	}

	public void setMustChoose(ArrayList<String> chooses) {
		this.chooses = chooses;
	}
	
	public ArrayList<String> getRevives() {
		return revives;
	}

	public void setRevives(ArrayList<String> revives) {
		this.revives = revives;
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
	
	public void setLinking(boolean linking) {
		this.linking = linking;
	}
	
	public boolean getLinking() {
		return linking;
	}
	
	public void setEmbedID(int index, String value) {
		embedIDs[index] = value;
		//System.out.println(embedIDs[index]);
	}
	
	public String getEmbedID(int index) {
		return embedIDs[index];
	}
	
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
		if (mode.contentEquals("2v2")) {
			String id = gameChannel.sendMessage("**[2v2]** :regional_indicator_a: and :regional_indicator_b: **vs** "
					+ ":regional_indicator_c: and :regional_indicator_d:").complete().getId();
			for (int i = 0; i < 4; i++) {
				gameChannel.addReactionById(id, Utils.numToLetterEmojis[i]).queue();;
			}
		}
	}
	
	public int getPlayAreaPage() {
		return playAreaPage;
	}
	
	// Updates play area with no reaction update
	public void setPlayAreaPage(int playAreaPage) {
		this.playAreaPage = playAreaPage;
		updatePlayArea(currentPlayer, false, false);
	}
	
	// Variables
	public transient MessageChannel gameChannel; // transient so need to update upon resuming
	public transient Guild guild; // transient so need to update upon resuming
	
	public String mode = "normal";
	public String hostID;
	public String status = "pregame";
	public int turnNumber = 0;
	public int playerCount = 0;
	public int votedForEnd = 0;
	public boolean infoNeedsUpdate = false;
	public String linkedCommand = null;
	public boolean linking = false;
	public Player startingNewTurnPlayer;
	
	public ArrayList<Integer> availableColors = new ArrayList<Integer>();
	public ArrayList<Player> players = new ArrayList<Player>();
	public Deck mainDeck;
	public Card[] centerRow = new Card[6];
	public int damageDealtThisTurn = 0;
	//public boolean isEndGame = false;
	public ArrayList<String> events = new ArrayList<String>();
	public ArrayList<String> messagesToDeleteIDs = new ArrayList<String>();
	
	public ArrayList<String> revives = new ArrayList<String>();
	public ArrayList<String> chooses = new ArrayList<String>();
	
	public int playAreaPage = 0;
	
//	public Player p1;
//	public Player p2;
//	public Player p3;
//	public Player p4;
	
	public Player currentPlayer;
	public String currentName;
	public String[] embedIDs = new String[5];
	public boolean isFirstBoard;
	public boolean turnIsEnding;
	public boolean confirmingEnd;

	public final int CARDS_PER_PAGE = 10;
	public final int FOOTER_SIZE = 5;
	public final int EVENTS_SIZE = 12;
	public final int HISTORY_NAME_LENGTH = 9;
}
