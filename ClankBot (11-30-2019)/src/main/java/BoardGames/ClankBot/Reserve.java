package BoardGames.ClankBot;

import java.util.ArrayList;

public class Reserve {
	public ArrayList<Card> explorers = new ArrayList<Card>();
	public ArrayList<Card> mercenaries = new ArrayList<Card>();
	public ArrayList<Card> secretTomes = new ArrayList<Card>();
	
	public Reserve() {
		for (int i = 0; i < 15; i++) {
			cloneAndAdd(explorers, "Explorer");
			cloneAndAdd(mercenaries, "Mercenary");
			if (i < 12) {
				cloneAndAdd(secretTomes, "Secret Tome");
			}
		}
	}
	
	// public Card(String name, String type, boolean isCompanion, int points, int cost, int skill, int boots,
	// int swords, int gold, int draw, int health, int clank, boolean teleport, boolean dragonAttack, boolean isDeep, String acquire, String[] condition,
	// boolean hasArrive, boolean hasDanger, boolean isUnique) {
	public void cloneAndAdd(ArrayList<Card> pile, String name) {
		Card c = GlobalVars.cardDatabase.get(name);
		pile.add(new Card(c.getName(),c.getType(),c.isCompanion(),c.getPoints(),c.getCost(),c.getSkill(),c.getBoots(),
						  c.getSwords(),c.getGold(),c.getDraw(),c.getHealth(),c.getClank(),c.isTeleport(),c.isDragonAttack(),c.isDeep(),c.getAcquire(),c.getCondition(),
						  c.isHasArrive(),c.isHasDanger(),c.isUnique()));
	}

	public ArrayList<Card> getExplorers() {
		return explorers;
	}

	public void removeExplorer() {
		explorers.remove(0);
	}

	public ArrayList<Card> getMercenaries() {
		return mercenaries;
	}

	public void removeMercenary() {
		mercenaries.remove(0);
	}

	public ArrayList<Card> getSecretTomes() {
		return secretTomes;
	}

	public void removeSecretTome() {
		secretTomes.remove(0);
	}
	
}
