package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class WhiteMarble implements MarketMarble {

    private final String color="WHITE";

    /**
     * @return marble's color
     */
    @Override
    public String getColor() {
        return color;
    }

    /**
     * this subclass overrides the strategy method,checking if the player has a leader card
     * with the ability to generate a resource from a white marble.
     * if that is possible, it calls the method that use this ability
     * @param player is a reference to the player
     * @return true if the player has a whiteMarbleAbility active and the storage of the corrispondent resourse is done correctly, false in other case
     */
    public boolean changeMarble(Player player) throws FullSupplyException {
        if(player.getWhiteMarbleAbility().size() == 1) {
            player.getResourceSupply().putResourceInContainer(player.getWhiteMarbleAbility().get(0));
            return true;
        }
        if(player.getWhiteMarbleAbility().size() == 2){
            player.getResourceSupply().putResourceInContainer(Resource.CHOOSABLE);
            return true;
        }
        return false;
    }

}
