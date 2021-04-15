package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class LeadAbilityWhiteMarble {

    public boolean activateAbilityOn(Player p){
        if(!p.isWhiteMarbleAbility()) {
            p.setWhiteMarbleAbility(true);
            return true;
        }else
            return false;
    }

   /*ste controlla appena legge una bianca se abilità è attiva, se attiva fa fare alla use ability anzichè muovere lui*/
  /*nel controller quando faccio il check se il player ha la lead giusta, chiedo che carta lead vuole usare se ne ha due dello stesso tipo
    public void useAbility(Resource leadCardRes){
        Container container= new Container();
       container.putResourceInContainer(leadCardRes);
    }*/
}
