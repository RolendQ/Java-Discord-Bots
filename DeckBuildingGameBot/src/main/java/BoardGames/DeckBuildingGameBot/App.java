package BoardGames.DeckBuildingGameBot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed.Footer;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RestAction;

/**
 * Hello world!
 *
 */
public class App extends ListenerAdapter {
	// Main method that runs the bot
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, FileNotFoundException, ClassNotFoundException, IOException {
    	// Removes error messages
    	//BasicConfigurator.configure();
    	
		// Creates bot with token
    	JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("Njg5MTk4MDYyNTY4MzQxNjA5.Xm_X3Q.Zy7p_kPVJtQ8npt1WdOZ658ftb4").buildBlocking();
    	jdaBot.addEventListener(new App());
    	jdaBot.getPresence().setGame(Game.of("WIP"));
    	
    	// Logs guilds
    	for (int i = 0; i < jdaBot.getGuilds().size(); i++) {
    	  System.out.println(jdaBot.getGuilds().get(i).getName() + ": " + jdaBot.getGuilds().get(i).getId());
    	}
    	
    	// Sets channel for sending board images
		GlobalVars.gamesChannel = jdaBot.getGuildById("394592207275032576").getTextChannelsByName("shards-bot-games", true).get(0);
    	System.out.println("Launching DBG Bot...");
    	GlobalVars.createFiles();
    	//UserStats.load();
    	System.out.println("DBG Bot successfully launched!");
    }
	
	// Detects when message is sent in any guild
	@SuppressWarnings("deprecation")
	@Override
    public void onMessageReceived(MessageReceivedEvent e) {
		String message = e.getMessage().getContent();
		if (e.getAuthor().isBot()) {
			// If bot's id is DBGbot
			if (e.getAuthor().getId().equals("689198062568341609")) {
				if (Utils.botUser == null) Utils.setBotUser(e.getAuthor()); //not using this ?
				// Deleting and settingID for embeds
				if (e.getMessage().getEmbeds().size() > 0) {
					String embed = e.getMessage().getEmbeds().get(0).getTitle();
					for (BoardGames.DeckBuildingGameBot.Game game : GlobalVars.currentGames) {
						// Detects if embed is for setting ids
						if (game.getGameChannel().equals(e.getChannel())) {
							// No title embeds like info
							if (embed == null) {
								game.getMessagesToDeleteIDs().add(e.getMessageId());
								return;
							} else if (embed.endsWith("Information")) {
								game.setEmbedID(0,e.getMessageId());
								game.updateReactionsInfo();
							} else if (embed.endsWith("Play Area")) {
								game.setEmbedID(1,e.getMessageId());
								game.updateReactionsPlayArea();
							} else if (embed.endsWith("Center Row")) {
								game.setEmbedID(2,e.getMessageId());
								game.updateReactionsCenterRow();
							} else if (embed.endsWith("Champions")) {
								game.setEmbedID(3,e.getMessageId());
								game.updateReactionsChampions();
							} else if (embed.endsWith("Events")) {
								game.setEmbedID(4,e.getMessageId());
								//e.getChannel().addReactionById(e.getMessageId(),GlobalVars.emojis.get("card_box")).queue();
								e.getChannel().addReactionById(e.getMessageId(),GlobalVars.emojis.get("calendar_spiral")).queue();
							} else {
								// Any other embeds
								game.getMessagesToDeleteIDs().add(e.getMessageId());
							}
						}
					}
					return;
				} 
				
				if (e.getChannel().getId().contentEquals(GlobalVars.gamesChannel.getId())) { // Games channel
					
				}
				
				// Deleting and catching nonembeds
				for (BoardGames.DeckBuildingGameBot.Game game : GlobalVars.currentGames) {
					if (game.getGameChannel().equals(e.getChannel())) {
						String msg = e.getMessage().getContent();
//							if (msg.startsWith("**[") || msg.startsWith(":dragon:") || msg.startsWith(":skull:") || 
//									msg.startsWith(":helicopter:") || msg.startsWith("_ _ _ _ :game_die:") || msg.startsWith("** ** ** ** :game_die:")) {
						if (msg.startsWith("**") || msg.startsWith(":one:") || msg.startsWith("You have unplayed")) {
							game.getMessagesToDeleteIDs().add(e.getMessageId());
						}
						if (msg.startsWith("**[Choose")) {
							game.addChoiceReactions(e.getMessageId());
							// add 1 2 or 3 emojis
//							for (int i = 1; i < 4; i++) {
//								if (msg.contains(Utils.numToEmoji[i])) {
//									e.getMessage().addReaction(Utils.numToNumEmoji[i]).queue();
//								}
//							}
						}
					}
				}
			}
			return;
		}
		// Not a bot
		
		// Deletes user's and other bots' messages when in game
	    for (BoardGames.DeckBuildingGameBot.Game game : GlobalVars.currentGames) {
            if (game.getGameChannel().equals(e.getChannel())) {
                game.getMessagesToDeleteIDs().add(e.getMessageId());
            }
        }
	    // Bot command symbol
		if (message.startsWith(GlobalVars.botCommandPrefix)) {
			processCommand(e, message.substring(1));
			// For instant delete instead of at end of turn
			//e.getMessage().delete().queue();
		// Force control command with specific ids to allow permission
		} else if (message.startsWith("f"+GlobalVars.botCommandPrefix)) {
            processCommand(e, message.substring(2), true);
		}
	}
	
