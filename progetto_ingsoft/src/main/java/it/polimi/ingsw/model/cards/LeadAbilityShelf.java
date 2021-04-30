package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class LeadAbilityShelf extends LeadAbility{

    @Override
    public boolean useAbility(Player p) {

        return true;

    }


    /*public boolean useAbility(LeadCard leadCard, Player p, Resource resourceIn){
       //player has ability warehouse, they choose what to put
       if(resourceIn.getResourceType().equals(leadCard.getLeadCardRes()))
         /**special? made by salvatore?
         if(specialShelf.isEmpty) {
             specialShelf.putResourceIn;
             return true;
         }
         else
             return false;
       return false;
    }*/

}
