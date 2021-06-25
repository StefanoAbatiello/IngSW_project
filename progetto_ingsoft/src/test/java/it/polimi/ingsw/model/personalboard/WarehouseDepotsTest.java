package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseDepotsTest {

    @Test
    void addValidResourceInShelf() throws ResourceNotValidException {
        WarehouseDepots warehouseDepots = new WarehouseDepots();

        assertFalse(warehouseDepots.addInShelf(0, Resource.COIN).getResources().isEmpty());
    }

    //TODO controller test
    /*@Test
    void addNotValidResourceInShelf() throws ResourceNotValidException {
        WarehouseDepots warehouseDepots = new WarehouseDepots();
        warehouseDepots.addInShelf(0, Resource.COIN);
        assertThrows(ResourceNotValidException.class, () -> warehouseDepots.addInShelf(0, Resource.SHIELD));
    }*/

    @Test
    void getValidResource() throws ResourceNotValidException {
        WarehouseDepots warehouseDepots = new WarehouseDepots();
        warehouseDepots.addInShelf(0, Resource.COIN);

        assertEquals(Resource.COIN, warehouseDepots.removeResource(Resource.COIN));
    }

    //TODO controller test
    /*@Test
    void getNotValidResource() throws ResourceNotValidException {
        WarehouseDepots warehouseDepots = new WarehouseDepots();
        warehouseDepots.addInShelf(0, Resource.COIN);

        assertThrows(ResourceNotValidException.class,()->warehouseDepots.showResource(Resource.SHIELD));
    }*/
}