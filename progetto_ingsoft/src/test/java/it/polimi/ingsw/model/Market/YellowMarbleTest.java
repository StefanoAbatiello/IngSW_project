package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.personalboard.FaithMarker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class YellowMarbleTest {

    /*
    this Test is implemented to check if a YellowMarble in changed correctly
     */
    @Test
    void changeMarbleTest() throws FullSupplyException {
        YellowMarble marble=new YellowMarble();
        ResourceSupply supply=new ResourceSupply();
        marble.changeMarble(new FaithMarker(),new Player(0));
        ArrayList <Resource> resources=new ArrayList<>();
        resources.add(Resource.COIN);
        assertEquals(resources,supply.showSupply());
    }

}