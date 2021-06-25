package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class GreyMarble implements MarketMarble {

    final Resource resource= Resource.STONE;
    private final String color="GREY";

    /**
     * @return marble's color
     */
    @Override
    public String getColor() {
        return color;
    }

    /**
     * this subclass override this method and put a stone in resourceSupply
     * @param player is a reference to the player who is taking this marble
     * @return true if method putResourceInContainer works correctly
     */
    @Override
    public boolean changeMarble(Player player) {
        player.getResourceSupply().putResourceInContainer(resource);
        return true;
    }
}


