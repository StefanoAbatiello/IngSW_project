package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Resource;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResourceSupplyTest {

    /*
    this Test is implemented to check if method putResourceInContainer
    put the correct resource in a container
     */
    @Test
    void putResourceInEmptyContainerTest() throws FullSupplyException {
        ResourceSupply supply=new ResourceSupply();
        supply.putResourceInContainer(Resource.SHIELD);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.SHIELD);
        assertEquals(resources, supply.getResources());
    }

    /*
     this Test is implemented to check if method putResourceInContainer
     throws the Exception in case of a full ResourceSupply
      */
    @Test
    void putResourceInFullContainerTest(){
        ResourceSupply supply=new ResourceSupply();
        for(Container container : supply.getContainers())
            container.fillContainer(Resource.SHIELD);
        assertThrows(FullSupplyException.class, ()-> supply.putResourceInContainer(Resource.SHIELD));
    }

    /*
    this Test is implemented to check if this structure works correctly is case of multiple usages
     */
    @Test
    void fillingSupplyTest() throws FullSupplyException {
        ResourceSupply supply=new ResourceSupply();
        supply.putResourceInContainer(Resource.COIN);
        supply.putResourceInContainer(Resource.SERVANT);
        supply.putResourceInContainer(Resource.SHIELD);
        supply.putResourceInContainer(Resource.STONE);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.COIN);
        resources.add(Resource.SERVANT);
        resources.add(Resource.SHIELD);
        resources.add(Resource.STONE);
        assertEquals(resources,supply.getResources());
    }

    /*
   this Test is implemented to check if this structure works correctly after has been emptied
    */
    @Test
    void takingSomeResourceTest() throws FullSupplyException{
        ResourceSupply supply=new ResourceSupply();
        supply.putResourceInContainer(Resource.COIN);
        supply.putResourceInContainer(Resource.SERVANT);
        supply.putResourceInContainer(Resource.SHIELD);
        supply.putResourceInContainer(Resource.STONE);
        supply.getResources();
        ArrayList<Resource> resources=new ArrayList<>();
        assertEquals(resources,supply.getResources());
    }

}