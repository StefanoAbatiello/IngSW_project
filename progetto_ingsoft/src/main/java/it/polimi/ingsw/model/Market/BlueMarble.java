package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class BlueMarble implements MarketMarble{

    private final String color="BLUE";
    final Resource resource=Resource.SHIELD;

    /**
     * @return marble's color
     */
    @Override
    public String getColor() {
        return color;
    }

    /**
     * this subclass override this method and put a shield in resourceSupply
     * @param player      is a reference to the player who's playing
     * @return true if method putResourceInContainer works correctly
     */
    @Override
    public boolean changeMarble(Player player) throws FullSupplyException {
        player.getResourceSupply().putResourceInContainer(resource);
        return true;
    }
}
