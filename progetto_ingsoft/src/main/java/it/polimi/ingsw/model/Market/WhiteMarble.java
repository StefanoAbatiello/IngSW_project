package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.LeadAbilityWhiteMarble;
import it.polimi.ingsw.model.personalboard.FaithMarker;


public class WhiteMarble implements MarketMarble {

    private final String color="WHITE";

    @Override
    public String getColor() {
        return color;
    }

    /**
     * this subclass overrides the strategy method,checking if the player has a leader card
     * with the ability to generate a resource from a white marble.
     * if that is possible, it calls the method that use this ability
     * @param player      is a reference to the player
     * @return true if the player has a whiteMarbleAbility active and the storage of the corrispondent resourse is done correctly, false in other case
     */
    //TODO gestione della scelta di quale risorsa prendere se può scegliere tra due

    public boolean changeMarble(Player player) throws FullSupplyException {
        /*if (player.WhiteMarbleAbility.length!=0) {
            ResourceSupply.putResourceInContainer(player.WhiteMarbleAbility[0]);
            return true;
        }*/
        return false;
    }

    //TODO check new method with new idea of ability
    /*public boolean changeMarble(FaithMarker faithMarker, Player player) throws FullSupplyException {
        if (player.getAbility1().getActive() && player.getAbility2().getActive()) {
            if (player.getAbility1() instanceof LeadAbilityWhiteMarble && player.getAbility2() instanceof LeadAbilityWhiteMarble)
                //TODO case of choice
                return true;
        }else if(player.getAbility1().getActive()){
            if(player.getAbility1() instanceof LeadAbilityWhiteMarble) {
                ResourceSupply.putResourceInContainer(player.getAbility1().getAbilityResource());
                return true;
            }}else if(player.getAbility2().getActive()){
            if(player.getAbility2() instanceof LeadAbilityWhiteMarble) {
                ResourceSupply.putResourceInContainer(player.getAbility2().getAbilityResource());
                return true;
            }
        }
        return false;
    }*/


}
