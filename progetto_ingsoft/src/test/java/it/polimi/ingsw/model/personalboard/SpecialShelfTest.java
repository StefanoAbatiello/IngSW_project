package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpecialShelfTest {

    //check if not special shelfes are available at the start of game
    @Test
    void SpShelfAvailabilityAtStart() {
        SpecialShelf specialShelf=new SpecialShelf(Resource.SHIELD);
        assertTrue(specialShelf.isSpShelfAvailability());
    }

    //check if two resources fill special shelf
    @Test
    void FullSpShelfAvailability() {
        SpecialShelf specialShelf=new SpecialShelf(Resource.SHIELD);
        specialShelf.addResources(Resource.SHIELD);
        specialShelf.addResources(Resource.SHIELD);
        assertFalse(specialShelf.isSpShelfAvailability());
    }

    //check if special shelf is active
    @Test
    void isSpActive() {
        SpecialShelf specialShelf=new SpecialShelf(Resource.SHIELD);
        assertTrue(specialShelf.isSpActive());
    }

    //check if compatible resources are added in the same shelf
    @Test
    void addCompatibleResources() {
        SpecialShelf specialShelf=new SpecialShelf(Resource.SHIELD);
        specialShelf.addResources(Resource.SHIELD);

        assertEquals(1,specialShelf.getSpecialSlots().size());
    }

    //check if incompatible resources are added in the same shelf
    @Test
    void addIncompatibleResources() {
        SpecialShelf specialShelf=new SpecialShelf(Resource.SHIELD);
        specialShelf.addResources(Resource.COIN);

        assertEquals(0,specialShelf.getSpecialSlots().size());
    }

    //check the resource type of specialShelf
    @Test
    void getResourceType() {
        SpecialShelf specialShelf=new SpecialShelf(Resource.SHIELD);
        assertEquals(Resource.SHIELD,specialShelf.getResourceType());
    }
    
}