	// Detects when reaction is added in any guild
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
		if (!e.getMember().getUser().isBot()) {
			//System.out.println("[DEBUG LOG/App.java] Detected Emoji: "+e.getReaction().getEmote().getName());
			System.out.print("\""+e.getReaction().getEmote().getName()+"\"");
			String emoji = e.getReaction().getEmote().getName();
			for (BoardGames.DeckBuildingGameBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(e.getChannel())) {
					
					if (game.getStatus().contentEquals("pregame")) { // Pregame reactions
						processCommand(e, GlobalVars.emojiCommands.get("pre "+emoji));
						return;
					}
//					Linking
//						
					if (emoji.contentEquals("🔍")) {
						game.setLinkedCommand("info ");
						System.out.println("[DEBUG LOG/Game.java] Started info reaction");
						return;
					
//					} else if (emoji.contentEquals("💥")) { // not using attack
//						game.setLinkedCommand("a ");
//						return;
					} else if (emoji.contentEquals("🗡️")) { // merc
						if (game.getCurrentPlayer().getPlayerID().contentEquals(e.getUser().getId())) { // need to be current player
							game.setLinkedCommand("s m");
							return;
						}
					// Letters
					} else if (Utils.isLetterEmoji(emoji)) {
						int val = Utils.indexOf(Utils.numToLetterEmojis,emoji)+1;
						if (e.getMessageId().contentEquals(game.getEmbedID(1))) { // Play Area
							if (game.hasLinkedCommand()) {
								// Discard
								if (game.getLinkedCommand().contentEquals("d ")) {
									e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
									processCommand(e, game.getLinkedCommand()+val);
									// Updates in discard command
								} else {
									// Play link
									game.addLinkedCommand(" "+val);
								}
							} else {
								processCommand(e, "p "+val); // Play
							}
						} else if (e.getMessageId().contentEquals(game.getEmbedID(2))) { // Center Row
							if (game.hasLinkedCommand()) {
								if (game.getLinkedCommand().endsWith("m")) { // Mercenary
									e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
									e.getChannel().removeReactionById(e.getMessageId(),"🗡️",e.getUser()).queue();
									processCommand(e, game.getLinkedCommand()+val);
									
								} // Removed chain and info
							} else {
								e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
								processCommand(e, "s "+val); // Select
							}
						}
					// Numbers
					} else if (Utils.isNumEmoji(emoji)) {
						int val = Utils.indexOf(Utils.numToNumEmoji,emoji);
//						if (e.getMessageId().contentEquals(game.getEmbedID(2))) { // Center Row
//							if (game.hasLinkedCommand()) {
//								if (game.getLinkedCommand().endsWith("m")) { // Mercenary
//									e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
//									e.getChannel().removeReactionById(e.getMessageId(),"🗡️",e.getUser()).queue();
//									processCommand(e, game.getLinkedCommand()+val);
//									
//								} else if (game.getLinkedCommand().startsWith("s")) {
//									game.addLinkedCommand(" "+val);
//									System.out.println("[DEBUG LOG/Game.java] Added a select to chain reaction");
//								} else if (game.getLinkedCommand().contentEquals("i ")) {
//									// Do info command
//									System.out.println("[DEBUG LOG/Game.java] Added card name to info reaction");
//									game.addLinkedCommand(game.centerRow[val-1].getName());
//									
//									e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
//									e.getChannel().removeReactionById(e.getMessageId(),"🔍",e.getUser()).queue();
//									processCommand(e, game.getLinkedCommand());
//									//game.setLinkedCommand(null);
//								}
//							} else {
//								e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
//								processCommand(e, "s "+val); // Select
//							}
						if (e.getMessageId().contentEquals(game.getEmbedID(3))) { // Champions
							processCommand(e, "x "+val); // Exhaust
						} else if (e.getMessageId().contentEquals(game.getEmbedID(0))) { // Info
							System.out.println("[DEBUG LOG/Game.java] Attacking champ");
							processCommand(e, "a "+val);
						} else {
							processCommand(e, "c "+val); // choose relic or champ
						}

					// Direct commands (includes books)
					} else if (GlobalVars.emojiCommands.containsKey(emoji)) {
						if (Utils.has(game.embedIDs, e.getMessageId())) {
							e.getChannel().removeReactionById(e.getMessageId(),emoji,e.getUser()).queue();
							if (emoji.contentEquals("⏩")) {
								if (e.getMessageId().contentEquals(game.getEmbedID(1))) processCommand(e, "p");
								else processCommand(e, "x");
								return;
							}
							// focus, play, end, deck, history
							processCommand(e, GlobalVars.emojiCommands.get(emoji));
						} else {
							// Choose
							String s = GlobalVars.emojiCommands.get(emoji);
							processCommand(e, "c "+s.substring(2));
						}
					}
				}
			}
			
