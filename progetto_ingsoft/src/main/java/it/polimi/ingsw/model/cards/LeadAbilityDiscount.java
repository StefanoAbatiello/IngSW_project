package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class LeadAbilityDiscount {

   public void activateAbilityOn(Player p){
        if(!p.isDiscountAbility()) {
            p.setDiscountAbility(true);
        }
    }

    //TODO nel controller faccio controllo discount

    /**nel controllo delle mensole, quando controllo risosrse, se risorsa è quella della abilità, diminuisco numero da controllare
    public boolean useAbility(Resource leadCardRes, DevCard devCard){
        if (devCard.getProdIn().contains(leadCardRes))
            /**come accetta Salvatore il pagamento e come avviene
             return true;
        else
            return false;
    */
    }



