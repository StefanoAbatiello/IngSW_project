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
    //this test checks the purchasable cards
    void getUpperDevCardsOnTable() throws IOException, ParseException {
        DevDeckMatrix matrix=new DevDeckMatrix();
        DevCard card = matrix.getDevMatrix()[0][0].littleDevDeck.get(0);
        DevCard[][] upperCards= matrix.getUpperDevCardsOnTable();
        assertEquals(card,upperCards[0][0]);
    }

    @Test
    //this test checks if the card on the same line has the same color
    void SameColorInALine()throws IOException, ParseException {
        DevDeckMatrix matrix=new DevDeckMatrix();
        int i=0;
        for(int j=0; j<3;j++)
            for(int k=0; k<4;k++)
                assertEquals("GREEN", matrix.getDevMatrix()[i][j].littleDevDeck.get(k).getColor());
    }

    @Test
    //this test checks if in the matrix there are all different cards
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
    //this method check if there are 4 card in a little deck at the start
    void getNumberOfCardsIn1Slot()throws IOException, ParseException  {
        DevDeckMatrix matrix=new DevDeckMatrix();
        assertEquals(4,matrix.getDevMatrix()[0][0].littleDevDeck.size());

    }

    @Test
    //this method check that the getUpper method puts null in the matrix in case of
    //one little deck is empty
    void getUpperWhenLittleIsEmpty() throws IOException, ParseException  {
        DevDeckMatrix matrix=new DevDeckMatrix();
        for(int i =0; i<4;i++)
            matrix.buyCard(matrix.getDevMatrix()[0][0].littleDevDeck.get(0));

        DevCard[][] upperCards= matrix.getUpperDevCardsOnTable();
        assertNull(upperCards[0][0]);
    }

    @Test
    //this method check if the card can be purchased
    void buyCard() throws IOException, ParseException {
        DevDeckMatrix matrix=new DevDeckMatrix();
        DevCard card = matrix.getDevMatrix()[0][0].littleDevDeck.get(0);
        assertTrue(matrix.buyCard(card));
    }

    @Test
    //this method check if the method findCardInMatrix  throws the CardnotOnTableException
    //if the card is not in the card is not in the purchasable cards
    void findCardException() throws IOException, ParseException {
        DevDeckMatrix matrix=new DevDeckMatrix();
        ArrayList<Resource> array= new ArrayList<>();
        array.add(Resource.STONE);
        DevCard card = new DevCard(2,4,"PURPLE",4,array,array,array,3);
        assertThrows(CardNotOnTableException.class, ()->matrix.findCardInMatrix(card.getId()) );
    }

    @Test
    //this method checks if the method findCardInMatrix find the card in the purchasable ones
    void findCardTest() throws IOException, ParseException, CardNotOnTableException {
        DevDeckMatrix matrix=new DevDeckMatrix();
        ArrayList<Resource> array= new ArrayList<>();
        array.add(Resource.STONE);
        DevCard card=matrix.getUpperDevCardsOnTable()[0][0];
        assertEquals(card,matrix.findCardInMatrix(card.getId()) );
    }
}