			// No game found
			Message msg = e.getChannel().getMessageById(e.getMessageId()).complete();
			if (msg.getEmbeds().size() > 0 && msg.getEmbeds().get(0).getTitle().contentEquals("Cards Library")) {
				int currentPage = Integer.parseInt(msg.getEmbeds().get(0).getFooter().getText().substring(5))-1;
				if (emoji.contentEquals("◀️")) {
					e.getChannel().editMessageById(e.getMessageId(), TextDatabase.buildCards(currentPage-1).build()).queue();
				} else if (emoji.contentEquals("▶️")) {
					e.getChannel().editMessageById(e.getMessageId(), TextDatabase.buildCards(currentPage+1).build()).queue();
				}
			}
		}
	}
	
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		if (!e.getMember().getUser().isBot()) {
			for (BoardGames.DeckBuildingGameBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(e.getChannel())) {
					String emoji = e.getReaction().getEmote().getName();
					// Check if there is a valid command
					if (emoji.contentEquals("🗡️") && game.hasLinkedCommand()) { // Cancel merc
						if (game.getCurrentPlayer().getPlayerID().contentEquals(e.getUser().getId())) {
							game.setLinkedCommand(null);
							game.setLinking(false);
						}
					}	
				}
			}
		}
	}
	
//	public void onPrivageMessageReceived(PrivateMessageReceivedEvent e) {
//		System.out.println("Detected");
//		String message = e.getMessage().getContent();
//		System.out.println(message);
//		if (!e.getAuthor().isBot()) {
//			if (message.contentEquals("t") || message.contentEquals("tutorial")) {
//				Tutorial t = new Tutorial(e.getAuthor().getId(),e.getChannel());
//				GlobalVars.add(t);
//			}
//		}
//	}
	
	public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent e) {
		if (!e.getUser().isBot()) {
			String emoji = e.getReaction().getEmote().getName();
			
			Message msg = null;
			try {
				msg = e.getChannel().getMessageById(e.getMessageId()).submit().get();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
			
			String title = msg.getEmbeds().get(0).getTitle();
			Footer footerObj =  msg.getEmbeds().get(0).getFooter();
			
			if (title != null && title.contains("History")) { // History
				String[] footer = footerObj.getText().split(" ");
				int page = (title.charAt(title.length()-3) - '0')-1; // starts at 0 while display starts at 1
				int totalPages = title.charAt(title.length()-1) - '0';
				String playerID = footer[1];
				String channelID = footer[3];
				if (emoji.contentEquals("◀️")) {
					page = (page - 1) % totalPages;
				} else if (emoji.contentEquals("▶️")) {
					page = (page + 1) % totalPages;
				}
				BoardGames.DeckBuildingGameBot.Game g = GlobalVars.findGame(channelID);
				Player p = GlobalVars.findPlayer(g, playerID);
				e.getChannel().editMessageById(e.getMessageId(), g.createHistoryEmbed(p, page).build()).queue();
				//e.getChannel().removeReactionById(e.getMessageId(),emoji).queue();
				
			} else { // Tutorial
				//System.out.println("Detected tutorial emoji");
				Tutorial tutorial = null;
				for (Tutorial t : GlobalVars.currentTutorials) {
					// Must be this user and fit one of their ids
					if (t.getUserID().contentEquals(e.getUser().getId()) && (e.getMessageId().contentEquals(t.getEmbedIDs()[1]) || e.getMessageId().contentEquals(t.getEmbedIDs()[0]))) {
						tutorial = t;
						break;
					}
				}
				
				if (tutorial == null) { // Not tutorial
					if (emoji.contentEquals("❓") && title != null) {
						System.out.println("Title: "+title);
						for (Message m : e.getChannel().getHistory().retrievePast(10).complete()) {
							if (m.getEmbeds().size() > 0 && m.getEmbeds().get(0).getFooter().getText().contentEquals("Example Game")) { // Find this embed
								e.getChannel().editMessageById(m.getId(), TextDatabase.exampleDisplayDescription(title).build()).queue();
							}
							return;
						}
					}
					
				} else { // Found tutorial
				
					if (emoji.contentEquals("⏪") && tutorial.getPage() != 0) {
						tutorial.showPage(-1);
						return;
					}
					
					String requiredEmoji = Tutorial.reactions[tutorial.getPage()][0];
					System.out.println(requiredEmoji);
					
					if (tutorial.getPage() == 8) { // Merc page
						if (!tutorial.getLastReaction().contentEquals("🗡️")) {
							requiredEmoji = "";
						}
						
					} else if (tutorial.getPage() == 13) {
						requiredEmoji = "🇧";
					}
					 
					if (requiredEmoji.contentEquals(emoji)) {
						tutorial.showPage(1);
					}
					tutorial.setLastReaction(emoji);
				}
			}
		}
	}
	
	public void processCommand(net.dv8tion.jda.core.events.Event e, String text) {
		processCommand(e, text, false);
	}
	
	public void processCommand(net.dv8tion.jda.core.events.Event e, String text, boolean forced) {
		System.out.println("[DEBUG LOG/Game.java] Processing: "+text);
		String commandName = text.split(" ")[0];
		String[] args = {};
		if (text.split(" ").length > 1) {
			args = text.replace(commandName+" ","").split(" ");
		}
		Commands command = new Commands(commandName,e,args);
		// For forcing a player to do a command
		if (forced) { 
			command.changeAuthor();
		}
		command.attempt();
		command = null;
	}
}


