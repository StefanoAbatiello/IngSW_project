package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrongBoxTest {

    @Test
    void addResourceInStrongbox() {
        StrongBox strongBox=new StrongBox();
        strongBox.addInStrongbox(Resource.COIN);

        assertEquals(1,strongBox.getStrongboxContent().size());
    }

    @Test
    void getValidResource() {
        StrongBox strongBox=new StrongBox();
        strongBox.addInStrongbox(Resource.COIN);
        strongBox.getStrongboxContent().remove(Resource.COIN);

        assertTrue(strongBox.getStrongboxContent().isEmpty());
    }

    @Test
    void getNotValidResource(){
        StrongBox strongBox = new StrongBox();
        strongBox.addInStrongbox(Resource.COIN);
        strongBox.getStrongboxContent().remove(Resource.SHIELD);

        assertEquals(1,strongBox.getStrongboxContent().size());
    }
}