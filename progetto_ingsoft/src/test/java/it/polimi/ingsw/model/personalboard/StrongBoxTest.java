package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resources;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrongBoxTest {

    @Test
    void addResourceInStrongbox() {
        StrongBox strongBox=new StrongBox();
        strongBox.addInStrongbox(Resources.COIN);

        assertEquals(1,strongBox.getStrongboxContent().size());
    }

    @Test
    void getValidResource() throws ResourceNotValidException {
        StrongBox strongBox=new StrongBox();
        strongBox.addInStrongbox(Resources.COIN);
        strongBox.getResource(Resources.COIN);

        assertTrue(strongBox.getStrongboxContent().isEmpty());
    }

    @Test
    void getNotValidResource() throws ResourceNotValidException {
        StrongBox strongBox = new StrongBox();
        strongBox.addInStrongbox(Resources.COIN);


        assertThrows(ResourceNotValidException.class,()->strongBox.getResource(Resources.SHIELD));
    }
}