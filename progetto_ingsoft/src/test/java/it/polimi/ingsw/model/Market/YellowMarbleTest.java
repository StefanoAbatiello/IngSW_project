package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class YellowMarbleTest {

    /*
    this Test is implemented to check if a YellowMarble in changed correctly
     */
    @Test
    void changeMarbleTest() throws FullSupplyException {
        YellowMarble marble=new YellowMarble();
        Player p=new Player("0");
        marble.changeMarble(p);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.COIN);
        assertEquals(resources,p.getResourceSupply().getResources());
    }

}