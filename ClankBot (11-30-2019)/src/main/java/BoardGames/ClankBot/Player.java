package BoardGames.ClankBot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

public class Player {
	
	// Initial constructor, sets color
	public Player(int number,String playerID,Piece piece) {
		this.number = number;
		this.playerID = playerID;
		this.piece = piece;
		this.health = 10;
		if (number == 0) this.color = Color.RED; 
		else if (number == 1) this.color = Color.BLUE;
		else if (number == 2) this.color = Color.YELLOW; 
		else if (number == 3) this.color = Color.GREEN; 
	}
	
	// Draw cards
	public void draw(int num) {
		for (int i = 0; i < num; i++) {
			// If deck is not empty, remove from deck and add to playArea
			if (deck.hasNext()) {
				playArea.add(deck.getNext());
				deck.removeTop();
			} else {
				// Shuffles discard into deck, clearing discard
				deck.shuffle(discardPile.getCards());
				discardPile.clear();
				playArea.add(deck.getNext());
				deck.removeTop();
			}
		}
		// Sort by name if start of turn indicated by drawing 5
		if (num == 5) {
			Collections.sort(playArea.cards);
		}
	}
	
	// Reset skill, swords, boots, playArea, etc
	public void endOfTurnReset() {
		setSkill(0);
		setSwords(0);
		setBoots(0);
		setTeleports(0);
		setStoppedInCave(false);
		setRunning(false);
		setFlying(false);
		setSwags(0);
		setSearches(0);
		playArea.clear();
	}
	
	// Calculate points for end of game
	public int calculatePoints() {
		int total = 0;
		if (isUnderground()) return 0;
		
		if (isFree) total += 20;
		
		// Calculating total for items
		for (int i = 0; i < inventory.size(); i++) {
			String item = inventory.get(i);
			if (item.startsWith("Artifact")) {
				total += Integer.parseInt(item.substring(8));
			} else if (item.startsWith("MonkeyIdol")) {
				total += 5;
			} else if (item.startsWith("Chalice")) {
				total += 7;
			} else if (item.startsWith("Egg")) {
				total += 3;
			} else if (item.startsWith("Key")) {
				total += 5;
			} else if (item.startsWith("Backpack")) {
				total += 5;
			} else if (item.startsWith("Crown")) {
				total += Integer.parseInt(item.substring(5));
			}
		}
		
		// Calculating total for cards
		for (int i = 0; i < deck.getSize(); i++) {
			total += Utils.calculatePointsForCard(deck.getCard(i), this);
		}
		// No points for play area
		for (int i = 0; i < discardPile.getSize(); i++) {
			total += Utils.calculatePointsForCard(discardPile.getCard(i), this);
		}
		total += gold;
		System.out.println("[DEBUG LOG/Player.java] Total Points: "+total);
		return total;
	}
	
	// Return inventory as a string
	public String getInventoryAsString(boolean wrapAround) {
		String treasures = "";
		int counter = 0;
		for (int i = 0; i < getInventory().size(); i++) {
			String item = getInventory().get(i);
			if (item.startsWith("Artifact")) {
				treasures += ":scroll:**"+item.substring(8)+"** ";
			} else if (item.startsWith("MonkeyIdol")) {
				treasures += ":monkey_face:**5** ";
			} else if (item.startsWith("Key")) {
				treasures += ":key:**5** ";
			} else if (item.startsWith("Backpack")) {
				treasures += ":briefcase:**5** ";
			} else if (item.startsWith("Crown")) {
				treasures += ":crown:**"+item.substring(5)+"** ";
			} else if (item.contentEquals("Chalice")) {
				treasures += ":wine_glass:**7** ";
			} else if (item.contentEquals("Egg")) {
				treasures += ":egg:**3** ";
			}
			counter++;
			if (wrapAround && counter % 3 == 0) {
				treasures += "\n";
			}
		}
		return treasures;
	}
	
