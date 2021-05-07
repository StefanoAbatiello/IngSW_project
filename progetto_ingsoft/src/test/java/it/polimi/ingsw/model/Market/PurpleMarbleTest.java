package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PurpleMarbleTest {

    /*
    this Test is implemented to check if a PurpleMarble in changed correctly
     */
    @Test
    void changeMarbleTest() throws FullSupplyException {
        PurpleMarble marble=new PurpleMarble();
        Player p = new Player("0");
        marble.changeMarble(p);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.SERVANT);
        assertEquals(resources,p.getResourceSupply().getResources());
    }

}