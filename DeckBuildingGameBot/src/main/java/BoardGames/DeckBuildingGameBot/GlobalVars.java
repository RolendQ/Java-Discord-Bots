package BoardGames.DeckBuildingGameBot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import net.dv8tion.jda.core.entities.MessageChannel;


public class GlobalVars {
	public static String botCommandPrefix = "]";
	public static String[] whitelistedIDs = {"146732744070791168","287334685292625920"};
	public static ArrayList<Game> currentGames = new ArrayList<Game>(); // existing games in memory
	public static ArrayList<Tutorial> currentTutorials = new ArrayList<Tutorial>(); // existing tutorials in memory
	public static HashMap<String, String> emojiCommands = new HashMap<String, String>(); // reactions to commands
	public static HashMap<String,BufferedImage> images = new HashMap<String,BufferedImage>(); // images (board, chars, other sprites)
	//public static HashMap<String,Piece> pieces = new HashMap<String,Piece>(); // four player pieces
	public static String gpath = "C:\\Users\\rolan\\eclipse-workspace\\DeckBuildingGameBot\\src\\main\\java\\BoardGames\\DeckBuildingGameBot\\Games\\"; // path of games
	public static String[] mainDeck = {"Mining Drones","Mining Drones","Mining Drones","Nil Assassin","Nil Assassin",
									   "Nil Assassin","Command Seer","Command Seer","Reactor Drone","Reactor Drone",
									   "Reactor Drone","Cryptofist Monk","Cryptofist Monk","Shard Abstractor",
									   "Shard Abstractor","Shard Abstractor","Data Heretic","Data Heretic","Data Heretic",
									   "Numeri Drones", "Numeri Drones", "Portal Monk", "Portal Monk", "Zetta, The Encryptor",
									   "Spore Cleric","Spore Cleric","Spore Cleric","Fungal Hermit","Fungal Hermit",
									   "Cache Warden","Cache Warden","Aetherbreaker","Aetherbreaker","The Grand Architect",
									   "Kiln Drone","Kiln Drone","Kiln Drone","Venator of the Wastes","Order Initiate",
									   "Order Initiate","Order Initiate","Omnius, The All-Knowing","Pall Shades","Pall Shades",
									   "Pall Shades","Wraethe Skirmisher","Wraethe Skirmisher","Wraethe Skirmisher",
									   "Scion of Nothingness","Scion of Nothingness","Arach Devotees","Arach Devotees",
									   "Arach Devotees","Undergrowth Aspirant","Undergrowth Aspirant","Undergrowth Aspirant",
									   "Shardwood Guardian","Shardwood Guardian","Shardwood Guardian","Le'shai Knight",
									   "Le'shai Knight","Le'shai Knight","Thorn Zealot","Thorn Zealot","Root of the Forest",
									   "Evokatus","Evokatus","Optio Crusher","Optio Crusher","Umbral Scourge","Umbral Scourge",
									   "Umbral Scourge","Shadow Apostle","Shadow Apostle","Shadow Apostle","Limiter Drones",
									   "Limiter Drones","Limiter Drones","Zara Ra, Soulflayer","Furrowing Elemental",
									   "Furrowing Elemental","The Lost","The Lost","Mainframe Abbot","Mainframe Abbot",
									   "Hounds of Volos","Hounds of Volos","Cloud Oracles","Cloud Oracles","Cloud Oracles",
									   "Ghostwillow Avenger","Drakonarius","Li Hin, The Shattered","Raidian, Cloud Master",
									   "Ferrata Guard","Ferrata Guard","Additri, Gaiamancer","Ru Bo Vai, The Transcendant",
									   "Axia","Giga, Source Adept","Fao Cu'tul, The Formless","Systema A.I.","Primus Pilus",
									   "Korvus Legionnaire","Korvus Legionnaire","Korvus Legionnaire","Shadebound Sentry",
									   "Shadebound Sentry","Zen Chi Set, Godkiller","Taur, Arachpriest","General Decurion",
									   "Ojas, Genesis Druid"};
	public static HashMap<String,String> cardInfo = new HashMap<String,String>(); // Cards with text to explain special effect
	// Turn into text file later?
	public static HashMap<String,Card> cardDatabase = new HashMap<String,Card>();
	public static List<String> cardNames = new ArrayList<String>();
	public static HashMap<String,Mastery[]> masteryDatabase = new HashMap<String,Mastery[]>();
	public static HashMap<String,String> uniqueTexts = new HashMap<String,String>();
	public static Card[] relics = new Card[8];
	public static HashMap<String,String> reviveEmojis = new HashMap<String,String>();
	
