package JavaTutorials.UltimateTTT;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;

public class Game {
	public char[][] board = {};
	public MessageChannel gameChannel;
	public Guild guild;
	public int requiredMoveBoard = -1; // -1 is free choice
	public String playerOneID;
	public String playerTwoID;
	public String nameOne;
	public String nameTwo;
	public char turnChar = 'X';
	public String turnPlayerID;
	// For buttons
	public int boardChoice = -1;
	// For fixed board
	public String boardID = null;
	public String tilesID = null;
	public String turnID = null;
	public String status = "ongoing";
	public Board boardObj;
	
	public Game(MessageChannel channel,Guild guild,String playerOneID, String playerTwoID) {
		this.guild = guild;
		this.gameChannel = channel;
		this.playerOneID = playerOneID;
		this.playerTwoID = playerTwoID;
		if (playerOneID.equals("Computer")) {
			this.nameOne = "Computer";
		} else {
			this.nameOne = guild.getMemberById(playerOneID).getEffectiveName();
		}
		this.nameTwo = guild.getMemberById(playerTwoID).getEffectiveName();
	}
	
	public void reset() {
		board = new char[][] {{' ',' ',' ',' ',' ',' ',' ',' ',' '},
							  {' ',' ',' ',' ',' ',' ',' ',' ',' '},
							  {' ',' ',' ',' ',' ',' ',' ',' ',' '},
							  {' ',' ',' ',' ',' ',' ',' ',' ',' '},
							  {' ',' ',' ',' ',' ',' ',' ',' ',' '},
							  {' ',' ',' ',' ',' ',' ',' ',' ',' '},
							  {' ',' ',' ',' ',' ',' ',' ',' ',' '},
							  {' ',' ',' ',' ',' ',' ',' ',' ',' '},
							  {' ',' ',' ',' ',' ',' ',' ',' ',' '},
							  {' ',' ',' ',' ',' ',' ',' ',' ',' '}};
		boardObj = new Board(board);
		turnChar = 'X';
		turnPlayerID = playerTwoID;
		requiredMoveBoard = -1;
		boardChoice = -1;
	}
	
	public void start() {
		// Challenged goes first
		gameChannel.sendMessage("**"+nameOne+"** vs **"+nameTwo+"**").queue();
		reset();
		refresh();
	}
	
	public void newTurn() {
		if (turnChar == 'O') {
			turnPlayerID = playerTwoID;
			turnChar = 'X';
		} else {
			turnPlayerID = playerOneID;
			turnChar = 'O';
		}	
		displayBoard();
		
		if (turnPlayerID.equals("Computer")) {
			System.out.println("Computer playing");
			int[] move = boardObj.getBestCPUMove(requiredMoveBoard);
			playMove('O',move[0],move[1]);
		}
		//gameChannel.sendMessage("``"+guild.getMemberById(turnPlayerID).getEffectiveName()+"``, it is your turn. (**"+turnChar+"**)").queue();
	}
	
	public void attemptMove(int b, int pos) {
		System.out.println("Attempting move at board " + b + " and position "+pos);
		if (b == -1) {
			//gameChannel.sendMessage("Please input a letter of a smaller board").queue();
			return;
		}
		if ((b == requiredMoveBoard || requiredMoveBoard == -1) && !boardObj.isComplete(b,'X') && !boardObj.isComplete(b,'O') && boardObj.getBoard()[b][pos] == ' ') {
			playMove(turnChar, b, pos);
		} else {
			System.out.println("Failed move");
		}
	}
	
	public void playMove(char c, int b, int pos) {
		//board[b][pos] = c;
		boardObj.play(c,b,pos);
		requiredMoveBoard = pos;
		// For button
		setBoardChoice(pos);
		
		// Free choice if full board
		if (boardObj.isComplete(requiredMoveBoard,'X') || boardObj.isComplete(requiredMoveBoard,'O')) {
			requiredMoveBoard = -1;
			System.out.println("Free choice since that board is full");
		}
		System.out.println("Added " + c + " to board " + b + " in position "+pos);
		boardObj.updateCompletedBoards();
		if (!boardObj.hasWon('X') && !boardObj.hasWon('O')) {
			newTurn();
//		} else if (boardObk) {
//			
		} else {
			status = "finished";
			refresh();
			displayBoard();
			if (turnPlayerID.equals("Computer")) {
				gameChannel.editMessageById(turnID,"Three-in-a-row. ``Computer`` has won!").queue();
			} else {
				gameChannel.editMessageById(turnID,"Three-in-a-row. ``"+guild.getMemberById(turnPlayerID).getEffectiveName()+"`` has won!").queue();
			}
			end();
		}
	}
	
