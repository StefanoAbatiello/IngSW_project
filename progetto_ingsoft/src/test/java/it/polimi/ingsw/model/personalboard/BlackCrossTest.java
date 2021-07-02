package it.polimi.ingsw.model.personalboard;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlackCrossTest {

    //test a random position of black cross tocker
    @Test
    void getValidCrossPosition() {
        BlackCross blackCross =new BlackCross();
        blackCross.updateBlackCross(3);
        assertEquals(3, blackCross.getCrossPosition());
    }

    //test initial position of black cross
    @Test
    void getInitialCrossPosition() {
        BlackCross blackCross =new BlackCross();
        assertEquals(0, blackCross.getCrossPosition());
    }
}