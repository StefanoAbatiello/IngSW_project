package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class LeadAbilityDiscount extends LeadAbility{

    public LeadAbilityDiscount(Resource resource){
       super(resource);
   }
    @Override
    public boolean activeAbility(Player p) {
        if(!(p.getDiscountAbility().contains(this.abilityResource))) {
            p.getDiscountAbility().add(this.abilityResource);
            return true;
        }else
            return false;

    }

    //TODO check discount in controller


}

