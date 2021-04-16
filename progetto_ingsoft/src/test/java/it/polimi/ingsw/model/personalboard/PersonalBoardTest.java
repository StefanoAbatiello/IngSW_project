package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonalBoardTest {

    @Test
    void getValidResource() throws ResourceNotValidException {
        PersonalBoard personalBoard=new PersonalBoard();
        ArrayList<Resource> resourcesArrayList=new ArrayList<>();
        personalBoard.getStrongBox().addInStrongbox(Resource.COIN);
        resourcesArrayList.add(Resource.COIN);
        personalBoard.removeResources(resourcesArrayList);

        assertTrue(personalBoard.getStrongBox().getStrongboxContent().isEmpty());
    }
}