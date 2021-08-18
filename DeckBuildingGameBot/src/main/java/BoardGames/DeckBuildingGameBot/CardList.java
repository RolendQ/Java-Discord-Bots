package BoardGames.DeckBuildingGameBot;

import java.io.Serializable;
import java.util.ArrayList;

public class CardList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1765355718821663284L;
	public ArrayList<Card> cards = new ArrayList<Card>();

	public void add(Card c) {
		cards.add(c);
	}
	
	public void remove(Card c) {
		cards.remove(c);
	}
	public void remove(int n) {
		cards.remove(n);
	}
	
	public void clear() {
		cards.clear();
	}
	
	public int getSize() {
		return cards.size();
	}
	
	public Card getCard(int n) {
		return cards.get(n);
	}
	
	public ArrayList<Card> getCards() {
		return cards;
	}
}
