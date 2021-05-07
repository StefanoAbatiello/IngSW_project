package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlueMarbleTest {

    /*
    this Test is implemented to check if a BlueMarble in changed correctly
     */
    @Test
    void changeMarbleTest() throws FullSupplyException {
        BlueMarble marble=new BlueMarble();
        Player p=new Player("0");
        marble.changeMarble(p);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.SHIELD);
        assertEquals(resources,p.getResourceSupply().getResources());
    }

}