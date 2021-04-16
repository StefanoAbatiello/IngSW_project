package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class LeadAbilityShelf {
    /**
     *     se devo inserire, do possibilità di mettere in questa risorse del tipo dell'abilità'
     * @return
     */
    public boolean activateAbilityOn(Player p){
        if(!p.isShelfAbility()) {
            p.setShelfAbility(true);
            return true;
        }else
            return false;
    }

    /*public boolean useAbility(LeadCard leadCard, Player p, Resource resourceIn){
       /**giocatore ha metodo usa abilità deposito, decide lui quando mettere qui, controllo solo sia risorsa giusta
       if(resourceIn.getResourceType().equals(leadCard.getLeadCardRes()))
         /**totto ha fatto special?
         if(specialShelf.isEmpty) {
             specialShelf.putResourceIn;
             return true;
         }
         else
             return false;
       return false;
    }*/

}
