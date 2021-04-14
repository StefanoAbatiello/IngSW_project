package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resources;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseDepotsTest {

    @Test
    void addValidResourceInShelf() throws ResourceNotValidException {
        WarehouseDepots warehouseDepots = new WarehouseDepots();

        assertFalse(warehouseDepots.addinShelf(0, Resources.COIN).getSlots().isEmpty());
    }

    @Test
    void addNotValidResourceInShelf() throws ResourceNotValidException {
        WarehouseDepots warehouseDepots = new WarehouseDepots();
        warehouseDepots.addinShelf(0, Resources.COIN);
        assertThrows(ResourceNotValidException.class, () -> warehouseDepots.addinShelf(0, Resources.SHIELD));
    }

    @Test
    void getValidResource() throws ResourceNotValidException {
        WarehouseDepots warehouseDepots = new WarehouseDepots();
        warehouseDepots.addinShelf(0, Resources.COIN);

        assertEquals(Resources.COIN, warehouseDepots.getResource(Resources.COIN));
    }

    @Test
    void getNotValidResource() throws ResourceNotValidException {
        WarehouseDepots warehouseDepots = new WarehouseDepots();
        warehouseDepots.addinShelf(0, Resources.COIN);

        assertThrows(ResourceNotValidException.class,()->warehouseDepots.getResource(Resources.SHIELD));
    }
}