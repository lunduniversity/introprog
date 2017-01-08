class HumanPlayer(name: String) extends Player(name: String) {
	/**
	 * Prints the name of the player, and asks via System.out what move the player wants to make.
	 * Waits for an int p in the interval [0,8], where game(p) == 0, from System.in.
	 * Returns p.
	 */
	def move(game: Array[Int], depth: Int): Int =  ???
}