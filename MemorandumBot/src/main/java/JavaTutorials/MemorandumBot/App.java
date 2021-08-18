package JavaTutorials.MemorandumBot;


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
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

public class App extends ListenerAdapter {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException, FileNotFoundException, ClassNotFoundException, IOException {
    	// Removes error messages
    	//BasicConfigurator.configure();
    	
    	JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("NDExNjA3MTAzNDAxMjMwMzc2.DV-Npg.IrwdKoDfREtB8-ztQ3_gJLTfD0o").buildBlocking();
    	jdaBot.addEventListener(new App());
    	jdaBot.getPresence().setGame(Game.of("type ;help"));
    	
    	System.out.println(jdaBot.getGuilds().get(0).getTextChannels());
    	System.out.println("Setting up bot...");
    	Stats.loadStats();
    	Stats.loadRecords();
    }
	
//    @Override
//    public void onGuildMemberNickChange(GuildMemberNickChangeEvent e) {
//    	if (e.getNewNick() == null) {
//    		return;
//    	}
//		System.out.println("New nick: " + e.getNewNick());
//		List<Member> membersWithNick = e.getGuild().getMembersByNickname(e.getNewNick(), true);
//		List<Member> membersWithName = e.getGuild().getMembersByName(e.getNewNick(), true);
//		System.out.println(membersWithName.toString());
//		if ((membersWithNick.size() > 1) || (membersWithName.size() > 0)) {
//			System.out.println("Duplicate nick");
//			
//			GuildController test = new GuildController(e.getGuild());
//			test.setNickname(e.getMember(),e.getPrevNick()).queue();
//			System.out.println(e.getPrevNick());
//		} else {
//			System.out.println("Not duplicate nick");
//		}
//    }
	
	@SuppressWarnings("deprecation")
	@Override
    public void onMessageReceived(MessageReceivedEvent e) {
		String message = e.getMessage().getContent();
		if (e.getAuthor().isBot()) {
			if (e.getMessage().getEmbeds().size() > 0 && e.getMessage().getEmbeds().get(0).getTitle().contains("Phase")) {
				for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
					if (game.getGameChannel().equals(e.getChannel())) {
						game.setScoreboardMessageID(e.getMessageId());
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
		if (!message.contains(" ") && !message.contains(";") && !message.contains(".")) {
			for (JavaTutorials.MemorandumBot.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(e.getChannel())) {
					System.out.println("Found game matching channel");
					if (game.getStatus().equals("takingAnswers")) {
						if (game.verifyAnswer(message.toUpperCase(),e.getAuthor().getId(),false).equals("duplicate")) {
		        			System.out.println("Repeat!");
		        			//e.getMessage().delete().queue();
						}
					}
				}
			}
		}
	}
}
