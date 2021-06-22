package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class PurpleMarble implements MarketMarble {

    private final String color="PURPLE";
    final Resource resource=Resource.SERVANT;

    /**
     * @return marble's color
     */
    @Override
    public String getColor() {
        return color;
    }

    /**
     * this subclass override this method and put a servant in  resourceSupply
     * @param player is a reference to the player who is taking this marble
     * @return true if method putResourceInContainer works correctly
     */
    @Override
    public boolean changeMarble(Player player) throws FullSupplyException {
        player.getResourceSupply().putResourceInContainer(resource);
        return true;
    }

}

