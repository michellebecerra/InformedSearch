import time
from copy import copy, deepcopy

class Cell(object):

	#def __init__(self, player, value):
		#self.player = player
		#self.value = value
	def __init__(self,value):
		self.value = value
		self.mymove = ""

	def markplayer(self, player):
		self.player = player
	def indexi(self, i):
		self.indexi = i
	def indexj(self, j):
		self.indexj = j

def alphabeta(state, player):

	v, action = max_value_alpha(state, 0, player, float("-inf"), float("inf"))
	printSolBoard(action)
	print(v)

def max_value_alpha(state, depth, player, alpha, beta):

	if terminal_test(state) or depth == max_depth: 
		return utility(state)
# generate a new board with new child
	v = alpha
	best_b = None
	list_act = actions(state, player)
	oplayer = otherPlayer(player)
	for b in list_act:
		v , best_b = max_func(v, min_value_alpha(b, depth + 1, oplayer, v, beta), best_b, b)
		if v >= beta: 
			return v, best_b
		alpha = max(alpha, v)
	return v, b
def min_value_alpha(state, depth, player, alpha, beta):
	if terminal_test(state) or depth == max_depth: 
		return utility(state)
	v = beta
	best_b = None
	list_act = actions(state, player)
	oplayer = otherPlayer(player)
	for b in list_act:
		v, best_b= min_func(v, max_value_alpha(b, depth + 1, oplayer, alpha, v), best_b, b)
		if v <= alpha: 
			return v, best_b
		beta = min(beta, v)
	return v, b

def minimax(state, player):
    v, action = max_value(state, 0, player)
    printSolBoard(action)
    print(v)
    #return v, action
def max_value(state, depth, player):
	if terminal_test(state) or depth == max_depth: 
		return utility(state)
# generate a new board with new child
	v = float("-inf")
	best_b = None
	list_act = actions(state, player)
	oplayer = otherPlayer(player)
	for b in list_act:
		v , best_b = max_func(v, min_value(b, depth + 1, oplayer), best_b, b)
		#printBoard(len(b), b)
	return v, best_b

def min_value(state, depth, player):
	if terminal_test(state) or depth == max_depth: 
		return utility(state)
	v = float("inf")
	best_b = None
	list_act = actions(state, player)
	oplayer = otherPlayer(player)
	for b in list_act:
		v,best_b= min_func(v, max_value(b, depth + 1, oplayer), best_b, b)
	#printBoard(len(b), b)
	return v, best_b 

def max_func(prev, curr, best_b, b):
	if type(curr) is tuple:
		curr = curr[0]
	if prev > curr:
		return prev, best_b
	elif curr > prev:
		return curr,b
	else:
		#check that stake is preffered over raid
		if best_b != None and b != None:
			tup = check(best_b, b)
			if tup == 1:
				return prev, best_b
			else:
				return curr, b
		else:
			return prev, best_b

def min_func(prev, curr, best_b, b):
	if type(curr) is tuple:
		curr = curr[0]
	if prev < curr:
		return prev, best_b
	elif curr < prev:
		return curr, b
	else:
		#check that stake is preffered over raid
		if best_b != None and b != None:
			tup = check(best_b, b)
			if tup == 1:
				return prev, best_b
			else:
				return curr, b
		else:
			return prev, best_b

def check(bestb, b):
	for i in range(len(bestb)):
		for j in range(len(bestb)):
			cell = bestb[i][j]
			if cell.mymove == "Steak":
				return 1
	for l in range(len(b)):
		for m in range(len(b)):
			cell = b[l][m]
			if cell.mymove == "Steak":
				return 2

def utility(state):
	maxInt = 0
	oplayer = otherPlayer(main_player)
	for i in range(len(state)):
		for j in range(len(state)):
			cell = state[i][j]
			if cell.player == main_player:
				maxInt += cell.value
			if cell.player == oplayer:
				maxInt -= cell.value			
	return maxInt

def stake(player, board, i, j):
	cell = board[i][j]
	if cell.player == '.':
		newBoard = deepcopy(board)
		new_cell = newBoard[i][j]
		new_cell.markplayer(player)
		new_cell.mymove = "Stake"
		new_cell.indexi(i)
		new_cell.indexj(j)
		#printBoard(length, newBoard)
		return newBoard
	return None

