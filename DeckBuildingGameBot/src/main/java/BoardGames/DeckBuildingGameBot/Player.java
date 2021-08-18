package BoardGames.DeckBuildingGameBot;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Player implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -801146176100498596L;
	// Initial constructor, sets color
	public Player(int number,String playerID, String effectiveName, int color) {
		this.effectiveName = effectiveName;
		this.number = number;
		this.playerID = playerID;
		this.health = 50;
		this.color = color;
		this.mastery = 0;
//		if (number == 0) this.color = Color.RED; 
//		else if (number == 1) this.color = Color.BLUE;
//		else if (number == 2) this.color = Color.YELLOW; 
//		else if (number == 3) this.color = Color.GREEN; 
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
		// Sort by name if start of turn indicated by drawing 5 or 8 
		if (num == 5 || num == 8) {
			Collections.sort(playArea.cards);
		}
	}
	
	// Reset stuff
	public void endOfTurnReset() {
		setShards(0);
		setPower(0);
		setShield(0); // set shield to 0 then update after drawing hand
		setBanish(0);
		setInstantSelect(0);
		setFreeSelect(0);
		setUsingTalons(false);
		setUsingDecurion(false);
		canFocus = true;
		playArea.clear(); // ?
	}
	
	public void calculateShield() {
		for (Card c : playArea.getCards()) {
			shield += c.getShield();
			if (c.getName().contentEquals("Datic Robes")) shield += mastery;
			if (hasChamp("Praetorian-02")) {
				shield += 3;
				if (mastery >= 20) shield += 3;
			}
		}
	}
	
	public boolean hasChamp(String name) {
		return countChamp(name) >= 1;
	}
	
	public int countChamp(String name) {
		int count = 0;
		for (Card c : champions) {
			if (c.getName().contentEquals(name)) count++;
		}
		return count;
	}
	
	public Card getZetta() {
		for (Card c : champions) {
			if (c.getName().contentEquals("Zetta, The Encryptor")) return c;
		}
		return null;
	}
	
	public boolean hasOneFromEach() {
		return (countGroup(0,null,playArea.getCards()) >= 1 
				&& countGroup(2,null,playArea.getCards()) >= 1 
				&& countGroup(3,null,playArea.getCards()) >= 1);
	}
	
	// Count ally
	public int countGroup(int color, ArrayList<Card> list) {
		return countGroup(color, "reg", list) + countGroup(color, "merc", list);
	}
	
	public int countGroup(int color, String type, ArrayList<Card> list) {
		int count = 0;
		for (Card c : list) {
			if (color == -1 || c.getColor() == color) {
				if (type == null || c.getType().contentEquals(type)) count++;
			}
		}
		return count;
	}
	
	public boolean hasHighestMastery(ArrayList<Player> players) {
		for (Player p : players) {
			if (p != this && p.getMastery() >= mastery) return false;
		}
		return true;
	}
	
	public String getHistoryString(int page) {
		String s = "";
		//for (int i = history.size()-1; i >= 0; i--) {
		int start = history.size() - 1 - (page * 10); // page 0 is most recent
		for (int i = start; i >= 0 && i > start-10; i--) {
			s += history.get(i)+"\n";
		}
		return s;
	}
	
	public String getPlayerID() {
		return playerID;
	}

	public String getHeartString() {
		if (color == 0) return ":heart:";
		if (color == 1) return ":blue_heart:";
		if (color == 2) return ":green_heart:";
		if (color == 3) return ":orange_heart:";
		return null;
	}
	
	public ArrayList<Card> getChampions() {
		return champions;
	}
	
	public boolean willCycle(int drawAmount) {
		return drawAmount > deck.getSize();
	}

	public Card banish(String cardName) {
		// Assume always better to banish from discard pile than hand (Maybe there is an exception in rare case)
		Card c = discardPile.remove(cardName);
		if (c != null) return c;
		
		for (int i = 0; i < playArea.getSize(); i++) {
			Card c2 = playArea.getCard(i);
			if (c2.getName().toUpperCase().startsWith(cardName.toUpperCase()) && !playArea.getCard(i).isPlayed()) {
				c2.setStringInHand("*[Banished]*");
				c2.setPlayed(true);
				return c2;
			}
		}
		return null;
	}
	
	public boolean hasUnexhaustedChamps() {
		for (Card c : champions) {
			if (!c.isExhausted()) return true;
		}
		return false;
	}
	public void clearChampions() {
		for (Card c : champions) {
			c.setExhausted(false);
			c.getOwner().getDiscardPile().add(c);
		}
		champions.clear();
	}
	
	public void reviveAll(String type) {
		for (Card c : discardPile.getCards()) {
			if (c.getType().contentEquals(type)) {
				discardPile.remove(c);
				playArea.add(c);
			}
		}
	}
	
	public Card revive(String cardName, ArrayList<String> revives) { // Reminder that player should revive in the order they got revives
		for (int i = 0; i < revives.size(); i++) {
			//Card cardTemplate = GlobalVars.cardDatabase.get(cardName);
			Card cardTemplate = GlobalVars.getCard(cardName); // May be slow
			// Find the specific card
			if (revives.get(i).contentEquals("champ")) {
				if (cardTemplate.getType().contentEquals("champ")) {
					Card c = discardPile.remove(cardName);
					if (c == null) return null;
					playArea.add(c);
					revives.remove(i);
					return c;
				}
			} else if (revives.get(i).contentEquals("merc")) {
				if (cardTemplate.getType().contentEquals("merc")) {
					Card c = discardPile.remove(cardName);
					if (c == null) return null;
					playArea.add(c);
					revives.remove(i);
					return c;
				}
			} else if (revives.get(i).contentEquals("red")) {
				if (cardTemplate.getColor() == 0) {
					Card c = discardPile.remove(cardName);
					if (c == null) return null;
					playArea.add(c);
					revives.remove(i);
					return c;
				}
			}
		}
		return null;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
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

	public int getShards() {
		return shards;
	}

	public void setShards(int shards) {
		this.shards = shards;
	}

	public int getMastery() {
		return mastery;
	}

	public void setMastery(int mastery) {
		this.mastery = mastery;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}
	
	public int getShield() {
		return shield;
	}
	
	public void setShield(int shield) {
		this.shield = shield;
	}
	
	public int getBanish() {
		return banish;
	}
	
	public void setBanish(int banish) {
		this.banish = banish;
	}
	
	public void updateResource(int index, int n) {
		if (index == 0) updateShards(n);
		else if (index == 1) updateMastery(n);
		else if (index == 2) updateHealth(n);
		else if (index == 3) updatePower(n);
		else if (index == 4) updateShield(n);
		else if (index == 5) draw(n);
		else if (index == 6) updateBanish(n);
	}
	
	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}
	
	public void updateShards(int n) {
		shards += n;
	}
	public void updatePower(int n) {
		power += n;
	}
	public void updateMastery(int n) {
		mastery += n;
	}
	public void updateHealth(int n) {
		health += n;
	}
	public void updateShield(int n) {
		shield += n;
	}
	public void updateBanish(int n) {
		banish += n;
	}


	public int getColor() {
		return color;
	}
	
	public Color getActualColor() {
		if (color == 0) return Color.RED;
		else if (color == 1) return Color.BLUE;
		else if (color == 2) return Color.GREEN;
		else if (color == 3) return Color.ORANGE;
		return null;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
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
			if (nextPlayer == this) return this; // Prevents loops
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
	
	public boolean canFocus() {
		return canFocus;
	}
	
	public void setFocus(boolean canFocus) {
		this.canFocus = canFocus;
	}
	
	public String getEffectiveName() {
		return effectiveName;
	}
	
	public boolean hasRelic() {
		return relic != null;
	}
	public void setRelic(Card relic) {
		this.relic = relic;
	}
	public Card getRelic() {
		return relic;
	}
	
	public int getInstantSelect() {
		return instantSelect;
	}
	
	public void updateInstantSelect(int n) {
		this.instantSelect += n;
	}
	
	public void setInstantSelect(int instantSelect) {
		this.instantSelect = instantSelect;
	}
	
	public int getFreeSelect() {
		return freeSelect;
	}
	
	public void updateFreeSelect(int n) {
		this.freeSelect += n;
	}
	
	public void setFreeSelect(int freeSelect) {
		this.freeSelect = freeSelect;
	}
	
	public boolean getUsingTalons() {
		return usingTalons;
	}
	
	public void setUsingTalons(boolean usingTalons) {
		this.usingTalons = usingTalons;
	}
	
	public boolean getUsingDecurion() {
		return usingDecurion;
	}
	
	public void setUsingDecurion(boolean usingDecurion) {
		this.usingDecurion = usingDecurion;
	}
	
	public Player nextPlayer;
	public boolean hasQuit;
	public boolean votedForEnd;
	
	public String effectiveName;
	public int number;
	public int health;
	public String playerID;
	public int shards;
	public int power;
	public int shield;
	public int mastery;
	public int banish;
	
	public boolean isDead = false;
	public boolean canFocus = true;
	public boolean usingTalons = false;
	public boolean usingDecurion = false;
	public Card relic = null;
	
	// Special cards
	public int instantSelect = 0;
	public int freeSelect = 0;
	
	public int color;
	public Deck deck = new Deck(true);
	public DiscardPile discardPile = new DiscardPile();
	public PlayArea playArea = new PlayArea();
	public ArrayList<Card> champions = new ArrayList<Card>();
	public ArrayList<String> history = new ArrayList<String>();
}
