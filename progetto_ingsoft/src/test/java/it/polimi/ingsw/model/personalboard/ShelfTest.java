package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ShelfTest {

    @Test
    void isShelfAvailability() throws ResourceNotValidException {
        Shelf shelf=new Shelf(3);

        shelf.addResources(Resource.SHIELD);
        shelf.addResources(Resource.SHIELD);
        assertTrue(shelf.isShelfAvailability());
    }

    @Test
    void addResources() throws ResourceNotValidException {
        Shelf shelf=new Shelf(3);

        shelf.addResources(Resource.SHIELD);
        shelf.addResources(Resource.SHIELD);

        assertEquals(2,shelf.getSlots().size());

    }

    @Test
    void getSlots() throws ResourceNotValidException {
        Shelf shelf=new Shelf(3);

        shelf.addResources(Resource.SHIELD);
        shelf.addResources(Resource.SHIELD);

        assertEquals(Resource.SHIELD,shelf.getSlots().get(0));
        assertEquals(Resource.SHIELD,shelf.getSlots().get(1));

    }
}