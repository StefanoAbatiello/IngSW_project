package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedMarbleTest {

    @Test
    //this method check if the marble is changed correctly
    void changeMarbleTest(){
        RedMarble marble=new RedMarble();
        assertTrue(marble.changeMarble(new Player("0")));
    }

}