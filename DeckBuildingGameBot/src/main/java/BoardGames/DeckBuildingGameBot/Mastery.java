package BoardGames.DeckBuildingGameBot;

import java.io.Serializable;

public class Mastery implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6136773300725714242L;
	public int requirement;
	public int reward;
	public int amount;
	public boolean isUnique;
	
	public Mastery(int requirement, int reward, int amount) {
		this(requirement, reward, amount, false);
	}
	
	public Mastery(int requirement, int reward, int amount, boolean isUnique) {
		this.requirement = requirement;
		this.reward = reward;
		this.amount = amount;
		this.isUnique = isUnique;
	}
	
	public int getRequirement() {
		return requirement;
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
	// To do
}
