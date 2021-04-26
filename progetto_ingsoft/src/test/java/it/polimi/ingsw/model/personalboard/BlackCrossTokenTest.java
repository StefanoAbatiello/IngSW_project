package it.polimi.ingsw.model.personalboard;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlackCrossTokenTest {

    @Test
    void getValidCrossPosition() {
        BlackCrossToken blackCrossToken=new BlackCrossToken();
        blackCrossToken.updateBlackCross(3);
        assertEquals(3,blackCrossToken.getCrossPosition());
    }

    @Test
    void getInitialCrossPosition() {
        BlackCrossToken blackCrossToken=new BlackCrossToken();
        assertEquals(0,blackCrossToken.getCrossPosition());
    }
}