package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.DevDeckMatrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SinglePlayerTest {

    /*
    this Test is implemented to check if the method return the correct value in case of an empty line in DevDeckMatrix
     */
    @Test
    void checkEmptyLineInMatrixTest() {
        new SinglePlayer();
        new DevDeckMatrix();
        for(int i=0;i<12;i++) {
            SinglePlayer.removeTokenCard("BLUE");
        }
        assertTrue(SinglePlayer.checkEmptyLineInMatrix());
    }

    /*
    this Test is implemented to check if the method return the correct value if there other yellow cards
     */
    @Test
    void checkLinesInMatrixTest() {
        DevDeckMatrix matrix = new DevDeckMatrix();
        for (int i=0; i<5; i++)
            SinglePlayer.removeTokenCard("BLUE");
        assertFalse(SinglePlayer.checkEmptyLineInMatrix());
    }

    @Test
    void checkBlackCrossPosition() {
    }

    @Test
    void draw() {
    }

    @Test
    void removeTokenCard() {
    }
}