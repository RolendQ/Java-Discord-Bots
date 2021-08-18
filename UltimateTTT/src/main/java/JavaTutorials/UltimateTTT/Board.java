package JavaTutorials.UltimateTTT;

import java.util.ArrayList;
import java.util.Arrays;

public class Board {
	
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
	
	public char[][] board;
	public char[] completedBoards;
	
	public Board(char[][] board) {
		this.board = board;
		this.completedBoards = new char[] {' ',' ',' ',' ',' ',' ',' ',' ',' '};
	}
	
	public void play(char c, int b, int pos) {
		board[b][pos] = c;
		updateCompletedBoards();
	}
	
	public char[][] getBoard() {
		return board;
	}
	
	public int[] getBestCPUMove(int b) {
		int highestnet = -10001;
		int[] result = {b,-1};
		if (b == -1) {
			for (int i = 0; i < 9; i++) {
				if (!isComplete(i,'X') && !isComplete(i,'O')) {
					for (int move = 0; move < 9; move++) {
						if (board[i][move] == ' ') {
							board[i][move] = 'O';
							updateCompletedBoards();
							int net = calcBoard('O') - getBestPlayerMoveValue(move);
							if (net > highestnet) {
								highestnet = net;
								result[1] = move;
								result[0] = i;
							}
							board[i][move] = ' ';
							System.out.println("Completed calculations for "+move+ ". Highest is "+highestnet);
						}
					}
				}
			}
			System.out.println("CPU Move Net Value: "+highestnet);
			return result;
		} else {
			for (int move = 0; move < 9; move++) {
				if (board[b][move] == ' ') {
					board[b][move] = 'O';
					updateCompletedBoards();
					System.out.println("Value for CPU: "+calcBoard('O'));
					int net = calcBoard('O') - getBestPlayerMoveValue(move);
					if (net > highestnet) {
						highestnet = net;
						result[1] = move;
					}
					board[b][move] = ' ';
					System.out.println("Completed calculations for "+move+ ". Highest is "+highestnet);
				}
			}
			System.out.println("CPU Move Net Value: "+highestnet);
			return result;
		}
	}
	
	public int getBestPlayerMoveValue(int b) {
		// Free choice
		if (isComplete(b,'X') || isComplete(b,'O')) return 9999;
		
		int highestscore = 0;
		//int bestmove = -1;
		for (int move = 0; move < 9; move++) {
			if (board[b][move] == ' ') {
				board[b][move] = 'X';
				updateCompletedBoards();
				int score = calcBoard('X');
				if (score > highestscore) {
					highestscore = score;
					//bestmove = move;
				}
				board[b][move] = ' ';
			}
		}
		return highestscore;
	}
	
//	public ArrayList<Integer> possibleMoves(int b) {
//		int count = 0;
//		ArrayList<Integer> moves = new ArrayList<Integer>();
//		for (char c : board[b]) {
//			if (c == ' ') {
//				count++;
//				moves.add(count);
//			}
//		}
//		return moves;
//	}
	
	// 10000 if win
	// 1000 for 2 in a row without block
	// 300 for 1 in a row without any block
	// 50 for completed section
	// 25 for section with 2 in a row without block
	// 5 for section with 1 in a row without block
	
