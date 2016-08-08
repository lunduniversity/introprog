object Game {
	
	/** 
	 * Asks the user what kind of players should play against each other.
	 * Creates the players that the user chooses via System.in 
	 * Asks the user if the board should be drawn.
	 * If the board should not be drawn, ask the user for n, the amount of games that should be played between the two players.
	 * Else n = 1.
	 * Call play n times with the two players, an empty board, depth = 0,
	 * and drawing true/false dependent on if the board should be printed or not. 
	 * Save the resuts from play. 
	 * Present the results after n games have been played. 
	 */
	def main(args: Array[String]): Unit = ???
	
	/**
	 * p1,p2 are the players that should play the game
	 * depth models amount of moves done by both players
	 * drawing is true if draw should be called before each move.
	 *(1) If drawing: call draw(game)
	 *(2) If game is won by any of the players, or depth == 9, return 1 if p1 wins, -1 if p2 wins and 0 if there is a draw.
	 *(3) Else: Asks player 1 for its move if depth%2 == 0, else ask player 2.
	 *(4) update game according to the move p1 or p2 does.
	 *(5) return play(p1,p2,game,depth+1,drawing) (if someone wins with the current move, it will be detected by (2) in this call of play)
	 */
	def play(p1: Player, p2: Player, game: Array[Int], depth: Int, drawing:Boolean): Int = ???
	
	/**
	 * Given an Array[Int] game of size 9, print the 3x3-board that the array represents. 
	 * [0,2] is the 1st, [3,5] is the 2nd and [6,8] is the 3rd row.
	 * -1 should be represented by 'o', 0 with '.' and 1 with 'x'.
	 * in particular [0,1,-1,0,0,1,0,1,-1] should print:
	 * .xo
	 * ..x
	 * .xo
	 */
	def draw(game:Array[Int]):Unit = ???
}