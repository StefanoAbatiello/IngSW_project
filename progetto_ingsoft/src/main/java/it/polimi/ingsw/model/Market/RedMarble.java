package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.model.Player;

public class RedMarble implements MarketMarble {

    private final String color="RED";

    /**
     * @return marble's color
     */
    @Override
    public String getColor() {
        return color;
    }

    /**
     * this subclass overrides this method and give a faith point to the player
     * @param player is a reference to the player who is taking this marble
     * @return true if faithMarker is updated correctly
     */
    @Override
    public boolean changeMarble(Player player) {
        int position=player.getPersonalBoard().getFaithMarker().getFaithPosition();
        return player.getPersonalBoard().getFaithMarker().updatePosition() == position + 1;
    }
}
