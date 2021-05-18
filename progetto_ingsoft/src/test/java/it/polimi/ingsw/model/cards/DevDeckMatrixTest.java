package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.CardNotOnTableException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DevDeckMatrixTest {

    @Test
    void getUpperDevCardsOnTable() {
        new DevDeckMatrix();
        DevCard card = DevDeckMatrix.getDevMatrix()[0][0].littleDevDeck.get(0);
        DevCard[][] upperCards= DevDeckMatrix.getUpperDevCardsOnTable();
        assertEquals(card,upperCards[0][0]);
    }

    @Test
    void SameColorInALine(){
        new DevDeckMatrix();
        int i=0;
        for(int j=0; j<3;j++)
            for(int k=0; k<4;k++)
                assertEquals("GREEN", DevDeckMatrix.getDevMatrix()[i][j].littleDevDeck.get(k).getColor());
    }

    @Test
    void sameColorInALine(){
        new DevDeckMatrix();
        int i=2;
        for(int j=0; j<3;j++)
            for(int k=0; k<4;k++)
                assertEquals("PURPLE", DevDeckMatrix.getDevMatrix()[i][j].littleDevDeck.get(k).getColor());
    }

    @Test
    void allDifferentCards() {
        new DevDeckMatrix();
        ArrayList<DevCard> check= new ArrayList<>();

        for(int i=0;i<4;i++){
            for(int j=0; j<3;j++){
                for(int k=0;k<4;k++)
                    check.add(DevDeckMatrix.getDevMatrix()[i][j].littleDevDeck.get(k));
            }
        }
        for(DevCard card: DevDeckMatrix.getDeck().getDevCards())
            assertTrue(check.contains(card));
    }

    @Test
    void getNumberOfCardsIn1Slot() {
        new DevDeckMatrix();
        assertEquals(4,DevDeckMatrix.getDevMatrix()[0][0].littleDevDeck.size());

    }

    @Test
    void getUpperWhenLittleIsEmpty() throws CardNotOnTableException {
        new DevDeckMatrix();
        for(int i =0; i<4;i++)
            DevDeckMatrix.buyCard(DevDeckMatrix.getDevMatrix()[0][0].littleDevDeck.get(0));

        DevCard[][] upperCards= DevDeckMatrix.getUpperDevCardsOnTable();
        assertNull(upperCards[0][0]);
    }

    @Test
    void buyCard() throws CardNotOnTableException {
        new DevDeckMatrix();
        DevCard card = DevDeckMatrix.getDevMatrix()[0][0].littleDevDeck.get(0);
        assertEquals(card,DevDeckMatrix.buyCard(card));
    }

    @Test
    void buyCardException() {
        new DevDeckMatrix();
        ArrayList<Resource> array= new ArrayList<>();
        array.add(Resource.STONE);
        DevCard card = new DevCard(2,4,"PURPLE",4,array,array,array,3);
        assertThrows(CardNotOnTableException.class, ()->DevDeckMatrix.buyCard(card) );
    }

}