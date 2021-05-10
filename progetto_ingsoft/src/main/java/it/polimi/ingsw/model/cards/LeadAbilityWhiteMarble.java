package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class LeadAbilityWhiteMarble extends LeadAbility {

    public LeadAbilityWhiteMarble(Resource resource){
        super(resource);
    }
    public boolean activeAbility(Player p) {
        if(!(p.getWhiteMarbleAbility().contains(this.abilityResource))) {
            p.getWhiteMarbleAbility().add(this.abilityResource);
            return true;
        }else
            return false;

    }

}
