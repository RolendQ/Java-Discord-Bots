package BoardGames.DeckBuildingGameBot;

import java.awt.Color;
import java.util.Set;
import java.util.function.Consumer;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public class TextDatabase {
	public static void printHelp(MessageChannel channel, int page) {
		String helpMenu;
		if (page == 1) {
			helpMenu = "Intro";
		} else {
			helpMenu = "How to play";
		}
		channel.sendMessage(helpMenu).queue();
	}

	public static void printCenterRowGuide(MessageChannel channel) {
		String guide1 = "**__Center Row Guide__**\r\n" + 
				"\r\n" + 
				"[ **X** :closed_book: ] - Indicates card cost and color\r\n" + 
				"(         ) - Play Effect (when you play it normally)\r\n" + 
				"{         } - Exhaust Effect (champion's ability)\r\n" + 
				"/         / - Passive Effect (as long as champion is in play)\r\n" +
				":crossed_swords: **Champion** - Remains in play until it is killed. Exhaust it to use its ability once per turn\r\n" + 
				":dagger: **Mercenary** - CAN be hired to gain its Play Effect immediately but does not join your deck\r\n" + 
				"\r\n" + 
				"**Card Colors/Factions:**\r\n" + 
				":closed_book: **RED** - Pure damage\r\n" +
				":blue_book: **BLUE** - Card draw, shield, and mastery\r\n" + 
				":green_book: **GREEN** -  Healing and some damage\r\n" + 
				":orange_book: **ORANGE** - Many champions\r\n" + 
				"\r\n" + 
				"**Effects:**\r\n" +
				"*Everything only lasts the turn it is gained except Mastery*\r\n" +
				":diamond_shape_with_a_dot_inside: **Shards** - Currency used to recruit cards from the Center Row\r\n" + 
				":boom: **Power** - Grants attack power that can be used to damage players or destroy champions\r\n" + 
				":book: **Draw** - Add card from deck to the play area\r\n" + 
				":green_heart: **Heart** - Healthy healing :)\r\n" + 
				":star: **Mastery** - Higher mastery levels unlock additional effects for certain cards\r\n" + 
				":wastebasket: **Banish** - Permanently remove a weaker card from your hand or discard pile\r\n" +
				":syringe: **Revive** - Return a card from your discard pile to your hand\r\n" +
				":shield: **Shield** - Blocks indicated amount of incoming damage if you draw it your next turn";
				//":infinity: **Infinity** - Cannot be killed by power. Can be destroyed";
		String guide2 = "**Faction Conditions:**\r\n" +
				":closed_book: ``Echo`` - Activates if you have a RED card in your discard pile\r\n" +
				":blue_book: ``Dominion``- Activates if you have a RED, GREEN, and ORANGE card in your play area\r\n" +
				":green_book: ``Unify`` - Activates if you have another GREEN card in your play area\r\n" +
				":orange_book: ``Inspire`` - Activates if you have a champion in play\r\n" +
				"\r\n" +
				"**Example:**\r\n" +
				"[ **3** :green_book: ] :dagger: Fungal Hermit ( **1** :star: ) (**10** :star: = **5** :green_heart: )\r\n" +
				"This GREEN card costs **3** :diamond_shape_with_a_dot_inside: to recruit from the Center Row. "
				+ "When played, the player will gain **1** :star:. Then, if they have **10** or more :star:, they can gain "
				+ "an additional **5** :green_heart:. "
				+ "It is also a MERCENARY so it can be hired to gain its effect immediately rather than have it join the discard pile "
				+ "upon recruiting";
 		channel.sendMessage(guide1).queue();
 		channel.sendMessage(guide2).queue();
	}
	
	public static void printRelics(MessageChannel channel) {
		String relics = "**__Relics__**\r\n"+
						"Upon reaching 10 :star: for the first time, you can recruit one of the two relics based on your faction/color\r\n";
		for (int i = 0; i < GlobalVars.relics.length; i++) {
			Card r = GlobalVars.relics[i];
			relics += "\n"+Utils.numToEmoji[(i % 2)+1]+" "+r.toString()+"\n\t\t\t\t\t"+r.getText();
		}
				
		channel.sendMessage(relics).queue();
	}
	
	// For reactions
//	public static void editCards(MessageChannel channel, int page) {
//		String relics = "**__Relics__**\r\n"+
//						"Upon reaching 10 :star: for the first time, you can recruit one of the two relics based on your faction/color\r\n";
//		for (int i = 0; i < GlobalVars.relics.length; i++) {
//			Card r = GlobalVars.relics[i];
//			relics += "\n"+Utils.numToEmoji[(i % 2)+1]+" "+r.toString()+"\n\t\t\t\t\t"+r.getText();
//		}
//		
//		
//				
//		channel.editMessageById(messageId, buildCards(page).build()).queue();
//	}
	
	public static EmbedBuilder buildCards(int page) {
		if (page < 0) return null;
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Cards Library");
		embed.setColor(Color.MAGENTA);
		embed.setFooter("Page "+(page+1), null);
		// 10 cards per page
		
		// 0 is first
//		String cards = "";
//		String extraEffects = "";
//		for (int i = page*10; i < (page*10 + 10) && i < GlobalVars.cardNames.size(); i++) {
//			int num = (i+1) % 10;
//			cards += Utils.numToEmoji[num] + " " + GlobalVars.cardDatabase.get(GlobalVars.cardNames.get(i)).toString() + "\n";
//			extraEffects += Utils.numToEmoji[num] + " " + GlobalVars.cardDatabase.get(GlobalVars.cardNames.get(i)).getText() + "\n";
//		}
//		embed.addField("Cards",cards,true);
//		embed.addField("Extra Effects",extraEffects,true);
		
		String cards = "";
		for (int i = page*10; i < (page*10 + 10) && i < GlobalVars.cardNames.size(); i++) {
			Card c = GlobalVars.cardDatabase.get(GlobalVars.cardNames.get(i));
			cards += c.toString()+c.getText()+"\n";
		}
		
		embed.setDescription(cards);
		
		return embed;
	}

	public static void printCommands(MessageChannel channel) {
		String commands = "**__Actions/Commands__**\r\n" + 
				":arrow_forward: **Play** - Play a card in your Play Area to gain resources such as Skill, Swords, Boots, etc.\r\n" + 
				":pushpin: **Select** - Select a card from the Dungeon Row. Certain cards can be acquired for an amount of Skill and will join your deck. Devices can be acquired similarly, but their effect occurs immediately and does not join your deck. Monsters can be fought with Swords, and will supply the player with rewards.\r\n" + 
				":boot: **Move** - Use 1 boot to move to an adjacent room. Path effects are applied. Footsteps require 2 boots instead of 1. Monsters require a Sword to fight or else 1 damage is taken. Locks require a Key. If the destination room is a Crystal Cave, the player cannot use move for the rest of their turn.\r\n" + 
				":crystal_ball: **Teleport** - Use 1 teleport to move to an adjacent room. Path effects are not applied and it bypasses locks or one way paths.\r\n" + 
				":scroll: **Grab** - Pick up the artifact or Monkey Idol in this room. Artifacts cannot be dropped. \r\n" + 
				":shopping_cart: **Buy** - Spend 7 gold to obtain either a Key, Backpack, or Crown. Must be in one of four Market squares\r\n" + 
				":tropical_drink: **Use** - Use a consumable picked up from entering a room with a Secret.\r\n" + 
				":point_up_2: **Choose** - Choose an option when presented one by a specific card.\r\n" + 
				":wastebasket: **Trash** - Remove a card from your play area, discard pile, or deck completely. It does not return.\r\n" + 
				":fire: **Discard** - Remove a card from your play area and add it to your discard pile without receiving its effects.\r\n" + 
				":checkered_flag: **End** - Ends your turn. Must play all cards and finish all choose/trash effects\r\n" + 
				":books: **Deck** - Display all the cards in your deck and discard pile\r\n" + 
				":newspaper: **Info** - Lists all the unique cards and their effects\r\n";
		channel.sendMessage(commands).queue();
	}
	
	
	public static void sendExample(User user) {
		user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
			public void accept(PrivateChannel channel) {
				EmbedBuilder e = new EmbedBuilder();
				e.setTitle("Events");
				e.setDescription("``PenguEvan`` hit Aero for 6 :boom:\r\n" + 
						"``Ray`` selected Optio Crusher\r\n" + 
						"``Ray`` selected Wraethe Skirmisher\r\n" + 
						"``Ray`` hit Rolend for 1 :boom:\r\n" + 
						"``Aero`` played Systema A.I. :crossed_swords:\r\n" + 
						"``Aero`` selected Undergrowth Aspirant\r\n" + 
						"``Aero`` used Focus to gain 1 :star:\r\n" + 
						"``Aero`` hit PenguEvan for 3 :boom:\r\n" + 
						"``Rolend`` played Numeri Drones :crossed_swords:\r\n" + 
						"``Rolend`` played Optio Crusher :crossed_swords:\r\n" + 
						"``Rolend`` selected Korvus Legionnaire\r\n" + 
						"``Rolend`` hit Ray for 3 :boom:");
				e.setColor(Color.GRAY);
				Message m = channel.sendMessage(e.build()).complete();
				m.addReaction(GlobalVars.emojis.get("question")).queue();
				m.addReaction(GlobalVars.emojis.get("calendar_spiral")).queue();
				
				e.setTitle("Center Row");
				e.setDescription(null);
				String cards = ":regional_indicator_a: [ **3** :blue_book: ] Mainframe Abbot* **3** :shield: ( **1** :book: )\r\n" + 
						":regional_indicator_b: [ **2** :orange_book: ] Mining Drones ( **1** :diamond_shape_with_a_dot_inside: **1** :book: )\r\n" + 
						":regional_indicator_c: [ **7** :closed_book: ] :crossed_swords: Zen Chi Set, Godkiller* **5** :heartpulse:\r\n" + 
						":regional_indicator_d: [ **5** :closed_book: ] :dagger: Zara Ra, Soulflayer ( **4** :boom: **1** :star: )\r\n" + 
						":regional_indicator_e: [ **4** :green_book: ] :dagger: Ghostwillow Avenger* ( **4** :boom: )\r\n" + 
						":regional_indicator_f: [ **4** :green_book: ] Shardwood Guardian ( **2** :boom: **1** :book: )";
				String extraEffects = ":regional_indicator_a: (*You are* :blue_book: = **1** :star:)\r\n" + 
						":regional_indicator_b:\r\n" + 
						":regional_indicator_c: {**3** :boom: | :syringe: any :closed_book:}\r\n" + 
						":regional_indicator_d: (**10** :star: = **2** :wastebasket: )\r\n" + 
						":regional_indicator_e: (**15** :star: = Destroy all enemy :crossed_swords:)\r\n" + 
						":regional_indicator_f: (``Unify`` = **6** :green_heart:)";
				e.addField("Cards",cards, true);
				e.addField("Extra Effects",extraEffects, true);
				m = channel.sendMessage(e.build()).complete();
				m.addReaction(GlobalVars.emojis.get("question")).queue();
				m.addReaction(Utils.numToLetterEmojis[0]).queue();
				m.addReaction(Utils.numToLetterEmojis[1]).queue();
				m.addReaction(Utils.numToLetterEmojis[3]).queue();
				m.addReaction(Utils.numToLetterEmojis[4]).queue();
				m.addReaction(Utils.numToLetterEmojis[5]).queue();
				m.addReaction(GlobalVars.emojis.get("dagger")).queue();
				
				
				e.setTitle("Players & Champions");
				e.setColor(Color.GREEN);
				e.clearFields();
				e.addField("``Aero`` (A) **36** :heart: **7** :star:",":one: :blue_book: Systema A.I. **4** :heartpulse: {**1** :star: | **20** :star: = **2** :book:}",false);
				e.addField("``Rolend`` (B) **41** :orange_heart: **6** :star:",":two: :orange_book: Numeri Drones* **5** :heartpulse: {**1** :diamond_shape_with_a_dot_inside: | Play the next :orange_book: :crossed_swords: you select}\r\n"
						+ ":three: :orange_book: Optio Crusher **4** :heartpulse: {**3** :boom: | **10** :star: = **2** :boom:}",false);
				e.addField(":atom:``PenguEvan`` (C) **39** :green_heart: **5** :star:",":four: :green_book: Additri, Gaiamancer **5** :heartpulse: {**2** :boom: | **2** :boom: each :green_book: non :crossed_swords:}",false);
				e.addField("``Ray`` (D) **40** :blue_heart: **8** :star:","",false);
				m = channel.sendMessage(e.build()).complete();
				m.addReaction(GlobalVars.emojis.get("question")).queue();
				m.addReaction(GlobalVars.emojis.get("fast_forward")).queue();
				m.addReaction(Utils.numToNumEmoji[4]).queue();
				
				e.setTitle("**PenguEvan**'s Information");
				e.clearFields();
				e.addField("**Shards** :diamond_shape_with_a_dot_inside:","``5``",true);
				e.addField("**Power** :boom:","``6``",true);
				e.setFooter("SPORE CLE /  LE'SHAI K /  ORDER INI /  ADDITRI,  /  LE'SHAI K /", null);
				m = channel.sendMessage(e.build()).complete();
				m.addReaction(GlobalVars.emojis.get("question")).queue();
				m.addReaction(GlobalVars.emojis.get("atom")).queue();
				m.addReaction(GlobalVars.emojis.get("book 0")).queue();
				m.addReaction(Utils.numToNumEmoji[1]).queue();
				m.addReaction(Utils.numToNumEmoji[2]).queue();
				m.addReaction(Utils.numToNumEmoji[3]).queue();
				
				e.setTitle("**PenguEvan**'s Play Area");
				e.clearFields();
				cards = ":white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\r\n" + 
						":white_check_mark: :green_book: Le'shai Knight ( **3** :boom: )\r\n" + 
						":white_check_mark: :orange_book: Mining Drones ( **1** :diamond_shape_with_a_dot_inside: **1** :book: )\r\n" + 
						":white_check_mark: :blue_book: Order Initiate ( **2** :diamond_shape_with_a_dot_inside: )\r\n" + 
						":regional_indicator_e: :green_book: Spore Cleric ( **4** :green_heart: )\r\n" + 
						":white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\r\n";
				extraEffects = ":white_check_mark:\r\n" + 
						":white_check_mark: (``Unify`` = **3** :boom:)\r\n" + 
						":white_check_mark:\r\n" + 
						":white_check_mark: (``Dominion`` = **2** :star:)\r\n" + 
						":regional_indicator_e:\r\n" + 
						":white_check_mark:";
				e.addField("Cards :recycle:",cards, true);
				e.addField("Extra Effects",extraEffects, true);
				m = channel.sendMessage(e.build()).complete();
				m.addReaction(GlobalVars.emojis.get("question")).queue();
				m.addReaction(GlobalVars.emojis.get("card_box")).queue();
				m.addReaction(GlobalVars.emojis.get("fast_forward")).queue();
				m.addReaction(Utils.numToLetterEmojis[4]).queue();
				
				e.setTitle("Example");
				e.setColor(Color.WHITE);
				e.clearFields();
				e.setDescription("Here’s what an example game looks like. At first, there may seem to be a lot of confusing displays, but if "
						+ "you break it down, it’s just everything you learned in the tutorial. Also, you will play every turn like this; meaning "
						+ "you will use these same displays for the rest of the game\r\n" + 
						"\r\n" + 
						"Click on the question :question: for any of the displays to learn about them. The other reactions just show what the current player can do\r\n" + 
						"");
				e.setFooter("Example Game", null);
				channel.sendMessage(e.build()).queue();
			}
		});
	}
	
	// String[] is {desc, title, desc}
	
	//
	
	public static EmbedBuilder exampleDisplayDescription(String display) {
		EmbedBuilder e = new EmbedBuilder();
		e.setColor(Color.BLACK);
		e.setFooter("Example Game",null);
		if (display.contentEquals("Events")) {
			e.setTitle("Events");
			e.setDescription("This display lists the most recent actions performed in the game\r\n" + 
					"\r\n" + 
					"Clicking history :calendar_spiral: direct messages you your history so you can see the actions you performed throughout the whole game\r\n" + 
					"");
		} else if (display.contentEquals("Center Row")) {
			e.setTitle("Center Row");
			e.setDescription("This display lists the 6 available cards for recruiting. Each card has a cost and color indicated in the square brackets. Some cards have extra effects \r\n" + 
					"\r\n" + 
					"Clicking a letter, like :regional_indicator_a:, recruits a card normally. If you want to hire a Mercenary immediately, you click on the dagger :dagger: first, then the letter\r\n" + 
					"");
		} else if (display.contentEquals("Players & Champions")) {
			e.setTitle("Players & Champions");
			e.setDescription("This display lists all the players and their individual health and mastery. Under each player is a list of their current Champions. Each Champion "
					+ "is identified with a number to interact with. Here is where you exhaust your Champions to gain specific effects\r\n" + 
					"\r\n" + 
					"Clicking exhaust all :fast_forward: will quickly exhaust them all while the :four: exhausts only the specific one\r\n" + 
					"");
		} else if (display.contentEquals("**PenguEvan**'s Information")) {
			e.setTitle("Player Information");
			e.setDescription("This display lists your resources. This includes shards, power, and banishes if you have any. Remember, these will disappear when your turn ends\r\n" + 
					"\r\n" + 
					"Clicking Focus :atom: makes you trade one shard for one mastery, once per turn. The book :closed_book: makes you attack red which is already the default target in this 2v2 game. "
					+ "The numbers, like :one:, let you kill a Champion by assigning enough power to it to reduce its health to 0\r\n" + 
					"");
		} else if (display.contentEquals("**PenguEvan**'s Play Area")) {
			e.setTitle("Play Area");
			e.setDescription("This display lists the cards you can play (or have played) this turn. Cards that you have already played are marked with a check :white_check_mark:. "
					+ "Sometimes you want to wait before playing a certain card if you have to do another action before meeting its extra conditions. The recycle :recycle: indicates that "
					+ "you will shuffle your Discard Pile back into your Deck next turn when you try to draw 5 cards\r\n" + 
					"\r\n" + 
					"Clicking Deck :card_box: lets you see the contents of your current Deck and Discard Pile for reference. :fast_forward: plays all non-played cards while :regional_indicator_e: plays the specific one\r\n" + 
					"");
		}
		
		return e;
	}

	public static EmbedBuilder[] getEmbeds(int page) {
		EmbedBuilder[] ebs = new EmbedBuilder[2];
		EmbedBuilder eb1 = new EmbedBuilder();
		eb1.setColor(Color.GRAY);
		eb1.setDescription(embedData[page][0]);
		ebs[0] = eb1;
		EmbedBuilder eb2 = new EmbedBuilder();
		eb2.setColor(Color.GRAY);
		eb2.setTitle(embedData[page][1]);
		if (page == 4) { // info specific ones
			eb2.addField("**Shards** :diamond_shape_with_a_dot_inside:","``4``",true);
			eb2.addField("**Power** :boom:","``1``",true);
		} else if (page == 10) { // info specific ones
			eb2.addField("**Shards** :diamond_shape_with_a_dot_inside:","``5``",true);
			eb2.addField("**Power** :boom:","``2``",true);
		} else if (page == 18) {
			eb2.addField("**Shards** :diamond_shape_with_a_dot_inside:","``6``",true);
			eb2.addField("**Power** :boom:","``6``",true);
		} else {
			eb2.setDescription(embedData[page][2]);
		}
		ebs[1] = eb2;
		return ebs;
	}
	
	public static String[][] embedData = {
			{"","",""},
			{"Shards is a multiplayer 2 to 4 player card-based game. Players take turns playing cards and gaining resources. One of these is called **power**. "
					+ "By assigning **power**, players can reduce the health of other players. The only way to win is to lower the health of your opponent(s) to 0 "
					+ "before they do the same to yours","Note","This tutorial is intended to provide a visual guide but some aspects may be slightly different from the actual game"},
			{"Shards is a deck building game. This means each player will begin with a basic deck of weak cards. Throughout the game, they will gain a resource "
					+ "called **shard** :diamond_shape_with_a_dot_inside: which lets them recruit (buy) more cards. These cards will join their Deck, and therefore cause the player to have access to stronger "
					+ "and stronger cards as the game progresses. The cards below each have a Play Effect listed in the parentheses","Example of Cards","[ :notebook_with_decorative_cover: ] Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
					+ "[ **3** :orange_book: ] Reactor Drone ( **3** :diamond_shape_with_a_dot_inside: )"},
			{"Your starting Deck has **10** cards. Each turn you will draw 5 cards which are shown in your Play Area. You should always "
					+ "play all your cards. Try that now by clicking on play all :fast_forward:","Your Play Area","**Cards**\n:regional_indicator_a: :notebook_with_decorative_cover: Blaster ( **1** :boom: )\n"
							+ ":regional_indicator_b: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_c: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_d: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_e: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"},
			{"Nice. By playing cards, you will gain various resources. The game will automatically total up your **shards** and **power** and display it in your Player Information. Any resource listed here,  " 
					+ "including the **shards** and **power**, disappears at the end of your turn. It's best to try to use it all this turn to avoid wasting any","Your Information"},
			{"You now have enough **shards** to recruit a card from the Center Row. The Center Row is like a shop that all players share. It will always display six cards (two for tutorial) you can recruit, "
					+ "immediately replacing any recruited cards with a new one from the main Deck. "
					+ "The cost of the card is indicated next to the Book :orange_book:. Try to recruit the top card which is identified as :regional_indicator_a:","Center Row",
					"**Cards**\n:regional_indicator_a: [ **4** :orange_book: ] Reactor Drone ( **3** :diamond_shape_with_a_dot_inside: )\n"
					+ ":regional_indicator_b: [ **5** :blue_book: ] :crossed_swords: Systema A.I. **4** :heartpulse:"},
			{"Good job. Your Deck and Discard Pile is listed below. Note that the five cards in your Play Area are not displayed. As you can see, the card you recruited has been added to your Discard Pile, "
					+ "not your Play Area or Deck. This means you do not gain its "
					+ "Play Effect yet (gaining 3 **shards**), but don't worry, you'll get it later. End your turn now by clicking on the flag :checkered_flag:","Your Deck",
					"**Deck**\n:notebook_with_decorative_cover: Crystal :notebook_with_decorative_cover: Crystal :notebook_with_decorative_cover: Crystal "
					+ ":notebook_with_decorative_cover: Infinity Shard :notebook_with_decorative_cover: Shard Reactor\n**Discard Pile**\n:orange_book: Reactor Drone"},
			{"By ending your turn, the five cards from your first turn are put in your Discard Pile. Now that it’s your second turn, you can see that you drew the last five cards of your starting Deck; "
					+ "your Deck is now empty. Again you should first play your cards so you have resources to perform actions.","Your Play Area","**Cards**\n:regional_indicator_a: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_b: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_c: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_d: :notebook_with_decorative_cover: Infinity Shard ( **2** :boom: )\n"
							+ ":regional_indicator_e: :notebook_with_decorative_cover: Shard Reactor ( **2** :diamond_shape_with_a_dot_inside: )\n"},
			{"Looking at the Center Row, the card you recruited got replaced by a new card. The dagger :dagger: indicates that this card is a **mercenary** type. This means you have the "
					+ "option to, instead of recruiting it normally, hire it instantly. Do that now by clicking the dagger :dagger: first, then the letter :regional_indicator_a:","Center Row",
					"**Cards**\n:regional_indicator_a: [ **2** :green_book: ] :dagger: Spore Cleric ( **4** :green_heart: )\n"
							+ ":regional_indicator_b: [ **5** :blue_book: ] :crossed_swords: Systema A.I. **4** :heartpulse:"},
			{"Good. Instead of the card going to your Discard Pile, it's now in your Play Area so you can play it this turn. However, unlike the rest of the cards in your hand, "
			+ "it will not go to your Discard Pile when you end your turn. Think of this as a short term gain instead of the long term investment from recruiting a card normally. Spore Cleric restores 4 **health** :green_heart: and is great when you're in desperate need of health","Your Play Area",
			"**Cards**\n:white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
					+ ":white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
					+ ":white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
					+ ":white_check_mark: :notebook_with_decorative_cover: Infinity Shard ( **2** :boom: )\n"
					+ ":white_check_mark: :notebook_with_decorative_cover: Shard Reactor ( **2** :diamond_shape_with_a_dot_inside: )\n"
					+ ":white_check_mark: :green_book: :dagger: Spore Cleric ( **4** :green_heart: )"},
			{"Now remember, the goal is to gather **power** :boom:, whether it's little by little or a lot in one turn. You have 2 now which you can assign to a player. You can choose a specific target "
					+ "by clicking on the book. If you end your turn, any left over power is automatically assigned to the next player. Try to attack Blue :blue_book: right now then end your turn.","Your Information"},
			{"On your third turn, you tried to draw 5 cards but remember, your Deck is empty. Whenever this happens, your Discard Pile is automatically shuffled and becomes your new Deck. This "
					+ "cycle ensures that you never run out of cards while simultaneously providing you with the cards you recruited before","Your Deck (before cycle)",
					"**Deck**\n*(Empty)*\n**Discard Pile**\n:notebook_with_decorative_cover: Blaster :notebook_with_decorative_cover: Crystal :notebook_with_decorative_cover: Crystal"
					+ " :notebook_with_decorative_cover: Crystal :notebook_with_decorative_cover: Crystal :notebook_with_decorative_cover: Crystal :notebook_with_decorative_cover: Crystal"
					+ " :notebook_with_decorative_cover: Crystal :notebook_with_decorative_cover: Infinity Shard :orange_book: Reactor Drone :notebook_with_decorative_cover: Shard Reactor"},
			{"Remember, you had 11 cards in your Discard Pile. This became your Deck which you just drew 5 from. Lucky! You got the new card you recruited on your first turn. Play your "
					+ "cards so you will have **shards** to recruit from the Center Row again","Your Play Area",
					"**Cards**\n:regional_indicator_a: :notebook_with_decorative_cover: Blaster ( **1** :boom: )\n"
							+ ":regional_indicator_b: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_c: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_d: :orange_book: Reactor Drone ( **3** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_e: :notebook_with_decorative_cover: Shard Reactor ( **2** :diamond_shape_with_a_dot_inside: )"},
			{"The second card has swords :crossed_swords: which indicates that this card is a **champion** type. **Champions** are recruited like normal, but they "
					+ "act differently when you play them. Notice also that some cards have extra effects listed in the Center Row. Go ahead and recruit the **champion** now","Center Row",
					"**Cards**\n:regional_indicator_a: [ **3** :blue_book: ] :dagger: Shard Abstractor ( **2** :star: )\n"
							+ ":regional_indicator_b: [ **5** :blue_book: ] :crossed_swords: Systema A.I. **4** :heartpulse:\n\n**Extra Effects**\n"
							+ ":regional_indicator_b: {**1** :star: | **20** :star: = **2** :book:}"},
			{"For the sake of the tutorial, we have skipped to a future turn where you drew the **champion** you recruited. When you play a **champion**, it will go onto the board and stay "
					+ "there until it dies (at which point it will go to your Discard Pile). It can be killed by other players assigning **power** to it. However, they cannot do partial "
					+ "damage to it; meaning that because it has 4 **health** :heartpulse:, the opponent needs a minimum of 4 **power** :boom: in one turn to kill it. Play all your cards now.","Your Play Area",
					"**Cards**\n:regional_indicator_a: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_b: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_c: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_d: :orange_book: Reactor Drone ( **3** :diamond_shape_with_a_dot_inside: )\n"
							+ ":regional_indicator_e: :blue_book: :crossed_swords: Systema A.I. **4** :heartpulse:\n\n**Extra Effects**\n"
							+ ":regional_indicator_e: {**1** :star: | **20** :star: = **2** :book:}"},
			{"Cool. Once per turn, you can exhaust each of your **champions** to gain its specific exhaust effect. Systema A.I. gives you 1 **mastery** :star: which you happen to have 19 of. "
					+ "Unlike other resources, you do not lose **mastery** when your turn ends. Exhaust it by clicking on the identifier number","Players & Champions",
					"``You`` **50** :heart: **19** :star:\n:one: :blue_book: Systema A.I. **4** :heartpulse: {**1** :star: | **20** :star: = **2** :book:}"},
			{"Great. Notice that there is also a conditional effect represented by the equals sign. If the left side of the condition is met, the right side is rewarded. Many conditional "
					+ "effects require a certain level of **mastery** :star:. For example, this one requires you to have at least 20 **mastery**, which you met after exhausting your **champion**. "
					+ "The reward is 2 **draw** :book: which means you automatically draw 2 more cards from your deck which you can play this turn. Go ahead and play them all","Your Play Area",
					"**Cards**\n:white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":white_check_mark: :orange_book: Reactor Drone ( **3** :diamond_shape_with_a_dot_inside: )\n"
							+ ":white_check_mark: :blue_book: :crossed_swords: Systema A.I. **4** :heartpulse:\n"
							+ ":regional_indicator_f: :notebook_with_decorative_cover: Blaster ( **1** :boom: )\n"
							+ ":regional_indicator_g: :notebook_with_decorative_cover: Infinity Shard ( **2** :boom: )\n\n**Extra Effects**\n"
							+ ":regional_indicator_f: {**1** :star: | **20** :star: = **2** :book:}\n"
							+ ":regional_indicator_g: (**10** :star: = **1** :boom: | **20** :star: = **2** :boom: | **30** :star: = **99** :boom: )"},
			{"Notice that one of the new cards you drew, called Infinity Shard, has multiple conditional effects. Since you have 20 :star:, you are rewarded with the first two "
					+ "conditional effects. In total, you get 2 + 1 + 2 :boom:. If you had 30 :star: (the maximum), you would actually gain an additional 99 :boom: which is an automatic win. "
					+ "In general, having high **mastery** gives you access to better card effects","Your Play Area",
					"**Cards**\n:white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":white_check_mark: :notebook_with_decorative_cover: Crystal ( **1** :diamond_shape_with_a_dot_inside: )\n"
							+ ":white_check_mark: :orange_book: Reactor Drone ( **3** :diamond_shape_with_a_dot_inside: )\n"
							+ ":white_check_mark: :blue_book: :crossed_swords: Systema A.I. **4** :heartpulse:\n"
							+ ":white_check_mark: :notebook_with_decorative_cover: Blaster ( **1** :boom: )\n"
							+ ":white_check_mark: :notebook_with_decorative_cover: Infinity Shard ( **2** :boom: )\n\n**Extra Effects**\n"
							+ ":white_check_mark: {**1** :star: | **20** :star: = **2** :book:}\n"
							+ ":white_check_mark: (**10** :star: = **1** :boom: | **20** :star: = **2** :boom: | **30** :star: = **99** :boom: )"},
			{"You can also gain one **mastery** :star: once per turn at the cost of one **shard** :diamond_shape_with_a_dot_inside:. This action is called using Focus :atom: and is great for any leftover **shard(s)** "
					+ "you have. Additionally, it is performed automatically if you end your turn with at least one **shard**. Click on Focus :atom: to do this manually","Your Player Information"},
			{"You may have noticed that cards have different colored books. They represent the unique factions they belong to. The red faction :closed_book: prioritizes gaining **power**. "
					+ "The blue faction :blue_book: prioritizes **drawing** cards, gaining **shield**, and increasing **mastery**. The green faction :green_book: prioritizes both **healing** and **power**. The orange "
					+ "faction :orange_book: prioritizes summoning many **champions** to combo together. When you begin the game, you will declare a faction/color. However, you can recruit and use ANY "
					+ "color card, even if it’s not your color","Example of Cards",
					"[ **2** :closed_book: ] :dagger: Nil Assassin ( **5** :boom: )\n"
					+ "[ **3** :blue_book: ] :dagger: Shard Abstractor ( **2** :star: )\n"
					+ "[ **2** :green_book: ] :dagger: Spore Cleric ( **4** :green_heart: )\n"
					+ "[ **5** :orange_book: ] :crossed_swords: Optio Crusher **4** :heartpulse: {**3** :boom: | **10** :star: = **2** :boom:}"},
			{"There are two reasons to declare a specific faction. First, there are a few cards that have conditional effects only rewarded if you are that specific "
					+ "faction. Secondly, whenever a player reaches 10 :star: for the first time, they can choose one of two powerful cards to add to their deck based "
					+ "on their faction. Because there are 2 of these cards, called Relics :trophy:, per faction, there are 8 Relics in total. It’s a good idea to assess the situation "
					+ "to determine which relic is best for you. Entropic Talons is a powerful Relic that gives you :boom: for healing","Example of Cards",
					"[ **3** :green_book: ] :dagger: Hounds of Volos* ( **5** :green_heart: ) (*You are :green_book:* = **5** :boom:)\n"
					+ "[ :green_book: ] __Entropic Talons__* ( **2** :book: ) (**1** :boom: each :green_heart: you gain this turn | **20** :star: = **10** :green_heart:)"},
			{"Finally, each faction has a unique condition. Cards of the respective faction can require that condition to gain a reward. For red, the ``Echo`` condition "
					+ "requires you to have a red card in your Discard Pile. For blue, the ``Dominion`` condition requires you to have a red, green, and orange card in your Play Area. For green, the ``Unify`` "
					+ "condition requires you to have another green non-Champion card in your Play Area. For orange, the ``Inspire`` condition requires you to have a Champion on board. Players can fulfill conditions "
					+ "of any faction","Example of Cards",
					"[ **1** :closed_book: ] Wraethe Skirmisher ( **2** :boom: ) (``Echo`` = **4** :boom:)\n" 
					+ "[ **1** :blue_book: ] Order Initiate ( **2** :diamond_shape_with_a_dot_inside: ) (``Dominion`` = **2** :star:)\n"
					+ "[ **3** :green_book: ] :dagger: Le'shai Knight ( **3** :boom: ) (``Unify`` = **3** :boom:)\n"
					+ "[ **2** :orange_book: ] Limiter Drones ( **1** :book: ) (``Inspire`` = **1** :wastebasket:)\n"},
			{"There are a few other effects that you may encounter throughout the game. Some cards have a certain amount of **shield** :shield: assigned to that card. If you "
					+ "are attacked and one of the 5 cards you will draw next turn has a **shield**, you will block that amount of damage. This means that recruiting a **shield** "
					+ "card doesn’t guarantee any immediate reduced damage, but there is a chance that you will block some in the future","Example of Card",
					"[ **5** :blue_book: ] Cryptofist Monk **8** :shield: ( **1** :book: )"},
			{"Another effect is called **banish** :wastebasket:. This lets you remove a card of your choice permanently from either your Play Area or your Discard Pile. However, you can only remove a card from your Play "
					+ "Area if you haven’t played it that turn yet. Banishing is useful because it lets you remove weak, starting cards from your total Deck so when you cycle, you will draw better cards more often",
					"Example of Cards",
					"[ **2** :closed_book: ] :dagger: Shadow Apostle ( **2** :boom: **1** :wastebasket: )"},
			{"The next effect is called **revive** :syringe:. This lets you return a card of your choice from your Discard Pile to your hand. You will also play it automatically. Shadebound Sentry lets you revive any "
					+ "**mercenary** type card. "
					+ "At any point, you can click the Deck :card_box: to check your Deck and Discard Pile contents. Mercenary cards are in italics","Example of Card",
					"[ **3** :closed_book: ] Shadebound Sentry* ( **3** :boom: ) (:syringe: any :dagger:)"},
			{"The last effect is called **choose**. Certain cards give you multiple choices so you have flexibility based on the situation. Ojas, Genesis Druid lets you play any non-Champion card from your Play Area again. "
					+ "Thorn Zealot lets you choose any Champion in play to destroy","Example of Cards",
					"[ **4** :green_book: ] Ojas, Genesis Druid* (Play a non :crossed_swords: again | **20** :star: = Play it again)\n"
					+ "[ **3** :green_book: ] Thorn Zealot* **3** :shield: ( **1** :book: ) (``Unify`` = Destroy a :crossed_swords:)"},
			{"You are almost ready to play! Last thing you should try to remember is the controls. Almost all of your actions can be performed by clicking on the reactions in their respective places. "
					+ "The game is also optimized to show you only the actions you can perform. The game tries to reduce the amount of button pressing through auto attacking and auto focusing when you end your turn. However, "
					+ "certain effects like banish, revive, and choose will require a card name. In this case, you must type out the command and the card name i.e. \"]revive spore cleric\"","Reactions",
					"**Play All/Exhaust All** :fast_forward:\n"
					+ "**End Turn (and attack)** :checkered_flag:\n"
					+ "**Focus** :atom:\n"
					+ "**Hire as Mercenary** :dagger:\n"
					+ "**Deck** :card_box:\n"
					+ "**History** :calendar_spiral:\n"
					+ "Now you are ready to play!"}
	};
}
