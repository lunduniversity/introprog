package lthopoly.spaces;

import lthopoly.GameBoard;
import lthopoly.cards.MoneyCard;

/**
 * Created by Tank on 4/17/2016.
 */
public class MoneySpace extends BoardSpace {

    /**
     * Creates a new MoneySpace. When landing on this space a card from the card array will be drawn
     */
    public MoneySpace(MoneyCard[] cards) {
    }

    /**
     * Returns an array of possible game actions permitted by this space
     */
    @Override
    public int[] getPossibleActions(GameBoard board) {
        return null;
    }

    /**
     * Performs a game action available while on this space
     */
    public void action(GameBoard board, int action) {
    }

    /**
     * Returns a string representation of this MoneySpace
     */
    @Override
    public String toString() {
        return null;
    }
}
