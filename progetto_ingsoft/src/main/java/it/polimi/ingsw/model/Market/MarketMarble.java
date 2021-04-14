package it.polimi.ingsw.model.Market;


import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.personalboard.FaithMarker;

public interface MarketMarble {

    /**
     * this is a strategy,
     * in fact each subclass overrides his only method
     * @param faithMarker is a reference to the player's FaithMarker
     * @param player is a reference to the player
     * @return a boolean that indicate if the method worked or not
     */
    boolean changeMarble(FaithMarker faithMarker, Player player) throws FullSupplyException;

}

