package BoardGames.DeckBuildingGameBot;

import java.io.Serializable;
import java.util.ArrayList;

public class DiscardPile extends CardList {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -2284665417542494155L;
	//public ArrayList<Card> cards = new ArrayList<Card>();
	
	public DiscardPile() {
		
	}
	
//	public void add(Card c) {
//		cards.add(c);
//	}
//	
//	public Card getCard(int n) {
//		return cards.get(n);
//	}
//	
//	public ArrayList<Card> getCards() {
//		//System.out.println("before shuffle " + cards.get(0).getName() + " " + cards.get(1).getName());
//		return cards;
//	}
//	
//	public int getSize() {
//		return cards.size();
//	}
//	
//	public void clear() {
//		cards.clear();
//	}
	
	// Remove from discardPile by cardName
	public Card remove(String cardName) {
		// If it was just trashed, remove it
//		if (cardName.contentEquals("trash")) {
//			for (int i = 0; i < cards.size(); i++) {
//				// Used to be .toString() which might be cause of bug
//				if (cards.get(i).toStringHand().contentEquals("*[Trashed]*")) {
//					cards.remove(i);
//					return true;
//				}
//			}
//			return false;			
//		} else {
			for (int i = 0; i < cards.size(); i++) {
				//if (cards.get(i).getName().contentEquals(cardName)) {
				if (cards.get(i).getName().toUpperCase().startsWith(cardName.toUpperCase())) { // starts with and not case sensitive
					return cards.remove(i);
				}
			}
			return null;
//		}
	}
}

