package lthopoly.spaces;

import lthopoly.GameBoard;


/**
 * Created by Tank on 4/17/2016.
 */

public abstract class BoardSpace {

    /**
     * Returns an array of possible game actions permitted by this space
     */
    public abstract int[] getPossibleActions(GameBoard board);

    /**
     * Performs a game action available while on this space
     */
    public abstract void action(GameBoard board, int action);

    /**
     * Returns a string representation of this BoardSpace
     */
    public abstract String toString();
}
