package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoubleCrossActionTest {

    /*
     this test is implemented to check if token effect works correctly after multiple usages
      */
    @Test
    void UpdatingBlackCrossTest() {
        DoubleCrossAction action=new DoubleCrossAction();
        new BlackCrossToken();
        for(int i=0;i<5;i++)
            action.applyEffect();
        assertEquals(BlackCrossToken.getCrossPosition()+2,action.applyEffect());
    }
}