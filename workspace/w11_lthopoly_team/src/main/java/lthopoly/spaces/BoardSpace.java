package lthopoly.spaces;

import lthopoly.GameBoard;


/**
 * Created by Tank on 4/17/2016.
 */

public abstract class BoardSpace {

    /**
     * Returns a array of int describing possible game actions available while on this space
     */
    public abstract int[] getPossibleActions(GameBoard board);

    /**
     * Executes a game action available while on this space
     */
    public abstract void action(GameBoard board, int action);

    /**
     * Returns a string representation of this BoardSpace
     */
    public abstract String toString();
}
