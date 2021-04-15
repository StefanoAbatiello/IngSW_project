package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.personalboard.FaithMarker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GreyMarbleTest {

    /*
    this Test is implemented to check if a GreyMarble is transformed in a correctly
    */
    @Test
    void changeMarbleTest() throws FullSupplyException {
        GreyMarble marble=new GreyMarble();
        ResourceSupply supply=new ResourceSupply();
        marble.changeMarble(new FaithMarker(),new Player(0));
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.STONE);
        assertEquals(resources,supply.showSupply());
    }

}