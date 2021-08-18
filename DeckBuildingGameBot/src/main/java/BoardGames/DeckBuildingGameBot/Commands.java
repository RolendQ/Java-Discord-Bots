package BoardGames.DeckBuildingGameBot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;

public class Commands {
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
	
	// For forcing quit or voting
	public void changeAuthor() {
		for (Game g : GlobalVars.currentGames) {
			if (g.getGameChannel().equals(channel)) {
				authorID = g.getCurrentPlayer().getPlayerID();
			}
		}
	}

	public void attempt() {
		// Check if requirements/args are met
		if (command.contentEquals("cg") || command.equals("create_game")) {
			// Check if there already is a game in this channel
			System.out.println(GlobalVars.currentGames.size());
			for (Game g : GlobalVars.currentGames) {
				if (g.getGameChannel().equals(channel)) {
					return;
				}
			}
		}
		if (command.contentEquals("eg") || command.equals("end_game") || command.contentEquals("shutdown")) {
			// Check if this is an whitelisted id
			if (!Utils.isAdmin(authorID)) return;
		}
		action();
	}
	
	public void action() {
		//System.out.println("Command is: "+command);
		System.out.println("Args is: "+Arrays.toString(args));

		
		if (command.contentEquals("shutdown")) {
			// Save
			channel.sendMessage("Shutting down...").queue();
			//UserStats.save();
			System.exit(0);
		}
		
		if (command.equals("ping")) {
			channel.sendMessage("pong").queue();
			return;
		}
		
		if (command.equals("test")) {
			//System.out.println(":regional_indicator_"+((char)(Integer.parseInt(args[0])+97))+":");
			String msgID = ((GenericMessageEvent) event).getMessageId();
			channel.addReactionById(msgID,"🔗").queue();
			channel.addReactionById(msgID, "\ud83c\udde6").queue();
			System.out.println("🇦"+Utils.isLetterEmoji("🇦"));
			System.out.println("0⃣"+Utils.isLetterEmoji("0⃣"));
		}
		
		if (command.equals("embedtest")) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.YELLOW);
			embed.setTitle("Market & Treasures *hi* :heart:");
			embed.addField("In Stock (**7x**:moneybag: each)",":key:**5** :key:**5** :briefcase:**5** :briefcase:**5** :crown:**9** :crown:**8** :crown:**7**", false);
			embed.addField("``Rolend`` **5x**:moneybag:",":key:**5** :wine_glass:**7**", false);
			embed.addField("``Aero`` **7x**:moneybag:",":briefcase:**5** :egg:**3**", false);
			embed.setFooter("12345678901234567890123456789012345678901234567890123456789012345678901234567890", null);
			channel.sendMessage(embed.build()).queue();
		}
		
		if (command.contentEquals("tutorial")) {
			user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
				public void accept(PrivateChannel channel) {
					Tutorial t = new Tutorial(authorID,channel);
					GlobalVars.add(t);
				}
			});
		}
		
		if (command.contentEquals("example")) {
			TextDatabase.sendExample(user);
		}
		
		
		if (command.contentEquals("help")) {
			if (args.length > 0) TextDatabase.printHelp(channel, Integer.parseInt(args[0]));
			else TextDatabase.printHelp(channel, 1);
			return;
		}
		
		if (command.contentEquals("shop") || command.contentEquals("centerrow")) {
			TextDatabase.printCenterRowGuide(channel);
			return;
		}
		
		if (command.contentEquals("relics")) {
			TextDatabase.printRelics(channel);
			return;
		}
		
		if (command.contentEquals("commands")) {
			TextDatabase.printCommands(channel);
			return;
		}
		
		if (command.contentEquals("cards")) {
			Message msg;
			if (args.length > 0) {
				msg = channel.sendMessage(TextDatabase.buildCards(Integer.parseInt(args[0])).build()).complete();
			} else msg = channel.sendMessage(TextDatabase.buildCards(0).build()).complete();
			msg.addReaction(GlobalVars.emojis.get("arrow_backward")).queue();
			msg.addReaction(GlobalVars.emojis.get("arrow_forward")).queue();
			//TextDatabase.printCards(channel,0);
			return;
		}
		
		if (command.contentEquals("i") || command.contentEquals("info")) {
			String cardName = "";
			for (int i = 0; i < args.length; i++) {
				cardName += " "+args[i];
			}
			// Info for each card
			EmbedBuilder embed = new EmbedBuilder();
			Card c = GlobalVars.getCard(cardName.substring(1));
			if (c == null) return;
			
			// Add bell for priority cards
			if (c.hasPriorityHint()) {
				embed.addField(c.getCleanType(),":bell: "+c.toString(), true);
			} else {
				embed.addField(c.getCleanType(),c.toString(), true);
			}
			if (c.getText() != "") {
				embed.addField("Extra Effect",c.getText(), true);
			}
			embed.setColor(Color.MAGENTA);
			channel.sendMessage(embed.build()).queue();
		}
		
		if (command.equals("create_game") || command.contentEquals("cg")) {
//			if (args.length == 0) {
//				channel.sendMessage("Please choose a faction/color to play as: RED, BLUE, GREEN, ORANGE").queue();
//				return;
//			}
			int color = -1;
			if (args.length > 0) color = Utils.getColorFromString(args[0]);
			boolean addReactions = true;
			if (args.length == 2 && args[1].contentEquals("test")) { // Quick start for testing
				addReactions = false;
			}
			Game newGame = new Game(channel,guild,authorID,color,addReactions);
			GlobalVars.add(newGame);
			System.out.println("[DEBUG LOG/Commands.java] Created game");
			if (args.length == 2) { // what about ]cg 2v2
				newGame.setMode(args[1]);
				if (args[1].contentEquals("test")) newGame.start(false);
			}
			return;
		}
		
		if (command.contentEquals("rg") || command.contentEquals("resume_game")) {
			//Game resumedGame = new Game(channel,guild,authorID,args[0]);
			//Game resumedGame = Game.loadFiles(args[0],channel,guild);
			Game resumedGame = Game.loadOnline(args[0],channel,guild);
			GlobalVars.add(resumedGame);
			System.out.println("[DEBUG LOG/Commands.java] Resumed game: "+args[0]);
			resumedGame.start(true);
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
		
		// Clear any linked command
//		if (event instanceof GuildMessageReactionAddEvent) {
//			game.setLinkedCommand(null);
//			game.setLinking(false);
//		}
		
		if (command.contentEquals("sg") || command.contentEquals("save_game")) {
			if (!game.getStatus().contentEquals("pregame")) {
				game.saveFiles();
			}
		}
		
		if (command.contentEquals("st") || command.contentEquals("start")) {
			if (game.getStatus().contentEquals("pregame") && game.players.get(0).getPlayerID().contentEquals(authorID)) { // Only first person can start it
				game.start(false);
			}
		}
		
		if (command.contentEquals("j") || command.equals("join")) {
			if (game.getStatus().contentEquals("pregame")) {
//				if (args.length == 0) {
//					channel.sendMessage("Please choose a faction/color to play as: RED, BLUE, GREEN, ORANGE").queue();
//					return;
//				}
				// Can join twice atm but no duplicate colors
				int color = -1;
				if (args.length > 0) color = Utils.getColorFromString(args[0]);
				if (game.getPlayerByColor(color) == null && game.getPlayerCount() < 4) { // Can have multiple no colors
					game.addPlayer(authorID,color);
				} else {
					channel.sendMessage("Game is full").queue();
				}
				if (args.length == 2) {
					game.setMode("2v2");
					int id = (int)(args[1].charAt(0))-97;
					game.setID(authorID, id);
					channel.addReactionById(((GenericMessageEvent) event).getMessageId(), Utils.numToLetterEmojis[id]).queue();
				}
			}
		}
		
		if (command.contentEquals("add")) {
			// Bug fixing
			game.addResource(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
		}
		
		if (command.contentEquals("addCard")) {
			String choice = "";
			for (String arg : args) {
				choice += arg+" ";
			}
			choice = choice.substring(0,choice.length()-1);
			game.addCard(choice);
			channel.addReactionById(((GenericMessageEvent) event).getMessageId(), "✅").queue();
		}
		
		if (command.contentEquals("color")) {
			if (game.getStatus().contentEquals("pregame")) {
				int color = Utils.getColorFromString(args[0]);
				if (game.getPlayerByColor(color) == null) {
					game.availableColors.remove((Integer) color);
					game.getPlayerByID(authorID).setColor(color);
					System.out.println("Set color: "+color);
					channel.addReactionById(((GenericMessageEvent) event).getMessageId(), GlobalVars.emojis.get("book "+color)).queue();
				}
			}
		}
		
		if (command.contentEquals("mode")) {
			if (game.getStatus().contentEquals("pregame")) {
				game.setMode(args[0]);
				channel.addReactionById(((GenericMessageEvent) event).getMessageId(), "✅").queue();
			}
		}
		
		if (command.contentEquals("cancel")) {
			if (game.getStatus().contentEquals("pregame") && game.players.get(0).getPlayerID().contentEquals(authorID)) { // Only first person can start it
				GlobalVars.remove(game);
				channel.addReactionById(((GenericMessageEvent) event).getMessageId(), "✅").queue();
			}
		}
		
		if (command.contentEquals("mull") || command.contentEquals("mulligan")) {
			if (game.getStatus().contentEquals("pregame")) {
				game.generateDeck(false,true);
			}
		}
		
		if (command.contentEquals("set") || command.contentEquals("setID")) {
			if (game.getStatus().contentEquals("pregame")) {
				//game.setMode("2v2");
				int id = (int)(args[0].charAt(0))-97;
				game.setID(authorID, id);
				channel.addReactionById(((GenericMessageEvent) event).getMessageId(), Utils.numToLetterEmojis[id]).queue();
			}
		}
		
		if (command.contentEquals("players")) {
			game.listPlayers();
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
//			if (game.getStatus().contentEquals("ingame")) {
//				game.voteEnd(authorID);
//			}
		}
		
		if (command.contentEquals("uve") || command.contentEquals("unvote_end")) {
//			if (game.getStatus().contentEquals("ingame")) {
//				game.unvoteEnd(authorID);
//			}
		}
		
		if (command.contentEquals("eg") || command.contentEquals("end_game")) {
			if (game.getStatus().contentEquals("ingame")) {
				System.out.println("[DEBUG LOG/Commands.java] Trying to force end game");
				channel.sendMessage("Ended the game").queueAfter(1000,TimeUnit.MILLISECONDS);
				GlobalVars.remove(game);
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
						game.displayDeck(game.getPlayerByID(((MessageReceivedEvent) event).getMessage().getMentionedMembers().get(0).getUser().getId()));
						return;
					} else if (args.length > 0) {
						game.displayDeck(Utils.getColorFromString(args[0]));
						return;
					}
				}
				game.displayDeck(game.getPlayerByID(authorID));
			}
		}
		
		// Commands that require the command user to be the current player or admin
		if (game.getCurrentPlayer() == null || (!Utils.isAdmin(authorID) && !game.getCurrentPlayer().getPlayerID().equals(authorID))) {
			return;
		}
		
		if (command.equals("e") || command.contentEquals("end")) {
			game.endTurn();
		}
		
		if (command.contentEquals("f")) {
			game.useFocus();
		}
		
		if (command.contentEquals("pg") || command.contentEquals("page")) {
			if (args.length > 0) {
				if (Utils.isInt(args[0])) {
					game.setPlayAreaPage(Integer.parseInt(args[0])); // updates in Game
				} else if (args[0].contentEquals("f")){
					int pages = ((game.getCurrentPlayer().getPlayArea().getSize()-1) / game.CARDS_PER_PAGE) + 1; // 5 -> 1, 9 -> 2, 10 -> 2, 11 -> 3
					game.setPlayAreaPage(Math.abs((game.getPlayAreaPage() + 1) % pages));
				} else if (args[0].contentEquals("b")){
					int pages = ((game.getCurrentPlayer().getPlayArea().getSize()-1) / game.CARDS_PER_PAGE) + 1; // 5 -> 1, 9 -> 2, 10 -> 2, 11 -> 3
					game.setPlayAreaPage(Math.abs((game.getPlayAreaPage() - 1) % pages));
				}
			}
		}
		
		if (command.equals("x") || command.contentEquals("exhaust")) {
			if (args.length == 0) game.doChampEffectAll();
			
			for (int i = 0; i < args.length; i++) {
				if (args[i].contentEquals("0")) game.useFocus();
				else {
					game.doChampEffect(game.getChamp(Integer.parseInt(args[i])),false);
					((TextChannel) channel).removeReactionById(game.embedIDs[3], Utils.numToNumEmoji[Integer.parseInt(args[i])], Utils.botUser).queue();
                	((TextChannel) channel).removeReactionById(game.embedIDs[3], Utils.numToNumEmoji[Integer.parseInt(args[i])], user).queue();
				}
			}
			if (!game.getCurrentPlayer().hasUnexhaustedChamps()) {
				((TextChannel) channel).removeReactionById(game.embedIDs[3], "⏩", Utils.botUser).queue();
            	((TextChannel) channel).removeReactionById(game.embedIDs[3], "⏩", user).queue();
			}
			game.updateICP();
		}
		
		if (command.equals("a") || command.contentEquals("attack")) {
			if (args.length == 0) return;
			
			if (Utils.isInt(args[0])) {
				// Attack champion
				game.attackChamp(Integer.parseInt(args[0]));
			} else {
				// Attack player
				boolean update = true;
				if (args.length == 2 && args[1].contentEquals("e")) update = false;
				if (args.length <= 1) {
					game.attackPlayer(Utils.getColorFromString(args[0]), update);
				} else {
					game.attackPlayer(Utils.getColorFromString(args[0]),Integer.parseInt(args[1]), update);
				}
				if (!update) game.endTurn(); // end turn after attacking with E
			}
		}
		
		if (command.equals("c") || command.contentEquals("choose")) {
			String choice = "";
			for (String arg : args) {
				choice += arg+" ";
			}
			choice = choice.substring(0,choice.length()-1);
			game.choose(choice);
		}
		
		if (command.equals("re") || command.contentEquals("relic")) {
			if (args.length == 0) return;
			game.selectRelic(Integer.parseInt(args[0]));
		}
		
		if (command.equals("r") || command.contentEquals("revive")) {
			String name = "";
			for (String arg : args) {
				name += arg+" ";
			}
			name = name.substring(0,name.length()-1);
			if (game.revive(name)) {
				String id = ((MessageReceivedEvent) event).getMessageId();
				channel.addReactionById(id, "✅").queue();
			}
		}
		
		if (command.equals("b") || command.contentEquals("banish")) {
			// Could make it so you can ]banish E for in hand
			String name = "";
			for (String arg : args) {
				name += arg+" ";
			}
			name = name.substring(0,name.length()-1);
			if (game.banish(name)) {
				String id = ((MessageReceivedEvent) event).getMessageId();
				channel.addReactionById(id, "✅").queue();
			}
		}
		
		if (command.equals("s") || command.contentEquals("select")) {
			for (int i = 0; i < args.length; i++) {
				boolean asMerc = false;
				if (args[i].contains("m")) {
					args[i] = args[i].replace("m", "");
					asMerc = true;
				}
				int num;
	    		// If it's a letter, convert to int
	    		if (!Utils.isInt(args[i])) num = ((int)args[i].charAt(0))-97;
	    		else num = Integer.parseInt(args[i])-1;
				if (i == args.length-1) {
					game.selectCard(num, asMerc, true);
				} else {
					game.selectCard(num, asMerc, false);
				}
			}
		}
		
		if (command.equals("p") || command.equals("play")) {
		    if (args.length == 0) {
		        // Play all cards
		        do {
		            for (int i = 0; i < game.getCurrentPlayer().getPlayArea().getSize(); i++) {
		                if (!game.getCurrentPlayer().getPlayArea().getCard(i).isPlayed()) game.playCard(i,false);
				    }
		        // Keep looping in case you drew another card
			    } while (game.getCurrentPlayer().getPlayArea().hasNonPlayed());
		        //game.updateReactionsPlayArea();
		        game.clearReactionsPlayArea();
		        game.updateICP(true, false);
		        //((TextChannel) channel).removeReactionById(game.embedIDs[1], GlobalVars.emojis.get("fast_forward"), user).queue();
		    } else {
		    	// Play each card by specific index
		    	for (int i = 0; i < args.length; i++) {
		    		int num;
		    		// If it's a letter, convert to int
		    		if (!Utils.isInt(args[i])) num = ((int)args[i].charAt(0))-97;
		    		else num = Integer.parseInt(args[i])-1;
	                if (!game.getCurrentPlayer().getPlayArea().getCard(num).isPlayed()) {
//	                	((TextChannel) channel).removeReactionById(game.embedIDs[1], Utils.numToLetterEmojis[num], Utils.botUser).queue();
	                	((TextChannel) channel).removeReactionById(game.embedIDs[1], Utils.numToLetterEmojis[num], user).queue();
	                	game.playCard(num,false);
	                }
		    	}
			    game.updateICP(true, true);
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