package BoardGames.ClankBot;

public class Card implements Comparable<Card> {
	public Card(String name, String type, boolean isCompanion, int points, int cost, int skill, int boots,
			int swords, int gold, int draw, int health, int clank, boolean teleport, boolean dragonAttack, boolean isDeep, String acquire, String[] condition,
			boolean hasArrive, boolean hasDanger, boolean isUnique) {
		super();
		this.name = name;
		this.type = type;
		this.isCompanion = isCompanion;
		this.points = points;
		this.cost = cost;
		this.skill = skill;
		this.boots = boots;
		this.swords = swords;
		this.gold = gold;
		this.draw = draw;
		this.health = health;
		this.clank = clank;
		this.teleport = teleport;
		this.dragonAttack = dragonAttack;
		this.isDeep = isDeep;
		this.acquire = acquire;
		this.condition = condition;
		this.hasArrive = hasArrive;
		this.hasDanger = hasDanger;
		this.isUnique = isUnique;
		
		createString();
	}
	
	// Adds and formats with appropriate emojis
	public void createString() {
		//[ **5** :blue_book:┃:dragon:┃:pick:┃:warning: ] __Name__ ( **+2** :shield: )═══{ **5** :diamond_shape_with_a_dot_inside: :boot: :crossed_swords: :book: :warning: :heart: :moneybag: :crystal_ball: } **10** :gem:
		if (!type.contentEquals("starting")) costString += "[ **"+cost+"** ";
		if (type.contentEquals("basic")) {
			if (isCompanion) costString += ":green_book:";
			else costString += ":blue_book:";
		}
		else if (type.contentEquals("gem")) costString += ":gem:";
		else if (type.contentEquals("monster")) costString += ":crossed_swords:";
		else if (type.contentEquals("device")) costString += ":orange_book:";
		if (dragonAttack) costString += "┃:dragon:";
		if (isDeep) costString += "┃:pick:";
		if (hasDanger) costString += "┃:exclamation:";
		if (!type.contentEquals("starting")) costString += " ] ";
		
		nameString = "__"+name+"__";
		if (isUnique && !isHiddenUnique()) {
			nameString += "*";
		}
		nameString += " ";
		
		if (acquire != null) {
			acquireString += "( ";
			if (acquire.contentEquals("health")) acquireString  += "**1** :heart:";
			if (acquire.contentEquals("swords")) acquireString  += "**1** :crossed_swords:";
			if (acquire.contentEquals("boots")) acquireString  += "**1** :boot:";
			acquireString += " ) "; // ═══
		}
		
		if (type.contentEquals("monster") || type.contentEquals("device")) effectString += "( ";
		else effectString += "{ ";
		
		if (skill > 0) effectString += "**"+skill+"** :diamond_shape_with_a_dot_inside: ";
		if (boots > 0) effectString += "**"+boots+"** :boot: ";
		if (swords > 0) effectString += "**"+swords+"** :crossed_swords: ";
		if (draw > 0) effectString += "**"+draw+"** :book: ";
		if (clank != 0) effectString += "**"+clank+"** :warning: ";
		if (health > 0) effectString += "**"+health+"** :heart: ";
		if (gold > 0) effectString += "**"+gold+"** :moneybag: ";
		if (teleport) effectString += "**"+1+"** :crystal_ball: ";
		if (isUnique) {
			if (name.contentEquals("Master Burglar")) {
				effectString += "**1** :wastebasket: a **Burgle** ";
			} else if (name.contentEquals("Tattle") || name.contentEquals("Watcher")) {
				effectString += "**OTHERS +1** :warning: ";
			} else if (name.contentEquals("Shrine")) {
				effectString += "**1** :moneybag: **~OR~** **1** :heart: ";
			} else if (name.contentEquals("Dragon Shrine")) {
				effectString += "**2** :moneybag: **~OR~** **1** :wastebasket: ";
			} else if (name.contentEquals("Underworld Dealing")) {
				effectString += "**1** :moneybag: **~OR~** **BUY** :notebook_with_decorative_cover: ";
			} else if (name.contentEquals("Sleight of Hand")) {
				effectString += "**DISCARD, 2** :book: ";
			} else if (name.contentEquals("Apothecary")) {
				effectString += "**DISCARD, 3** :crossed_swords: **~OR~** **2** :moneybag: **~OR~** **1** :heart: ";
			} else if (name.contentEquals("Mister Whiskers")) {
				effectString += ":dragon: **~OR~** **-2** :warning: ";
			} else if (name.contentEquals("Wand of Wind")) {
				effectString += "**1** :crystal_ball: ~OR~ Take a **SECRET** ";
			}
		}
		if (condition != null) {
			//[ **2** :green_book:  ] __Rebel Soldier__  { **2** :crossed_swords:┃**IF** :green_book:**,**  **1** :book: } **1** :star:
			effectString += "┃**IF** ";
			if (condition[0].contentEquals("companion")) {
				effectString += ":green_book:**,** ";
				if (condition[1].contentEquals("draw")) effectString += "**1** :book: ";
			}
			else if (condition[0].contentEquals("artifact")) {
				effectString += ":scroll:**,** ";
				if (condition[1].contentEquals("teleport")) effectString += "**1** :crystal_ball: ";
				else if (condition[1].contentEquals("skill")) effectString += "**2** :diamond_shape_with_a_dot_inside: ";
			}
			else if (condition[0].contentEquals("crown")) {
				effectString += ":crown:**,** ";
				if (condition[1].contentEquals("heart")) effectString += "**1** :heart: ";
				else if (condition[1].contentEquals("swordboot")) effectString += "**1** :boot: **1** :crossed_swords: ";
			}
			else if (condition[0].contentEquals("monkeyidol")) {
				effectString += ":monkey_face:**,** ";
				if (condition[1].contentEquals("skill")) effectString += "**2** :diamond_shape_with_a_dot_inside: ";
			}
		}
		
		if (type.contentEquals("monster") || type.contentEquals("device")) effectString += ")";
		else effectString += "}";
		if (points > 0) effectString += " **"+points+"** :star:";
		
		setStringInHand();
	}
	
