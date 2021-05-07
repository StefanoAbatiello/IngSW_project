package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CrossShuffleActionTest {

    /*
   this test is implemented to check if token effect modifies BlackCrossPosition correctly
    */
    @Test
    void UpdatingBlackCrossTest() throws playerLeadsNotEmptyException {
        SinglePlayer sP=new SinglePlayer("USER");
        CrossShuffleAction action=new CrossShuffleAction();
        for(int i=0;i<5;i++)
            action.applyEffect(sP.getTokensStack());
        assertEquals(BlackCrossToken.getCrossPosition()+1, action.applyEffect(sP.getTokensStack()));
    }

    /*
    this test is implemented to check if token effect works correctly after multiple usages
     */
    @Test
    void ShufflingTest() throws playerLeadsNotEmptyException {
        SinglePlayer sP = new SinglePlayer("USER");
        sP.getTokensStack().clear();
        sP.getTokensStack().add(new CrossShuffleAction());
        sP.getTokensStack().add(new DoubleCrossAction());
        sP.getTokensStack().add(new DoubleCrossAction());
        sP.getTokensStack().add(new DiscardDevCardAction("YELLOW"));
        sP.getTokensStack().add(new DiscardDevCardAction("GREEN"));
        sP.getTokensStack().add(new DiscardDevCardAction("PURPLE"));
        sP.getTokensStack().add(new DiscardDevCardAction("BLUE"));
        ArrayList<ActionToken> list1 = new ArrayList<>(sP.getTokensStack());
        list1.add(list1.remove(0));
        sP.draw();
        assertNotEquals(list1, sP.getTokensStack());

    }

}