package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.AlreadyActivatedException;

/**
 * This interface is a Strategy pattern made to activate the different abilities
 */
public abstract class LeadAbility {

    private Resource abilityResource;
    private boolean active;


    public abstract boolean useAbility(Player p);

    public boolean getActive(){
        return active;
    }

    public boolean setActive(boolean active) throws AlreadyActivatedException {
        if(!this.active) {
            this.active = active;
            return active;
        }else
            throw new AlreadyActivatedException("This ability is already active");
    }

    public Resource setAbilityResource(Resource resource){
        abilityResource=resource;
        return abilityResource;
    }

    public Resource getAbilityResource(){
        return abilityResource;
    }


}
