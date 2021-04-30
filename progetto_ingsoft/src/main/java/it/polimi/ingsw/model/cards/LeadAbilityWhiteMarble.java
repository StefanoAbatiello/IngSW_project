package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class LeadAbilityWhiteMarble extends LeadAbility {

    @Override
    public boolean useAbility(Player p) {

        return true;
    }

    /*public void useAbility(Resource leadCardRes){
        Container container= new Container();
       container.putResourceInContainer(leadCardRes);
    }*/
}
