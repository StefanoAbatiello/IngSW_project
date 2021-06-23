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

        try {
            assertEquals(2,shelf.getResources().size());
        } catch (it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException e) {
            e.printStackTrace();
        }

    }

    @Test
    void getSlots() throws ResourceNotValidException {
        Shelf shelf=new Shelf(3);

        shelf.addResources(Resource.SHIELD);
        shelf.addResources(Resource.SHIELD);

        try {
            assertEquals(Resource.SHIELD,shelf.getResources().get(0));
        } catch (it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException e) {
            e.printStackTrace();
        }
        try {
            assertEquals(Resource.SHIELD,shelf.getResources().get(1));
        } catch (it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException e) {
            e.printStackTrace();
        }

    }
}