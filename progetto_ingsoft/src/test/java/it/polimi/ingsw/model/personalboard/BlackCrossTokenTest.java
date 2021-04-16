package it.polimi.ingsw.model.personalboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlackCrossTokenTest {

    @Test
    void getValidCrossPosition() {
        BlackCrossToken blackCrossToken=new BlackCrossToken();
        blackCrossToken.UpdateBlackCross(3);
        assertEquals(3,blackCrossToken.getCrossPosition());
    }

    @Test
    void getInitialCrossPosition() {
        BlackCrossToken blackCrossToken=new BlackCrossToken();
        assertEquals(0,blackCrossToken.getCrossPosition());
    }

    @Test
    void getNotValidCrossPosition(){
        BlackCrossToken blackCrossToken=new BlackCrossToken();
        blackCrossToken.UpdateBlackCross(25);
        assertEquals(0,blackCrossToken.getCrossPosition());
    }
}