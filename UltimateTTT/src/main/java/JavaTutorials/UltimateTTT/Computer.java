package JavaTutorials.UltimateTTT;

import java.util.ArrayList;
import java.util.Arrays;

public class Computer {
	
	// Go through possible moves
	// "Play" each move and then get a value of the board
	// 10000 if win
	// 1000 for 2 in a row without block
	// 300 for 1 in a row without any block
	// 50 for completed section
	// 25 for section with 2 in a row without block
	// 5 for section with 1 in a row without block
	
	// Go through player's possible moves after
	// Subtract same values from above
	
	public static int getBestCPUMove(int b, char[][] board) {
		int highestnet = -10001;
		int bestmove = -1;
		for (int move : possibleMoves(b, board)) {
			board[b][move] = 'O';
			int net = calcBoard('O', board) - getBestPlayerMoveValue(move, board);
			if (net > highestnet) {
				highestnet = net;
				bestmove = move;
			}
			board[b][move] = ' ';
		}
		return bestmove;
	}
	
	public static int getBestPlayerMoveValue(int b, char[][] board) {
		int highestscore = -10001;
		//int bestmove = -1;
		for (int move : possibleMoves(b, board)) {
			board[b][move] = 'X';
			int score = calcBoard('X', board);
			if (score > highestscore) {
				highestscore = score;
				//bestmove = move;
			}
			board[b][move] = ' ';
		}
		return highestscore;
	}
	
	public static void tryMove(int b, int pos, char[][] board) {
		
	}
	
	public static ArrayList<Integer> possibleMoves(int b, char[][] board) {
		int count = 0;
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for (char c : board[b]) {
			if (c == ' ') {
				count++;
				moves.add(count);
			}
		}
		return moves;
	}
	
	// 10000 if win
	// 1000 for 2 in a row without block
	// 300 for 1 in a row without any block
	// 50 for completed section
	// 25 for section with 2 in a row without block
	// 5 for section with 1 in a row without block
	
	public static int calcBoard(char c, char[][] board) {
		int total = 0;
		
		// Check for win
		if (hasWon(c,board)) {
			return 1000;
		}
		
		return total;
	}
	
	public static boolean isComplete(int b, char c, char[][] board) {
		if ((board[b][0] == c && board[b][1] == c && board[b][2] == c) || 
			(board[b][3] == c && board[b][4] == c && board[b][5] == c) || 
			(board[b][6] == c && board[b][7] == c && board[b][8] == c) || 
			(board[b][0] == c && board[b][3] == c && board[b][6] == c) || 
			(board[b][1] == c && board[b][4] == c && board[b][7] == c) || 
			(board[b][2] == c && board[b][5] == c && board[b][8] == c) || 
			(board[b][0] == c && board[b][4] == c && board[b][8] == c) || 
			(board[b][6] == c && board[b][4] == c && board[b][2] == c)) {
			return true;
		}
		return false;
	}
	
	public static boolean hasWon(char c, char[][] board) {
		if ((isComplete(0,c,board) && isComplete(1,c,board) && isComplete(2,c,board)) ||
			(isComplete(3,c,board) && isComplete(4,c,board) && isComplete(5,c,board)) ||
			(isComplete(6,c,board) && isComplete(7,c,board) && isComplete(8,c,board)) ||
			(isComplete(0,c,board) && isComplete(3,c,board) && isComplete(6,c,board)) ||
			(isComplete(1,c,board) && isComplete(4,c,board) && isComplete(7,c,board)) ||
			(isComplete(2,c,board) && isComplete(5,c,board) && isComplete(8,c,board)) ||
			(isComplete(0,c,board) && isComplete(4,c,board) && isComplete(8,c,board)) ||
			(isComplete(6,c,board) && isComplete(4,c,board) && isComplete(2,c,board))) {
			return true;
		}
		return false;
	}
}
