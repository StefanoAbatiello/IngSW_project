package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeadDeckTest {

    @Test
    @BeforeAll
    void createDeck() throws NoSuchRequirementException {
        LeadDeck deck=new LeadDeck();
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityDiscount(Resource.SERVANT);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49,2,cardsReq,leadAbility);
        LeadCard wantedCard= LeadDeck.getCardFromId(49);
        assertEquals(card.getPoints(), wantedCard.getPoints());
        assertEquals(card.getAbility().getClass(), wantedCard.getAbility().getClass());
        assertEquals(card.getAbility().getAbilityResource(), wantedCard.getAbility().getAbilityResource());
        //since the empty Hashmap creates some problems in the getResource, it will be tested in other tests in the single card
        assertEquals(card.getDevCardRequired(), wantedCard.getDevCardRequired());
    }

    @Test
    void shuffle() {
        LeadDeck deck=new LeadDeck();
        ArrayList<LeadCard> newDeck= deck.shuffle();
        assertEquals(deck.getLeadDeck().size(), newDeck.size());
        for(LeadCard card: deck.getLeadDeck())
            assertTrue (newDeck.contains(card));
        //TODO ordine diverso
    }



    @Test
    void giveToPlayer() throws playerLeadsNotEmptyException {
        LeadDeck deck =new LeadDeck();
        deck.shuffle();
        Player p= new Player("4");
        ArrayList<LeadCard> leadCards = new ArrayList<>();
        deck.shuffle();
        for(int i=0;i<4;i++)
            leadCards.add(deck.getLeadDeck().get(i));
        deck.giveToPlayer(p);
        ArrayList<LeadCard> playerLeads = p.getLeadCards();
        assertEquals(leadCards,playerLeads);
    }
        /*in entrata 4 carte, se giocatore ha array con stesse 4 carte, funziona, ritorna true
    }*/

    @Test
    void cannotGiveToPlayerExc() throws playerLeadsNotEmptyException {
        LeadDeck deck=new LeadDeck();
        deck.shuffle();
        Player p= new Player("4");
        ArrayList<LeadCard> leadCards = new ArrayList<>();
        for(int i=0;i<3;i++)
            leadCards.add(deck.getLeadDeck().get(i));
        deck.giveToPlayer(p);
        assertThrows(playerLeadsNotEmptyException.class,()->deck.giveToPlayer(p));
    }

    @Test
    void giveCardsToPlayersAllDifferent() throws playerLeadsNotEmptyException {
        LeadDeck deck=new LeadDeck();
        Player p1= new Player("2");
        Player p2= new Player("4");
        shuffle();
        ArrayList<LeadCard> leadCards1 = new ArrayList<>();
        for(int i=0;i<4;i++)
            leadCards1.add(deck.getLeadDeck().get(i));
        deck.giveToPlayer(p1);
        ArrayList<LeadCard> leadCards2 = new ArrayList<>();
        for(int i=0;i<4;i++)
            leadCards2.add(deck.getLeadDeck().get(i));
        deck.giveToPlayer(p2);
        ArrayList<LeadCard> p1Leads = p1.getLeadCards();
        assertEquals(leadCards1,p1Leads);
        ArrayList<LeadCard> p2Leads = p2.getLeadCards();
        assertEquals(leadCards2,p2Leads);
        for(LeadCard card: p1Leads)
            assertFalse(p2Leads.contains(card));
    }

}