	public boolean has(String item) {
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).startsWith(item)) {
				return true;
			}
		}
		return false;
	}
	
	// Has another companion (total 2) in just playArea
	public boolean hasCompanion() {
		int count = 0;
		for (int i = 0; i < playArea.getSize(); i++) {
			if (playArea.getCard(i).isCompanion()) {// && playArea.getCard(i).isPlayed()) {
				count++;
				if (count == 2) return true;
			}
		}
		return false;
	}
	
	// Count number of this item in inventory
	public int count(String item) {
		int count = 0;
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).startsWith(item)) {
				count++;
			}
		}
		return count;
	}
	
	// Trash (only playArea or discard)
	public boolean trash(String cardName) {
		// Assume always better to trash play area, otherwise they would play first
		for (int i = 0; i < playArea.getSize(); i++) {
			if (playArea.getCard(i).getName().contentEquals(cardName)) {
				//playArea.remove(i);
				playArea.getCard(i).setStringInHand("*[Trashed]*");
				if (playArea.getCard(i).isPlayed()) {
					discardPile.remove("trash");
				} else {
					playArea.getCard(i).setPlayed(true);
				}
				return true;
			}
		}
		
		return discardPile.remove(cardName);
	}

	// Discard (only playArea)
	public boolean discard(int num) {		
		if (!playArea.getCard(num).isPlayed()) {
			playArea.getCard(num).setStringInHand("*[Discarded]*");
			discardPile.add(playArea.getCard(num));
			playArea.getCard(num).setPlayed(true);
			return true;
		}
		return false;
	}
	
	// Discards all cards not played (used for dying/escaping players)
	public void discardAllCardsNotPlayed() {
		for (int i = 0; i < playArea.getSize(); i++) {
			if (!playArea.getCard(i).isPlayed()) {
				discardPile.add(playArea.getCard(i));
			}
		}
	}
	
	// Return string of heart
	public String getHeartString() {
		if (number == 0) return ":heart:";
		if (number == 1) return ":blue_heart:";
		if (number == 2) return ":yellow_heart:";
		if (number == 3) return ":green_heart:";
		return null;
	}
	
	// Pickup item
	public void pickup(String item) {
		inventory.add(item);
		if (item.startsWith("Artifact")) {
			setHighestArtifactValue(Integer.parseInt(item.substring(8)));
		}
	}
	
	// Pickup consumable
	public void pickupConsum(String item) {
		consumables.add(item);
	}
	
	// Return history for command
	public String getHistoryString() {
		String s = "";
		for (int i = 0; i < history.size(); i++) {
			s += history.get(i)+"\n";
		}
		return s;
	}
	
	public String getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(int currentRoom) {
		this.currentRoom = currentRoom;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getClankOnBoard() {
		return clankOnBoard;
	}

	public void setClankOnBoard(int clankOnBoard) {
		this.clankOnBoard = clankOnBoard;
	}

	public int getClankInBag() {
		return clankInBag;
	}

	public void setClankInBag(int clankInBag) {
		this.clankInBag = clankInBag;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Deck getDeck() {
		return deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public DiscardPile getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(DiscardPile discardPile) {
		this.discardPile = discardPile;
	}

	public PlayArea getPlayArea() {
		return playArea;
	}

	public void setPlayArea(PlayArea playArea) {
		this.playArea = playArea;
	}

	public int getSkill() {
		return skill;
	}

	public void setSkill(int skill) {
		this.skill = skill;
	}

	public int getBoots() {
		return boots;
	}

	public void setBoots(int boots) {
		this.boots = boots;
	}

	public int getSwords() {
		return swords;
	}

	public void setSwords(int swords) {
		this.swords = swords;
	}
	
	public int getTeleports() {
		return teleports;
	}
	
	public void setTeleports(int teleports) {
		this.teleports = teleports;
	}
	
	public void updateBoots(int n) {
		boots += n;
	}
	public void updateSkill(int n) {
		skill += n;
	}
	public void updateSwords(int n) {
		swords += n;
	}
	public void updateGold(int n) {
		gold += n;
		if (searches > 0 && n > 0) gold += 1*searches;
	}
	public void updateTeleports(int n) {
		teleports += n;
	}
	public void updateClankOnBoard(int n) {
		clankOnBoard += n;
		if (clankOnBoard < 0) {
			clankOnBoard = 0;
		}
	}
	public void updateClankInBag(int n) {
		clankInBag += n;
	}
	public void updateHealth(int n) {
		health += n;
	}
	
	public void insertClank() {
		clankInBag += clankOnBoard;
		clankOnBoard = 0;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isStoppedInCave() {
		return stoppedInCave;
	}

	public void setStoppedInCave(boolean stoppedInCave) {
		this.stoppedInCave = stoppedInCave;
	}

	public ArrayList<String> getInventory() {
		return inventory;
	}

	public void setInventory(ArrayList<String> inventory) {
		this.inventory = inventory;
	}
	
	public ArrayList<String> getConsumables() {
		return consumables;
	}

	public void setConsumables(ArrayList<String> consumables) {
		this.consumables = consumables;
	}
	
	public void remove(String item) {
		inventory.remove(item);
	}

	public boolean isFlying() {
		return isFlying;
	}

	public void setFlying(boolean isFlying) {
		this.isFlying = isFlying;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isUnderground() {
		// Room 16 is cutoff
		return (currentRoom > 15 && currentRoom < 39);
	}

	public boolean isInCave() {
		// specific for map
		return (currentRoom == 6 || currentRoom == 7 || currentRoom == 9 || currentRoom == 13 
				|| currentRoom == 17 || currentRoom == 21 || currentRoom == 22 || currentRoom == 26 
				|| currentRoom == 31 || currentRoom == 38);
	}
	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public boolean getAlreadyPickedUpMonkeyIdol() {
		return alreadyPickedUpMonkeyIdol;
	}

	public void setAlreadyPickedUpMonkeyIdol(boolean alreadyPickedUpMonkeyIdol) {
		this.alreadyPickedUpMonkeyIdol = alreadyPickedUpMonkeyIdol;
	}

	public int getSecretTomes() {
		return secretTomes;
	}

	public void updateSecretTomes(int secretTomes) {
		this.secretTomes += secretTomes;
	}
	
	public int getHighestArtifactValue() {
		return highestArtifactValue;
	}
	
	public void setHighestArtifactValue(int highestArtifactValue) {
		if (highestArtifactValue > this.highestArtifactValue) {
			this.highestArtifactValue = highestArtifactValue;
		}
	}
	
	public boolean getAddedArtifactValue() {
		return addedArtifactValue;
	}
	
	public void setAddedArtifactValue(boolean addedArtifactValue) {
		this.addedArtifactValue = addedArtifactValue;
	}

	public int getSwags() {
		return swags;
	}

	public void setSwags(int swags) {
		this.swags = swags;
	}

	public int getSearches() {
		return searches;
	}

	public void setSearches(int searches) {
		this.searches = searches;
	}

	public ArrayList<String> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<String> history) {
		this.history = history;
	}
	
	public Player getNextPlayer() {
		// If next player quit, set new next player
		if (nextPlayer.hasQuit()) {
			setNextPlayer(nextPlayer.getNextPlayer());
		}
		return nextPlayer;
	}
	
	public void setNextPlayer(Player nextPlayer) {
		this.nextPlayer = nextPlayer;
	}
	
	public boolean hasQuit() {
		return hasQuit;
	}
	
	public void setHasQuit(boolean hasQuit) {
		this.hasQuit = hasQuit;
	}
	
	public boolean votedForEnd() {
		return votedForEnd;
	}
	
	public void setVotedForEnd(boolean votedForEnd) {
		this.votedForEnd = votedForEnd;
	}
	
	public Player nextPlayer;
	public boolean hasQuit;
	public boolean votedForEnd;
	
	public Piece piece;
	public int number;
	public int health;
	public String playerID;
	public int currentRoom;
	public int gold;
	public int skill;
	public int boots;
	public int swords;
	public int teleports;
	public int clankOnBoard;
	public int clankInBag;
	
	// Statuses
	public boolean stoppedInCave;
	public boolean isFlying;
	public boolean isRunning;
	public boolean isFree;
	public boolean isDead;
	public boolean alreadyPickedUpMonkeyIdol;

	public int secretTomes;
	public int highestArtifactValue; // for ties only
	public boolean addedArtifactValue;
	
	// Special cards
	public int swags;
	public int searches;
	
	public Color color;
	public Deck deck = new Deck(true);
	public DiscardPile discardPile = new DiscardPile();
	public PlayArea playArea = new PlayArea();
	public ArrayList<String> inventory = new ArrayList<String>();
	public ArrayList<String> consumables = new ArrayList<String>();
	public ArrayList<String> history = new ArrayList<String>();
}
