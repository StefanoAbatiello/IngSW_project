package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.personalboard.FaithMarker;

public class RedMarble implements MarketMarble {

    private final String color="RED";

    @Override
    public String getColor() {
        return color;
    }

    /**
     * this subclass overrides this method and give a faith point to the player
     * @param player      is a reference to the player
     * @return true if faithMarker is updated correctly
     */
    @Override
    public boolean changeMarble(Player player) throws FullSupplyException {
        int position=player.getPersonalBoard().getFaithMarker().getFaithPosition();
        return player.getPersonalBoard().getFaithMarker().updatePosition() == position + 1;
    }
}
