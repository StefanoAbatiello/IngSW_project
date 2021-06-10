package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DoubleCrossActionTest {

    /*
     this test is implemented to check if token effect works correctly after multiple usages
      */
    @Test
    void UpdatingBlackCrossTest() throws playerLeadsNotEmptyException, IOException, ParseException {
        SinglePlayer sP = new SinglePlayer("USER");
        DoubleCrossAction action=new DoubleCrossAction(sP);
        for(int i=0;i<5;i++)
            action.applyEffect(sP.getTokensStack());
        int pos=sP.getBlackCrossToken().getCrossPosition();
        action.applyEffect(sP.getTokensStack());
        assertEquals(pos+2,sP.getBlackCrossToken().getCrossPosition());
    }
}