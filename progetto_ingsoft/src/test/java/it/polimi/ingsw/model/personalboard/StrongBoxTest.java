package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StrongBoxTest {

    //check if resources are added correctly in strongbox
    @Test
    void addResourceInStrongbox() {
        StrongBox strongBox=new StrongBox();
        ArrayList<Resource> res= new ArrayList<>();
        res.add(Resource.COIN);
        res.add(Resource.COIN);
        strongBox.addInStrongbox(res);
        assertEquals(2,strongBox.getStrongboxContent().size());
    }

    @Test
    void getValidResource() {
        StrongBox strongBox=new StrongBox();
        ArrayList<Resource> res= new ArrayList<>();
        res.add(Resource.COIN);
        res.add(Resource.COIN);
        strongBox.addInStrongbox(res);
        strongBox.getStrongboxContent().remove(Resource.COIN);
        strongBox.getStrongboxContent().remove(Resource.COIN);
        assertTrue(strongBox.getStrongboxContent().isEmpty());
    }

    @Test
    void getNotValidResource(){
        StrongBox strongBox = new StrongBox();
        ArrayList<Resource> res= new ArrayList<>();
        res.add(Resource.COIN);
        res.add(Resource.COIN);
        strongBox.addInStrongbox(res);
        strongBox.getStrongboxContent().remove(Resource.SHIELD);

        assertEquals(2,strongBox.getStrongboxContent().size());
    }
}