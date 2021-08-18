package BoardGames.ClankBot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.dv8tion.jda.core.entities.MessageChannel;


public class GlobalVars {
	public static String[] whitelistedIDs = {"146732744070791168","287334685292625920"};
	public static ArrayList<Game> currentGames = new ArrayList<Game>(); // existing games in memory
	public static HashMap<String, String> emojiCommands = new HashMap<String, String>(); // reactions to commands
	public static HashMap<String,BufferedImage> images = new HashMap<String,BufferedImage>(); // images (board, chars, other sprites)
	public static HashMap<String,Piece> pieces = new HashMap<String,Piece>(); // four player pieces
	public static String ppath = "C:\\Users\\rolan\\Downloads\\ClankBot-NEW\\ClankBot\\Pictures\\"; // path of pictures
	public static int[][] playerCoordsPerRoom = {{52,17},{17,64},{106,65},{190,65},{271,65},{362,50},{34,128},{127,138},{194,136},{272,149}, // 9
												 {349,120},{18,199},{94,209},{189,214},{277,218},{359,197},{34,272},{123,279},{206,296},{298,297}, //19
												 {364,263},{436,281},{76,326},{117,376},{206,363},{298,363},{366,338},{472,353},{20,396},{109,425}, //30
												 {191,433},{269,441},{349,452},{415,397},{472,416},{44,486},{143,490},{243,492},{430,472},
												 {105,13},{154,13},{202,13},{250,13},{299,13}};
	public static int[][] playersOffset = {{0,0},{18,0},{4,9},{22,9}}; // Offset coords to draw without overlapping
	public static int[][] playerToItemOffset = {{7,3},{4,-1}}; // Offset when on items?
	public static int[][] adjacentRoomsPerRoom = {{1},{0,2},{1,3,7},{2,4,8,9},{3,9},{4,10},{2,11},{2,12,13},{3,13},{3,4,10,14},{5,9,15},{6,16},
												  {7,16,17},{7,8,14,17,18},{9,13,15,20},{10,14,20},{11,12,17,21,22,28},{12,13,16,23,24},{13,24},
												  {20,25,26},{14,15,19,21},{16,20,27},{16},{17,24},{17,18,23,25,29,30},{19,24,31},{19},{21,34},
												  {22,35},{24,28,30,35},{24,29,31,36,37},{25,30,32},{31,33,37,38},{26,32},{27},{29,36},{30,35},
												  {30,32,38},{32,37}};
	public static int[][] teleportRoomsPerRoom = {{},{},{6},{},{5},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{28},{},{},{},{33},{},
												  {16,29},{},{},{},{},{},{},{28}};
	public static String[] mainDeck = {"Pickaxe","Pickaxe","Tunnel Guide","Tunnel Guide","Sneak","Sneak","Move Silently","Move Silently",
	                                   "Bracers of Agility","Bracers of Agility","Lucky Coin","Lucky Coin","Treasure Map","Sapphire",
	                                   "Sapphire","Sapphire","Ruby","Ruby","Ruby","Emerald","Emerald","Diamond","Cleric of the Sun",
	                                   "Cleric of the Sun","Silver Spear","Silver Spear","Amulet of Vigor","Boots of Swiftness","Ladder",
	                                   "Ladder","Teleporter","Teleporter","Vault","Kobold","Kobold","Orc Grunt","Orc Grunt","Orc Grunt","Ogre",
	                                   "Ogre","Belcher","Belcher","Cave Troll","Watcher","Watcher","Watcher","Overlord","Overlord",
	                                   "Invoker of the Ancients","Singing Sword","Elven Cloak","Elven Dagger","Elven Boots","Brilliance",
	                                   "Scepter of the Ape Lord","MonkeyBot 3000","Dead Run","Dead Run","Flying Carpet","Archaeologist",
	                                   "Archaeologist","Rebel Captain","Rebel Scout","Rebel Miner","Rebel Soldier","The Mountain King",
	                                   "The Queen of Hearts","Wand of Recall","Tattle","Tattle","Kobold Merchant","Crystal Golem","Crystal Golem",
	                                   "Master Burglar","Master Burglar","Shrine","Shrine","Shrine","Dragon Shrine","Dragon Shrine",
	                                   "Wizard","Dragon's Eye","The Duke","Dwarven Peddler","Treasure Hunter","Treasure Hunter","Swagger",
	                                   "Swagger","Search","Search","Gem Collector","Underworld Dealing","Sleight of Hand","Sleight of Hand",
	                                   "Apothecary","Mister Whiskers","Wand of Wind"};
//	public static String[] mainDeck = {"Treasure Hunter","Treasure Hunter","Swagger",
//            "Swagger","Search","Search","Gem Collector","Underworld Dealing","Sleight of Hand","Sleight of Hand",
//            "Apothecary","Mister Whiskers","Treasure Hunter","Treasure Hunter","Swagger",
//            "Swagger","Search","Search","Gem Collector","Underworld Dealing","Sleight of Hand","Sleight of Hand",
//            "Apothecary","Mister Whiskers","Sapphire",
//            "Sapphire","Sapphire","Ruby","Ruby","Ruby","Emerald","Emerald","Diamond","Wand of Wind","Wand of Wind","Wand of Wind",
//            "Wand of Wind"};
	public static HashMap<String,String> cardInfo = new HashMap<String,String>(); // Cards with text to explain special effect


