package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeadDeckTest {

    /*@Test
    void shuffleCards() {
        /**entrata e uscita stessa misura, ma ordine cose interne diverso
    }

    @Test
    void giveCardsToPlayer() {
        LeadDeck leadDeck= new LeadDeck("cards");
        leadDeck.shuffleCards();
        Player p= new Player();
        LeadCard[] leadCards = new LeadCard[4];
            for(int i=0;i<3;i++)
                leadCards[i]=leadDeck.get(i);
            assertEquals(leadCards,leadDeck.giveCardsToPlayer(p, leadDeck));
        }
        /**in entrata 4 carte, se giocatore ha array con stesse 4 carte, funziona, ritorna true
    }


    @Test
    void giveCardsToPlayersAllDifferent() {
        Player p1= new Player();
        Player p2= new Player();
        leadDeck.giveCardsToPlayer(p1);
        leadDeck.giveCardsToPlayer(p2);
        for(LeadCard leadCard: p1.leadCards)

        assertNotEquals(p1.);
        }

        /**le carte dei giocatori devono essere diverse
    }
}/**array giocatore pieno*/
}