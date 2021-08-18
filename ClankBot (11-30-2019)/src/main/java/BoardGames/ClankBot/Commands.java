package BoardGames.ClankBot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;

public class Commands {
	// Set it null to delete it
//	public Commands(String command,MessageReceivedEvent event,String[] args) {
//		this.command = command;
//		this.args = args;
//		this.authorID = event.getAuthor().getId();
//		this.event = event;
//		this.channel = event.getChannel();
//		this.user = event.getAuthor();
//		this.guild = event.getGuild();
//	}
//	
//	public Commands(String command, GuildMessageReactionAddEvent event, String[] args) {
//		this.command = command;
//		this.args = args;
//		this.authorID = event.getUser().getId();
//		this.event = event;
//		this.channel = event.getChannel();
//		this.user = event.getUser();
//		this.guild = event.getGuild();
//	}
	
	public Commands(String command, Event event, String[] args) {
		this.command = command;
		this.args = args;
		this.event = event;
		if (event instanceof GuildMessageReactionAddEvent) {
			this.authorID = ((GuildMessageReactionAddEvent) event).getUser().getId();
			this.user = ((GuildMessageReactionAddEvent) event).getUser();
			this.channel = ((GuildMessageReactionAddEvent) event).getChannel();
			this.guild = ((GuildMessageReactionAddEvent) event).getGuild();
		} else if (event instanceof MessageReceivedEvent) {
			this.authorID = ((MessageReceivedEvent) event).getAuthor().getId();
			this.user = ((MessageReceivedEvent) event).getAuthor();
			this.channel = ((MessageReceivedEvent) event).getChannel();
			this.guild = ((MessageReceivedEvent) event).getGuild();
		} else if (event instanceof GuildMessageReactionRemoveEvent) {
			this.authorID = ((GuildMessageReactionRemoveEvent) event).getUser().getId();
			this.user = ((GuildMessageReactionRemoveEvent) event).getUser();
			this.channel = ((GuildMessageReactionRemoveEvent) event).getChannel();
			this.guild = ((GuildMessageReactionRemoveEvent) event).getGuild();
		}
	}

	public void attempt() {
		// Check if requirements/args are met
		if (command.contentEquals("cg") || command.equals("create_game")) {
			// Check if there already is a game in this channel
			for (Game g : GlobalVars.currentGames) {
				if (g.getGameChannel().equals(channel)) {
					return;
				}
			}
		}
		if (command.contentEquals("eg") || command.equals("end_game")) {
			// Check if this is an whitelisted id
			if (!Utils.isAdmin(authorID)) return;
		}
		action();
	}
	
	public void action() {
		//System.out.println("Command is: "+command);
		//System.out.println("Args is: "+Arrays.toString(args));
		
		// Debug commands
		if (command.equals("spawnArtifact")) {
			for (Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(channel)) {
					if (game.getStatus().contentEquals("ingame")) {
						if (game.getCurrentPlayer().getPlayerID().contentEquals(authorID)) {
							String value = args[0];
							game.getCurrentPlayer().pickup("Artifact"+value);
							game.updatePlayArea(game.getCurrentPlayer(), false);
						}
					}
				}
			}
		}
		
		if (command.equals("getTeleport")) {
			for (Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(channel)) {
					if (game.getStatus().contentEquals("ingame")) {
						if (game.getCurrentPlayer().getPlayerID().contentEquals(authorID)) {
							game.getCurrentPlayer().updateTeleports(1);
							game.updateInfo(game.getCurrentPlayer(), false);
						}
					}
				}
			}
		}
		
		
		if (command.equals("ping")) {
			channel.sendMessage("pong").queue();
			return;
		}
		
		if (command.equals("test")) {
			System.out.println(":regional_indicator_"+((char)(Integer.parseInt(args[0])+97))+":");
			String msgID = ((GenericMessageEvent) event).getMessageId();
			channel.addReactionById(msgID,"🔗").queue();
		}
		
		if (command.equals("embedtest")) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.YELLOW);
