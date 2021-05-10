package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LeadCardTest {

    @Test
    void getLeadCardAbility() {
        LeadDeck deck = new LeadDeck();
        ArrayList<String> color=new ArrayList<>();
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.STONE);
        LeadAbility wrongLeadAbility= new LeadAbilityDiscount(Resource.STONE);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(2, leadAbility,new HashMap<Integer, Resource>(), cardsReq);

        assertEquals(leadAbility,card.getAbility());
        assertNotSame(wrongLeadAbility,card.getAbility());
    }

    @Test
    void getLeadCardRes() {
        LeadDeck deck = new LeadDeck();
        ArrayList<String> color=new ArrayList<>();
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);

        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(2,leadAbility,new HashMap<Integer, Resource>(), cardsReq );

        assertEquals(Resource.SHIELD,card.getAbility().getAbilityResource());
        assertNotEquals(Resource.COIN,card.getAbility().getAbilityResource());
    }

    @Test
    void getResExceptionThrowable() {
        LeadDeck deck = new LeadDeck();
        LeadCard wantedCard= deck.getLeadDeck().get(0);
        assertThrows(NoSuchRequirementException.class, () -> wantedCard.getResources() );
    }

    @Test
    void getResourceReqTrue() throws NoSuchRequirementException {
        HashMap<Integer, Resource> resReq= new HashMap<>();
        resReq.put(5,Resource.COIN);
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);
        ArrayList<Resource> requirements = new ArrayList<>();
        for(int i=0; i<5; i++)
            requirements.add(Resource.COIN);
        LeadCard card= new LeadCard(2,leadAbility, resReq, new HashMap<Integer, ArrayList<String>>());
        assertEquals(requirements, card.getResources());

    }

    @Test
    void getResourceReqFalse() throws NoSuchRequirementException {
        HashMap<Integer, Resource> resReq= new HashMap<>();
        resReq.put(5,Resource.COIN);
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);
        ArrayList<Resource> requirements = new ArrayList<>();
        for(int i=0; i<4; i++)
            requirements.add(Resource.SERVANT);
        LeadCard card= new LeadCard(2,leadAbility, resReq, new HashMap<Integer, ArrayList<String>>());
        assertNotEquals(requirements, card.getResources());

    }


    @Test
    void getDevCardReq() {
        LeadDeck deck = new LeadDeck();
        ArrayList<String> color=new ArrayList<>();
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(2,leadAbility,new HashMap<Integer, Resource>(), cardsReq );
        assertEquals(cardsReq, card.getDevCardRequired());

    }

    @Test
    void getDevCardReqWrong() {
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
        LeadCard card= new LeadCard(2,leadAbility,new HashMap<Integer, Resource>(), cardsReq );
        assertEquals(cardsReq, card.getDevCardRequired());
///TODO assertNotEquals
    }

    @Test
    void isLeadActive() throws InvalidActiveParameterException {
        ArrayList<String> color=new ArrayList<>();
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityWhiteMarble(Resource.SHIELD);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(2,leadAbility,new HashMap<Integer, Resource>(), cardsReq );
        card.setActive(true);
        assertTrue(card.isActive());
    }

    /*@Test
    //testo che parte throws
    void setLeadActive(boolean leadActive) {
    }

    @Test
    void useAbility() {
    }*/
}