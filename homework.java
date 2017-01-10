/*
* Author: Michelle Becerra
* Date: 10/17/16
* Foundations of Artificial Intelligence Homework 2 Minimax and Alpha Beta pruning
* Professor Dr. Itti
*/

import java.util.*;
import java.io.*;

class homework {

	//GLOBAL VARIABLES
	private static char main_player = Character.MIN_VALUE;
	private static int max_depth = 0;
	private static HashMap<Integer,Cell[][]> boardMap = new HashMap<Integer, Cell[][]>();
	
	//Calls Parse File which parses the file and determines which algorithms needs to run
	public static void main(String[] args) throws FileNotFoundException {

		parseFile();
	}

	public static void parseFile() throws FileNotFoundException {

		Scanner br = new Scanner(new File("input.txt"));
		// Size of board
		int n = 0;
		if (br.hasNextInt()) {
			n = br.nextInt();
		}
		// Mode of algorithm
		String mode = "";
		if (br.hasNextLine()) {
			mode = br.next();
		}
		// Player
		if (br.hasNextLine()) {
			main_player = br.next().charAt(0);
		}
		// Max depth
		if (br.hasNextInt()) {
			max_depth = br.nextInt();
		}

		// Creates nxn matrix of cell objects
		Cell[][] board = new Cell[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				int val = br.nextInt();
				board[i][j] = new Cell(val);
			}
		}

		// Adds players to the cell objects in nxn matrix
		for (int i = 0; i < n; i++) {
			String line = br.next();
			for (int j = 0; j < n; j++) {
				char c = line.charAt(j);
				if ('X' == c || 'O' == c) {
					board[i][j].setPlayer(c);
				}
			}
		}

		//Process either minimax or alpha beta pruning
		if (mode.equals("MINIMAX")) {

			minimax(board);
		}
		else if (mode.equals("ALPHABETA")) {
			alphabeta(board);
		}
		else {
			br.close();
			return;
		}

