abstract class Player(name: String) {
	
	/**
	 * Abstract method. Should not be implemented here,
	 * but required by subtypes of Player.
	 * Returns an int p in the interval [0,8] where game(p) == 0,
	 * the index of the move this player does. 
	 */
	def move(game: Array[Int], depth: Int): Int;
	
	// returns true if there exists a row, column or diagonal where all the elements are equal to who.
	def gameWon(game: Array[Int], who: Int): Boolean = ???
	
	// Returns a String with information about the player, for example the name.
	override def toString(): String = ???
}
