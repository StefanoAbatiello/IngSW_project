package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class LeadAbilityProduction extends LeadAbility{

    public LeadAbilityProduction(Resource resource){
        super(resource);
    }
    @Override
    public boolean activeAbility(Player p) {
        if(!(p.getProductionAbility().contains(this.abilityResource))) {
            p.getProductionAbility().add(this.abilityResource);
            return true;
        }else
            return false;

    }
}

