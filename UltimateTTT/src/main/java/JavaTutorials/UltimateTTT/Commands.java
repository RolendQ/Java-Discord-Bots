package JavaTutorials.UltimateTTT;

import java.util.Arrays;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Commands {
	// Set it null to delete it
	public Commands(String command,MessageReceivedEvent event,String[] args) {
		this.command = command;
		this.args = args;
		this.event = event;
	}
	
	public void attempt() {
		// Check if requirements/args are met
		if (command.equals("creategame")) {
			
		}
		action();
	}
	
	public void action() {
		System.out.println("Command is: "+command);
		System.out.println("Args is: "+Arrays.toString(args));
		
		if (command.equals("rules")) {
			event.getChannel().sendMessage("__Ultimate Tic Tac Toe__ \r\n" + 
					"Objective: Complete 3 sections in a row with giant X's or O's on this board below!\n"+ 
					"```╔---A---╦---B---╦--[C]--╗\r\n" + 
					"│       |       |       |\r\n" + 
					"│   O   |   O   |   O   |\r\n" + 
					"│     X |       |       |\r\n" + 
					"╠---D---╬---E---╬---F---╣\r\n" + 
					"│     X | \\ \\/ /|       |\r\n" + 
					"│   O   |  >  < | X     |\r\n" + 
					"│       | /_/\\_\\| X     |\r\n" + 
					"╠---G---╬---H---╬---I---╣\r\n" + 
					"│ O     |       |       |\r\n" + 
					"│       | O     |       |\r\n" + 
					"│       |   X   |   O   |\r\n" + 
					"╚-------╩-------╩-------╝```This is a 3x3 board comprised of 9 **sections** (labeled A through I); Each section is also a 3x3 board comprised of 9 **tiles**(labeled 1 through 9).\r\n" + 
					"\r\n" + 
					"Player 1 has **free choice** on his first turn. This means he can play on any of the empty tiles. Subsequent moves are influenced by the preceding move: Player 2 must play in the section corresponding to the tile Player 1 played in (i.e. tile 1 corresponds to section A, 2 to B, etc). Once a player wins a section by completing 3 X's or O's in a row, the section will be shown as a giant X or O, respectively, and no further move will be allowed in said section. If a player is \"sent\" to a completed section, then said player will have free choice on that turn").queue();
		}
		
		if (command.equals("help")) {
			event.getChannel().sendMessage("To begin playing, one player must issue a challenge by typing either ``;challenge @otherplayer`` or ``;challenge`` to open it up to anyone.\r\n" + 
					"Then, the second player must type ``;accept @firstplayer`` to start the game.\r\n" + 
					"Players move by typing one of the 81 positions on the board when it's their turn. Positions are specified by one of the 9 **sections** and one of the 9 **tiles** in each section. A valid move would be ``E5`` which is the center tile of the center section\r\n" + 
					"```╔---A---╗\r\n" + 
					"│ 1 2 3 |\r\n" + 
					"│ 4 5 6 |\r\n" + 
					"│ 7 8 9 |\r\n" + 
					"╚-------╝```\r\n" + 
					"If a player is forced to play in a section, they can omit the letter and just type the number. For example: ``9``\n" +
					"Players can also use ``;refresh`` to move the board to the bottom of the channel and ``;end`` to forfeit the game").queue();
		}
		
		if (command.equals("creategame")) {
			Game newGame = new Game(event.getChannel(),event.getGuild(),"","");
			GlobalVars.add(newGame);
			System.out.println("Created game manually");
		}
		
		if (command.equals("test")) {
			System.out.println("ID: "+event.getMessage().getMentionedMembers().get(0).getUser().getId());
		}
		
		if (command.equals("printvalue")) {
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					System.out.println("Value of "+args[0].charAt(0)+": "+game.getValue(args[0].charAt(0)));
				}
			}
		}
		
		if (command.equals("score")) {
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					event.getChannel().sendMessage("Current score (X +) (O -): " + (game.getValue('X') - game.getValue('O'))).queue();
				}
			}
		}
		
		if (command.equals("addreaction") ) {
			System.out.println("adding reaction");
			//event.getMessage().addReaction("🌍").queue();
			//event.getMessage().addReaction(":regional_indicator_b:");
			String msgID = event.getMessageId();
			event.getChannel().addReactionById(msgID,"🇦").queue();
			System.out.println("added reaction");
		}
		
		if (command.equals("listchallenges")) {
			for (Challenge c : GlobalVars.pendingChallenges) {
				if (c.getChallengedID() == null) {
					System.out.println("Challenge: "+c.getChallengerID());
				} else {
					System.out.println("Challenge: "+c.getChallengerID()+" > "+c.getChallengedID());
				}
			}
		}
		if (command.equals("challenge")) {
			Challenge newChallenge;
			if (event.getMessage().getMentionedMembers().size() > 0) {
				if (event.getMessage().getMentionedMembers().get(0).getUser().getId().equals("435162402808528897")) {
					Game newGame = new Game(event.getChannel(),event.getGuild(),"Computer",event.getAuthor().getId());
					GlobalVars.add(newGame);
					System.out.println("Created game with computer");
					newGame.start();	
					return;
				} else {
					newChallenge = new Challenge(event.getAuthor().getId(),event.getMessage().getMentionedMembers().get(0).getUser().getId());
				}
			} else {
				newChallenge = new Challenge(event.getAuthor().getId());
			}
			GlobalVars.add(newChallenge);
		}
		
		if (command.equals("accept")) {
			if (event.getMessage().getMentionedMembers().size() > 0) {
				for (Challenge c : GlobalVars.pendingChallenges) {
					if (c.getChallengerID().equals(event.getMessage().getMentionedMembers().get(0).getUser().getId()) && (c.getChallengedID() == null || c.getChallengedID().equals(event.getAuthor().getId()))) {
						Game newGame = new Game(event.getChannel(),event.getGuild(),c.getChallengerID(),event.getAuthor().getId());
						GlobalVars.add(newGame);
						System.out.println("Created game from challenge");
						newGame.start();
						break;
					}
				}
			}
		}
		
		if (command.equals("reset")) {
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					game.reset();
				}
			}
		}
		
		if (command.equals("end")) {
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					game.end();
					event.getChannel().sendMessage("Ended game.").queue();
				}
			}			
		}
		
		if (command.equals("playmove")) {
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					game.playMove(args[0].charAt(0),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
				}
			}
		}
		
		if (command.equals("displayboardraw")) {
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					//game.displayBoardRaw();
					System.out.println("Displayed board");
				}
			}
		}
		
		if (command.equals("displayboard")) {
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					game.displayBoard();
				}
			}
		}
		
		if (command.equals("refresh")) {
			for (JavaTutorials.UltimateTTT.Game game : GlobalVars.currentGames) {
				if (game.getGameChannel().equals(event.getChannel())) {
					game.refresh();
				}
			}
		}
	}
	
	public String help() {
		
		return "help menu";
	}
	public String command = "";
	public String[] args = {};
	MessageReceivedEvent event;
}
