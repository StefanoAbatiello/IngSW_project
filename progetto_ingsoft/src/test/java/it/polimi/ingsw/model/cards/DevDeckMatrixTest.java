package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.CardNotOnTableException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DevDeckMatrixTest {

    @Test
    void getUpperDevCardsOnTable() {
        DevCard card = DevDeckMatrix.getDevMatrix()[0][0].littleDevDeck.get(0);
        DevCard[][] upperCards= DevDeckMatrix.getUpperDevCardsOnTable();
        assertEquals(card,upperCards[0][0]);
    }

    @Test
    void getUpperWhenLittleIsEmpty() throws CardNotOnTableException {
        DevCard card = DevDeckMatrix.getDevMatrix()[0][0].littleDevDeck.get(0);
        for(int i =0; i<4;i++)
        DevDeckMatrix.buyCard(card);
        card = DevDeckMatrix.getDevMatrix()[0][0].littleDevDeck.get(0);

        DevCard[][] upperCards= DevDeckMatrix.getUpperDevCardsOnTable();
        assertNull(upperCards[0][0]);
    }

    @Test
    void buyCard() throws CardNotOnTableException {
        DevCard card = DevDeckMatrix.getDevMatrix()[0][0].littleDevDeck.get(0);
        assertEquals(card,DevDeckMatrix.buyCard(card));
    }

    @Test
    void buyCardException() throws CardNotOnTableException {
        ArrayList<Resource> array= new ArrayList<>();
        array.add(Resource.STONE);
        DevCard card = new DevCard(4,"PURPLE",4,array,array,array,3);
        assertThrows(CardNotOnTableException.class, ()->DevDeckMatrix.buyCard(card) );
        //TODO lancia l'eccezione se non c'è carta
    }

}