package BoardGames.DeckBuildingGameBot;

import java.io.Serializable;

public class Card implements Comparable<Card>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4579904668690368381L;

	public Card(String name, int color, String type, int life, boolean isRelic, Mastery[] masteries, 
			int cost, int shards, int mastery, int health, int power, int shield,
			int draw, int banish, Perk perk, String text, boolean isUnique) {
		this.name = name;
		this.color = color;
		this.type = type;
		this.life = life;
		this.isRelic = isRelic;
		this.masteries = masteries;
		this.cost = cost;
		this.shards = shards;
		this.mastery = mastery;
		this.health = health;
		this.power = power;
		this.shield = shield;
		this.draw = draw;
		this.banish = banish;
		this.perk = perk;
		this.text = text;
		this.isUnique = isUnique;
		
		createString();
		createText();
	}
	
	// Adds and formats with appropriate emojis
	public void createString() {
		//[ **5** :blue_book:┃:dragon:┃:pick:┃:warning: ] __Name__ ( **+2** :shield: )═══{ **5** :diamond_shape_with_a_dot_inside: :boot: :crossed_swords: :book: :warning: :heart: :moneybag: :crystal_ball: } **10** :gem:
		if (isRelic) costString += "[**☆** "+Utils.colorBooks[color]+" ] ";
		else costString += "[ **"+cost+"** "+Utils.colorBooks[color]+" ] ";
		
		if (type.contentEquals("merc")) typeString = ":dagger: ";
		else if (type.contentEquals("champ")) typeString = ":crossed_swords: ";
		
		nameString = name;
		if (isUnique) {
			nameString += "*";
		}
		nameString += " ";
		
		if (shield > 0) effectString += "**"+shield+"** :shield: ";
		
		if (type.contentEquals("champ")) {
			effectString += "**"+life+"** :heartpulse: ";
		}
		
		if (hasBasicEffect()) {
			effectString += "( ";
		
			if (shards > 0) effectString += "**"+shards+"** :diamond_shape_with_a_dot_inside: ";
			if (power > 0) effectString += "**"+power+"** :boom: ";
			if (health > 0) effectString += "**"+health+"** :green_heart: ";
			if (mastery > 0) effectString += "**"+mastery+"** :star: ";
			if (draw > 0) effectString += "**"+draw+"** :book: ";
			if (banish > 0) effectString += "**"+banish+"** :wastebasket: ";
			if (isUnique) {
				//
			}
			
			effectString += ")";
		}
		
		if (masteries != null || isUnique) {
			// Add text
		}
		
		setStringInHand();
	}
	
	public void createText() {
		if (isUnique || type.contentEquals("champ")) {
			text = GlobalVars.uniqueTexts.get(name);
			return;
		}
		if (masteries != null) {
			text = "(";
			if (type.contentEquals("champ")) text = "{";
			for (Mastery m : masteries) {
				text += "**"+m.getRequirement()+"** :star: = **"+m.getAmount()+"** "+Utils.resourceEmojis[m.getReward()]+" | ";
			}
			text = text.substring(0,text.length()-2);
			if (type.contentEquals("champ")) text += "}";
			else text += ")";
		} else if (perk != null) {
			text = "(";
			if (type.contentEquals("champ")) text = "{";
			if (color == 0) text += "``Echo``";
			else if (color == 1) text += "``Dominion``";
			else if (color == 2) text += "``Unify``";
			else if (color == 3) text += "``Inspire``";
			text += " = **"+perk.getAmount()+"** "+Utils.resourceEmojis[perk.getReward()];
			if (type.contentEquals("champ")) text += "}";
			else text += ")";
		}
	}
	
	public boolean hasBasicEffect() {
		return shards > 0 || power > 0 || health > 0 || mastery > 0 || draw > 0 || banish > 0;
	}
	
	// Has priority hint in hand/info
	public boolean hasPriorityHint() {
		return name.contentEquals("Swagger") || name.contentEquals("Search") || name.contentEquals("Sleight of Hand")
				|| name.contentEquals("Apothecary");
	}
	
	// Unique but not indicated and not in info command
	public boolean isHiddenUnique() {
		return name.contentEquals("Dragon Shrine") || name.equals("Master Burglar") || name.contentEquals("Apothecary") || 
				name.contentEquals("Sleight of Hand") || name.contentEquals("Tattle") || name.contentEquals("Mister Whiskers");
	}
	
	public void setStringInHand(String s) {
		this.stringInHand = s;
	}
	
	public void setStringInHand() {
		//this.stringInHand = nameString+effectString;
		this.stringInHand = Utils.colorBooks[color]+" "+nameString+effectString;
	}
	
	public String toString() {
		String result = costString+typeString+nameString+effectString;
		//if (isShiny) result = result.replace(name,":sparkles:**"+name+"**");
		return result.replaceAll("null","");
	}
	
	public String toStringHand() {
//		if (stringInHand.equals("*[Banished]*")) return stringInHand;
//		//if (isShiny) return stringInHand.replaceAll("null","").replace(name,":sparkles:**"+name+"**");
//		String temp = Utils.colorBooks[color]+" ";
		if (isExhausted()) {
			String temp = Utils.colorBooks[color]+" ~~"+nameString+"~~"+effectString; 
			return temp.replaceAll("null", "");
		}
//		else if (isRelic) temp += "__"+nameString+"__"+effectString;
//		else if (type.contentEquals("merc")) temp += "*"+nameString+"*"+effectString;
//		else if (type.contentEquals("champ")) temp += "**"+nameString+"**"+effectString;
//		else temp += nameString+effectString;
//		return temp.replaceAll("null","");
		return stringInHand.replaceAll("null","");
	}
	
	public String toStringChamp() {
		if (isExhausted()) {
			String temp = Utils.colorBooks[color]+" ~~"+nameString+"~~"; // No effect string
			return temp.replaceAll("null", "");
		}
		return stringInHand.replaceAll("null","");
	}
	
	public String toStringHandMerc() {
		//if (isShiny) return stringInHand.replaceAll("null","").replace(name,":sparkles:**"+name+"**");
		String temp = Utils.colorBooks[color]+" :dagger: "+nameString+effectString;
		return temp.replaceAll("null","");
	}
	
	public String getName() {
		return name;
	}
	
	public String getNameInList() {
		if (isRelic) return Utils.colorBooks[color]+"__"+name+"__";
		else if (type.contentEquals("merc")) return Utils.colorBooks[color]+"*"+name+"*";
		else if (type.contentEquals("champ")) return Utils.colorBooks[color]+"**"+name+"**";
		else return Utils.colorBooks[color]+name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public int getColor() {
		return color;
	}
	public String getType() {
		return type;
	}
	
	public String getCleanType() {
		if (color == 4) return "STARTING";
		else if (isRelic) return "RELIC";
		else if (type.contentEquals("reg")) return "REGULAR";
		else if (type.contentEquals("merc")) return "MERCENARY";
		else if (type.contentEquals("champ")) return "CHAMPION";
		return "OTHER";
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getShards() {
		return shards;
	}
	public int getShield() {
		return shield;
	}
	public int getMastery() {
		return mastery;
	}
	public Mastery[] getMasteries() {
		return masteries;
	}
	public int getLife() {
		return life;
	}
	public String getText() {
		if (text == null) return "";
		return text;
	}
	public int getPower() {
		return power;
	}
	public int getBanish() {
		return banish;
	}
	public boolean isRelic() {
		return isRelic;
	}
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
	
//	public boolean isShiny() {
//		return isShiny;
//	}
//	
//	public void setShiny(boolean isShiny) {
//		this.isShiny = isShiny;
//	}
	
	public Perk getPerk() {
		return perk;
	}
	
	public boolean asMerc() {
		return asMerc;
	}
	
	public void setAsMerc(boolean asMerc) {
		this.asMerc = asMerc;
	}

	public boolean isPlayed() {
		return played;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}
	
	
	public boolean isBought() {
		return bought;
	}

	public void setBought(boolean bought) {
		this.bought = bought;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	// Comparing names
	public int compareTo(Card c) {
		return getName().compareTo(c.getName());
	}
	
	public boolean isExhausted() {
		return exhausted;
	}
	
	public void setExhausted(boolean exhausted) {
		this.exhausted = exhausted;
	}
	
	public int ID;
	public String name;
	public int color;
	public String type; 
	public boolean isRelic;
	public int life;
	public Mastery[] masteries;
	public int cost;
	public int shards;
	public int mastery;
	public int health;
	public int power;
	public int shield;
	public int draw;
	public int banish;
	public Perk perk;
	public String text;
	public boolean isUnique;
	public boolean played = false;
	public boolean bought = false;
	public boolean asMerc = false;
	public boolean exhausted = false;
	//public boolean isShiny;
	
	// if Champ
	public Player owner;
	
	// Parts of string
	public String costString;
	public String typeString;
	public String nameString;
	public String effectString;
	
	public String stringInHand;
}