package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LeadCardTest {

    @Test
    //it tests if the lead ability is the same as predicted after the parsing
    void getLeadCardAbility() throws IOException, ParseException {
        LeadDeck deck = new LeadDeck();
        ArrayList<String> color=new ArrayList<>();
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.STONE);
        LeadAbility wrongLeadAbility= new LeadAbilityDiscount(Resource.STONE);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, leadAbility);

        assertEquals(leadAbility,card.getAbility());
        assertNotSame(wrongLeadAbility,card.getAbility());
    }

    @Test
    //it tests the parser and if the resources parsed are right
    void getLeadCardRes() throws IOException, ParseException{
        LeadDeck deck = new LeadDeck();
        ArrayList<String> color=new ArrayList<>();
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);

        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, leadAbility);

        assertEquals(Resource.SHIELD,card.getAbility().getAbilityResource());
        assertNotEquals(Resource.COIN,card.getAbility().getAbilityResource());
    }


    @Test
    //it tests the parser if it works with the requirements
    void getResourceReqTrue() {
        HashMap<Integer, Resource> resReq= new HashMap<>();
        resReq.put(5,Resource.COIN);
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);
        ArrayList<Resource> requirements = new ArrayList<>();
        for(int i=0; i<5; i++)
            requirements.add(Resource.COIN);
        LeadCard card= new LeadCard(49, 2, leadAbility, resReq);
        assertEquals(requirements, card.getResources());

    }

    @Test
    //it tests the parser does not put wrong res required
    void getResourceReqFalse() {
        HashMap<Integer, Resource> resReq= new HashMap<>();
        resReq.put(5,Resource.COIN);
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);
        ArrayList<Resource> requirements = new ArrayList<>();
        for(int i=0; i<4; i++)
            requirements.add(Resource.SERVANT);
        LeadCard card= new LeadCard(49, 2, leadAbility, resReq);
        assertNotEquals(requirements, card.getResources());

    }


    @Test
    // it tests if the parser puts the right dev card required
    void getDevCardReq() throws IOException, ParseException {
        LeadDeck deck = new LeadDeck();
        ArrayList<String> color=new ArrayList<>();
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, leadAbility);
        assertEquals(cardsReq, card.getDevCardRequired());

    }

    @Test
    //it tests if the parser gives wrong res required
    void getDevCardReqWrong() throws IOException, ParseException {
        LeadDeck deck = new LeadDeck();
        ArrayList<String> color=new ArrayList<>();
        color.add("GREEN");
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        ArrayList<String> wrongColor=new ArrayList<>();
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);
        HashMap<Integer, ArrayList<String>> wCardsReq= new HashMap<>();
        wCardsReq.put(2,wrongColor);
        LeadCard card= new LeadCard(49, 2, cardsReq, leadAbility);
        assertEquals(cardsReq, card.getDevCardRequired());
///TODO assertNotEquals
    }

    @Test
    //it tests if set active method works and the getter
    void isLeadActive() {
        ArrayList<String> color=new ArrayList<>();
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49, 2, cardsReq, leadAbility);
        card.setActive();
        assertTrue(card.isActive());
    }

}