package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeadDeckTest {

    @Test
    @BeforeAll
    //this test creates the deck
    void createDeck() throws IOException, ParseException, CardChosenNotValidException {
        LeadDeck deck=new LeadDeck();
        ArrayList<String> color=new ArrayList<>();
        color.add("YELLOW");
        color.add("GREEN");
        LeadAbility leadAbility= new LeadAbilityDiscount(Resource.SERVANT);
        HashMap<Integer, ArrayList<String>> cardsReq= new HashMap<>();
        cardsReq.put(1,color);
        LeadCard card= new LeadCard(49,2,cardsReq,leadAbility);
        LeadCard wantedCard= deck.getCardFromId(49);
        assertEquals(card.getPoints(), wantedCard.getPoints());
        assertEquals(card.getAbility().getClass(), wantedCard.getAbility().getClass());
        assertEquals(card.getAbility().getAbilityResource(), wantedCard.getAbility().getAbilityResource());
        //since the empty Hashmap creates some problems in the showResource, it will be tested in other tests in the single card
        assertEquals(card.getDevCardRequired(), wantedCard.getDevCardRequired());
    }

    @Test
    //this test checks if the deck is shuffled correctly
    void shuffle() throws IOException, ParseException {
        LeadDeck deck=new LeadDeck();
        ArrayList<LeadCard> newDeck= deck.shuffle();
        assertEquals(deck.getLeadDeck().size(), newDeck.size());
        for(LeadCard card: deck.getLeadDeck())
            assertTrue (newDeck.contains(card));
    }



    @Test
    //this test checks if the card are correctly distributed to the player
    void giveToPlayer() throws playerLeadsNotEmptyException, IOException, ParseException {
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

    @Test
    //this test checks if the exception is correctly throw in distribution too many cards
    void cannotGiveToPlayerExc() throws playerLeadsNotEmptyException, IOException, ParseException {
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
    //this test checks if the card distributed are all different
    void giveCardsToPlayersAllDifferent() throws playerLeadsNotEmptyException, IOException, ParseException {
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
