class OptimalPlayer(name: String) extends Player(name: String) {
	
	/**
	 * Returns the best move the player has. This should be done by calling the eval-function with the games we can reach.
	 * Studying the eval-function might help you out how to implement this.
	 */
	def move(game: Array[Int], depth: Int): Int =  ???
	
	/**
	 * returns 1 if there is a guaranteed strategy for who to win 
	 * returns 0 if there is a guaranteed strategy for who to draw 
	 * returns -1 if the opponent can force a win,
	 * no matter what who does.
	 * This is done by min,max-evaluation. 
	 * Find the move that gives the opponent the worst possible
	 * position (min) and return -min, this is our max.
	 * depth is the amount of empty cells in game.
	 * who is 1 if it's this players turn to make a move,
	 * -1 if it's the opponents turn to make a move. 
	 * From move, eval should be called with who = -1.
	 */
	def eval(game: Array[Int], depth: Int, who: Int): Int = {
		if(gameWon(game,-who)) return -1
		if(depth == 9) return 0
		var min = 1
		for(i <- 0 until 9) {
			if(game(i) == 0) {
				game(i) = who
				val score = eval(game,depth+1,-who)
				if(score<min){
					min = score
				}
				game(i) = 0
			}
		}
		-min
	}
}