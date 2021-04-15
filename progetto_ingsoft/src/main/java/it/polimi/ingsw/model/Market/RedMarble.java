package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.personalboard.FaithMarker;

public class RedMarble implements MarketMarble {

    /**
     * this subclass overrides this method and give a faith point to the player
     * @param faithMarker is a reference to the player's FaithMarker
     * @param player      is a reference to the player
     * @return true if faithMarker is updated correctly
     */
    @Override
    public boolean changeMarble(FaithMarker faithMarker, Player player) throws FullSupplyException {
        int position=faithMarker.getFaithPosition();
        if(faithMarker.updatePosition()==position+1)
            return true;
        return false;
    }
}