		br.close();
	}
	//Alpha beta pruning - Outputs to output.txt 
	public static void alphabeta(Cell[][] board) throws FileNotFoundException{
		int val;
		val = max_value_alpha(board, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
		printSolBoard(val, board.length);
	}
	public static int max_value_alpha(Cell[][] board, int depth, int alpha, int beta) {
		if(terminal_test(board) || depth == max_depth){
			return utility(board);
		}

		
		int length = board.length;
		Cell[][] best_c = new Cell[length][length];
		List<Cell[][]>  successors = actions(board, main_player);

		Boolean root_level = false;
		if(depth == 0) root_level = true;

		int value = Integer.MIN_VALUE;
		//best_c = successors.get(0);
		int val;
		
		for(Cell[][] child : successors){
			val =  min_value_alpha(child, depth + 1, alpha, beta);
			if(val > value){
				best_c = child;
				value = val;
			}

			if( value >= beta){
				boardMap.put(value, best_c);
				return value;
			}
			alpha = Math.max(alpha, value);
		}

		boardMap.put(value, best_c);
		return value;
	}
	public static int min_value_alpha(Cell[][] board, int depth, int alpha, int beta){
		if(terminal_test(board) || depth == max_depth){
			return utility(board);
		}
		int value = Integer.MAX_VALUE;
		char oplayer = otherPlayer(main_player);
		List<Cell[][]>  successors = actions(board, oplayer);
		int length = board.length;
		Cell[][] best_c = new Cell[length][length];
		int val;
		for(Cell[][] child: successors){
			val = max_value_alpha(child, depth + 1, alpha, beta);
			if( val < value){
				best_c = child;
				value = val;
			}

			if(value <= alpha){
				boardMap.put(value, best_c);
				return value;
			}
			beta = Math.min(beta, value);
		}
		boardMap.put(value, best_c);
		return value;
	}
	//Minimax - Outputs to output.txt 
	public static void minimax(Cell[][] board) throws FileNotFoundException {
		int val;
		val = max_value(board, 0);
		printSolBoard(val, board.length);
	}
	public static int max_value(Cell[][] board, int depth) {
		if(terminal_test(board) || depth == max_depth){
			return utility(board);
		}
		int value = Integer.MIN_VALUE;
		List<Cell[][]>  successors = actions(board, main_player);
		int length = board.length;
		Cell[][] best_c = new Cell[length][length];
		//best_c = successors.get(0);
		Boolean root_level = false;
		if(depth == 0) root_level = true;
		int val;
		for(Cell[][] child: successors){
			val = min_value(child, depth + 1);
			
			if( val > value){
				best_c = child;
				value = val;
			}			
		}
		//create a hashmap mapping value to board
		boardMap.put(value, best_c);
		return value;
	}
	public static int min_value(Cell[][] board, int depth){
		if(terminal_test(board) || depth == max_depth){
			return utility(board);
		}
		int value = Integer.MAX_VALUE;
		char oplayer = otherPlayer(main_player);
		List<Cell[][]>  successors = actions(board, oplayer);
		int length = board.length;
		Cell[][] best_c = new Cell[length][length];
		//best_c = successors.get(0);
		int val;
		for(Cell[][] child: successors){
			val = max_value(child, depth + 1);
			if( val < value){
				best_c = child;
				value = val;
			}
		}
		boardMap.put(value, best_c);		
		return value;
	}

	public static int utility(Cell[][] board) {
		int maxInt = 0;

		char oplayer = otherPlayer(main_player);
		
		int length = board.length;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				Cell cell = board[i][j];

				if(cell.player() == main_player){
					maxInt += cell.value();
				}
				if (cell.player() == oplayer) {
					maxInt -= cell.value();
				}

			}
		}
		return maxInt;
	}
	//Gets the possible actions for current player
	public static List<Cell[][]> actions(Cell[][] board, char player){
		int length = board.length;
		//Process stake moves first
		List<Cell[][]> moves = new ArrayList<Cell[][]>();
		for(int i = 0; i < length; i ++){
			for(int j = 0; j < length; j++){
				Cell[][] stakeBoard = stake(board,player, i, j);
				if(stakeBoard != null){
					moves.add(stakeBoard);
				}

			}
		}
		//Process raid moves next
		for(int l = 0; l < length; l++){
			for(int m = 0; m < length; m++){
				List<Cell[][]> raidList = raidNew(board,player, l, m);
				if(raidList != null){
					for(Cell[][] raidBoard: raidList){
						moves.add(raidBoard);
					}
				}

			}
		}
		return moves;
	}
	//Stake move - can move to an unocuppied space only
	public static Cell[][] stake(Cell[][] board, char player, int i, int j) {
		 Cell cell = board[i][j];
		 if (cell.player() == '.') {
			Cell[][] newBoard = deepCopy(board);
			Cell new_cell = newBoard[i][j];
			new_cell.setPlayer(player);
			new_cell.setMove("Stake");
			new_cell.setIndexi(i);
			new_cell.setIndexj(j);
			//printBoard(newBoard);
			return newBoard;
		 }
		 return null;
	}

	public static List<Cell[][]> raidNew(Cell[][] board, char player, int i, int j){
		Cell cell = board[i][j];
		int length = board.length;
		char oplayer = otherPlayer(player);
		List<Cell[][]> boardList = new ArrayList<Cell[][]>();

		 if (cell.player() == '.') {
		 	
		 	//Player is at left
		 	if (j - 1 >= 0 && board[i][j - 1].player() == player){
				Cell[][] newBoard1 = deepCopy(board);
				newBoard1[i][j].setMove("Raid");
				newBoard1[i][j].setIndexi(i);
				newBoard1[i][j].setIndexj(j);
				newBoard1[i][j].setPlayer(player);
	
				//Check if we can conquer adjacent pieces

				//right
				if (j + 1 <= length - 1 && newBoard1[i][j + 1].player() == oplayer){
					newBoard1[i][j + 1].setPlayer(player);
				}
				//down 
				if (i + 1 <= length - 1 && newBoard1[i + 1][j].player() == oplayer){
					newBoard1[i + 1][j].setPlayer(player);
				}
				//up
				if (i - 1 >= 0 && newBoard1[i - 1][j].player() == oplayer){
					newBoard1[i - 1][j].setPlayer(player);
				}
				boardList.add(newBoard1);	
				//return boardList;
		 	}
		 	//PLayer is at right
		 	else if(j + 1 <= length - 1 && board[i][j + 1].player() == player){
		 		Cell[][] newBoard = deepCopy(board);
		 		newBoard[i][j].setMove("Raid");
		 		newBoard[i][j].setIndexi(i);
				newBoard[i][j].setIndexj(j);
				newBoard[i][j].setPlayer(player);

				//Check if we can conquer adjacent pieces
				//left
				if(j - 1 >= 0 && newBoard[i][j - 1].player() == oplayer){
					newBoard[i][j - 1].setPlayer(player);
				} 
				//down
				if(i + 1 <= length - 1 && newBoard[i + 1][j].player() == oplayer){
					newBoard[i + 1][j].setPlayer(player);
				}
				//up
				if(i - 1 >= 0 && newBoard[i - 1][j].player() == oplayer){
					newBoard[i - 1][j].setPlayer(player);
				}
				boardList.add(newBoard);
				//return boardList;
		 	}
		 	//Player is below me
		 	else if(i + 1 <= length - 1 && board[i + 1][j].player() == player){
		 		Cell[][] newBoard3 = deepCopy(board);
				newBoard3[i][j].setMove("Raid");
				newBoard3[i][j].setIndexi(i);
				newBoard3[i][j].setIndexj(j);
				newBoard3[i][j].setPlayer(player);
	
				//left
				if (j - 1 >= 0 && newBoard3[i][j - 1].player() == oplayer){
					newBoard3[i][j - 1].setPlayer(player);
				}
				//right
				if (j + 1 <= length - 1 && newBoard3[i][j + 1].player() == oplayer){
					newBoard3[i][j + 1].setPlayer(player);
				}
				//above
				if (i - 1 >= 0 && newBoard3[i - 1][j].player() == oplayer){
					newBoard3[i - 1][j].setPlayer(player);
				}
				boardList.add(newBoard3);
				//return boardList;
				
		 	}
		 	//Player is above me
		 	else if(i - 1 >= 0 && board[i - 1][j].player() == player){
		 		Cell[][] newBoard2 = deepCopy(board);
		 		newBoard2[i][j].setMove("Raid");
		 		newBoard2[i][j].setIndexi(i);
				newBoard2[i][j].setIndexj(j);
				newBoard2[i][j].setPlayer(player);

				//Check if we can conquer adjacent pieces
				//left
				if (j - 1 >= 0 && newBoard2[i][j -1].player() == oplayer){
					newBoard2[i][j - 1].setPlayer(player);
				}
				//right
				if (j + 1 <= length - 1 && newBoard2[i][j + 1].player() == oplayer){
					newBoard2[i][j + 1].setPlayer(player);
				}
				//below
				if (i + 1 <= length - 1 && newBoard2[i + 1][j].player() == oplayer){
					newBoard2[i + 1][j].setPlayer(player);
				}
				boardList.add(newBoard2);
				//return boardList;
		 	}
		 	return boardList;
		 }
		 return null;
	}
	//Raid move - can take over an unoccupied space with another player in the same
	//            team occupies a neighboring piece. Can then conquer adjacent pieces
	public static List<Cell[][]> raid(Cell[][] board, char player,  int i, int j) {
		char oplayer = otherPlayer(player);
		Cell cell = board[i][j];
		int length = board.length;
		List<Cell[][]> boardList = new ArrayList<Cell[][]>();

	//CHECK that a player on the same team is to the Left, Right, Down or Up 
	if (cell.player() == player){
		
		//Left of occupied piece
		if (j - 1 >= 0 && board[i][j - 1].player() == '.'){
			Cell[][] newBoard = deepCopy(board);
			Cell new_cell = newBoard[i][j - 1];
			new_cell.setPlayer(player);
			new_cell.setMove("Raid");
			new_cell.setIndexi(i);
			new_cell.setIndexj(j - 1);

			//Check if we can conquer adjacent pieces
			if(j - 2 >= 0 && newBoard[i][j - 2].player() == oplayer){
				newBoard[i][j - 2].setPlayer(player);
			} 
			if(i + 1 <= length - 1 && j - 1 >= 0 && newBoard[i + 1][j - 1].player() == oplayer){
				newBoard[i + 1][j - 1].setPlayer(player);
			}
			if(i - 1 >= 0 && j - 1 <= length - 1 && newBoard[i - 1][j - 1].player() == oplayer){
				newBoard[i - 1][j - 1].setPlayer(player);
			}
			boardList.add(newBoard);
		}
		//Right of occupied piece
		if (j + 1 <= length - 1 && board[i][j + 1].player() == '.'){
			Cell[][] newBoard1 = deepCopy(board);
			Cell new_cell1 = newBoard1[i][j + 1];
			new_cell1.setPlayer(player);
			new_cell1.setMove("Raid");
			new_cell1.setIndexi(i);
			new_cell1.setIndexj(j + 1);

			//Check if we can conquer adjacent pieces
			if (j + 2 <= length - 1 && newBoard1[i][j + 2].player() == oplayer){
				newBoard1[i][j + 2].setPlayer(player);
			}
			if (i + 1 <= length - 1 && j + 1 <= length && newBoard1[i + 1][j + 1].player() == oplayer){
				newBoard1[i + 1][j + 1].setPlayer(player);
			}
			if (i - 1 >= 0 && j + 1 <= length - 1 && newBoard1[i - 1][j + 1].player() == oplayer){
				newBoard1[i - 1][j + 1].setPlayer(player);
			}
			boardList.add(newBoard1);
		}
		//Below of occupied piece
		if (i + 1 <= length - 1 && board[i + 1][j].player() == '.'){
			Cell[][] newBoard2 = deepCopy(board);
			Cell new_cell2 = newBoard2[i + 1][j];
			new_cell2.setPlayer(player);
			new_cell2.setMove("Raid");
			new_cell2.setIndexi(i + 1);
			new_cell2.setIndexj(j);

			//Check if we can conquer adjacent pieces
			if (i + 1 <= length - 1 && j - 1 >= 0 && newBoard2[i + 1][j -1].player() == oplayer){
				newBoard2[i + 1][j - 1].setPlayer(player);
			}
			if (i + 1 <= length - 1 && j + 1 <= length - 1 && newBoard2[i + 1][j + 1].player() == oplayer){
				newBoard2[i + 1][j + 1].setPlayer(player);
			}	
			if (i + 2 <= length - 1 && newBoard2[i + 2][j].player() == oplayer){
				newBoard2[i + 2][j].setPlayer(player);
			}
			boardList.add(newBoard2);
		}
		//Above occupied piece
		if (i - 1 >= 0 && board[i - 1][j].player() == '.'){
			Cell[][] newBoard3 = deepCopy(board);
			Cell new_cell3 = newBoard3[i - 1][j];
			new_cell3.setPlayer(player);
			new_cell3.setMove("Raid");
			new_cell3.setIndexi(i - 1);
			new_cell3.setIndexj(j);

			if (i - 1 >= 0 && j - 1 >= 0 && newBoard3[i - 1][j - 1].player() == oplayer){
				newBoard3[i - 1][j - 1].setPlayer(player);
			}
			if (i - 1 >= 0 && j + 1 <= length - 1 && newBoard3[i - 1][j + 1].player() == oplayer){
				newBoard3[i - 1][j + 1].setPlayer(player);
			}
			if (i - 2 >= 0 && newBoard3[i - 2][j].player() == oplayer){
				newBoard3[i - 2][j].setPlayer(player);
			}
			boardList.add(newBoard3);
		}
		return boardList;
	}
		return null;

	}
	//Checks to see if there any unoccupied spaces
	public static boolean terminal_test(Cell[][] board) {
		int length = board.length;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				Cell cell = board[i][j];

				if (cell.player() == '.') {
					return false;
				}
			}
		}
		return true;
	}
	//Returns opposite player
	public static char otherPlayer(char player) {

		if ('X' == player) {
			return 'O';
		}
		else {
			return 'X';
		}
	}
	//Prints the solution move to output.txt
	public static void printSolBoard(int value, int length) throws FileNotFoundException {
		PrintWriter printer = new PrintWriter(new File("output.txt"));
		Cell[][] board = boardMap.get(value);
		String move = "";
		String col = "";
		int row;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				Cell cell = board[i][j];
				//Ouput move first in format: [A-Z][1-26] "Steak/Raid"
				if (!cell.move().equals("")) {
					move = cell.move();
					row = cell.indexi() + 1;
					col = String.valueOf((char)(cell.indexj() + 'A'));
					printer.write(col);
					printer.write(row + " ");
					printer.write(move);
					printer.write("\n");

				}
			}
		}
		//Output the pieces/players of the solution board
		for (int k = 0; k < length; k++) {
			for (int l = 0; l < length; l++) {
				Cell cell = board[k][l];
				printer.write(cell.player());
			}
			printer.write("\n");
		}
		printer.close();
	}
	//Creates a deep copy of the board and a deep copy of the cell objets
	public static Cell[][] deepCopy(Cell[][] old){
		Cell[][] newBoard = new Cell[old.length][old.length];
		
		for(int i = 0; i < old.length; i++){
			for(int j = 0; j < old.length; j++){
				newBoard[i][j] = new Cell(old[i][j]);
			}
		}
		return newBoard;
	}
	//Created for my testing purposes
	public static void printBoard(Cell[][] board) {
		int length = board.length;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				Cell cell = board[i][j];

				System.out.print(cell.value() +""+ cell.player() + " ");	
			}
		    System.out.println();
		}
	}

}


