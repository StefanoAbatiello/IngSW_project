package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import it.polimi.ingsw.model.singlePlayerMode.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SinglePlayerTest {

    /*
    this Test is implemented to check if the method return the correct value in case of an empty line in DevDeckMatrix
     */
    @Test
    void checkEmptyLineInMatrixTest() throws playerLeadsNotEmptyException {
        SinglePlayer sP=new SinglePlayer("USER");
        int i=0, j=0;
        String color = sP.getDevDeckMatrix().getDevMatrix()[i][j].getLittleDevDeck().get(0).getColor();
        for(int k = 0; k <12; k++) {
            sP.removeTokenCard(color);
        }
        assertTrue(sP.checkEmptyLineInMatrix());
    }

    /*
    this Test is implemented to check if the method return the correct value if there other cards of a specified color
     */
    @Test
    void checkLinesInMatrixTest() throws playerLeadsNotEmptyException {
        SinglePlayer sP=new SinglePlayer("USER");
        for (int i=0; i<5; i++)
            sP.removeTokenCard("BLUE");
        assertFalse(sP.checkEmptyLineInMatrix());
    }

    /*
    this Test is implemented to check if the method return the correct value in case Lorenzo's BlackCross reaches the end of FaithTrack
     */
    @Test
    void LorenzoWinTest() throws playerLeadsNotEmptyException {
        SinglePlayer singlePlayer=new SinglePlayer("Player");
        ArrayList<ActionToken> tokens = new ArrayList<>();
        DoubleCrossAction token = new DoubleCrossAction(singlePlayer);
        tokens.add(token);
        for(int i=0; i<12; i++)
            token.applyEffect(tokens);
        assertTrue(singlePlayer.checkBlackCrossPosition());
    }

    /*
    this test is implemented to check if the scrolling of TokensStack works correctly
     */
    @Test
    void drawTest() throws playerLeadsNotEmptyException {
        SinglePlayer sP = new SinglePlayer("USER");
        sP.getTokensStack().clear();
        sP.getTokensStack().add(new DoubleCrossAction(sP));
        sP.getTokensStack().add(new DoubleCrossAction(sP));
        sP.getTokensStack().add(new DiscardDevCardAction("YELLOW",sP));
        sP.getTokensStack().add(new DiscardDevCardAction("GREEN",sP));
        sP.getTokensStack().add(new DiscardDevCardAction("PURPLE",sP));
        sP.getTokensStack().add(new DiscardDevCardAction("BLUE",sP));
        sP.getTokensStack().add(new CrossShuffleAction(sP));
        ArrayList<ActionToken> list= new ArrayList<>();
        for (int i=1;i<=6;i++) {
            list.add(sP.getTokensStack().get(i));
        }
        list.add(sP.getTokensStack().get(0));
        sP.draw();
        assertEquals(list, sP.getTokensStack());
    }

    /*
    this Test is implemented to check if the card is removed correctly
     */
    @Test
    void removeTokenCardTest() throws playerLeadsNotEmptyException {
        SinglePlayer sP=new SinglePlayer("USER");
        int i=0, j=0;
        ArrayList<DevCard> list = new ArrayList<>(sP.getDevDeckMatrix().getDevMatrix()[i][j].getLittleDevDeck());
        String color = list.remove(0).getColor();
        sP.removeTokenCard(color);
        assertEquals(list,sP.getDevDeckMatrix().getDevMatrix()[i][j].getLittleDevDeck());
    }
}