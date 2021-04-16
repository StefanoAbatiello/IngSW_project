package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;

/**
 * This interface is a Strategy pattern made to activate the different abilities
 */
public interface LeadAbility {
    public void activateAbilityOn(Player p);
    public void useAbility(Player p);
}