	static HashMap<String,String> emojis = new HashMap<String, String>();
	
	public static MessageChannel gamesChannel = null;
	
	// Initiates everything
	public static void createFiles() throws IOException {		
		emojiCommands.put("⏩","p"); // not used because of exhaustAll
		emojiCommands.put("🏁","e");
		emojiCommands.put("🗃️","de");
		emojiCommands.put("🗓️","h");
		emojiCommands.put("⚛️","f");
		//emojiCommands.put("⚛️","f");
		emojiCommands.put("📕","a r");
		emojiCommands.put("📘","a b");
		emojiCommands.put("📗","a g");
		emojiCommands.put("📙","a o");
		emojiCommands.put("▶️","pg f");
		emojiCommands.put("◀️","pg b");
		emojiCommands.put("pre 📕","j r");
		emojiCommands.put("pre 📘","j b");
		emojiCommands.put("pre 📗","j g");
		emojiCommands.put("pre 📙","j o");
		emojiCommands.put("pre ▶️","st");
		emojiCommands.put("pre 🎭","mode 2v2");
		emojiCommands.put("pre 🇦","set a");
		emojiCommands.put("pre 🇧","set b");
		emojiCommands.put("pre 🇨","set c");
		emojiCommands.put("pre 🇩","set d");
		emojiCommands.put("pre 🔁","mull");
		
		
		emojis.put("fast_forward","⏩");
		emojis.put("boom","💥");
		emojis.put("link","🔗"); // No link needed for this
		emojis.put("checkered_flag","🏁");
		emojis.put("mag","🔍");
		emojis.put("card_box","🗃️");
		emojis.put("calendar_spiral","🗓️");
		emojis.put("book 0", "📕");
		emojis.put("book 1", "📘");
		emojis.put("book 2", "📗");
		emojis.put("book 3", "📙");
		emojis.put("atom", "⚛️");
		emojis.put("dagger", "🗡️");
		emojis.put("arrow_backward","◀️");
		emojis.put("arrow_forward","▶️");
		emojis.put("performing_arts","🎭");
		emojis.put("repeat","🔁");
		emojis.put("question","❓");
		
		
		uniqueTexts.put("Scion of Nothingness","(``Echo`` = **2** :boom: each)");
		uniqueTexts.put("Thorn Zealot","(``Unify`` = Destroy a :crossed_swords:)");
		uniqueTexts.put("Venator of the Wastes","(``Inspire`` = A player loses **2** :star:)");
		uniqueTexts.put("Numeri Drones","{**1** :diamond_shape_with_a_dot_inside: | Play the next :orange_book: :crossed_swords: you select}");
		uniqueTexts.put("Portal Monk","(Recruit a card with cost <= 6 for free | **15** :star: = Put it into your hand)");
		uniqueTexts.put("Zetta, The Encryptor","/You and your other :crossed_swords: can't be attacked/");
		uniqueTexts.put("Furrowing Elemental","(*You have **50** :green_heart:* = **6** :boom:)");
		uniqueTexts.put("The Lost","(*You are :closed_book:* = **1** :wastebasket:)");
		uniqueTexts.put("Mainframe Abbot","(*You are :blue_book:* = **1** :star:)");
		uniqueTexts.put("Hounds of Volos","(*You are :green_book:* = **5** :boom:)");
		uniqueTexts.put("Evokatus","{**1** :boom: each :orange_book: :crossed_swords:}");
		uniqueTexts.put("Cloud Oracles","(*You have more :star: than others* = **2** :diamond_shape_with_a_dot_inside:)");
		uniqueTexts.put("Ghostwillow Avenger","(**15** :star: = Destroy all enemy :crossed_swords:)");
		uniqueTexts.put("Drakonarius","{**6** :boom:}/*You have **General Decurion*** = This has **99** :heartpulse:/");
		uniqueTexts.put("Li Hin, The Shattered","{**1** :boom:}");
		uniqueTexts.put("Raidian, Cloud Master","{**1** :book:}/*Attacker has less :star:* = This has **99** :heartpulse:/");
		uniqueTexts.put("Additri, Gaiamancer","{**2** :boom: | **2** :boom: each :green_book: non :crossed_swords:}");
		uniqueTexts.put("Optio Crusher","{**3** :boom: | **10** :star: = **2** :boom:}");
		uniqueTexts.put("Giga, Source Adept","{``Dominion`` = **3** :star:}");
		uniqueTexts.put("Primus Pilus","{*You have 3 :orange_book: :crossed_swords:* = **2** :book:}");
		uniqueTexts.put("Systema A.I.","{**1** :star: | **20** :star: = **2** :book:}");
		uniqueTexts.put("Fao Cu'tul, The Formless","{**2** :boom: | **20** :star: = Double your :boom:}");
		
		uniqueTexts.put("Ferrata Guard","{**1** :diamond_shape_with_a_dot_inside: each :orange_book: :crossed_swords: you have in play}/*You are :orange_book:* = All your :crossed_swords: have **+2** :heartpulse:");
		uniqueTexts.put("Ru Bo Vai, The Transcendant","{**4** :boom:}/**10** :star: = Your attacks ignore :shield:/");
		uniqueTexts.put("Axia","{**7** :boom:}/Costs **1** :diamond_shape_with_a_dot_inside: less for each :orange_book: :crossed_swords: you have in play/");
		uniqueTexts.put("Korvus Legionnaire","(:syringe: any :crossed_swords:)");
		uniqueTexts.put("Shadebound Sentry","(:syringe: any :dagger:)");
		uniqueTexts.put("Zen Chi Set, Godkiller","{**3** :boom: | :syringe: any :closed_book:}");
		uniqueTexts.put("Taur, Arachpriest","{Play a :green_book: non :crossed_swords: again}");
		uniqueTexts.put("Ojas, Genesis Druid","(Play a non :crossed_swords: again | **20** :star: = Play it again)");
		uniqueTexts.put("General Decurion","{**3** :diamond_shape_with_a_dot_inside: | **20** :star: = Play every :orange_book: non :crossed_swords: again for this turn}");
		
		uniqueTexts.put("The Heart of Nothing","(**20** :star: = **5** :boom: | *You deal 10 unblocked damage to an opponent* = **3** :book: next turn)");
		uniqueTexts.put("The World Piercer","(:syringe: any :dagger: | **20** :star: = :syringe: ALL :dagger:)");
		uniqueTexts.put("Entropic Talons","(**1** :boom: each :green_heart: you gain this turn | **20** :star: = **10** :green_heart:)");
		uniqueTexts.put("Panconscious Crown","(**20** :star: **&** ``Unify`` = **50** :green_heart:)");
		uniqueTexts.put("Datic Robes","**X** :shield: equal to your :star: (**20** :star: = **1** :book:)");
		uniqueTexts.put("Terminal Crescents","(**X** :boom: equal to half your :star: | **20** :star: = **X** :boom: equal to other half)");
		uniqueTexts.put("Praetorian-01","*Play a :crossed_swords:* = :syringe: this (**20** :star: = **4** :boom:)");
		uniqueTexts.put("Praetorian-02","/You have **3** :shield: | **20** :star: = **3** :shield:/");
		// Relics need text
		
		Mastery[] m1 = {new Mastery(5,0,1),new Mastery(15,0,1)};
		masteryDatabase.put("Shard Reactor",m1);
		Mastery[] m2 = {new Mastery(10,3,1),new Mastery(20,3,2),new Mastery(30,3,99)};
		masteryDatabase.put("Infinity Shard",m2);
		Mastery[] m3 = {new Mastery(10,2,5)};
		masteryDatabase.put("Fungal Hermit",m3);
		Mastery[] m4 = {new Mastery(10,5,1)};
		masteryDatabase.put("Cache Warden",m4);
		Mastery[] m5 = {new Mastery(10,3,4)};
		masteryDatabase.put("Aetherbreaker",m5);
		Mastery[] m6 = {new Mastery(10,6,2)};
		masteryDatabase.put("Zara Ra, Soulflayer",m6);
		Mastery[] m7 = {new Mastery(20,3,5)};
		masteryDatabase.put("The Heart of Nothing",m7);
		Mastery[] m8 = {new Mastery(20,5,1)};
		masteryDatabase.put("Datic Robes",m8);
		Mastery[] m9 = {new Mastery(20,2,10)};
		masteryDatabase.put("Entropic Talons",m9);
		Mastery[] m10 = {new Mastery(20,3,4)};
		masteryDatabase.put("Praetorian-01",m10);
		
		cardDatabase.put("Crystal",new Card("Crystal",4,"reg",0,false,null,0,1,0,0,0,0,0,0,null,null,false));
		cardDatabase.put("Blaster",new Card("Blaster",4,"reg",0,false,null,0,0,0,0,1,0,0,0,null,null,false));
		//String txt4 = "(**5** :star: = **1** :diamond_shape_with_a_dot_inside: | **15** :star: = **1** :diamond_shape_with_a_dot_inside:)";
		cardDatabase.put("Shard Reactor",new Card("Shard Reactor",4,"reg",0,false,masteryDatabase.get("Shard Reactor"),0,2,0,0,0,0,0,0,null,null,false));
		//String txt5 = "(**10** :star: = **1** :boom: | **20** :star: = **2** :boom: | **30** :star: = **99** :boom:)";
		cardDatabase.put("Infinity Shard",new Card("Infinity Shard",4,"reg",0,false,masteryDatabase.get("Infinity Shard"),0,0,0,0,2,0,0,0,null,null,false));
		
		cardDatabase.put("Mining Drones",new Card("Mining Drones",3,"reg",0,false,null,2,1,0,0,0,0,1,0,null,null,false));
		cardDatabase.put("Nil Assassin",new Card("Nil Assassin",0,"merc",0,false,null,2,0,0,0,5,0,0,0,null,null,false));
		cardDatabase.put("Command Seer",new Card("Command Seer",1,"reg",0,false,null,4,2,0,0,0,5,0,0,null,null,false));
		cardDatabase.put("Reactor Drone",new Card("Reactor Drone",3,"reg",0,false,null,3,3,0,0,0,0,0,0,null,null,false));
		cardDatabase.put("Cryptofist Monk",new Card("Cryptofist Monk",1,"reg",0,false,null,5,0,0,0,0,8,1,0,null,null,false));
		cardDatabase.put("Shard Abstractor",new Card("Shard Abstractor",1,"merc",0,false,null,3,0,2,0,0,0,0,0,null,null,false));
		cardDatabase.put("Data Heretic",new Card("Data Heretic",1,"merc",0,false,null,3,0,0,0,0,0,2,0,null,null,false));
		cardDatabase.put("Spore Cleric",new Card("Spore Cleric",2,"merc",0,false,null,2,0,0,4,0,0,0,0,null,null,false));
		cardDatabase.put("Fungal Hermit",new Card("Fungal Hermit",2,"merc",0,false,masteryDatabase.get("Fungal Hermit"),3,0,1,0,0,0,0,0,null,null,false));
		cardDatabase.put("Cache Warden",new Card("Cache Warden",1,"merc",0,false,masteryDatabase.get("Cache Warden"),2,0,1,0,0,0,0,0,null,null,false));
		cardDatabase.put("Aetherbreaker",new Card("Aetherbreaker",0,"merc",0,false,masteryDatabase.get("Aetherbreaker"),4,0,0,0,4,0,0,0,null,null,false));
		cardDatabase.put("The Grand Architect",new Card("The Grand Architect",1,"merc",0,false,null,7,0,5,0,0,0,0,0,null,null,false));
		
		cardDatabase.put("Kiln Drone",new Card("Kiln Drone",3,"reg",0,false,null,1,2,0,0,0,0,0,0,new Perk(0,2),null,false));
		cardDatabase.put("Venator of the Wastes",new Card("Venator of the Wastes",3,"merc",0,false,null,4,0,0,0,4,0,0,0,new Perk(0,0,true),null,true)); // unique
		cardDatabase.put("Order Initiate",new Card("Order Initiate",1,"reg",0,false,null,1,2,0,0,0,0,0,0,new Perk(1,2),null,false));
		cardDatabase.put("Omnius, The All-Knowing",new Card("Omnius, The All-Knowing",1,"merc",0,false,null,6,0,0,0,0,0,2,0,new Perk(1,5),null,false));
		cardDatabase.put("Pall Shades",new Card("Pall Shades",0,"reg",0,false,null,2,0,0,0,0,0,1,0,new Perk(3,3),null,false));
		cardDatabase.put("Wraethe Skirmisher",new Card("Wraethe Skirmisher",0,"reg",0,false,null,1,0,0,0,2,0,0,0,new Perk(3,4),null,false));
		cardDatabase.put("Scion of Nothingness",new Card("Scion of Nothingness",0,"merc",0,false,null,5,0,0,0,3,0,0,0,new Perk(0,0,true),null,true)); // unique
		cardDatabase.put("Arach Devotees",new Card("Arach Devotees",2,"reg",0,false,null,2,0,0,0,0,0,1,0,new Perk(2,3),null,false));
		cardDatabase.put("Undergrowth Aspirant",new Card("Undergrowth Aspirant",2,"reg",0,false,null,1,0,0,3,0,0,0,0,new Perk(3,5),null,false));
		cardDatabase.put("Shardwood Guardian",new Card("Shardwood Guardian",2,"reg",0,false,null,4,0,0,0,2,0,1,0,new Perk(2,6),null,false));
		cardDatabase.put("Thorn Zealot",new Card("Thorn Zealot",2,"reg",0,false,null,3,0,0,0,0,3,1,0,new Perk(0,0,true),null,true)); // unique
		cardDatabase.put("Le'shai Knight",new Card("Le'shai Knight",2,"merc",0,false,null,3,0,0,0,3,0,0,0,new Perk(3,3),null,false));
		cardDatabase.put("Root of the Forest",new Card("Root of the Forest",2,"merc",0,false,null,7,0,0,10,0,0,0,0,new Perk(3,10),null,false));
		
		cardDatabase.put("Umbral Scourge",new Card("Umbral Scourge",0,"merc",0,false,null,3,0,1,0,0,0,0,1,null,null,false));
		cardDatabase.put("Shadow Apostle",new Card("Shadow Apostle",0,"merc",0,false,null,2,0,0,0,2,0,0,1,null,null,false));
		cardDatabase.put("Limiter Drones",new Card("Limiter Drones",3,"reg",0,false,null,2,0,0,0,0,0,1,0,new Perk(6,1),null,false));
		cardDatabase.put("Zara Ra, Soulflayer",new Card("Zara Ra, Soulflayer",0,"merc",0,false,masteryDatabase.get("Zara Ra, Soulflayer"),5,0,1,0,4,0,0,0,null,null,false));
		
		// UNIQUE
		cardDatabase.put("Numeri Drones",new Card("Numeri Drones",3,"champ",5,false,null,3,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Portal Monk",new Card("Portal Monk",1,"reg",0,false,null,3,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Zetta, The Encryptor",new Card("Zetta, The Encryptor",1,"champ",5,false,null,5,0,0,0,0,5,0,0,null,null,true));
		cardDatabase.put("Drakonarius",new Card("Drakonarius",3,"champ",2,false,null,6,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Li Hin, The Shattered",new Card("Li Hin, The Shattered",0,"champ",99,false,null,3,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Raidian, Cloud Master",new Card("Raidian, Cloud Master",1,"champ",3,false,null,5,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Furrowing Elemental",new Card("Furrowing Elemental",2,"reg",0,false,null,5,0,0,4,0,0,1,0,null,null,true));
		cardDatabase.put("The Lost",new Card("The Lost",0,"merc",0,false,null,4,0,0,0,6,0,0,0,null,null,true));
		cardDatabase.put("Mainframe Abbot",new Card("Mainframe Abbot",1,"reg",0,false,null,3,0,0,0,0,3,1,0,null,null,true));
		cardDatabase.put("Hounds of Volos",new Card("Hounds of Volos",2,"merc",0,false,null,3,0,0,5,0,0,0,0,null,null,true));
		cardDatabase.put("Ferrata Guard",new Card("Ferrata Guard",3,"champ",4,false,null,4,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Ru Bo Vai, The Transcendant",new Card("Ru Bo Vai, The Transcendant",0,"champ",4,false,null,5,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Axia",new Card("Axia",3,"champ",7,false,null,7,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Cloud Oracles",new Card("Cloud Oracles",1,"reg",0,false,null,2,0,0,0,0,0,1,0,null,null,true));
		cardDatabase.put("Ghostwillow Avenger",new Card("Ghostwillow Avenger",2,"merc",0,false,null,4,0,0,0,4,0,0,0,null,null,true));
		cardDatabase.put("Korvus Legionnaire",new Card("Korvus Legionnaire",3,"reg",0,false,null,3,0,0,0,2,2,0,0,null,null,true));
		cardDatabase.put("Shadebound Sentry",new Card("Shadebound Sentry",0,"reg",0,false,null,3,0,0,0,3,0,0,0,null,null,true));
		cardDatabase.put("Zen Chi Set, Godkiller",new Card("Zen Chi Set, Godkiller",0,"champ",5,false,null,7,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Taur, Arachpriest",new Card("Taur, Arachpriest",2,"champ",4,false,null,5,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("General Decurion",new Card("General Decurion",3,"champ",7,false,null,7,0,0,0,0,0,0,0,null,null,true));
		cardDatabase.put("Ojas, Genesis Druid",new Card("Ojas, Genesis Druid",2,"reg",0,false,null,4,0,0,0,0,0,0,0,null,null,true));
		
		// Champs
		cardDatabase.put("Giga, Source Adept",new Card("Giga, Source Adept",1,"champ",4,false,null,2,0,0,0,0,0,1,0,null,null,false)); // Dominion is in exhaust
		cardDatabase.put("Fao Cu'tul, The Formless",new Card("Fao Cu'tul, The Formless",0,"champ",4,false,null,4,0,0,0,0,0,0,0,null,null,false));
		cardDatabase.put("Systema A.I.",new Card("Systema A.I.",1,"champ",4,false,null,3,0,0,0,0,0,0,0,null,null,false));
		cardDatabase.put("Primus Pilus",new Card("Primus Pilus",3,"champ",6,false,null,2,0,0,0,0,0,0,0,null,null,false));
		cardDatabase.put("Evokatus",new Card("Evokatus",3,"champ",2,false,null,4,0,0,0,0,0,1,0,null,null,false));
		cardDatabase.put("Additri, Gaiamancer",new Card("Additri, Gaiamancer",2,"champ",5,false,null,5,0,0,0,0,0,0,0,null,null,false));
		cardDatabase.put("Optio Crusher",new Card("Optio Crusher",3,"champ",4,false,null,5,0,0,0,0,0,0,0,null,null,false));
		
		// Relics
		cardDatabase.put("The Heart of Nothing",new Card("The Heart of Nothing",0,"reg",0,true,masteryDatabase.get("The Heart of Nothing"),0,0,0,0,5,0,0,0,null,null,true));
		cardDatabase.put("The World Piercer",new Card("The World Piercer",0,"reg",0,true,null,0,0,2,0,0,0,0,0,null,null,true));
		cardDatabase.put("Datic Robes",new Card("Datic Robes",1,"reg",0,true,masteryDatabase.get("Datic Robes"),0,0,0,0,0,0,1,0,null,null,true));
		cardDatabase.put("Terminal Crescents",new Card("Terminal Crescents",1,"reg",0,true,null,0,0,1,0,0,0,0,0,null,null,true));
		cardDatabase.put("Entropic Talons",new Card("Entropic Talons",2,"reg",0,true,masteryDatabase.get("Entropic Talons"),0,0,0,0,0,0,2,0,null,null,true)); // Changed interpretation slightly
		cardDatabase.put("Panconscious Crown",new Card("Panconscious Crown",2,"reg",0,true,null,0,0,2,2,0,0,0,0,null,null,true)); // both mastery and perk
		cardDatabase.put("Praetorian-01",new Card("Praetorian-01",3,"reg",0,true,masteryDatabase.get("Praetorian-01"),0,0,0,0,8,0,0,0,null,null,true));
		cardDatabase.put("Praetorian-02",new Card("Praetorian-02",3,"champ",9,true,null,0,0,0,0,0,0,0,0,null,null,true));
		
		for (String key : cardDatabase.keySet()) {
			cardNames.add(key);
		}
		Collections.sort(cardNames);
		
		System.out.println("Main Deck Size: "+mainDeck.length);
		System.out.println("Card Database Size: "+cardDatabase.size());
		
		relics[0] = clone("The Heart of Nothing");
		relics[1] = clone("The World Piercer");
		relics[2] = clone("Datic Robes");
		relics[3] = clone("Terminal Crescents");
		relics[4] = clone("Entropic Talons");
		relics[5] = clone("Panconscious Crown");
		relics[6] = clone("Praetorian-01");
		relics[7] = clone("Praetorian-02");

		reviveEmojis.put("merc", ":dagger:");
		reviveEmojis.put("champ", ":crossed_swords:");
		reviveEmojis.put("red", ":closed_book:");
		//reviveEmojis.put("Praetorian-01","Play :crossed_swords: to :syringe: :trophy:");
		
		//	public Card(String name, String color, String type, int life, boolean isRelic, boolean hasMastery,
		//  String[] masteries, int cost, int shards, int mastery, int health, int power, int shield,
		//  int draw, String text, boolean isUnique) {
		//cardDatabase.put("Burgle",new Card("Burgle","starting",false,0,0,1,0,0,0,0,0,0,false,false,false,null,null,false,false,false));
		
		int count = 1;
		for (Card c : cardDatabase.values()) {
			if (c.getType().contentEquals("basic")) {
				c.setID(count);
				count++;
				System.out.print(count);
			}
		}
	}
	
	public static Game findGame(String channelID) {
		for (Game g : currentGames) {
			if (g.getGameChannel().getId().contentEquals(channelID)) {
				return g;
			}
		}
		return null;
	}
	
	public static Player findPlayer(Game game, String playerID) {
		for (Player p : game.players) {
			if (p.getPlayerID().contentEquals(playerID)) {
				return p;
			}
		}
		return null;
	}
	
	public static Card clone(String name) {
		Card c = GlobalVars.cardDatabase.get(name);
		return new Card(c.getName(),c.getColor(),c.getType(),c.getLife(),c.isRelic(),c.getMasteries(),c.getCost(),
						   c.getShards(),c.getMastery(),c.getHealth(),c.getPower(),c.getShield(),c.getDraw(),c.getBanish(),c.getPerk(),c.getText(),c.isUnique());
	}
	
	public static void add(Game game) {
		currentGames.add(game);
		System.out.println("[DEBUG LOG/GlobalVars.java] Added a game");
	}
	
	public static void remove(Game game) {
		currentGames.remove(game);
		System.out.println("[DEBUG LOG/GlobalVars.java] Removed a game");
	}
	
	public static void add(Tutorial t) {
		currentTutorials.add(t);
		System.out.println("[DEBUG LOG/GlobalVars.java] Added a tutorial");
	}
	
	public static void remove(Tutorial t) {
		currentTutorials.remove(t);
		System.out.println("[DEBUG LOG/GlobalVars.java] Removed a tutorial");
	}
	
	public static Card getCard(String name) {
		for (String cname : cardDatabase.keySet()) {
			if (cname.toUpperCase().startsWith(name.toUpperCase())) {
				return cardDatabase.get(cname);
			}
		}
		return null;
	}
	
}