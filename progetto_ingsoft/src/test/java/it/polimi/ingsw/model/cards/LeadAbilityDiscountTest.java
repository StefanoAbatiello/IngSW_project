package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LeadAbilityDiscountTest {

    @Test
    void activeAbility1() throws InvalidActiveParameterException {
        Player p= new Player("pincopallo");
        LeadAbility disAb= new LeadAbilityDiscount(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, disAb);
        p.activateAbility(card);
        assertTrue(p.getDiscountAbility().contains(Resource.STONE));
        assertTrue(p.getDiscountAbility().size()==1);
    }
    @Test
    void activeAbility2TimesSameCard() throws InvalidActiveParameterException {
        Player p= new Player("pincopallo");
        LeadAbility disAb= new LeadAbilityDiscount(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, disAb);
        p.activateAbility(card);
        p.activateAbility(card);
        assertTrue(p.getDiscountAbility().contains(Resource.STONE));
        assertFalse(p.getDiscountAbility().size()==2);
    }
    @Test
    void activeAbility2Times() throws InvalidActiveParameterException {
        Player p= new Player("pincopallo");
        LeadAbility disAb= new LeadAbilityDiscount(Resource.STONE);
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityDiscount(Resource.SERVANT);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card1= new LeadCard(49, 2, cardsReq, disAb);
        LeadCard card2= new LeadCard(49, 2, cardsReq, leadAbility);
        p.activateAbility(card1);
        p.activateAbility(card2);
        assertTrue(p.getDiscountAbility().contains(Resource.STONE));
        assertTrue(p.getDiscountAbility().contains(Resource.SERVANT));
        assertTrue(p.getDiscountAbility().size()==2);
    }
}