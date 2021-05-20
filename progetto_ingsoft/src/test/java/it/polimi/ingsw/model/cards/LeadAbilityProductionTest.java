package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LeadAbilityProductionTest {

    @Test
    void activeAbility1() throws InvalidActiveParameterException {
        Player p= new Player("pincopallo");
        LeadAbility prodAb= new LeadAbilityProduction(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, prodAb);
        p.activateAbility(card);
        assertTrue(p.getProductionAbility().contains(Resource.STONE));
        assertTrue(p.getProductionAbility().size()==1);
    }
    @Test
    void activeAbility2TimesSameCard() throws InvalidActiveParameterException {
        Player p= new Player("pincopallo");
        LeadAbility prodAb= new LeadAbilityProduction(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, prodAb);
        p.activateAbility(card);
        p.activateAbility(card);
        assertTrue(p.getProductionAbility().contains(Resource.STONE));
        assertFalse(p.getProductionAbility().size()==2);
    }
    @Test
    void activeAbility2Times() throws InvalidActiveParameterException {
        Player p= new Player("pincopallo");
        LeadAbility prodAb= new LeadAbilityProduction(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityProduction(Resource.SERVANT);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card1= new LeadCard(49, 2, cardsReq, prodAb);
        LeadCard card2= new LeadCard(49, 2, cardsReq, leadAbility);
        p.activateAbility(card1);
        p.activateAbility(card2);
        assertTrue(p.getProductionAbility().contains(Resource.STONE));
        assertTrue(p.getProductionAbility().contains(Resource.SERVANT));
        assertTrue(p.getProductionAbility().size()==2);
    }
}