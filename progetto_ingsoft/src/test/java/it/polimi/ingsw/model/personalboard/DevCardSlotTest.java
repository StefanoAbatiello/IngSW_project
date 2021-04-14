package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.InvalidSlotException;
import it.polimi.ingsw.model.cards.DevCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DevCardSlotTest {

    @Test
    void overlapinInvalidSlot() throws InvalidSlotException {
        DevCardSlot devCardSlot=new DevCardSlot();
        DevCard devCard=new DevCard();
        assertThrows(InvalidSlotException.class,()->devCardSlot.overlap(devCard,3));
    }

    @Test
    void OverlapInValidSlot() throws InvalidSlotException {
        DevCardSlot devCardSlot=new DevCardSlot();
        DevCard devCard=new DevCard();
        devCardSlot.overlap(devCard,2);

        assertTrue(devCardSlot.getSlot()[2].get(0).isActive());
    }

    @Test
    void getVictoryPoints() throws InvalidSlotException {
        DevCardSlot devCardSlot=new DevCardSlot();
        DevCard devCard=new DevCard();
        devCardSlot.overlap(devCard,2);

        assertEquals(1,devCardSlot.getVictoryPoints());
    }

    @Test
    void getActiveCards() throws InvalidSlotException {
        DevCardSlot devCardSlot=new DevCardSlot();
        DevCard devCard=new DevCard();
        devCardSlot.overlap(devCard,2);

        assertEquals(1,devCardSlot.getActiveCards().size());
    }
}