	public int calcBoard(char c) {
		int total = 0;
		
		// Check for win
		if (hasWon(c)) {
			return 10000;
		}
		
		// 2 in a row sections
		if (twoInARow(completedBoards[0],completedBoards[1],completedBoards[2]) == c) total += 1000;
		if (twoInARow(completedBoards[3],completedBoards[4],completedBoards[5]) == c) total += 1000;
		if (twoInARow(completedBoards[6],completedBoards[7],completedBoards[8]) == c) total += 1000;
		if (twoInARow(completedBoards[0],completedBoards[3],completedBoards[6]) == c) total += 1000;
		if (twoInARow(completedBoards[1],completedBoards[4],completedBoards[7]) == c) total += 1000;
		if (twoInARow(completedBoards[2],completedBoards[5],completedBoards[8]) == c) total += 1000;
		if (twoInARow(completedBoards[0],completedBoards[4],completedBoards[8]) == c) total += 1000;
		if (twoInARow(completedBoards[2],completedBoards[4],completedBoards[6]) == c) total += 1000;
		
		// 1 in a row sections
		if (oneInARow(completedBoards[0],completedBoards[1],completedBoards[2]) == c) total += 300;
		if (oneInARow(completedBoards[3],completedBoards[4],completedBoards[5]) == c) total += 300;
		if (oneInARow(completedBoards[6],completedBoards[7],completedBoards[8]) == c) total += 300;
		if (oneInARow(completedBoards[0],completedBoards[3],completedBoards[6]) == c) total += 300;
		if (oneInARow(completedBoards[1],completedBoards[4],completedBoards[7]) == c) total += 300;
		if (oneInARow(completedBoards[2],completedBoards[5],completedBoards[8]) == c) total += 300;
		if (oneInARow(completedBoards[0],completedBoards[4],completedBoards[8]) == c) total += 300;
		if (oneInARow(completedBoards[2],completedBoards[4],completedBoards[6]) == c) total += 300;
		
		// Per section
		for (char symbol : completedBoards) {
			if (symbol == c) total += 100;
		}
		
		for (int i = 0; i < 9; i++) {
			if (completedBoards[i] == ' ') {
				if (twoInARow(board[i][0],board[i][1],board[i][2]) == c) total += 25;
				if (twoInARow(board[i][3],board[i][4],board[i][5]) == c) total += 25;
				if (twoInARow(board[i][6],board[i][7],board[i][8]) == c) total += 25;
				if (twoInARow(board[i][0],board[i][3],board[i][6]) == c) total += 25;
				if (twoInARow(board[i][1],board[i][4],board[i][7]) == c) total += 25;
				if (twoInARow(board[i][2],board[i][5],board[i][8]) == c) total += 25;
				if (twoInARow(board[i][0],board[i][4],board[i][8]) == c) total += 25;
				if (twoInARow(board[i][2],board[i][4],board[i][6]) == c) total += 25;
				
				if (oneInARow(board[i][0],board[i][1],board[i][2]) == c) total += 5;
				if (oneInARow(board[i][3],board[i][4],board[i][5]) == c) total += 5;
				if (oneInARow(board[i][6],board[i][7],board[i][8]) == c) total += 5;
				if (oneInARow(board[i][0],board[i][3],board[i][6]) == c) total += 5;
				if (oneInARow(board[i][1],board[i][4],board[i][7]) == c) total += 5;
				if (oneInARow(board[i][2],board[i][5],board[i][8]) == c) total += 5;
				if (oneInARow(board[i][0],board[i][4],board[i][8]) == c) total += 5;
				if (oneInARow(board[i][2],board[i][4],board[i][6]) == c) total += 5;
			}
		}
		
		//System.out.println("Total of: "+total);
		return total;
	}
	
	public char twoInARow(char a, char b, char c) {
		int sum = (a == 'X' ? 1 : 0) + (b == 'X' ? 1 : 0) + (c == 'X' ? 1 : 0) + (a == 'O' ? -1 : 0) + (b == 'O' ? -1 : 0) + (c == 'O' ? -1 : 0);
		if (sum == 2) {
			return 'X';
		} else if (sum == -2) {
			return 'O';
		} else {
			return ' ';
		}
	}
	
	public char oneInARow(char a, char b, char c) {
		int sum = (a == 'X' ? 10 : 0) + (b == 'X' ? 10 : 0) + (c == 'X' ? 10 : 0) + (a == 'O' ? -1 : 0) + (b == 'O' ? -1 : 0) + (c == 'O' ? -1 : 0);
		if (sum == 10) {
			return 'X';
		} else if (sum == -1) {
			return 'O';
		} else {
			return ' ';
		}
	}
	
	public void updateCompletedBoards() {
		for (int i = 0; i < 9; i++) {
			if (isComplete(i,'X')) {
				completedBoards[i] = 'X';
			} else if (isComplete(i,'O')) {
				completedBoards[i] = 'O';
			} else {
				completedBoards[i] = ' ';
			}
		}
	}
	
	public boolean isComplete(int b, char c) {
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
	
	public boolean hasWon(char c) {
		if ((completedBoards[0] == c && completedBoards[1] == c && completedBoards[2] == c) ||
			(completedBoards[3] == c && completedBoards[4] == c && completedBoards[5] == c) ||
			(completedBoards[6] == c && completedBoards[7] == c && completedBoards[8] == c) ||
			(completedBoards[0] == c && completedBoards[3] == c && completedBoards[6] == c) ||
			(completedBoards[1] == c && completedBoards[4] == c && completedBoards[7] == c) ||
			(completedBoards[2] == c && completedBoards[5] == c && completedBoards[8] == c) ||
			(completedBoards[0] == c && completedBoards[4] == c && completedBoards[8] == c) ||
			(completedBoards[6] == c && completedBoards[4] == c && completedBoards[2] == c)) {
			return true;
		}
		return false;
	}
}
