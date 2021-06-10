package it.polimi.ingsw.model.personalboard;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlackCrossTest {

    @Test
    void getValidCrossPosition() {
        BlackCross blackCross =new BlackCross();
        blackCross.updateBlackCross(3);
        assertEquals(3, blackCross.getCrossPosition());
    }

    @Test
    void getInitialCrossPosition() {
        BlackCross blackCross =new BlackCross();
        assertEquals(0, blackCross.getCrossPosition());
    }
}