package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.singlePlayerMode.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SinglePlayerTest {

    /*
    this Test is implemented to check if the method return the correct value in case of an empty line in DevDeckMatrix
     */
    @Test
    void checkEmptyLineInMatrixTest() {
        new SinglePlayer();
        int i=0, j=0;
        String color = DevDeckMatrix.getDevMatrix()[i][j].getLittleDevDeck().get(0).getColor();
        for(int k = 0; k <12; k++) {
            SinglePlayer.removeTokenCard(color);
        }
        assertTrue(SinglePlayer.checkEmptyLineInMatrix());
    }

    /*
    this Test is implemented to check if the method return the correct value if there other cards of a specified color
     */
    @Test
    void checkLinesInMatrixTest() {
        new SinglePlayer();
        for (int i=0; i<5; i++)
            SinglePlayer.removeTokenCard("BLUE");
        assertFalse(SinglePlayer.checkEmptyLineInMatrix());
    }

    /*
    this Test is implemented to check if the method return the correct value in case Lorenzo's BlackCross reaches the end of FaithTrack
     */
    @Test
    void LorenzoWinTest() {
        ArrayList<ActionToken> tokens = new ArrayList<>();
        DoubleCrossAction token = new DoubleCrossAction();
        tokens.add(token);
        for(int i=0; i<12; i++)
            token.applyEffect(tokens);
        assertTrue(SinglePlayer.checkBlackCrossPosition());
    }

    /*
    this test is implemented to check if the scrolling of TokensStack works correctly
     */
    @Test
    void drawTest(){
        SinglePlayer sP = new SinglePlayer();
        sP.getTokensStack().clear();
        sP.getTokensStack().add(new DoubleCrossAction());
        sP.getTokensStack().add(new DoubleCrossAction());
        sP.getTokensStack().add(new DiscardDevCardAction("YELLOW"));
        sP.getTokensStack().add(new DiscardDevCardAction("GREEN"));
        sP.getTokensStack().add(new DiscardDevCardAction("PURPLE"));
        sP.getTokensStack().add(new DiscardDevCardAction("BLUE"));
        sP.getTokensStack().add(new CrossShuffleAction());
        ArrayList<ActionToken> list= new ArrayList<>();
        for (int i=1;i<=6;i++) {
            list.add(sP.getTokensStack().get(i));
        }
        list.add(sP.getTokensStack().get(0));
        assertEquals(list,sP.draw());
    }

    /*
    this Test is implemented to check if the card is removed correctly
     */
    @Test
    void removeTokenCardTest() {
        new SinglePlayer();
        int i=0, j=0;
        ArrayList<DevCard> list = new ArrayList<>(DevDeckMatrix.getDevMatrix()[i][j].getLittleDevDeck());
        String color = list.remove(0).getColor();
        SinglePlayer.removeTokenCard(color);
        assertEquals(list,DevDeckMatrix.getDevMatrix()[i][j].getLittleDevDeck());
    }
}