	public void end() {
		GlobalVars.remove(this);
		boardObj = null;
		System.out.println("Game ended");
	}
	
	public int getValue(char c) {
		boardObj.updateCompletedBoards();
		return boardObj.calcBoard(c);
	}
	
//	public void displayBoardRaw() {
//		for (int i = 0; i < boardObj.getBoard().length; i++) {
//			String line = "{";
//			for (int j = 0; j < boardObj.getBoard()[i].length; j++) {
//				line += boardObj.getBoard()[i][j] + ",";
//			}
//			line += "}";
//			System.out.println(line);
//		}
//	}
	
	public void clearReactions() {
		((TextChannel) gameChannel).clearReactionsById(boardID).complete();
		((TextChannel) gameChannel).clearReactionsById(tilesID).complete();
		System.out.println("Reactions cleared");
	}
	
	public void refresh() {
		if (boardID != null) {
			gameChannel.deleteMessageById(boardID).queue();
			gameChannel.deleteMessageById(tilesID).queue();
			gameChannel.deleteMessageById(turnID).queue();
		}
		gameChannel.sendMessage("```Loading board...```").queue();
		gameChannel.sendMessage("```╔-------╗\r\n" + 
				"│ 1 2 3 |\r\n" + 
				"│ 4 5 6 |\r\n" + 
				"│ 7 8 9 |\r\n" + 
				"╚-------╝```").queue();
		gameChannel.sendMessage("Prepping ASCII art...").queue();
		System.out.println("Refreshed");
	}
	
