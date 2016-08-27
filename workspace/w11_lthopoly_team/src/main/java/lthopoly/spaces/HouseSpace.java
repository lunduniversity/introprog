package lthopoly.spaces;


import lthopoly.GameBoard;

/**
 * Created by Tank on 4/17/2016.
 */
public class HouseSpace extends BoardSpace {

    /**
     * Creates a new housespace with rent and a description
     */
    public HouseSpace(int rent, String description) {
    }

    /**
     * Returns an array of possible game actions permitted by this space
     */
    @Override
    public int[] getPossibleActions(GameBoard board) {
        return null;
    }

    /**
     * Performs a HouseSpace-related action.
     */
    @Override
    public void action(GameBoard board, int action) {
    }

    /**
     * Returns a string representation of the HouseSpace with the format "HouseName [Owner] (Rent)"
     */
    @Override
    public String toString() {
        return null;
    }

}
