package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.InvalidSlotException;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.cards.DevCard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DevCardSlotTest {

    //TODO test controller
    /*@Test
    void overlapInInvalidSlot()  {
        DevCardSlot devCardSlot=new DevCardSlot();
        ArrayList<Resource> array= new ArrayList<>();
        array.add(Resource.STONE);
        DevCard card = new DevCard(2,4,"PURPLE",4,array,array,array,3);
        assertThrows(InvalidSlotException.class,()->devCardSlot.overlap(card,3));
    }*/

    @Test
    void OverlapInValidSlot() throws InvalidSlotException {
        DevCardSlot devCardSlot=new DevCardSlot();
        ArrayList<Resource> array= new ArrayList<>();
        array.add(Resource.STONE);
        DevCard card = new DevCard(3,4,"PURPLE",1,array,array,array,3);
        devCardSlot.overlap(card,2);

        assertTrue(devCardSlot.getSlot()[2].get(0).isActive());
    }

    @Test
    void getVictoryPoints() throws InvalidSlotException {
        DevCardSlot devCardSlot=new DevCardSlot();
        ArrayList<Resource> array= new ArrayList<>();
        array.add(Resource.STONE);
        DevCard card = new DevCard(5,4,"PURPLE",1,array,array,array,3);
        devCardSlot.overlap(card,2);
        assertEquals(4,devCardSlot.getPoints());
    }

    @Test
    void getActiveCards() throws InvalidSlotException {
        DevCardSlot devCardSlot=new DevCardSlot();
        ArrayList<Resource> array= new ArrayList<>();
        array.add(Resource.STONE);
        DevCard card = new DevCard(2,4,"PURPLE",1,array,array,array,3);
        devCardSlot.overlap(card,2);

        assertEquals(1,devCardSlot.getActiveCards().size());
    }
}