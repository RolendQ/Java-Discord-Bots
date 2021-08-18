package JavaTutorials.UltimateTTT;

public class Challenge {
	private String challengerID;
	private String challengedID = null;
	
	public Challenge(String challengerID) {
		this.challengerID = challengerID;
	}
	public Challenge(String challengerID, String challengedID) {
		this.challengerID = challengerID;
		this.challengedID = challengedID;
	}
	
	public String getChallengerID() {
		return challengerID;
	}
	
	public String getChallengedID() {
		return challengedID;
	}
}
