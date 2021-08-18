package JavaTutorials.MemorandumBot;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

public class Commands {
	// Set it null to delete it
	public Commands(String command,MessageReceivedEvent event,String[] args) {
		this.command = command;
		this.args = args;
		this.event = event;
	}
	public void attempt() {
		// Check if requirements/args are met
		if (command.equals("startgame")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					event.getChannel().sendMessage("There is already a game going on in this channel").queue();
					return;
				}
			}
			if (args.length < 1 || !Utils.currentModes.contains(args[0].toUpperCase())) {
				event.getChannel().sendMessage("Please specify a gamemode: classic (c), perpetual (p), elimination (e), or survival (s)").queue();
				return;
			}
		}
		if (command.equals("dropout")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					if (!game.getMode().equals("perpetual")) {
						event.getChannel().sendMessage("You can only drop out of perpetual games").queue();
						return;
					}
					if (game.getPlayers().contains(event.getAuthor().getId())) {
						System.out.println("Actioning");
						action();
					} else {
						event.getChannel().sendMessage("You haven't scored any points so you aren't in the game yet").queue();
					}
					return;
				}
			}
			event.getChannel().sendMessage("There is no game going on in this channel").queue();
			return;
		}
		if (command.equals("rejoin")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					if (!game.getMode().equals("perpetual")) {
						event.getChannel().sendMessage("You can only rejoin perpetual games").queue();
						return;
					}
					if (game.getDroppedOuts().contains(event.getAuthor().getId())) {
						action();
					} else {
						event.getChannel().sendMessage("You haven't dropped out of the game").queue();
					}
					return;
				}
			}
			event.getChannel().sendMessage("There is no game going on in this channel").queue();
			return;
		}
		if (command.equals("check")) {
			if (args.length < 1) {
				event.getChannel().sendMessage("Please provide a word to check").queue();
				return;
			}
		}
		if (command.equals("define")) {
			if (args.length < 1) {
				event.getChannel().sendMessage("Please provide a word to define").queue();
				return;
			}
			if (!Utils.dictionarySearch(args[0].toUpperCase())) {
				event.getChannel().sendMessage("No word recognized").queue();
				return;
			}
		}
		if (command.equals("example")) {
			if (args.length < 1) {
				event.getChannel().sendMessage("Choose a mode to show an example round for: Classic [c], Perpetual [p], Elimination [e]").queue();
				return;
			}
		}
		if (command.equals("nick")) {
			if (args.length > 0) {
				List<Member> membersWithNick = event.getGuild().getMembersByNickname(args[0], true);
				List<Member> membersWithName = event.getGuild().getMembersByName(args[0], true);
				if ((membersWithNick.size() > 0) || (membersWithName.size() > 0)) {
					event.getChannel().sendMessage("That name is already taken. Please choose a different one").queue();
					return;
				}
			}
		}
		if (command.equals("dismissgame") || command.equals("cancelgame")) {
			if (!Utils.isManager(event.getMember())) {
				return;
			}
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					action();
					return;
				}
			}
			event.getChannel().sendMessage("There is no game going on in this channel").queue();
			return;
		}
		if (command.equals("terminate")) {
			if (!Utils.isDev(event.getMember())) {
				return;
			}			
		}
		if (command.equals("tops")) {
			if (args.length < 1 || !Utils.currentModes.contains(args[0].toUpperCase())) {
				event.getChannel().sendMessage("The current modes are: Classic [c], Perpetual [p], Elimination [e]").queue();
				return;
			}
			int mode = Utils.currentModes.indexOf(args[0].toUpperCase()) % 4;
			if (args.length < 2 || !Utils.has(Stats.statsTypes[mode], args[1].toUpperCase())) {
//				if (mode == 0) {
//					
//				}
//				if (mode == 1) {
//					event.getChannel().sendMessage("You can sort Perpetual stats by: POINTS, WINS, LOSSES, CORRECTS, MISSES, FIRSTS, LONES, HIGHESTS, ROUNDS, PENALTIES, BONUSES, TRIPLES, HANDICAPS, WINSTREAK, ROUNDSTREAK").queue();
//				}
//				if (mode == 2) {
//					
//				} 
//				if (mode == 3) {
//					
//				}
				event.getChannel().sendMessage("You can sort all gamemode stats by: POINTS, WINS, LOSSES, CORRECTS, MISSES, FIRSTS, LONES, HIGHESTS, ROUNDS, PENALTIES, BONUSES, TRIPLES, HANDICAPS, WINSTREAK, ROUNDSTREAK").queue();
				return;
			}
		}
		if (command.equals("topr")) {
			if (args.length < 1 || !Utils.currentModes.contains(args[0].toUpperCase())) {
				event.getChannel().sendMessage("The current modes are: Classic [c], Perpetual [p], Elimination [e]").queue();
				return;
			}
			int mode = Utils.currentModes.indexOf(args[0].toUpperCase()) % 4;
			if (args.length < 2 || !Utils.has(Stats.recordsTypes[mode], args[1].toUpperCase())) {
//				if (mode == 0) {
//					
//				}
//				if (mode == 1) {
//					event.getChannel().sendMessage("You can sort Perpetual records by: WINSTREAK, ROUNDSTREAK, SHORTESTGAME, LONGESTGAME, POINTSWORD, POINTSGAME").queue();
//				}
//				if (mode == 2) {
//					
//				} 
//				if (mode == 3) {
//					
//				}
				event.getChannel().sendMessage("You can sort all gamemode records by: WINSTREAK, ROUNDSTREAK, SHORTESTGAME, LONGESTGAME, POINTSWORD, POINTSGAME").queue();
				return;
			}
		}
		action();
	}
	//
	//
	//
	
	public void action() {
		System.out.println("Command is: "+command);
		System.out.println("Args is: "+Arrays.toString(args));
		// Does stuff here
		if (command.equals("hi")) {
			event.getChannel().sendMessage("Hi there, "+event.getAuthor().getName()).queue();
		}
		if (command.equals("embedtest")) {
			// ╔═══╦═══╦═══╗
			// ║ X ║ O ║ X ║
			// ╠═══╬═══╬═══╣
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(Color.YELLOW);
			embed.setTitle("Title");
			String boardA = "```hi u g```";
			String boardB = "``` ╔═══╦═══╦═══╗\n ║ X ║ O ║ X ║\n ╠═══╬═══╬═══╣\n ║ O ║ O ║ X ║\n ╠═══╬═══╬═══╣\n ║ X ║ X ║   ║\n ╚═══╩═══╩═══╝```";
			embed.addField("A",boardB,true);
			embed.addField("B",boardB,true);
			embed.addField("C",boardB,true);
			embed.addField("D",boardB,true);
			embed.addField("E",boardB,true);
			embed.addField("F",boardB,true);
			embed.addField("G",boardB,true);
			embed.addField("H",boardB,true);
			embed.addField("I",boardB,true);
			event.getChannel().sendMessage(embed.build()).queue();
			EmbedBuilder embed2 = new EmbedBuilder();
			embed2.setColor(Color.YELLOW);
			embed2.setTitle("Title");
			String boardB2 = "``` ╔═══╦═══╦═══╗\n ║ X ║ O ║ X ║\n ╠═══╬═══╬═══╣\n ║ O ║ O ║ X ║\n ╠═══╬═══╬═══╣\n ║ X ║ X ║   ║\n ╚═══╩═══╩═══╝```";
			embed2.addField("A",boardB2,true);
			event.getChannel().sendMessage(embed2.build()).queue();
		}
		if (command.equals("getId")) {
			System.out.println(event.getMessageId());
		}
		if (command.equals("ping")) {
			event.getChannel().sendMessage(event.getAuthor().getName()+", your ping is: " + event.getJDA().getPing() + "ms").queue();
		}
		if (command.equals("createStats")) {
			Stats.createStatsProfile(args[0]);
		}
		if (command.equals("help")) {
			if (args.length > 0) {
				int mode = Utils.currentModes.indexOf(args[0].toUpperCase()) % 4;
				String helpMsg = "**Help** Memorandum (Game Bot)";
				if (mode == 1) {
					helpMsg += "\n*Perpetual Mode*";
					helpMsg += "\nEach round, you are given 2 or 3 random letters which serve as the target letters";
					helpMsg += "\nAnd you have 15 seconds to type any english word containing the target letters";
					helpMsg += "\nFor example, if the target letters are **PL**, you can correctly type '**pl**ay' or 'ap**pl**e' but not 'pull'";
					helpMsg += "\nThere are also 3 different bonus letters per round. If your word has any, you gain bonus points";
					helpMsg += "\nOther ways to gain additional points include answering first and being the only one to answer correctly";
					helpMsg += "\nSome rounds contain a trap letter. Words containing it are not counted";
					helpMsg += "\nIf you get the first answer bonus one round, you cannot get it the next round: handicap displayed as [H]";
					helpMsg += "\nIf nobody enters a correct answer, the last guesser loses points";
					helpMsg += "\nYou win after reaching a certain number of points based on the number of rounds you played";
					helpMsg += "\nTo start playing, type ``;startgame p``, you need a minimum of 2 players";
					helpMsg += "Still confused? Type ``;example p`` fore more help";
					//helpMsg += "\nFor a list of commands, type ``?commands``";
				}
				event.getChannel().sendMessage(helpMsg).queue();
			} else {
				String helpMsg = "**Help** Memorandum (Game Bot)";
				helpMsg += "\nThis basic bot runs a game for you and your friends to play";
				helpMsg += "\nEssentially, the game is based on using quick thinking to type correct words in chat";
				helpMsg += "\nThere are currently **4** different modes. Classic (c), Perpetual (p), Elimination (e), and Survival (s)";
				helpMsg += "\nTo begin learning about each mode, type ``;help c/p/e/s``";
				event.getChannel().sendMessage(helpMsg).queue();
			}
		}
		if (command.equals("addstat")) {
			Stats.addStat(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]));
		}
		if (command.equals("addrecord")) {
			System.out.println("adding record");
			Stats.addRecord(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]));
		}
		if (command.equals("clearstats")) {
			Stats.clearStats();
		}
		if (command.equals("clearrecords")) {
			Stats.clearRecords();
		}
		if (command.equals("currentgames")) {
			for (Game game : GlobalVars.currentGames) {
				System.out.println("Channel Name: "+game.gameChannel.getName()+"ID: "+game.gameChannel.getId());
			}
		}
		if (command.equals("save")) {
			if (args[0].equals("stats")) {
				try {
					Stats.saveStats();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (args[0].equals("records")) {
				try {
					Stats.saveRecords();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (command.equals("load")) {
			if (args[0].equals("stats")) {
				try {
					Stats.loadStats();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (args[0].equals("records")) {
				try {
					Stats.loadRecords();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (command.equals("startgame")) {
			int mode = Utils.currentModes.indexOf(args[0].toUpperCase()) % 4;
			if (mode == 0) {
				Classic classicGame = new Classic(event.getChannel(),event.getGuild());
				GlobalVars.addGame(classicGame);
				classicGame.pregame();
				//classicGame.addPlayer(event.getAuthor().getId());
			} else if (mode == 1) {
				Perpetual perpetualGame = new Perpetual(event.getChannel(),event.getGuild());
				GlobalVars.addGame(perpetualGame);
				perpetualGame.countdown();
			} else if (mode == 2) {
				Elimination eliminationGame = new Elimination(event.getChannel(),event.getGuild());
				GlobalVars.addGame(eliminationGame);
				eliminationGame.pregame();
			}
		}
		if (command.equals("join")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					if (!game.getPlayers().contains(event.getAuthor().getId()) && (game.getMode().equals("classic") || game.getMode().equals("elimination"))) {
						if (game.getStatus().equals("pregame")) {
							game.addPlayer(event.getAuthor().getId());
						} else {
							event.getChannel().sendMessage("You can only join Classic or Elimination games before they start").queue();
						}
					}
				}
			}
		}
		if (command.equals("leave")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					if (game.getMode().equals("classic") || game.getMode().equals("elimination")) {
						if (game.getStatus().equals("pregame")) {
							if (game.getPlayers().contains(event.getAuthor().getId())) {
								game.removePlayer(event.getAuthor().getId());
							}
						} else {
							event.getChannel().sendMessage("You can only leave Classic or Elimination games before they start").queue();
						}
					}
				}
			}
		}
		if (command.equals("ready")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					if (game.getMode().equals("classic") || game.getMode().equals("elimination")) {
						if (game.getPlayers().contains(event.getAuthor().getId())) {
							if (game.getStatus().equals("pregame")) {
								game.readyPlayer(event.getAuthor().getId());
							}
						} else {
							event.getChannel().sendMessage("You are not in the game yet. Type ``;join``").queue();
						}
					}
				}
			}
		}
		if (command.equals("players")) {
				for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
					if (game.getGameChannel().equals(event.getChannel())) {
						if ((game.getMode().equals("classic") || game.getMode().equals("elimination")) && game.getStatus().equals("pregame")) {
							game.displayPlayers();
						}
					}
				}
		}
		if (command.equals("dropout")) {
			System.out.println("Detected dropout command");
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					System.out.println("Matched");
					game.dropOut(event.getAuthor().getId());
				}
			}
		}
		if (command.equals("rejoin")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					game.rejoin(event.getAuthor().getId());
				}
			}
		}
		if (command.equals("check")) {
			String word = args[0].toUpperCase();
			if (Utils.dictionarySearch(word)) {
				event.getChannel().sendMessage(":white_check_mark: " + word + " is a word!").queue();
			} else {
				// Doesn't work for other servers due to emoji
				event.getChannel().sendMessage("<:incorrect:404126060117229578> " + word + " is not a word!").queue();
				// Prevents cheating system
				for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
					if (game.getGameChannel().equals(event.getChannel())) {
						if (game.getStatus().equals("takingAnswers") && game.verifyAnswer(word, event.getAuthor().getId(), true).equals("wrong")) {
							game.setLastAnswerer(event.getAuthor().getId());
							System.out.println("Wrong guess using check");
						}
					}
				}
			}
		}
		if (command.equals("define")) {
			String definition = "";
			String word = args[0].toLowerCase();
			try {
				JSONArray jsons = new JSONArray(Utils.readUrl("https://owlbot.info/api/v2/dictionary/"+word+"?format=json"));
				definition = "**Defining "+word.toUpperCase()+"**";
				if (jsons.length() > 0) {
					for (int i = 0; i < jsons.length(); i++) {
						Object typeObject = jsons.getJSONObject(i).get("type");
						String type = "";
						if (!JSONObject.NULL.equals(typeObject)) {
							type = (String) typeObject;
							type = "*"+Character.toUpperCase(type.charAt(0)) + type.substring(1)+"*"+": ";
						}
						definition += "\n" + type + jsons.getJSONObject(i).get("definition");
					}
				} else {
					definition = "No definition found for "+word.toUpperCase();
				}

			} catch (JSONException e1) {
			    e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			definition = definition.replaceAll("<b>","").replaceAll("</b>","").replaceAll("Definition of ","").replaceAll(". :",":").replaceAll("â", "\"");
			event.getChannel().sendMessage(definition).queue();
		}
		if (command.equals("sentence")) {
			String word = args[0].toLowerCase();
			try {
				JSONArray jsons = new JSONArray(Utils.readUrl("https://owlbot.info/api/v2/dictionary/"+word+"?format=json"));
				if (jsons.length() > 0) {
					for (int i = 0; i < jsons.length(); i++) {
						Object exampleObject = jsons.getJSONObject(i).get("example");
						String example = "";
						if (!JSONObject.NULL.equals(exampleObject)) {
							example = (String) exampleObject;
							example = example.replaceAll("<b>","").replaceAll("</b>","").replaceAll("â", "\"");
							example = Character.toUpperCase(example.charAt(0)) + example.substring(1);
							event.getChannel().sendMessage("**Sentence with "+word.toUpperCase()+"**: "+example).queue();
							return;
						}
					}
				}
				event.getChannel().sendMessage("No sentence found for "+word.toUpperCase()).queue();
			} catch (JSONException e1) {
			    e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (command.equals("example")) {
			int mode = Utils.currentModes.indexOf(args[0].toUpperCase()) % 4;
			if (mode == 0) {
				event.getChannel().sendMessage("*Here is an example of what a classic mode round looks like:*").queue();
				String example1 = "══════ **[START OF EXAMPLE ROUND]** ══════\nTarget is: **RAB** Bonuses are: T (4) I (1) X (8)";
				String example2 = "\n\n**(Player1)**: rabbit";
				example2 += "\n\n:white_check_mark: RABBIT!  [1st] ``Player1`` earned **16** points!";
				String example3 = "\n\n**(Player2)**: grab";
				example3 += "\n\n:white_check_mark: GRAB! ``Player2`` earned **3** points!";
				String example4 = "\n*Example round ending in 3 seconds*";
				String example5 = "\n\n══════ **[END OF EXAMPLE ROUND]** ══════\n**★** A total of **19** points was awarded last round!";
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.YELLOW);
				String names = "[H] Player1\nPlayer2";
				String scores = "*16*\n*3*";
				embed.setTitle("Round #1 | First to 100 Points");
				embed.addField("PLAYERS",names,true);
				embed.addField("SCORES",scores,true);
				event.getChannel().sendMessage(example1).queueAfter(1, TimeUnit.SECONDS);
				event.getChannel().sendMessage(example2).queueAfter(6, TimeUnit.SECONDS);
				event.getChannel().sendMessage(example3).queueAfter(6, TimeUnit.SECONDS);
				event.getChannel().sendMessage(example4).queueAfter(15, TimeUnit.SECONDS);
				event.getChannel().sendMessage(example5).queueAfter(18, TimeUnit.SECONDS);
				event.getChannel().sendMessage("══════ **[EXAMPLE SCOREBOARD]** ══════").queueAfter(22,  TimeUnit.SECONDS);
				event.getChannel().sendMessage(embed.build()).queueAfter(22, TimeUnit.SECONDS);
			}
			if (mode == 1) {
				event.getChannel().sendMessage("*Here is an example of what a perpetual mode round looks like:*").queue();
				String example1 = "══════ **[START OF EXAMPLE ROUND]** ══════\nTarget is: **RAB** Bonuses are: T (4) I (1) X (8)";
				String example2 = "\n\n**(Player1)**: rabbit";
				example2 += "\n\n:white_check_mark: RABBIT!  [1st] ``Player1`` earned **16** points!";
				String example3 = "\n*Example round ending in 3 seconds*";
				String example4 = "\n\n══════ **[END OF EXAMPLE ROUND]** ══════\n**»** ``Player1`` was the only person to answer correctly! **+10**\n**★** A total of **26** points was awarded last round!";
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(Color.YELLOW);
				String names = "[H] Player1";
				String scores = "*26*/50";
				String rounds = "1";
				embed.addField("PLAYERS",names,true);
				embed.addField("SCORES",scores,true);
				embed.addField("ROUNDS",rounds,true);
				
				event.getChannel().sendMessage(example1).queueAfter(1, TimeUnit.SECONDS);
				event.getChannel().sendMessage(example2).queueAfter(6, TimeUnit.SECONDS);
				event.getChannel().sendMessage(example3).queueAfter(13, TimeUnit.SECONDS);
				event.getChannel().sendMessage(example4).queueAfter(16, TimeUnit.SECONDS);
				event.getChannel().sendMessage("══════ **[EXAMPLE SCOREBOARD]** ══════").queueAfter(20,  TimeUnit.SECONDS);
				event.getChannel().sendMessage(embed.build()).queueAfter(20, TimeUnit.SECONDS);
			}
			if (mode == 2) {
				
			}
		}
		if (command.equals("rawstats") ) {
			for (String player : Stats.playerStats.get(Integer.parseInt(args[0])).keySet()) {
				System.out.print(player + ": " + Stats.playerStats.get(Integer.parseInt(args[0])).get(player).get(Integer.parseInt(args[1])) + " |");
			}
		}
		if (command.equals("stats")) {
			String user = event.getAuthor().getId();
			String avatarUrl = event.getAuthor().getEffectiveAvatarUrl();
			int mode = 1;
			if (args.length > 0) {
				if (Utils.isInt(args[0])) {
					mode = Integer.parseInt(args[0]);
				} else if (Utils.currentModes.contains(args[0].toUpperCase())) {
					mode = Utils.currentModes.indexOf(args[0].toUpperCase()) % 4;
				}
			}
			if (args.length > 1) {
				if (event.getGuild().getMembersByEffectiveName(args[1], true).size() > 0) {
					user = Utils.getID(args[1], event.getGuild());
					avatarUrl = event.getGuild().getMemberById(user).getUser().getEffectiveAvatarUrl();
				} else {
					event.getChannel().sendMessage("No user in this server found with the name: "+args[1]).queue();
					return;
				}
			}
			System.out.println(Stats.playerStats.get(mode).get(user));
			// Not fully working?
			if (Stats.playerStats.get(mode).get(user).isEmpty()) {
				event.getChannel().sendMessage("No stats in mode "+mode+" found for "+args[0]).queue();
				return;
			}
			System.out.println("Fetching stats");
			EmbedBuilder embed = new EmbedBuilder();
			
			embed.setAuthor(Utils.getEffectiveName(user, event.getGuild()) + " ("+user+")", null, avatarUrl);
			embed.setTitle("["+Utils.currentModes.get(mode+4) + "]");
			embed.setColor(Color.CYAN);
			if (mode == 0 || mode == 1 || mode == 2) {
				embed.addField("Points", "`` "+Stats.playerStats.get(mode).get(user).get(0)+" ``", true);
				embed.addField("Wins", "`` "+Stats.playerStats.get(mode).get(user).get(1)+" ``", true);
				embed.addField("Losses", "`` "+Stats.playerStats.get(mode).get(user).get(2)+" ``", true);
				embed.addField("Correct Guesses", "`` "+Stats.playerStats.get(mode).get(user).get(3)+" ``", true);
				embed.addField("Rounds Missed", "`` "+Stats.playerStats.get(mode).get(user).get(4)+" ``", true);
				embed.addField("First Guesses", "`` "+Stats.playerStats.get(mode).get(user).get(5)+" ``", true);
				embed.addField("Lone Guesses", "`` "+Stats.playerStats.get(mode).get(user).get(6)+" ``", true);
				embed.addField("Highest Points", "`` "+Stats.playerStats.get(mode).get(user).get(7)+" ``", true);
				embed.addField("Rounds Played", "`` "+Stats.playerStats.get(mode).get(user).get(8)+" ``", true);
				embed.addField("Penalties", "`` "+Stats.playerStats.get(mode).get(user).get(9)+" ``", true);
				embed.addField("Bonus Uses", "`` "+Stats.playerStats.get(mode).get(user).get(10)+" ``", true);
				embed.addField("Triple Bonus Uses", "`` "+Stats.playerStats.get(mode).get(user).get(11)+" ``", true);
				embed.addField("Handicaps", "`` "+Stats.playerStats.get(mode).get(user).get(12)+"``", true);
				embed.addField("Win Streak", "`` "+Stats.playerStats.get(mode).get(user).get(13)+" ``", true);
				embed.addField("Round Streak", "`` "+Stats.playerStats.get(mode).get(user).get(14)+" ``", true);
				event.getChannel().sendMessage(embed.build()).queue();
//				if (mode == 1) {
//					EmbedBuilder embed2 = new EmbedBuilder();
//					embed2.addField("3+ Streaks", "`` "+Stats.playerStats.get(mode).get(user).get(15)+"``", true);
//					embed2.addField("5+ Streaks", "`` "+Stats.playerStats.get(mode).get(user).get(16)+" ``", true);
//					embed2.addField("10+ Streaks", "`` "+Stats.playerStats.get(mode).get(user).get(17)+" ``", true);
//					event.getChannel().sendMessage(embed2.build()).queue();
//				}
			}

		}
		if (command.equals("records")) {
			String user = event.getAuthor().getId();
			String avatarUrl = event.getAuthor().getEffectiveAvatarUrl();
			int mode = 1;
			if (args.length > 0) {
				if (Utils.isInt(args[0])) {
					mode = Integer.parseInt(args[0]);
				} else if (Utils.currentModes.contains(args[0].toUpperCase())) {
					mode = Utils.currentModes.indexOf(args[0].toUpperCase()) % 4;
				}
			}
			if (args.length > 1) {
				if (event.getGuild().getMembersByEffectiveName(args[1], true).size() > 0) {
					user = Utils.getID(args[1], event.getGuild());
					avatarUrl = event.getGuild().getMemberById(user).getUser().getEffectiveAvatarUrl();
				} else {
					event.getChannel().sendMessage("No user in this server found with the name: "+args[1]).queue();
					return;
				}
			}
			System.out.println(Stats.playerRecords.get(mode).get(user));
			// Not fully working?
			if (Stats.playerRecords.get(mode).get(user).isEmpty()) {
				event.getChannel().sendMessage("No records in mode "+mode+" found for "+args[0]).queue();
				return;
			}
			System.out.println("Fetching records");
			EmbedBuilder embed = new EmbedBuilder();
			
			embed.setAuthor(Utils.getEffectiveName(user, event.getGuild()) + " ("+user+")", null, avatarUrl);
			if (mode == 0 || mode == 1 || mode == 2) {
				embed.addField("Longest Win Streak", "`` "+Stats.playerRecords.get(mode).get(user).get(0)+" ``", true);
				embed.addField("Longest Round Streak", "`` "+Stats.playerRecords.get(mode).get(user).get(1)+" ``", true);
				embed.addField("Shortest Game", "`` "+Stats.playerRecords.get(mode).get(user).get(2)+" ``", true);
				embed.addField("Longest Game", "`` "+Stats.playerRecords.get(mode).get(user).get(3)+" ``", true);
				embed.addField("Most Points in Word", "`` "+Stats.playerRecords.get(mode).get(user).get(4)+" ``", true);
				embed.addField("Most Points in Game", "`` "+Stats.playerRecords.get(mode).get(user).get(5)+" ``", true);
				embed.setColor(Color.GREEN);
			}
			event.getChannel().sendMessage(embed.build()).queue();
		}
		if (command.equals("tops")) {
			int mode = Utils.currentModes.indexOf(args[0].toUpperCase()) % 4;
			String type = args[1].toUpperCase();
			int index = Utils.indexOf(Stats.statsTypes[mode],type);
			System.out.println("Creating leaderboard for mode "+mode+" and index "+index);
			List<Entry<String, Integer>> sortedList = Stats.sortData(Stats.playerStats, mode, index);
			int counter = 0;
			EmbedBuilder embed = new EmbedBuilder();
			embed.setAuthor("Stats Leaderboards for "+Utils.currentModes.get(mode+4));
			embed.setColor(Color.RED);
			String name = "──────────────\n";
			String stat = "──────────────\n";
			for (Map.Entry<String, Integer> entry : sortedList) {
				counter++;
				name += counter + ". ``" + Utils.getEffectiveName(entry.getKey(), event.getGuild()) + "``\n";
				stat += "\t\t**" + entry.getValue() + "**\n";
				if (counter == 10) {
					break;
				}
			}
			embed.addField("PLAYERS",name,true);
			embed.addField(type,stat,true);
			event.getChannel().sendMessage(embed.build()).queue();
			System.out.println("Displayed "+stat);
		}
		if (command.equals("topr")) {
			int mode = Utils.currentModes.indexOf(args[0].toUpperCase()) % 4;
			String type = args[1].toUpperCase();
			int index = Utils.indexOf(Stats.recordsTypes[mode],type);
			List<Entry<String, Integer>> sortedList = Stats.sortData(Stats.playerRecords, mode, index);
			// For SHORTESTGAME
			if (index == 2) {
				Collections.reverse(sortedList);
			}
			int counter = 0;
			EmbedBuilder embed = new EmbedBuilder();
			embed.setAuthor("Records Leaderboards for "+Utils.currentModes.get(mode+4));
			embed.setColor(Color.ORANGE);
			String name = "──────────────\n";
			String record = "──────────────\n";
			for (Map.Entry<String, Integer> entry : sortedList) {
				counter++;
				name += counter + ". ``" + Utils.getEffectiveName(entry.getKey(), event.getGuild()) + "``\n";
				record += "\t\t**" + entry.getValue() + "**\n";
				if (counter == 10) {
					break;
				}
			}
			embed.addField("PLAYERS",name,true);
			embed.addField(type,record,true);
			event.getChannel().sendMessage(embed.build()).queue();
		}
		if (command.equals("nick")) {
			GuildController test = new GuildController(event.getGuild());
			if (args.length < 1) {
				test.setNickname(event.getMember(),event.getAuthor().getName()).queue();
				event.getChannel().sendMessage("Reset nickname").queue();
			} else {
				test.setNickname(event.getMember(),args[0]).queue();
				event.getChannel().sendMessage("Changed nickname to: **"+args[0]+"**").queue();
			}
		}
		if (command.equals("dismissgame")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					game.calculateResult("No winner");
					game.endGame();
					event.getChannel().sendMessage("══════ **[GAME OVER]** ══════\n**Dismissed game.** Stats counted but no winner.").queue();
				}
			}
		}
		if (command.equals("cancelgame")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					game.endGame();
					try {
						Stats.loadStats();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					event.getChannel().sendMessage("══════ **[GAME OVER]** ══════\n**Cancelled game.** Stats not counted.").queue();
				}
			}
		}
		if (command.equals("terminate")) {
			try {
				Stats.saveStats();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Stats.saveRecords();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			event.getChannel().sendMessage("Terminated bot").queue();
			System.exit(0);
		}
	}
	
	public String help() {
		
		return "help menu";
	}
	public String command = "";
	public String[] args = {};
	MessageReceivedEvent event;
}
