package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.AlreadyActivatedException;

/**
 * This interface is a Strategy pattern made to activate the different abilities
 */
public abstract class LeadAbility {

    protected Resource abilityResource;

    public LeadAbility(Resource resource){
        this.abilityResource=resource;
    }

    public abstract boolean activeAbility(Player p);

    public Resource getAbilityResource(){
        return abilityResource;
    }


}
