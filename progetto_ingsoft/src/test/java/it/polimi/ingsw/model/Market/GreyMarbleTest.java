package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GreyMarbleTest {

    /*
    this Test is implemented to check if a GreyMarble is transformed in a correctly
    */
    @Test
    void changeMarbleTest() throws FullSupplyException {
        GreyMarble marble=new GreyMarble();
        Player p=new Player("0");
        marble.changeMarble(p);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.STONE);
        try {
            assertEquals(resources,p.getResourceSupply().getResources());
        } catch (it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException e) {
            e.printStackTrace();
        }
    }

}