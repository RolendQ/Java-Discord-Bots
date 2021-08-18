package BoardGames.ClankBot;

import java.awt.Event;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class App extends ListenerAdapter {
	// Main method that runs the bot
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, FileNotFoundException, ClassNotFoundException, IOException {
    	// Removes error messages
    	//BasicConfigurator.configure();
    	
		// Creates bot with token
    	JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("NTQ2NzE3MDMzMzQ2MTA1MzQ1.D0sRfw.8ea41tULEeC4bDNQy8RScrxw3Ug").buildBlocking();
    	jdaBot.addEventListener(new App());
    	jdaBot.getPresence().setGame(Game.of("WIP"));
    	
    	// Logs guilds
    	for (int i = 0; i < jdaBot.getGuilds().size(); i++) {
    	  System.out.println(jdaBot.getGuilds().get(i).getName() + ": " + jdaBot.getGuilds().get(i).getId());
    	}
    	
    	// Sets channel for sending board images
		GlobalVars.filesChannel = jdaBot.getGuildById("394592207275032576").getTextChannelsByName("clank-bot-files", true).get(0);
    	System.out.println("Launching Clank Bot...");
    	GlobalVars.createFiles();
    	System.out.println("Clank Bot successfully launched!");
    }
	
	// Detects when reaction is added in any guild
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
		if (!e.getMember().getUser().isBot()) {
			//System.out.println("[DEBUG LOG/App.java] Detected Emoji: "+e.getReaction().getEmote().getName());
			System.out.print("\""+e.getReaction().getEmote().getName()+"\"");
			for (BoardGames.ClankBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(e.getChannel())) {
					String emoji = e.getReaction().getEmote().getName();
					if (emoji.contentEquals("🔗")) {
						if (e.getMessageId().contentEquals(game.getPlayAreaID())) {
							game.setLinkedCommand("p");
							return;
						} else if (e.getMessageId().contentEquals(game.getDungeonRowID())) {
							game.setLinkedCommand("s");
							return;
						}
						
					} else if (emoji.contentEquals("🔍")) {
						game.setLinkedCommand("i ");
						System.out.println("[DEBUG LOG/Game.java] Started info reaction");
						return;
					
					} else if (emoji.contentEquals("🔮")) {
						game.setLinkedCommand("tp ");
						game.updateReactionsInfo();
						return;
						
					// Letters
					} else if (Utils.has(Utils.numToLetterEmojis,emoji)) {
						if (e.getMessageId().contentEquals(game.getPlayAreaID())) {
							int val = Utils.indexOf(Utils.numToLetterEmojis,emoji)+1;
							if (game.hasLinkedCommand()) {
								game.addLinkedCommand(" "+val);
							} else {
								processCommand(e, "p "+val);
							}
						} else if (e.getMessageId().contentEquals(game.getInfoID())) {
							int val = Utils.indexOf(Utils.numToLetterEmojis,emoji);
							int prevRoom = game.getCurrentPlayer().getCurrentRoom();
							if (game.hasLinkedCommand()) {
								if (game.getLinkedCommand().startsWith("m")) {
									game.addLinkedCommand(" "+GlobalVars.adjacentRoomsPerRoom[prevRoom][val]);
								}
								else if (game.getLinkedCommand().contentEquals("tp ")) {
									// Do tp command
									game.addLinkedCommand(""+GlobalVars.teleportRoomsPerRoom[prevRoom][val]);
									e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
									e.getChannel().removeReactionById(e.getMessageId(),"🔮",e.getUser()).queue();
									processCommand(e, game.getLinkedCommand());
									game.setLinkedCommand(null);
								}
							} else {
								processCommand(e, "m "+GlobalVars.adjacentRoomsPerRoom[prevRoom][val]);
							}
						}
					// Numbers
					} else if (Utils.has(Utils.numToNumEmoji,emoji)) {
						if (e.getMessageId().contentEquals(game.getPlayAreaID())) {
							int val = Utils.indexOf(Utils.numToNumEmoji,emoji)+1;
							processCommand(e, "u "+val);
						} else if (e.getMessageId().contentEquals(game.getDungeonRowID())) {
							int val = Utils.indexOf(Utils.numToNumEmoji,emoji);
							if (game.hasLinkedCommand()) {
								if (game.getLinkedCommand().startsWith("s")) {
									game.addLinkedCommand(" "+val);
									System.out.println("[DEBUG LOG/Game.java] Added a select to chain reaction");
								} else if (game.getLinkedCommand().contentEquals("i ")) {
									// Do info command
									System.out.println("[DEBUG LOG/Game.java] Added card name to info reaction");
									game.addLinkedCommand(game.dungeonRow[val-4].getName());
									e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
									e.getChannel().removeReactionById(e.getMessageId(),"🔍",e.getUser()).queue();
									processCommand(e, game.getLinkedCommand());
									game.setLinkedCommand(null);
								}
							} else {
								processCommand(e, "s "+val);
							}
						} else if (e.getMessageId().contentEquals(game.getInfoID())) {
							int val = Utils.indexOf(Utils.numToNumEmoji,emoji);
							// swords
						}
					// Direct commands
					} else if (GlobalVars.emojiCommands.containsKey(emoji)) {
						// buy, grab, play, end, deck, history
						e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
						processCommand(e, GlobalVars.emojiCommands.get(emoji));
					}
				}
			}
		}
	}
	
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		if (!e.getMember().getUser().isBot()) {
			for (BoardGames.ClankBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(e.getChannel())) {
					String emoji = e.getReaction().getEmote().getName();
					// Check if there is a valid command
					if (emoji.contentEquals("🔗") && game.hasLinkedCommand() && game.getLinkedCommand().length() > 2) {
						processCommand(e, game.getLinkedCommand());
						game.setLinkedCommand(null);
					}
				}
			}
		}
	}
	
	// Detects when message is sent in any guild
	@SuppressWarnings("deprecation")
	@Override
    public void onMessageReceived(MessageReceivedEvent e) {
		String message = e.getMessage().getContent();
		if (e.getAuthor().isBot()) {
			// If bot's id is ClankBot
			if (e.getAuthor().getId().equals("546717033346105345")) {
				
				// Deleting and settingID for embeds
				if (e.getMessage().getEmbeds().size() > 0) {
					String embed = e.getMessage().getEmbeds().get(0).getTitle();
					for (BoardGames.ClankBot.Game game : GlobalVars.currentGames) {
						// Detects board in special channel and calls method to update it in correct game channel
						if (e.getChannel().getName().contentEquals("clank-bot-files") && game.getGameChannel().getName().equals(embed)) {
							game.afterReceivedBoardURL(e.getMessage().getEmbeds().get(0).getImage().getUrl());
						}
						
						// Detects if embed is for setting ids
						if (game.getGameChannel().equals(e.getChannel())) {
							// No title embeds like info
							if (embed == null) {
								game.getMessagesToDeleteIDs().add(e.getMessageId());
								return;
							} else if (embed.endsWith("Information")) {
								game.setInfoID(e.getMessageId());
								game.updateReactionsInfo();
							} else if (embed.endsWith("Play Area")) {
								game.setPlayAreaID(e.getMessageId());
								game.updateReactionsPlayArea();
							} else if (embed.endsWith("Dungeon Row")) {
								game.setDungeonRowID(e.getMessageId());
								game.updateReactionsDungeonRow();
							} else if (embed.endsWith("Board")) {
								game.setBoardID(e.getMessageId());
							} else if (embed.endsWith("Events")) {
								game.setEventsID(e.getMessageId());
								e.getChannel().addReactionById(e.getMessageId(),GlobalVars.emojiCommands.get("card_box")).queue();
								e.getChannel().addReactionById(e.getMessageId(),GlobalVars.emojiCommands.get("stopwatch")).queue();
							} else if (embed.endsWith("Treasures")) {
								game.setMarketAndTsID(e.getMessageId());
							} else {
								// Any other embeds
								game.getMessagesToDeleteIDs().add(e.getMessageId());
							}
						}
					}
					return;
				} 
				
				// Deleting and catching nonembeds
				for (BoardGames.ClankBot.Game game : GlobalVars.currentGames) {
					if (game.getGameChannel().equals(e.getChannel())) {
						String msg = e.getMessage().getContent();
//						if (msg.startsWith("**[") || msg.startsWith(":dragon:") || msg.startsWith(":skull:") || 
//								msg.startsWith(":helicopter:") || msg.startsWith("_ _ _ _ :game_die:") || msg.startsWith("** ** ** ** :game_die:")) {
						if (msg.startsWith("**") || msg.startsWith(":")) {
							game.getMessagesToDeleteIDs().add(e.getMessageId());
						}
						if (msg.startsWith("_ _ _ _ :game_die:")) {
							System.out.println("[DEBUG LOG/App.java] Caught last dragon attack");
							game.getMessagesToDeleteIDs().add(e.getMessageId());
							game.addEvent(game.dragonAttackEventText, true);
							if (game.turnIsEnding) {
								game.determineNextPlayer();
							}
						}
						// Suppose to checkIfGameOver after the death shows
						if (msg.startsWith(":skull: **")) {
							// If the current player just died and turn is not already ending
							System.out.println("[DEBUG LOG/Game.java] Caught death message");
							if (!game.checkIfGameIsOver() && game.getCurrentPlayer().isDead() && !game.turnIsEnding) { // DEATH CONDITION 1
								game.endTurn();
							}
						}
					}
				}
			}
			return;
		}
		// Not a bot
		
		// Deletes user's and other bots' messages when in game
	    for (BoardGames.ClankBot.Game game : GlobalVars.currentGames) {
            if (game.getGameChannel().equals(e.getChannel())) {
                game.getMessagesToDeleteIDs().add(e.getMessageId());
            }
        }
	    // Bot command symbol
		if (message.startsWith("]")) {
			processCommand(e, message.substring(1));
			// For instant delete instead of at end of turn
			//e.getMessage().delete().queue();
		// Force control command with specific ids to allow permission
		} else if (message.startsWith("f]")) {
            processCommand(e, message.substring(2));
		}
	}
	
	public void processCommand(net.dv8tion.jda.core.events.Event e, String text) {
		System.out.println("[DEBUG LOG/Game.java] Processing: "+text);
		String commandName = text.split(" ")[0];
		String[] args = {};
		if (text.split(" ").length > 1) {
			args = text.replace(commandName+" ","").split(" ");
		}
		Commands command = new Commands(commandName,e,args);
		command.attempt();
		command = null;
	}
}
