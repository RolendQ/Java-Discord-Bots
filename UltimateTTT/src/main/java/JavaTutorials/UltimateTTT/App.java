package JavaTutorials.UltimateTTT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.log4j.BasicConfigurator;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

public class App extends ListenerAdapter {
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, FileNotFoundException, ClassNotFoundException, IOException {
    	// Removes error messages
    	//BasicConfigurator.configure();
    	
    	JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("NDM1MTYyNDAyODA4NTI4ODk3.DbU8ZQ.WOMLQ8IfaVCh1-mE3DpJxGyipfE").buildBlocking();
    	jdaBot.addEventListener(new App());
    	jdaBot.getPresence().setGame(Game.of("type ;help"));
    	
    	System.out.println(jdaBot.getGuilds().get(0).getTextChannels());
    	System.out.println("Setting up UltimateTTT bot...");
    }

	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
		if (!e.getMember().getUser().isBot()) {
			System.out.println("Emoji: "+e.getReaction().getEmote().getName());
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (e.getMember().getUser().getId().equals(game.getTurnPlayerID()) && game.getGameChannel().equals(e.getChannel())) {
					String emoji = e.getReaction().getEmote().getName();
					if (Utils.has(Utils.boardLettersEmoji, emoji)) {
						// Overwrite with every new reaction
						game.setBoardChoice(Utils.indexOf(Utils.boardLettersEmoji, emoji));
						System.out.println("Detected reaction of: "+game.getBoardChoice());
					} else if (Utils.has(Utils.numbersEmoji, emoji)) {
						game.attemptMove(game.getBoardChoice(), Utils.indexOf(Utils.numbersEmoji, emoji));
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
    public void onMessageReceived(MessageReceivedEvent e) {
		String message = e.getMessage().getContent();
		if (e.getAuthor().isBot()) {
			System.out.println(e.getAuthor().getId());
			// Bot ID needed
			if (e.getAuthor().getId().equals("435162402808528897")) {
				if (message.equals("```Loading board...```")) {
					for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
						if (game.getGameChannel().equals(e.getChannel())) {
							game.setBoardID(e.getMessageId());
						}
					}
				} else if (message.equals("```╔-------╗\r\n" + 
						"│ 1 2 3 |\r\n" + 
						"│ 4 5 6 |\r\n" + 
						"│ 7 8 9 |\r\n" + 
						"╚-------╝```")) {
					for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
						if (game.getGameChannel().equals(e.getChannel())) {
							game.setTilesID(e.getMessageId());
						}
					}					
				} else if (message.equals("Prepping ASCII art...")) {
					for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
						if (game.getGameChannel().equals(e.getChannel())) {
							game.setTurnID(e.getMessageId());
							game.displayBoard();
						}
					}		
				}
			}
			return;
		}
		// Bot command symbol
		if (message.startsWith(";")) {
			String commandName = message.split(" ")[0];
			String[] args = {};
			if (message.split(" ").length > 1) {
				args = message.replace(commandName+" ","").split(" ");
			}
			Commands command = new Commands(commandName.substring(1),e,args);
			command.attempt();
			command = null;
		}
		if (message.length() < 3) {
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(e.getChannel())) {
					// check for real move
					if (e.getAuthor().getId().equals(game.getTurnPlayerID())) {
						if (message.length() == 2) {
							if (Utils.has(Utils.boardLetters, message.toUpperCase().charAt(0))) {
								if (Utils.isInt(message.substring(1)) && Integer.parseInt(message.substring(1)) > 0) {
									game.attemptMove(Utils.indexOf(Utils.boardLetters, message.toUpperCase().charAt(0)),Integer.parseInt(message.substring(1))-1);
								} else {
									e.getChannel().sendMessage("Inputted number must be between 1-9").queue();
								}
							} else {
								e.getChannel().sendMessage("Inputted letter must be between A and I").queue();
							}
						} else {
							if (Utils.isInt(message.substring(0)) && Integer.parseInt(message.substring(0)) > 0) {
								game.attemptMove(game.getRequiredMoveBoard(),Integer.parseInt(message.substring(0))-1);
							} else {
								e.getChannel().sendMessage("Inputted number must be between 1-9").queue();
							}
						}
					}
				}
			}
		}
	}
}
