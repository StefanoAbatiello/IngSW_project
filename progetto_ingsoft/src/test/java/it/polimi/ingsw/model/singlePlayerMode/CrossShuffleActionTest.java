package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CrossShuffleActionTest {

    /*
   this test is implemented to check if token effect works correctly
    */
    @Test
    void UpdatingBlackCrossTest(){
        CrossShuffleAction action=new CrossShuffleAction();
        new BlackCrossToken();
        for(int i=0;i<5;i++)
            action.applyEffect();
        assertEquals(BlackCrossToken.getCrossPosition()+1, action.applyEffect());
    }

    /*
    this test is implemented to check if token effect works correctly after multiple usages
     */
    @Test
    void ShufflingTest() {
        SinglePlayer.getTokensStack().add(new CrossShuffleAction());
        SinglePlayer.getTokensStack().add(new DoubleCrossAction());
        SinglePlayer.getTokensStack().add(new DoubleCrossAction());
        SinglePlayer.getTokensStack().add(new DiscardDevCardAction("YELLOW"));
        SinglePlayer.getTokensStack().add(new DiscardDevCardAction("GREEN"));
        SinglePlayer.getTokensStack().add(new DiscardDevCardAction("PURPLE"));
        SinglePlayer.getTokensStack().add(new DiscardDevCardAction("BLUE"));
        ArrayList<ActionToken> list1 = new ArrayList<>(SinglePlayer.getTokensStack());
        list1.add(list1.remove(0));
        SinglePlayer.getTokensStack().get(0).applyEffect();
        assertNotEquals(list1, SinglePlayer.getTokensStack());

    }

}