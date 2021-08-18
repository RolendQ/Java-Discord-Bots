package BoardGames.DeckBuildingGameBot;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck extends CardList implements Serializable {
    //public ArrayList<Card> cards = new ArrayList<Card>();
	public ArrayList<Card> balancedStart;
	public boolean isPlayer;
	
	// if it's player, add starting cards, otherwise main deck
	public Deck(boolean isPlayer) {
		this.isPlayer = isPlayer;
		if (isPlayer) {
			// Starting cards
			for (int i = 0; i < 7; i++) {
				cards.add(GlobalVars.clone("Crystal"));
			}
			cards.add(GlobalVars.clone("Blaster"));
			cards.add(GlobalVars.clone("Shard Reactor"));
			cards.add(GlobalVars.clone("Infinity Shard"));
		} else {
			// Main deck
			for (int i = 0; i < GlobalVars.mainDeck.length; i++) {
				cards.add(GlobalVars.clone(GlobalVars.mainDeck[i]));
			}
		}
		shuffle();
		if (!isPlayer) setUpBalancedStart(3);
	}
	
	public void setUpBalancedStart(int n) {
		System.out.println("Setting up balanced start");
		balancedStart = new ArrayList<Card>();
		int[] colorCounts = {n,n,n,n};
		
		int repetitions = 0;
		while (colorCounts[0] != 0 || colorCounts[1] != 0 || colorCounts[2] != 0 || colorCounts[3] != 0) {
			Card c = cards.get(cards.size()-1);
			//System.out.println(c.getName() + ": "+c.getColor());
			if (colorCounts[c.getColor()] > 0) {
				balancedStart.add(c);
				colorCounts[c.getColor()]--;
			} else {
				getCards().add(0,c); // Put it at bottom
			}
			cards.remove(cards.size()-1);
			repetitions++;
		}
		System.out.println("Found "+n+" of each. Took: "+repetitions);
	}
	// String name, String color, String type, int life, boolean isRelic, Mastery masteries, 
	// int cost, int shards, int mastery, int health, int power, int shield,
	// int draw, String text, boolean isUnique) {
	
	public boolean hasNext() {
		return cards.size() > 0;
	}
	public Card getNext() {
		return getNext(false);
	}
	// Shiny card
	public Card getNext(boolean hasShinyChance) {
		//Card c = cards.get(0);
		if (!isPlayer && balancedStart.size() > 0) {
			return balancedStart.get(balancedStart.size()-1);
		}
		Card c = cards.get(cards.size()-1);
//		if (hasShinyChance && c.getType().contentEquals("basic")) { // Only basic cards can be shiny
//			Random r = new Random();
//			if (r.nextInt(1000) < 5) {
//				c.setShiny(true);
//			}
//		}
		return c;
	}
	
//	public Card getCard(int n) {
//		return cards.get(n);
//	}
	
	public void removeTop() {
		//cards.remove(0);
		if (!isPlayer && balancedStart.size() > 0) {
			balancedStart.remove(balancedStart.size()-1);
		} else cards.remove(cards.size()-1);
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
	
//	public int getSize() {
//		return cards.size();
//	}
}

