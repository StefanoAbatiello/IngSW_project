package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Resources;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpecialShelfTest {

    @Test
    void SpShelfAvailabilityAtStart() {
        SpecialShelf specialShelf=new SpecialShelf(Resources.SHIELD);
        assertTrue(specialShelf.isSpShelfAvailability());
    }

    @Test
    void FullSpShelfAvailability() {
        SpecialShelf specialShelf=new SpecialShelf(Resources.SHIELD);
        specialShelf.addResources(Resources.SHIELD);
        specialShelf.addResources(Resources.SHIELD);
        assertFalse(specialShelf.isSpShelfAvailability());
    }

    @Test
    void isSpActive() {
        SpecialShelf specialShelf=new SpecialShelf(Resources.SHIELD);
        assertTrue(specialShelf.isSpActive());
    }

    @Test
    void addCompatibleResources() {
        SpecialShelf specialShelf=new SpecialShelf(Resources.SHIELD);
        specialShelf.addResources(Resources.SHIELD);

        assertEquals(1,specialShelf.getSpecialSlots().size());
    }

    @Test
    void addIncompatibleResources() {
        SpecialShelf specialShelf=new SpecialShelf(Resources.SHIELD);
        specialShelf.addResources(Resources.COIN);

        assertEquals(0,specialShelf.getSpecialSlots().size());
    }


    @Test
    void getResourceType() {
        SpecialShelf specialShelf=new SpecialShelf(Resources.SHIELD);
        assertEquals(Resources.SHIELD,specialShelf.getResourceType());
    }
    
}