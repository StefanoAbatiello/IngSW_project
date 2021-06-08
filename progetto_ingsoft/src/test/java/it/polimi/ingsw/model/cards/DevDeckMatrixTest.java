package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.cardExceptions.CardNotOnTableException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DevDeckMatrixTest {

    @Test
    void getUpperDevCardsOnTable() throws IOException, ParseException {
        DevDeckMatrix matrix=new DevDeckMatrix();
        DevCard card = matrix.getDevMatrix()[0][0].littleDevDeck.get(0);
        DevCard[][] upperCards= matrix.getUpperDevCardsOnTable();
        assertEquals(card,upperCards[0][0]);
    }

    @Test
    void SameColorInALine()throws IOException, ParseException {
        DevDeckMatrix matrix=new DevDeckMatrix();
        int i=0;
        for(int j=0; j<3;j++)
            for(int k=0; k<4;k++)
                assertEquals("GREEN", matrix.getDevMatrix()[i][j].littleDevDeck.get(k).getColor());
    }

    @Test
    void sameColorInALine()throws IOException, ParseException {
        DevDeckMatrix matrix=new DevDeckMatrix();
        int i=2;
        for(int j=0; j<3;j++)
            for(int k=0; k<4;k++)
                assertEquals("PURPLE", matrix.getDevMatrix()[i][j].littleDevDeck.get(k).getColor());
    }

    @Test
    void allDifferentCards() throws IOException, ParseException {
        DevDeckMatrix matrix=new DevDeckMatrix();
        ArrayList<DevCard> check= new ArrayList<>();

        for(int i=0;i<4;i++){
            for(int j=0; j<3;j++){
                for(int k=0;k<4;k++)
                    check.add(matrix.getDevMatrix()[i][j].littleDevDeck.get(k));
            }
        }
        for(DevCard card: matrix.getDeck().getDevCards())
            assertTrue(check.contains(card));
    }

    @Test
    void getNumberOfCardsIn1Slot()throws IOException, ParseException  {
        DevDeckMatrix matrix=new DevDeckMatrix();
        assertEquals(4,matrix.getDevMatrix()[0][0].littleDevDeck.size());

    }

    @Test
    void getUpperWhenLittleIsEmpty() throws CardNotOnTableException, IOException, ParseException  {
        DevDeckMatrix matrix=new DevDeckMatrix();
        for(int i =0; i<4;i++)
            matrix.buyCard(matrix.getDevMatrix()[0][0].littleDevDeck.get(0));

        DevCard[][] upperCards= matrix.getUpperDevCardsOnTable();
        assertNull(upperCards[0][0]);
    }

    //TODO test inutile?
    /*@Test
    void buyCard() throws CardNotOnTableException {
        new DevDeckMatrix();
        DevCard card = DevDeckMatrix.getDevMatrix()[0][0].littleDevDeck.get(0);
        assertTrue(DevDeckMatrix.buyCard(card));
    }*/

   //TODO test controller
    /*@Test
    void buyCardException() {
        new DevDeckMatrix();
        ArrayList<Resource> array= new ArrayList<>();
        array.add(Resource.STONE);
        DevCard card = new DevCard(2,4,"PURPLE",4,array,array,array,3);
        assertThrows(CardNotOnTableException.class, ()->DevDeckMatrix.buyCard(card) );
    }*/

}