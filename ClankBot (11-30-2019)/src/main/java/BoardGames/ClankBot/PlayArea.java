package BoardGames.ClankBot;

import java.util.ArrayList;

public class PlayArea {
	public ArrayList<Card> cards = new ArrayList<Card>();
	
	public PlayArea() {
	
	}
	
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
	
	// Return total number of non played cards in playArea
	public int getNonPlayedSize() {
		int count = 0;
		for (int i = 0; i < cards.size(); i++) {
			if (!cards.get(i).isPlayed()) {
				count++;
			}
		}
		return count;
	}
	
	public boolean has(String name) {
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getName().contentEquals(name) && cards.get(i).isPlayed()) return true;
		}
		return false;
	}
}