	public static int[][] effectsPerPath = {new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],
											new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],
											new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],
											new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],
											new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],new int[39],
											new int[39],new int[39],new int[39],new int[39],new int[39]}; // Effects for every possible path
	// Turn into text file later?
	public static HashMap<String,Card> cardDatabase = new HashMap<String,Card>();
	static HashMap<String,String> emojis = new HashMap<String, String>();
	
	public static MessageChannel filesChannel = null;
	
	// Intiates everything
	public static void createFiles() throws IOException {		
		emojiCommands.put("⏩","p");
		emojiCommands.put("🏁","e");
		emojiCommands.put("🔮","tp");
		emojiCommands.put("📜","g");
		emojiCommands.put("🐵","g");
		emojiCommands.put("🔑","b 1");
		emojiCommands.put("💼","b 2");
		emojiCommands.put("👑","b 3");
		emojiCommands.put("🗃️","de");
		emojiCommands.put("⏱️","h");
				
		
		emojis.put("fast_forward","⏩");
		emojis.put("link","🔗");
		emojis.put("checkered_flag","🏁");
		emojis.put("mag","🔍");
		emojis.put("key","🔑");
		emojis.put("briefcase","💼");
		emojis.put("crown","👑");
		emojis.put("card_box","🗃️");
		emojis.put("stopwatch","⏱️");
		emojis.put("crystal_ball","🔮");
		emojis.put("scroll","📜");
		emojis.put("monkey_face","🐵");
		
		pieces.put("red",new Piece(new File(ppath+"RedChar.png"),"player",0,0));
		pieces.put("blue",new Piece(new File(ppath+"BlueChar.png"),"player",0,0));
		pieces.put("yellow",new Piece(new File(ppath+"YellowChar.png"),"player",0,0));
		pieces.put("green",new Piece(new File(ppath+"GreenChar.png"),"player",0,0));
		
		images.put("Board1",ImageIO.read(new File(ppath+"board1.png")));
		images.put("RedChar",ImageIO.read(new File(ppath+"RedChar.png")));
		images.put("BlueChar",ImageIO.read(new File(ppath+"BlueChar.png")));
		images.put("YellowChar",ImageIO.read(new File(ppath+"YellowChar.png")));
		images.put("GreenChar",ImageIO.read(new File(ppath+"GreenChar.png")));
		images.put("MinorSecret2",ImageIO.read(new File(ppath+"MinorSecret2.png")));
		images.put("MinorSecret1",ImageIO.read(new File(ppath+"MinorSecret1.png")));
		images.put("MajorSecret1",ImageIO.read(new File(ppath+"MajorSecret1.png")));
		images.put("MonkeyIdol",ImageIO.read(new File(ppath+"MonkeyIdol.png")));
		images.put("Dragon",ImageIO.read(new File(ppath+"Dragon.png")));
		images.put("Artifact5",ImageIO.read(new File(ppath+"Artifact5.png")));
		images.put("Artifact7",ImageIO.read(new File(ppath+"Artifact7.png")));
		images.put("Artifact10",ImageIO.read(new File(ppath+"Artifact10.png")));
		images.put("Artifact15",ImageIO.read(new File(ppath+"Artifact15.png")));
		images.put("Artifact20",ImageIO.read(new File(ppath+"Artifact20.png")));
		images.put("Artifact25",ImageIO.read(new File(ppath+"Artifact25.png")));
		images.put("Artifact30",ImageIO.read(new File(ppath+"Artifact30.png")));
		
		// 0 nothing, 1 footsteps, 2 monster, 3 lock, 4 foots+monster, 5 two monsters
		effectsPerPath[2][3] = 1;
		effectsPerPath[3][4] = 1;
		effectsPerPath[9][10] = 1;
		// one way
		effectsPerPath[6][2] = 1;
		effectsPerPath[29][28] = 1;
		
		effectsPerPath[7][12] = 1;
		effectsPerPath[11][16] = 1;
		effectsPerPath[13][17] = 1;
		effectsPerPath[17][23] = 4;
		
		effectsPerPath[24][29] = 1;
		effectsPerPath[30][37] = 1;
		effectsPerPath[32][33] = 4;
		effectsPerPath[37][38] = 4;
		effectsPerPath[4][9] = 2;
		effectsPerPath[6][11] = 2;
		effectsPerPath[7][13] = 2;
		effectsPerPath[9][14] = 2;
		effectsPerPath[12][16] = 2;
		// one way
		effectsPerPath[16][28] = 2;
		effectsPerPath[19][26] = 2;
		effectsPerPath[13][18] = 5;
		effectsPerPath[24][30] = 5;
		effectsPerPath[27][34] = 2;
		effectsPerPath[29][30] = 5;
		effectsPerPath[31][32] = 5;
		effectsPerPath[30][36] = 2;
		effectsPerPath[32][37] = 5;
		
		effectsPerPath[3][8] = 3;
		effectsPerPath[5][10] = 3;
		effectsPerPath[8][13] = 3;
		effectsPerPath[14][20] = 3;
		effectsPerPath[21][27] = 3;
		effectsPerPath[23][24] = 3;
		effectsPerPath[25][31] = 3;
		effectsPerPath[30][31] = 3;
		effectsPerPath[35][36] = 3;
		
		cardInfo.put("Crystal Golem","Can only be attacked in a Crystal Cave");
		cardInfo.put("Watcher","Upon arriving, all players gain 1 Clank");
		cardInfo.put("Shrine","Upon arriving, return 3 Dragon Clank to bag");
		cardInfo.put("Dead Run","You don’t have to stop in Crystal Caves this turn");
		cardInfo.put("Flying Carpet","You don’t have to stop in Crystal Caves and you can ignore monsters in tunnels this turn");
		cardInfo.put("Underworld Dealing","Gain one gold OR Buy 2 Secret Tomes for 7 gold");
		cardInfo.put("Wand of Wind","Teleport to an adjacent room OR Take a Secret from an adjacent room");
		cardInfo.put("Gem Collector","Gems cost 2 less Skill this turn");
		cardInfo.put("Treasure Hunter","Replace a card in the dungeon row. Doesn’t trigger dragon attacks");
		cardInfo.put("Swagger","Every time you gain Clank this turn, gain the same amount of Skill");
		cardInfo.put("Search","Every time you gain Gold this turn, gain 1 extra Gold");
		cardInfo.put("Dragon's Eye","Worth 10 extra points if you escaped");
		cardInfo.put("The Duke","Worth 1 extra point for every 5 Gold you have");
		cardInfo.put("Wizard","Worth 2 extra points for every Secret Tome you have");
		cardInfo.put("Dwarven Peddler","Worth 4 extra points if you have 2 distinct items of the following: Chalice, Dragon Egg, or Monkey Idol");

		//String name, String type, boolean isCompanion, int points, int cost, int skill, int boots,
		//int swords, int gold, int draw, int health, int clank, boolean teleport, boolean dragonAttack, boolean isDeep, String acquire, String[] condition,
		//boolean hasArrive, boolean hasDanger, boolean isUnique
		cardDatabase.put("Burgle",new Card("Burgle","starting",false,0,0,1,0,0,0,0,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Stumble",new Card("Stumble","starting",false,0,0,0,0,0,0,0,0,1,false,false,false,null,null,false,false,false));
		cardDatabase.put("Sidestep",new Card("Sidestep","starting",false,0,0,0,1,0,0,0,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Scramble",new Card("Scramble","starting",false,0,0,1,1,0,0,0,0,0,false,false,false,null,null,false,false,false));
		
		cardDatabase.put("Explorer",new Card("Explorer","basic",false,0,3,2,1,0,0,0,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Mercenary",new Card("Mercenary","basic",true,0,2,1,0,2,0,0,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Secret Tome",new Card("Secret Tome","basic",false,7,7,0,0,0,0,0,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Goblin",new Card("Goblin","monster",false,0,2,0,0,0,1,0,0,0,false,false,false,null,null,false,false,false));
		
		cardDatabase.put("Pickaxe",new Card("Pickaxe","basic",false,2,4,0,0,2,2,0,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Treasure Map",new Card("Treasure Map","basic",false,0,6,0,0,0,5,0,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Tunnel Guide",new Card("Tunnel Guide","basic",true,1,1,0,1,1,0,0,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Sneak",new Card("Sneak","basic",false,0,2,1,1,0,0,0,0,-2,false,false,false,null,null,false,false,false));
		cardDatabase.put("Move Silently",new Card("Move Silently","basic",false,0,3,0,2,0,0,0,0,-2,false,false,false,null,null,false,false,false));
		cardDatabase.put("Bracers of Agility",new Card("Bracers of Agility","basic",false,2,5,0,0,0,0,2,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Lucky Coin",new Card("Lucky Coin","basic",false,1,1,1,0,0,0,1,0,1,false,false,false,null,null,false,false,false));
		cardDatabase.put("Sapphire",new Card("Sapphire","gem",false,4,4,0,0,0,0,1,0,0,false,true,false,null,null,false,false,false));
		cardDatabase.put("Emerald",new Card("Emerald","gem",false,5,5,0,0,0,0,1,0,0,false,true,false,null,null,false,false,false));
		cardDatabase.put("Ruby",new Card("Ruby","gem",false,6,6,0,0,0,0,1,0,0,false,true,false,null,null,false,false,false));
		cardDatabase.put("Diamond",new Card("Diamond","gem",false,8,8,0,0,0,0,1,0,0,false,true,false,null,null,false,false,false));
		cardDatabase.put("Cleric of the Sun",new Card("Cleric of the Sun","basic",true,1,2,2,0,1,0,0,0,0,false,false,false,"health",null,false,false,false));
		cardDatabase.put("Silver Spear",new Card("Silver Spear","basic",false,2,3,0,0,3,0,0,0,0,false,false,false,"swords",null,false,false,false));
		cardDatabase.put("Amulet of Vigor",new Card("Amulet of Vigor","basic",false,3,7,4,0,0,0,0,0,0,false,false,false,"health",null,false,false,false));
		cardDatabase.put("Boots of Swiftness",new Card("Boots of Swiftness","basic",false,3,5,0,3,0,0,0,0,0,false,false,false,"boots",null,false,false,false));
		cardDatabase.put("Invoker of the Ancients",new Card("Invoker of the Ancients","basic",true,1,4,0,0,0,0,0,0,1,true,false,false,null,null,false,false,false));
		cardDatabase.put("Singing Sword",new Card("Singing Sword","basic",false,2,5,3,0,2,0,0,0,0,false,true,false,null,null,false,false,false));
		cardDatabase.put("Elven Cloak",new Card("Elven Cloak","basic",false,2,4,1,0,0,0,1,0,-2,false,false,false,null,null,false,false,false));
		cardDatabase.put("Elven Dagger",new Card("Elven Dagger","basic",false,2,4,1,0,1,0,1,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Elven Boots",new Card("Elven Boots","basic",false,2,4,1,1,0,0,1,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Brilliance",new Card("Brilliance","basic",false,0,6,0,0,0,0,3,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Scepter of the Ape Lord",new Card("Scepter of the Ape Lord","basic",false,3,3,3,0,0,0,0,0,3,false,false,false,null,null,false,false,false));
		cardDatabase.put("MonkeyBot 3000",new Card("MonkeyBot 3000","basic",true,1,5,0,0,0,0,3,0,3,false,true,false,null,null,false,false,false));
		String[] arch = {"monkeyidol","skill"};
		cardDatabase.put("Archaeologist",new Card("Archaeologist","basic",true,1,2,0,0,0,0,1,0,0,false,false,false,null,arch,false,false,false));
		String[] comp = {"companion","draw"};
		cardDatabase.put("Rebel Captain",new Card("Rebel Captain","basic",true,1,3,2,0,0,0,0,0,0,false,false,false,null,comp,false,false,false));
		cardDatabase.put("Rebel Miner",new Card("Rebel Miner","basic",true,1,2,0,0,0,2,0,0,0,false,false,false,null,comp,false,false,false));
		cardDatabase.put("Rebel Scout",new Card("Rebel Scout","basic",true,1,3,0,2,0,0,0,0,0,false,false,false,null,comp,false,false,false));
		cardDatabase.put("Rebel Soldier",new Card("Rebel Soldier","basic",true,1,2,0,0,2,0,0,0,0,false,false,false,null,comp,false,false,false));
		String[] king = {"crown","swordboot"};
		cardDatabase.put("The Mountain King",new Card("The Mountain King","basic",true,3,6,2,1,1,0,0,0,0,false,false,false,null,king,false,false,false));
		String[] queen = {"crown","heart"};
		cardDatabase.put("The Queen of Hearts",new Card("The Queen of Hearts","basic",true,3,6,3,0,1,0,0,0,0,false,false,false,null,queen,false,false,false));
		String[] merch = {"artifact","skill"};
		cardDatabase.put("Kobold Merchant",new Card("Kobold Merchant","basic",true,1,3,0,0,0,2,0,0,0,false,false,false,null,merch,false,false,false));
		String[] wand = {"artifact","teleport"};
		cardDatabase.put("Wand of Recall",new Card("Wand of Recall","basic",false,1,5,2,0,0,0,0,0,0,false,false,false,null,wand,false,false,false));
		
		cardDatabase.put("Ladder",new Card("Ladder","device",false,0,3,0,2,0,0,0,0,0,false,false,false,null,null,false,false,false));
		cardDatabase.put("Teleporter",new Card("Teleporter","device",false,0,4,0,0,0,0,0,0,0,true,false,false,null,null,false,false,false));
		cardDatabase.put("Vault",new Card("Vault","device",false,0,3,0,0,0,5,0,0,3,false,true,true,null,null,false,false,false));
		
		cardDatabase.put("Kobold",new Card("Kobold","monster",false,0,1,1,0,0,0,0,0,0,false,true,false,null,null,false,true,false));
		cardDatabase.put("Animated Door",new Card("Animated Door","monster",false,0,1,0,1,0,0,0,0,0,false,true,false,null,null,false,false,false));
		cardDatabase.put("Ogre",new Card("Ogre","monster",false,0,3,0,0,0,5,0,0,0,false,true,false,null,null,false,false,false));
		cardDatabase.put("Orc Grunt",new Card("Orc Grunt","monster",false,0,2,0,0,0,3,0,0,0,false,true,false,null,null,false,false,false));
		cardDatabase.put("Belcher",new Card("Belcher","monster",false,0,2,0,0,0,4,0,0,2,false,true,false,null,null,false,false,false));
		cardDatabase.put("Cave Troll",new Card("Cave Troll","monster",false,0,4,0,0,0,3,0,0,0,false,true,true,null,null,false,false,false));
		cardDatabase.put("Overlord",new Card("Overlord","monster",false,0,2,0,0,0,0,2,0,0,false,true,false,null,null,true,false,false));
		
		// Unique but Hidden
		cardDatabase.put("Master Burglar",new Card("Master Burglar","basic",true,2,3,2,0,0,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Shrine",new Card("Shrine","device",false,0,2,0,0,0,0,0,0,0,false,false,false,null,null,true,false,true));
		cardDatabase.put("Sleight of Hand",new Card("Sleight of Hand","basic",false,0,2,0,0,0,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Apothecary",new Card("Apothecary","basic",true,2,3,0,0,0,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Mister Whiskers",new Card("Mister Whiskers","basic",true,1,1,0,0,0,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Tattle",new Card("Tattle","basic",false,0,3,2,0,0,0,0,0,0,false,false,false,null,null,false,false,true));
		
		// Unique
		cardDatabase.put("Dragon Shrine",new Card("Dragon Shrine","device",false,0,4,0,0,0,0,0,0,0,false,false,false,null,null,false,true,true));
		cardDatabase.put("Watcher",new Card("Watcher","monster",false,0,3,0,0,0,3,0,0,0,false,false,false,null,null,true,false,true));
		cardDatabase.put("Dead Run",new Card("Dead Run","basic",false,0,3,0,2,0,0,0,0,2,false,false,false,null,null,false,false,true));
		cardDatabase.put("Flying Carpet",new Card("Flying Carpet","basic",false,2,6,0,2,0,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Crystal Golem",new Card("Crystal Golem","monster",false,0,3,3,0,0,0,0,0,0,false,true,false,null,null,false,false,true));
		cardDatabase.put("Wizard",new Card("Wizard","basic",true,0,6,3,0,0,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Dragon's Eye",new Card("Dragon's Eye","gem",false,0,5,0,0,0,0,1,0,0,false,false,true,null,null,false,false,true));
		cardDatabase.put("The Duke",new Card("The Duke","basic",true,0,5,2,0,2,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Dwarven Peddler",new Card("Dwarven Peddler","basic",true,0,4,0,1,0,2,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Treasure Hunter",new Card("Treasure Hunter","basic",false,1,3,2,0,2,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Swagger",new Card("Swagger","basic",false,0,2,0,1,0,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Search",new Card("Search","basic",false,0,4,2,1,0,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Gem Collector",new Card("Gem Collector","basic",false,2,4,2,0,0,0,0,0,-2,false,false,false,null,null,false,false,true));
		cardDatabase.put("Underworld Dealing",new Card("Underworld Dealing","basic",false,0,1,0,0,0,0,0,0,0,false,false,false,null,null,false,false,true));
		cardDatabase.put("Wand of Wind",new Card("Wand of Wind","basic",false,3,6,0,0,0,0,0,0,0,false,false,false,null,null,false,false,true));
	}
	
	public static void add(Game game) {
		currentGames.add(game);
		System.out.println("[DEBUG LOG/GlobalVars.java] Added a game");
	}
	
	public static void remove(Game game) {
		currentGames.remove(game);
		System.out.println("[DEBUG LOG/GlobalVars.java] Removed a game");
	}
	
}