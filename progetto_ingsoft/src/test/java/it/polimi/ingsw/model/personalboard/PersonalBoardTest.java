package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PersonalBoardTest {

    //check if player has the right resources in the strongbox in order to activate actions
    @Test
    void checkResourcesForUsages() {
        Player p1=new Player("cia");
        ArrayList<Resource> resourceArrayList=new ArrayList<>();
        resourceArrayList.add(Resource.COIN);
        resourceArrayList.add(Resource.SERVANT);
        PersonalBoard personalBoard=new PersonalBoard(p1);
        p1.getPersonalBoard().getStrongBox().addInStrongbox(resourceArrayList);
        assertTrue(personalBoard.checkResourcesForUsages(resourceArrayList,p1.getStrongboxResources()));
    }


    //check if resources are removed correctly
    @Test
    void removeResources() {
        Player p1=new Player("cia");
        ArrayList<Resource> resourceArrayList=new ArrayList<>();
        resourceArrayList.add(Resource.COIN);
        resourceArrayList.add(Resource.SERVANT);
        PersonalBoard personalBoard=new PersonalBoard(p1);
        p1.getPersonalBoard().getStrongBox().addInStrongbox(resourceArrayList);
        p1.getPersonalBoard().removeResources(resourceArrayList);
        assertTrue(personalBoard.getStrongBox().getStrongboxContent().isEmpty());
    }
}