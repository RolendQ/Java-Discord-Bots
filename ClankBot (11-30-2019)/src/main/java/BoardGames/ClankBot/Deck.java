package BoardGames.ClankBot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
	public ArrayList<Card> cards = new ArrayList<Card>();
	
	// if it's player, add starting cards, otherwise main deck
	public Deck(boolean isPlayer) {
		if (isPlayer) {
			// Starting cards
			for (int i = 0; i < 6; i++) {
				cloneAndAdd("Burgle");
			}
			cloneAndAdd("Stumble");
			cloneAndAdd("Stumble");
			cloneAndAdd("Sidestep");
			cloneAndAdd("Scramble");
		} else {
			// Main deck
			for (int i = 0; i < GlobalVars.mainDeck.length; i++) {
				cloneAndAdd(GlobalVars.mainDeck[i]);
			}
		}
		shuffle();
	}
	
	//String name, String type, boolean isCompanion, int points, int cost, int skill, int boots,
	//int swords, int gold, int draw, int clank, boolean dragonAttack, boolean isDeep, String acquire, boolean hasDefeat,
	//boolean hasArrive, boolean hasDanger, boolean isUnique
	
	// Have to clone instances of cards to have duplicate copies
	public void cloneAndAdd(String name) {
		Card c = GlobalVars.cardDatabase.get(name);
		cards.add(new Card(c.getName(),c.getType(),c.isCompanion(),c.getPoints(),c.getCost(),c.getSkill(),c.getBoots(),
						   c.getSwords(),c.getGold(),c.getDraw(),c.getHealth(),c.getClank(),c.isTeleport(),c.isDragonAttack(),c.isDeep(),c.getAcquire(),c.getCondition(),
						   c.isHasArrive(),c.isHasDanger(),c.isUnique()));
	}
	
	public boolean hasNext() {
		return cards.size() > 0;
	}
	public Card getNext() {
		return cards.get(0);
	}
	
	public Card getCard(int n) {
		return cards.get(n);
	}
	
	public void removeTop() {
		cards.remove(0);
	}
	
	public void shuffle() {
		Collections.shuffle(cards);
	}
	// Shuffle by taking cards from another deck
	public void shuffle(ArrayList<Card> newDeck) {
		cards.clear();
		while (newDeck.size() > 0) {
			Random r = new Random();
			int num = r.nextInt(newDeck.size());
			cards.add(newDeck.get(num));
			newDeck.remove(num);
		}
	}
	
	public int getSize() {
		return cards.size();
	}
}

