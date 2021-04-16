package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DevDeckTest {

    @Test
        //check that the deck of dev cards contains 48 cards
    void  sizeDevDeck() {
        DevDeck deck = new DevDeck();
        assertEquals(48, deck.getDevCards().size());
    }

    @Test
    //check that the deck is created in the right way and the cards have all the attributes needed
    void devDeckCreated() {
        DevDeck deck = new DevDeck();
        DevCard card = deck.getDevCards().get(0);
        assertEquals(1,card.getPoints());
        assertEquals("GREEN",card.getColor());
        assertEquals(1,card.getLevel());
        ArrayList<Resource> requirements =new ArrayList<>();
        requirements.add(Resource.SHIELD);
        requirements.add(Resource.SHIELD);
        assertEquals(requirements, card.getRequirements());
        assertEquals(1,card.getFaithPoint());
    }

    @Test
    //check that the deck contains all the cards and that there are no clones
    void allCardsAreDifferent(){
        DevDeck deck = new DevDeck();
        for(int i=0;i<48;i++)
            for(int j=i+1;j<48;j++)
                    assertNotEquals(deck.getDevCards().get(i), deck.getDevCards().get(j));

    }

    @Test
    //check if the  color deck created contains all the 12 cards of that color
    void colorDeckHas12Cards(){
        DevDeck deck = new DevDeck();
        ArrayList<DevCard> green= deck.createLittleDecks("GREEN");
        assertEquals(12,green.size());
    }

    @Test
    //check if the color deck created contains the card of the color requested
    void deckColorIsRight(){
        DevDeck deck = new DevDeck();
        ArrayList<DevCard> green= deck.createLittleDecks("GREEN");
        for (DevCard devCard : green)
            assertEquals("GREEN", devCard.getColor());
    }

    //check if the color deck created contains the card ordered by level
    @Test
    void colorCardsInLevelOrder(){
        DevDeck deck = new DevDeck();
        ArrayList<DevCard> green= deck.createLittleDecks("GREEN");
        for(int i=0;i<4;i++)
            assertEquals(1,green.get(i).getLevel());
        for(int i=4;i<8;i++)
            assertEquals(2,green.get(i).getLevel());
        for(int i=8;i<12;i++)
            assertEquals(3,green.get(i).getLevel());

    }
}