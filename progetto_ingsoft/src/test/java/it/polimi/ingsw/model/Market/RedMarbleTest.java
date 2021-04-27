package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.personalboard.FaithMarker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedMarbleTest {

    @Test
    void changeMarbleTest() throws FullSupplyException {
        RedMarble marble=new RedMarble();
        assertTrue(marble.changeMarble(new Player(0)));
    }

}