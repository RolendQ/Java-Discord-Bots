package BoardGames.DeckBuildingGameBot;

import java.io.Serializable;

public class Perk implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8748832457789019735L;
	//public int requirement;
	public int reward;
	public int amount;
	public boolean isUnique;
	
	public Perk(int reward, int amount) {
		this(reward, amount, false);
	}
	
	public Perk(int reward, int amount, boolean isUnique) {
		this.reward = reward;
		this.amount = amount;
		this.isUnique = isUnique;
	}
	
	public int getReward() {
		return reward;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public boolean isUnique() {
		return isUnique;
	}
}
