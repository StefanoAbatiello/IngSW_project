package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LeadAbilityWhiteMarbleTest {

    @Test
    void activeAbility1() {
        Player p= new Player("pincopallo");
        LeadAbility whiteAb= new LeadAbilityWhiteMarble(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, whiteAb);
        p.activateAbility(card);
        assertTrue(p.getWhiteMarbleAbility().contains(Resource.STONE));
        assertTrue(p.getWhiteMarbleAbility().size()==1);
    }
    @Test
    void activeAbility2TimesSameCard()  {
        Player p= new Player("pincopallo");
        LeadAbility whiteAb= new LeadAbilityWhiteMarble(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, whiteAb);
        p.activateAbility(card);
        p.activateAbility(card);
        assertTrue(p.getWhiteMarbleAbility().contains(Resource.STONE));
        assertFalse(p.getWhiteMarbleAbility().size()==2);
    }
    @Test
    void activeAbility2Times(){
        Player p= new Player("pincopallo");
        LeadAbility whiteAb= new LeadAbilityWhiteMarble(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SERVANT);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card1= new LeadCard(49, 2, cardsReq, whiteAb);
        LeadCard card2= new LeadCard(49, 2, cardsReq, leadAbility);
        p.activateAbility(card1);
        p.activateAbility(card2);
        assertTrue(p.getWhiteMarbleAbility().contains(Resource.STONE));
        assertTrue(p.getWhiteMarbleAbility().contains(Resource.SERVANT));
        assertTrue(p.getWhiteMarbleAbility().size()==2);
    }
}