def raid(player, board, i, j):
	length = len(board)
	oplayer = otherPlayer(player)
	cell = board[i][j]
	newBoardList = []
	if cell.player == player:
		#left of occupied piece
		if j - 1 >= 0 and board[i][j - 1].player == '.':
			newBoard = deepcopy(board)
			new_cell = newBoard[i][j - 1]
			new_cell.markplayer(player)
			new_cell.mymove = "Raid"
			new_cell.indexi(i)
			new_cell.indexj(j - 1)

			if j - 2 >= 0 and newBoard[i][j - 2].player == oplayer: 
				newBoard[i][j - 2].markplayer(player)
			if i + 1 <= length - 1 and j - 1 >= 0 and newBoard[i + 1][j - 1].player == oplayer:
				newBoard[i + 1][j - 1].markplayer(player)
			if i - 1 >= 0 and j - 1 <= length - 1 and newBoard[i - 1][j - 1].player == oplayer:
				newBoard[i - 1][j - 1].markplayer(player)
			#printBoard(length, newBoard)
			newBoardList.append(newBoard)
		#right of occupied piece
		if j + 1 <= length - 1 and board[i][j + 1].player == '.':
			newBoard = deepcopy(board)
			new_cell = newBoard[i][j + 1]
			new_cell.markplayer(player)
			new_cell.mymove = "Raid"
			new_cell.indexi(i)
			new_cell.indexj(j + 1)

			if j + 2 <= length - 1 and newBoard[i][j + 2].player == oplayer:
				newBoard[i][j + 2].markplayer(player)
			if i + 1 <= length - 1 and j + 1 <= length and newBoard[i + 1][j + 1].player == oplayer:
				newBoard[i + 1][j + 1].markplayer(player)
			if i - 1 >= 0 and j + 1 <= length - 1 and newBoard[i - 1][j + 1].player == oplayer:
				newBoard[i - 1][j + 1].markplayer(player)
			#printBoard(length, newBoard)
			newBoardList.append(newBoard)
		#below of occupied piece
		if i + 1 <= length - 1 and board[i + 1][j].player == '.':
			newBoard = deepcopy(board)
			new_cell = newBoard[i + 1][j]
			new_cell.markplayer(player)
			new_cell.mymove = "Raid"
			new_cell.indexi(i + 1)
			new_cell.indexj(j)

			if i + 1 <= length - 1 and j - 1 >= 0 and newBoard[i + 1][j -1].player == oplayer:
				newBoard[i + 1][j - 1].markplayer(player)
			if i + 1 <= length - 1 and j + 1 <= length - 1 and newBoard[i + 1][j + 1].player == oplayer:
				newBoard[i + 1][j + 1].markplayer(player)
			if i + 2 <= length - 1 and newBoard[i + 2][j].player == oplayer:
				newBoard[i + 2][j].markplayer(player)
			#printBoard(length, newBoard)
			newBoardList.append(newBoard)
		#above occupied piece
		if i - 1 >= 0 and board[i - 1][j].player == '.':
			newBoard = deepcopy(board)
			new_cell = newBoard[i - 1][j]
			new_cell.markplayer(player)
			new_cell.mymove= "Raid"
			new_cell.indexi(i - 1)
			new_cell.indexj(j)

			if i - 1 >= 0 and j - 1 >= 0 and newBoard[i - 1][j - 1].player == oplayer:
				newBoard[i - 1][j - 1].markplayer(player)
			if i - 1 >= 0 and j + 1 <= length - 1 and newBoard[i - 1][j + 1].player == oplayer:
				newBoard[i - 1][j + 1].markplayer(player)
			if i - 2 >= 0 and newBoard[i - 2][j].player == oplayer:
				newBoard[i - 2][j].markplayer(player)
			#printBoard(length, newBoard)
			newBoardList.append(newBoard)
	#Did not find a valid move
	if newBoardList:
		return newBoardList
	else:
		return None
def terminal_test(state):
	#return boolean if we are at determined depth or all the spaces are filled
	length = len(state)
	for i in range(length):
		for j in range(length):
			#cell = 
			if state[i][j].player == '.':
				return False
	return True

def otherPlayer(player):
	otherplayer = ''
	if player == 'X':
		otherplayer = 'O'
	else:
		otherplayer = 'X'
	return otherplayer

def actions(state, player):
	length = len(state)
	moves = []
	for i in range(length):
		for j in range(length):
			stakeboard = stake(player, state, i, j)
			raidboardList = raid(player, state, i, j)

			if stakeboard != None:
				moves.append(stakeboard)
			if raidboardList != None:
				moves.extend(raidboardList)
	if moves:
		return moves
	else:
	 	return None

def printSolBoard(board):
	f = open('output.txt', 'w')
	length = len(board)
	row = 0
	col = ''
	move = ''
	for i in range(length):
		for j in range(length):
			cell = board[i][j]
			if cell.mymove != "":
				move = cell.mymove
				row = cell.indexi + 1
				col = chr(cell.indexj + ord('A'))
				f.write(col)
				f.write(str(row) + " ")
				f.write(move)
				f.write('\n')

	for k in range(length):
		for l in range(length):		
			cell = board[k][l]
			f.write(cell.player)
		f.write('\n')
	f.close()

def printBoard(n, board):
	for i in range(n):
		for j in range(n):
			c = board[i][j]
			print(c.value, end='')
			print(c.player, end=' ')
		print()

def main():
	with open('input.txt', 'r') as f:
		n = int((f.readline().split())[0])
		mode = f.readline().split()[0]
		global main_player
		main_player = f.readline().split()[0]
		global max_depth 
		max_depth = int((f.readline().split())[0])
		board = [[0 for x in range(n)] for y in range(n)]
		for i in range(n):
			l = f.readline().split()
			for j in range(n):
				board[i][j] = Cell(int(l[j]))
		for m in range(n):
			lin = list(f.readline().split()[0])
			for k in range(n):
				cell = board[m][k]
				cell.markplayer(lin[k])
	f.close()
	if mode == "MINIMAX":
	 	minimax(board, main_player)
	elif mode == "ALPHABETA":
	 	alphabeta(board, main_player)


if __name__ == "__main__":
	main()