	public void displayBoard() {
		// Edit turn message
		if (status.equals("ongoing")) {
			if (turnPlayerID.equals("Computer")) {
				gameChannel.editMessageById(turnID, "Computer is playing...").queue();
			} else {
				gameChannel.editMessageById(turnID, "``"+guild.getMemberById(turnPlayerID).getEffectiveName()+"``, it is your turn. (**"+turnChar+"**)").queue();
			}
		}
		
		String finalBoard = "```╔---A---╦---B---╦---C---╗";
		// i = 0, 3, 6
		for (int i = 0; i < 7; i += 3) {
			// n = 0, 1, 2
			for (int n = 0; n < 3; n++) {
				finalBoard += "\n│";
				// b = 0, 1, 2
				for (int b = 0; b < 3; b++) {
					if (boardObj.isComplete(i+b,'X')) {
						// Draw X
						if (n == 0) {
							finalBoard += " \\ \\/ /";
						} else if (n == 1) {
							finalBoard += "  >  < ";
						} else {
							finalBoard += " /_/\\_\\";
						}
					} else if (boardObj.isComplete(i+b,'O')) {
						// Draw O
						if (n == 0) {
							finalBoard += " / _ \\ ";
						} else if (n == 1) {
							finalBoard += "| (_) |";
						} else {
							finalBoard += " \\___/ ";
						}
					} else {
						finalBoard += " "+boardObj.getBoard()[i+b][3*n]+" "+boardObj.getBoard()[i+b][1+(3*n)]+" "+boardObj.getBoard()[i+b][2+(3*n)]+" ";
					}
					finalBoard += "|";
				}
			}
			if (i == 0) {
				finalBoard += "\n╠---D---╬---E---╬---F---╣";
			}
			if (i == 3) {
				finalBoard += "\n╠---G---╬---H---╬---I---╣";
			}
		}
		finalBoard += "\n╚-------╩-------╩-------╝```";
		
//		String base1 = "╔---A---╦---B---╦---C---╗";
//		String row1 = "\n│ "+board[0][0]+" "+board[0][1]+" "+board[0][2]+" │ "+board[1][0]+" "+board[1][1]+" "+board[1][2]+" │ "+board[2][0]+" "+board[2][1]+" "+board[2][2]+" │ ";
//		String row2 = "\n│ "+board[0][3]+" "+board[0][4]+" "+board[0][5]+" │ "+board[1][3]+" "+board[1][4]+" "+board[1][5]+" │ "+board[2][3]+" "+board[2][4]+" "+board[2][5]+" │ ";
//		String row3 = "\n│ "+board[0][6]+" "+board[0][7]+" "+board[0][8]+" │ "+board[1][6]+" "+board[1][7]+" "+board[1][8]+" │ "+board[2][6]+" "+board[2][7]+" "+board[2][8]+" │ ";
//		String base2 = "\n╠---D---╬---E---╬---F---╣";
//		String row4 = "\n│ "+board[3][0]+" "+board[3][1]+" "+board[3][2]+" │ "+board[4][0]+" "+board[4][1]+" "+board[4][2]+" │ "+board[5][0]+" "+board[5][1]+" "+board[5][2]+" │ ";
//		String row5 = "\n│ "+board[3][3]+" "+board[3][4]+" "+board[3][5]+" │ "+board[4][3]+" "+board[4][4]+" "+board[4][5]+" │ "+board[5][3]+" "+board[5][4]+" "+board[5][5]+" │ ";
//		String row6 = "\n│ "+board[3][6]+" "+board[3][7]+" "+board[3][8]+" │ "+board[4][6]+" "+board[4][7]+" "+board[4][8]+" │ "+board[5][6]+" "+board[5][7]+" "+board[5][8]+" │ ";
//		String base3 = "\n╠---G---╬---H---╬---I---╣";
//		String row7 = "\n│ "+board[6][0]+" "+board[6][1]+" "+board[6][2]+" │ "+board[7][0]+" "+board[7][1]+" "+board[7][2]+" │ "+board[8][0]+" "+board[8][1]+" "+board[8][2]+" │ ";
//		String row8 = "\n│ "+board[6][3]+" "+board[6][4]+" "+board[6][5]+" │ "+board[7][3]+" "+board[7][4]+" "+board[7][5]+" │ "+board[8][3]+" "+board[8][4]+" "+board[8][5]+" │ ";
//		String row9 = "\n│ "+board[6][6]+" "+board[6][7]+" "+board[6][8]+" │ "+board[7][6]+" "+board[7][7]+" "+board[7][8]+" │ "+board[8][6]+" "+board[8][7]+" "+board[8][8]+" │ ";
//		String base4 = "\n╚-------╩-------╩-------╝";
//		String finalBoard = "```"+base1+row1+row2+row3+base2+row4+row5+row6+base3+row7+row8+row9+base4+"```";
		
		// Remove reactions, only on your turn for against computers
		if (playerOneID.equals("Computer") && !turnPlayerID.equals("Computer")) {
			clearReactions();
		}
		
		// Add appropriate reactions
		if (status.equals("ongoing") && !turnPlayerID.equals("Computer")) {
			if (requiredMoveBoard == -1) {
				System.out.println("Free choice");
				for (int i = 0; i < 9; i++) {
					if (!boardObj.isComplete(i,'X') && !boardObj.isComplete(i,'O')) {
						finalBoard = finalBoard.replace("-"+Utils.boardLetters[i]+"-", "["+Utils.boardLetters[i]+"]");
						gameChannel.addReactionById(boardID, Utils.boardLettersEmoji[i]).queue();
					}
					gameChannel.addReactionById(tilesID, Utils.numbersEmoji[i]).queue();
				}
			} else {
				System.out.println("Required to move in: "+requiredMoveBoard);
				finalBoard = finalBoard.replace("-"+Utils.boardLetters[requiredMoveBoard]+"-", "["+Utils.boardLetters[requiredMoveBoard]+"]");
				gameChannel.addReactionById(boardID, Utils.boardLettersEmoji[requiredMoveBoard]).queue();
				
				for (int i = 0; i < 9; i++) {
					if (boardObj.getBoard()[requiredMoveBoard][i] == ' ') {
						gameChannel.addReactionById(tilesID, Utils.numbersEmoji[i]).queue();
					}
				}
			}
		}
		//gameChannel.sendMessage(finalBoard).queue();
		// Edit
		
		gameChannel.editMessageById(boardID, finalBoard).queue();
		
	}
	
	public String getTurnPlayerID() {
		return turnPlayerID;
	}
	
	public int getRequiredMoveBoard() {
		return requiredMoveBoard;
	}
	
	// For buttons
	public int getBoardChoice() {
		return boardChoice;
	}
	
	public void setBoardChoice(int b) {
		boardChoice = b;
	}
	
	public void setBoardID(String id) {
		boardID = id;
	}
	
	public void setTilesID(String id) {
		tilesID = id;
	}
	
	public void setTurnID(String id) {
		turnID = id;
	}

	public MessageChannel getGameChannel() {
		return gameChannel;
	}
}
