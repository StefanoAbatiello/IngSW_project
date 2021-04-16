package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.personalboard.FaithMarker;


public class WhiteMarble implements MarketMarble {

    /**
     * this subclass overrides the strategy method,checking if the player has a leader card
     * with the ability to generate a resource from a white marble.
     * if that is possible, it calls the method that use this ability
     * @param faithMarker is a reference to the player's FaithMarker
     * @param player      is a reference to the player
     * @return true if the player has a whiteMarbleAbility active and the storage of the corrispondent resourse is done correctly, false in other case
     */
    //TODO gestione della scelta di quale risorsa prendere se può scegliere tra due

    public boolean changeMarble(FaithMarker faithMarker, Player player) throws FullSupplyException {
        /*if (player.WhiteMarbleAbility.length!=0) {
            ResourceSupply.putResourceInContainer(player.WhiteMarbleAbility[0]);
            return true;
        }*/
        return false;
    }

}
