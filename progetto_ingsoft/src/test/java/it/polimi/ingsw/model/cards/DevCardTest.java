package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DevCardTest {

    @Test
    void useProductionExceptionTest() throws ResourceNotValidException {
        Player player = new Player(4);
        ArrayList<Resource> testArray1 = new ArrayList<>();
        ArrayList<Resource> testArray2 = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            player.getPersonalBoard().getStrongBox().addInStrongbox(Resource.COIN);
        }
        for (int i = 0; i < 4; i++) {
            testArray2.add(Resource.SERVANT);
        }
        DevCard card = new DevCard(2, "GREEN", 3, testArray2, testArray2, testArray2, 1);

        assertThrows(ResourceNotValidException.class,()->card.useProduction(player));
    }


    @Test
    void useProductionWorkingRight() throws ResourceNotValidException {
        Player player = new Player(4);
        ArrayList<Resource> testArray2 = new ArrayList<>();
        ArrayList<Resource> testArray3 = new ArrayList<>();


        for (int i = 0; i < 4; i++) {
            player.getPersonalBoard().getStrongBox().addInStrongbox(Resource.SERVANT);
            testArray3.add(Resource.SERVANT);
        }
        for (int i = 0; i < 4; i++) {
            testArray2.add(Resource.SHIELD);
        }
        DevCard card = new DevCard(2, "GREEN", 3, testArray2, testArray3, testArray2, 1);

        card.useProduction(player);

        assertEquals(testArray2, player.getPersonalBoard().getStrongBox().getStrongboxContent());
    }

}