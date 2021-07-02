package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LeadAbilityShelfTest {

    @Test
        //this test check if the ability is activated correctly
    void activeAbility1() {
        Player p= new Player("pippo");
        LeadAbility shelfAb= new LeadAbilityShelf(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49,2,cardsReq,shelfAb);
        p.activateAbility(card);
        assertEquals(Resource.STONE, p.getPersonalBoard().getSpecialShelves().get(0).get().getResourceType());

    }
}