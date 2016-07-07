abstract class Player(name: String) {
	
	/**
	 * abstract method, should not be implemented here, but requierd by extentions of Player.
	 * returns an int p in the interval [0,8] where game(p) == 0,
	 * the index of the move this player does. 
	 */
	def move(game: Array[Int], depth: Int): Int;
	
	// returns true if there exists a row, collumn or diagonal where all the elements are equal to who.
	def gameWon(game: Array[Int],who:Int): Boolean =  ???
	
	// returns a String with information about the player, the name, for example.
	override def toString(): String = ???
}