	// Has priority hint in hand/info
	public boolean hasPriorityHint() {
		return name.contentEquals("Swagger") || name.contentEquals("Search") || name.contentEquals("Sleight of Hand");
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
		this.stringInHand = nameString+effectString;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isCompanion() {
		return isCompanion;
	}
	public void setCompanion(boolean isCompanion) {
		this.isCompanion = isCompanion;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getSkill() {
		return skill;
	}
	public void setSkill(int skill) {
		this.skill = skill;
	}
	public int getBoots() {
		return boots;
	}
	public void setBoots(int boots) {
		this.boots = boots;
	}
	public int getSwords() {
		return swords;
	}
	public void setSwords(int swords) {
		this.swords = swords;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
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

	public int getClank() {
		return clank;
	}
	public void setClank(int clank) {
		this.clank = clank;
	}
	
	public boolean isTeleport() {
		return teleport;
	}

	public void setTeleport(boolean teleport) {
		this.teleport = teleport;
	}

	public boolean isDragonAttack() {
		return dragonAttack;
	}

	public void setDragonAttack(boolean dragonAttack) {
		this.dragonAttack = dragonAttack;
	}

	public boolean isDeep() {
		return isDeep;
	}
	public void setDeep(boolean isDeep) {
		this.isDeep = isDeep;
	}
	public String getAcquire() {
		return acquire;
	}
	public void setAcquire(String acquire) {
		this.acquire = acquire;
	}
	public String[] getCondition() {
		return condition;
	}
	public boolean isHasArrive() {
		return hasArrive;
	}
	public void setHasArrive(boolean hasArrive) {
		this.hasArrive = hasArrive;
	}
	public boolean isHasDanger() {
		return hasDanger;
	}
	public void setHasDanger(boolean hasDanger) {
		this.hasDanger = hasDanger;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
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

	public String toString() {
//		if (acquireString == null || acquireString.contentEquals("null")) {
//			return costString+nameString+effectString;
//		}
		String result = costString+nameString+acquireString+effectString;
		return result.replaceAll("null","");
	}
	
	public String toStringHand() {
		return stringInHand.replaceAll("null","");
	}

	// Comparing names
	public int compareTo(Card c) {
		return getName().compareTo(c.getName());
	}
	
	public String name;
	public String type; // Starting, Basic, Gem, Device, Monster
	public boolean isCompanion;
	public int points;
	public int cost;
	public int skill;
	public int boots;
	public int swords;
	public int gold;
	public int draw;
	public int health;
	public int clank; // + or -
	public boolean teleport;
	public boolean dragonAttack;
	public boolean isDeep;
	public String acquire;
	public String[] condition;
	public boolean hasArrive;
	public boolean hasDanger;
	public boolean isUnique;
	public boolean played = false;
	public boolean bought = false;
	
	// Parts of string
	public String costString;
	public String nameString;
	public String acquireString;
	public String effectString;
	
	public String stringInHand;
}

//class SortByName implements Comparator<Card> {
//
//	public int compare(Card c1, Card c2) {
//		return c1.getName().compareTo(c2.getName());
//	}
//	
//}