//			embed.setTitle("Title");
//			embed.addField(":book:s",":book:s", true);
//			embed.addField("**B**",":book:",true);
//			embed.addField("_C_","TestA",true);
//			embed.addField("__D__","TestA",true);
//			embed.addField("``E``","TestA",true);
//			embed.addField("`F`","TestA",true);
//			embed.addField("G","TestA",true);
//			embed.addField("H","TestA",true);
//			embed.addField("I","TestA",true);
//			embed.addField("J","TestA",true);
//			embed.addField("K","TestA",true);
//			embed.addField("L","TestA",true);
//			embed.addField("M","TestA",true);
//			embed.addField("N","TestA",true);
//			embed.addField("O","TestA",true);
			embed.setTitle("Market & Treasures *hi* :heart:");
			embed.addField("In Stock (**7x**:moneybag: each)",":key:**5** :key:**5** :briefcase:**5** :briefcase:**5** :crown:**9** :crown:**8** :crown:**7**", false);
			embed.addField("``Rolend`` **5x**:moneybag:",":key:**5** :wine_glass:**7**", false);
			embed.addField("``Aero`` **7x**:moneybag:",":briefcase:**5** :egg:**3**", false);
			channel.sendMessage(embed.build()).queue();
		}
		
//		if (command.equals("giveClank")) {
//			for (Game game : GlobalVars.currentGames) {
//				if (game.getGameChannel().equals(event.getChannel())) {
//					if (game.getStatus().contentEquals("ingame")) {
//						if (game.getCurrentPlayer().getPlayerID().contentEquals(authorID)) {
//							game.giveOthersClank(1);
//						}
//					}
//				}
//			}
//		}
		
		if (command.contentEquals("help")) {
			String helpMenu;
			if (args.length > 0 && args[0].contentEquals("2")) {
				helpMenu = "**__Selecting Cards and Building Your Deck__**\r\n" + 
						"The Dungeon Row consists of 10 cards labeled 0 through 9. The first 4 (0-3), the **Reserve**, and are up for selection until they run out, whereas the last 6 (4-9), the **Rotation**, are replaced at the end of a player’s turn. \r\n" + 
						"\r\n" + 
						":blue_book: **[Basic]** Upon acquiring, the card is placed in your discard pile\r\n" + 
						":green_book: **[Companion]** Same as [Basic] but it can trigger other cards’ effects\r\n" + 
						":orange_book: **[Device]** Immediate rewards upon selecting\r\n" + 
						":crossed_swords: **[Monster]** Costs Swords instead of Skill. Immediate rewards upon selecting\r\n" + 
						"\r\n" + 
						"**__Market Items__**\r\n" + 
						"Can be bought in Rooms 18, 19, 24, and 25 for 7 gold :moneybag:\r\n" + 
						":key: Key - worth 5 points; allows player to travel through locked paths\r\n" + 
						":briefcase: Backpack - worth 5 points; allows player to carry one more artifact \r\n" + 
						":crown: Crown - worth 10, 9, and 8 points for first, second, and third crown, respectively\r\n" + 
						"\r\n" + 
						"**__Treasures__**\r\n" + 
						":scroll: **Artifacts** - worth the number of points printed; needed to finish the game with points\r\n" + 
						":monkey_face: **Monkey Idol** - worth 5 points; can pick up 1 each time a player is in Room 18\r\n" + 
						":wine_glass: **Chalice** - worth 7 points\r\n" + 
						":egg: **Dragon Egg** - worth 3 points; increases Dragon level upon picking it up\r\n" + 
						"\r\n" + 
						"**__Ending the game__**\r\n" + 
						"Any player can pick up an artifact and escape to the starting point. Other players then have 4 final turns to collect points and/or escape. During every one of the first escaped player’s turn, there will be a dragon attack :dragon:. After the 4 turns, all players still left instantly die. Dying in the dungeon or dying without at least one artifact results in disqualification. Dying with an artifact above ground has no effect, but escaping with at least one artifact grants 20 additional points.\r";
			} else {
				helpMenu = "**__How to play Clank__**\r\n" + 
						"Clank is a multiplayer deck-building game where up to 4 players compete to secure the most points without dying in the depths of the dungeon. In this deck-building game, all players start with the same 10 basic cards. Players will take turns playing cards and performing actions while simultaneously building up their deck for future turns. Each turn, the active player draws 5 cards and plays them **all** in any order. Cards will generate different resources.\r\n" + 
						"\r\n" + 
						"**__Objective: Finish the game with the most points :star:__**\r\n" + 
						"Points come from **artifacts,** other **treasures**, certain **cards**, and leftover **gold** :moneybag:. However, all points will be lost if the player dies in the dungeon or without an artifact. \r\n" + 
						"\r\n" + 
						"**__Resources__**\r\n" + 
						":diamond_shape_with_a_dot_inside: **Skill** - Currency used to acquire new cards for your deck\r\n" + 
						":boot: **Boots** - Used to move to a new room\r\n" + 
						":crossed_swords: **Swords** - Used to negate damage from monsters on paths or for defeating monsters in the dungeon row\r\n" + 
						":moneybag: **Gold** - Used to buy either a key, backpack, or crown from the **market** (See *Market Items*)\r\n" + 
						":warning: **Clank** - (harmful) During a dragon attack :dragon:, all players’ **board clank** becomes **bag clank**, and a specified number of bag clanks is drawn. A player takes damage if his/her bag clank is drawn.\r\n";
			}
			channel.sendMessage(helpMenu).queue();
			return;
		}
		
		if (command.contentEquals("dungeonrow")) {
			String guide = "**__Dungeon Row Guide__**\r\n" + 
					"\r\n" + 
					"[   ] - Indicates card type\r\n" + 
					"(   ) - Immediate effects upon selection\r\n" + 
					"{   } - Effects when played\r\n" + 
					"\r\n" + 
					"*Note: The number before the card type indicates the cost of the card*\r\n" + 
					"\r\n" + 
					"**Card Types:**\r\n" + 
					":blue_book: **Basic** - Who said basic cards can't be powerful?\r\n" + 
					":green_book: **Companion** - Some cards may have additional effects when played with companion cards (e.g. **IF** :green_book:, **2** :diamond_shape_with_a_dot_inside:). \r\n" + 
					":orange_book: **Device** - Grants immediate one-time effects.\r\n" + 
					":gem: **Gem** - Gives a lot of points (:star:), but increases player's board clank (:warning:) by 2 upon selection.\r\n" + 
					":pick: **Pickaxe** - Can only be selected underground\r\n" + 
					":crossed_swords: **Monster** - Can be slain with swords (:crossed_swords:) and drops loot. Doesn't join player's deck.\r\n" + 
					":dragon: **Dragon** - Invokes the wrath of the dungeon's dragon upon appearance in Dungeon Row. The dragon attacks by randomly pulling clanks from the clank bag.\r\n" + 
					":exclamation: **Danger** - Increases the number of clanks pulled during dragon attacks by 1 when in Dungeon Row.\r\n" + 
					"\r\n" + 
					"**Effects:**\r\n" + 
					":diamond_shape_with_a_dot_inside: **Skill** - Currency used to select cards from the Dungeon Row. Resets to 0 upon ending turn.\r\n" + 
					":boot: **Boot** - Grants movement. Paths with footsteps require 2 boots.\r\n" + 
					":crossed_swords: **Swords** - Grants attack power that can be used to slay Dungeon Row monsters (:crossed_swords:) or fight off monsters on paths (e.g. ]m 34,1 moves into Room 34, using 1 sword to fight off 1 monster)\r\n" + 
					":book: **Draw** - Add card from deck to the play area.\r\n" + 
					":warning: **Clank** - Increases the player's board clank. Board clanks are transferred into bag upon dragon attack. Bag clanks cannot be removed.\r\n" + 
					":heart: **Heart** - Healthy healing :)\r\n" + 
					":moneybag: **Gold** - Currency for *Market*. All items in shop cost 7 gold. 1 :moneybag: = 1 :star:\r\n" + 
					":crystal_ball: **Teleport** - Teleport to an adjacent room. Circumvents any path effects.";
			channel.sendMessage(guide).queue();
			return;
		}
		
		if (command.contentEquals("commands")) {
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
			return;
		}
		
		if (command.equals("i") || command.contentEquals("info")) {
			String cardName = "";
			for (int i = 0; i < args.length; i++) {
				if (!args[i].contentEquals("1")) cardName += " "+args[i];
			}
			// Info page 1
			if (cardName.contentEquals("")) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("Information Page 1");
				String cards = "";
				int count = 1;
				for (String key : GlobalVars.cardInfo.keySet()) {
					cards += "\n**"+key+":** "+GlobalVars.cardInfo.get(key);
					if (count > 6) break;
					count++;
				}
				embed.addField("Unique Cards", cards, true);
				channel.sendMessage(embed.build()).queue();
			// Info page 2
			} else if (cardName.contentEquals(" 2")) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle("Information Page 2");
				String cards = "";
				int count = 1;
				for (String key : GlobalVars.cardInfo.keySet()) {
					if (count > 7) cards += "\n**"+key+":** "+GlobalVars.cardInfo.get(key);
					count++;
				}
				embed.addField("Unique Cards", cards, true);
				channel.sendMessage(embed.build()).queue();
			} else {
				// Info for each card
				EmbedBuilder embed = new EmbedBuilder();
				Card c = GlobalVars.cardDatabase.get(cardName.substring(1));
				// Add bell for priority cards
				if (c.hasPriorityHint()) {
					embed.addField(c.getType().toUpperCase(),":bell: "+c.toString(), true);
				} else {
					embed.addField(c.getType().toUpperCase(),c.toString(), true);
				}
				if (c.isUnique() && !c.isHiddenUnique()) {
					embed.addField("Unique Effect",GlobalVars.cardInfo.get(cardName.substring(1)), true);
				}
				embed.setColor(Color.MAGENTA);
				channel.sendMessage(embed.build()).queue();
			}
		}
		
		if (command.equals("create_game") || command.contentEquals("cg")) {
			Game newGame = new Game(channel,guild,authorID);
			GlobalVars.add(newGame);
			System.out.println("[DEBUG LOG/Commands.java] Created game");
			return;
		}
		
		// Commands that require a game (any status or player)
		Game game = null;
		for (Game g : GlobalVars.currentGames) {
			if (g.getGameChannel().equals(channel)) {
				game = g;
			}
		}
		// Didn't find game
		if (game == null) return;
		
		if (command.contentEquals("st") || command.contentEquals("start")) {
			game.start();
		}
		
		if (command.contentEquals("j") || command.equals("join")) {
			if (game.getStatus().contentEquals("pregame")) {
				// Can join twice atm
				if (game.getPlayerCount() < 4) {
					game.addPlayer(authorID);
				} else {
					// full
				}
			}
		}
		
		if (command.contentEquals("q") || command.contentEquals("quit")) {
			game.quitGame(authorID);
		}
		
		if (command.contentEquals("t") || command.contentEquals("transfer")) {
			if (event instanceof MessageReceivedEvent) {
				if (((MessageReceivedEvent) event).getMessage().getMentionedMembers().size() > 0) {
					game.transfer(authorID, ((MessageReceivedEvent) event).getMessage().getMentionedMembers().get(0).getUser().getId());
				}
			}
		}
		
		if (command.contentEquals("ve") || command.contentEquals("vote_end")) {
			if (game.getStatus().contentEquals("ingame")) {
				game.voteEnd(authorID);
			}
		}
		
		if (command.contentEquals("uve") || command.contentEquals("unvote_end")) {
			if (game.getStatus().contentEquals("ingame")) {
				game.unvoteEnd(authorID);
			}
		}
		
		if (command.contentEquals("eg") || command.contentEquals("end_game")) {
			if (game.getStatus().contentEquals("ingame")) {
				System.out.println("[DEBUG LOG/Commands.java] Trying to force end game");
				// Could be buggy
				game.gameOver();
			}
		}
		
		if (command.contentEquals("h") || command.contentEquals("history")) {
			if (event instanceof MessageReceivedEvent) {
				if (((MessageReceivedEvent) event).getMessage().getMentionedMembers().size() > 0) {
					game.sendHistory(user,((MessageReceivedEvent) event).getMessage().getMentionedMembers().get(0).getUser().getId());
					return;
				} 
			}
			game.sendHistory(user,authorID);
		}
		
		if (command.contentEquals("de") || command.contentEquals("deck")) {
			if (game.getStatus().contentEquals("ingame")) {
				if (event instanceof MessageReceivedEvent) {
					if (((MessageReceivedEvent) event).getMessage().getMentionedMembers().size() > 0) {
						game.displayDeck(((MessageReceivedEvent) event).getMessage().getMentionedMembers().get(0).getUser().getId());
						return;
					} 
				}
				game.displayDeck(authorID);
			}
		}
		
		// Commands that require the command user to be the current player or admin
		if (game.getCurrentPlayer() == null || (!Utils.isAdmin(authorID) && !game.getCurrentPlayer().getPlayerID().equals(authorID))) {
			return;
		}
		
		if (command.equals("m") || command.contentEquals("move")) {
			// Doesn't support letters here
			int[] swords = new int[args.length];
			for (int i = 0; i < args.length; i++) {
				// Check for usage of swords
				if (args[i].contains(",")) {
					swords[i] = Integer.parseInt(args[i].substring(args[i].length()-1));
					args[i] = args[i].substring(0,args[i].length()-2);
				} else {
					swords[i] = 0;
				}
				// If last, set isLast to true
				if (i == args.length-1) {
					game.move(Integer.parseInt(args[i]),swords[i],true);
				} else {
					game.move(Integer.parseInt(args[i]),swords[i],false);
				}
			}
		}
		
		if (command.equals("b") || command.contentEquals("buy")) {
			game.buy(Integer.parseInt(args[0]));
		}
		
		if (command.equals("tp") || command.contentEquals("teleport")) {
			for (int i = 0; i < args.length; i++) {
				if (i == args.length-1) {
					game.teleport(Integer.parseInt(args[i]),true);
				} else {
					game.teleport(Integer.parseInt(args[i]),false);
				}
			}
		}
		
		if (command.equals("e") || command.contentEquals("end")) {
			game.endTurn();
		}
		
		if (command.equals("g") || command.contentEquals("grab")) {
			String item = game.getMapContents()[game.getCurrentPlayer().getCurrentRoom()];
			if (item != null && item != "Heart") {
				game.grab();
			}
		}
		
		if (command.equals("u") || command.contentEquals("use")) {
			game.useItem(Integer.parseInt(args[0])-1);
		}
		
		if (command.equals("c") || command.contentEquals("choose")) {
			if (game.getMustChoose().size() > 0) {
				game.choose(Integer.parseInt(args[0]));
			}
		}
		
		if (command.equals("t") || command.contentEquals("trash")) {
			if (game.getMustTrash().size() > 0) {
				game.trash(args[0]);
			}
		}
		
		if (command.equals("d") || command.contentEquals("discard")) {
			if (game.getMustDiscard().size() > 0) {
				game.discard(Integer.parseInt(args[0])-1);
			}
		}
		
		if (command.equals("s") || command.contentEquals("select")) {
			for (int i = 0; i < args.length; i++) {
				if (i == args.length-1) {
					game.selectCard(Integer.parseInt(args[i]), true);
				} else {
					game.selectCard(Integer.parseInt(args[i]), false);
				}
			}
		}
		
		if (command.equals("p") || command.equals("play")) {
		    if (args.length == 0) {
		        // Play all cards
		        do {
		            for (int i = 0; i < game.getCurrentPlayer().getPlayArea().getSize(); i++) {
		                if (!game.getCurrentPlayer().getPlayArea().getCard(i).isPlayed()) {
		                	if (game.getCurrentPlayer().getPlayArea().getNonPlayedSize() == 1) {
		                		game.playCard(i,true);
		                	} else {
		                		game.playCard(i,false);
		                	}
		                }
				    }
		        // Keep looping in case you drew another card
			    } while (game.getCurrentPlayer().getPlayArea().getNonPlayedSize() > 0);
		    } else {
		    	// Play each card by specific index
		    	for (int i = 0; i < args.length; i++) {
		    		int num;
		    		// If it's a letter, convert to int
		    		if (!Utils.isInt(args[i])) {
		    			num = ((int)args[i].charAt(0))-96;
		    		} else {
		    			num = Integer.parseInt(args[i]);
		    		}
	                if (!game.getCurrentPlayer().getPlayArea().getCard(num-1).isPlayed()) {
	                	if (i == args.length-1) {
	                		game.playCard(num-1,true);
	                	} else {
	                		game.playCard(num-1,false);
	                	}
	                }
		    	}
		    }
		}
		
		if (command.contentEquals("clear")) {
			game.clearMessages();
		}

	}
	public String command = "";
	public String authorID = "";
	public String[] args = {};
	public MessageChannel channel;
	public User user;
	public Guild guild;
	//MessageReceivedEvent event;
	Event event;
}