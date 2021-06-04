package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoubleCrossActionTest {

    /*
     this test is implemented to check if token effect works correctly after multiple usages
      */
    @Test
    void UpdatingBlackCrossTest() throws playerLeadsNotEmptyException {
        SinglePlayer sP = new SinglePlayer("USER");
        DoubleCrossAction action=new DoubleCrossAction();
        for(int i=0;i<5;i++)
            action.applyEffect(sP.getTokensStack());
        int pos=BlackCrossToken.getCrossPosition();
        action.applyEffect(sP.getTokensStack());
        assertEquals(pos+2,BlackCrossToken.getCrossPosition());
    }
}