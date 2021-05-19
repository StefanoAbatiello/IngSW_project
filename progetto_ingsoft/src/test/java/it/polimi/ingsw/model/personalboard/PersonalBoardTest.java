package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonalBoardTest {

    @Test
    //TODO da controllare
    void getValidResource() throws ResourceNotValidException {
        PersonalBoard personalBoard=new PersonalBoard();
        ArrayList<Resource> resourcesArrayList=new ArrayList<>();
        ArrayList<Resource> res= new ArrayList<>();
        res.add(Resource.COIN);
        res.add(Resource.COIN);
        personalBoard.getStrongBox().addInStrongbox(res);
        resourcesArrayList.add(Resource.COIN);
        personalBoard.removeResources(resourcesArrayList);

        assertTrue(personalBoard.getStrongBox().getStrongboxContent().isEmpty());
    }
}