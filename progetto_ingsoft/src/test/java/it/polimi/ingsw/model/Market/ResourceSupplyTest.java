package it.polimi.ingsw.model.Market;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class ResourceSupplyTest {

    /*
    this Test is implemented to check if method putResourceInContainer
    put the correct resource in a container
     */
    @Test
    void putResourceInEmptyContainerTest() throws FullSupplyException {
        ResourceSupply supply = new ResourceSupply();
        supply.putResourceInContainer(Resource.SHIELD);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.SHIELD);
        assertEquals(resources, supply.showSupply());
    }

    /*
     this Test is implemented to check if method putResourceInContainer
     throws the Exception in case of a full ResourceSupply
      */
    @Test
    void putResourceInFullContainerTest() throws FullSupplyException {
        ResourceSupply supply = new ResourceSupply();
        for(Container container : ResourceSupply.containers)
            container.fillContainer(Resource.SHIELD);
        assertThrows(FullSupplyException.class, ()-> supply.putResourceInContainer(Resource.SHIELD));
    }

    /*
    this Test is implemented to check if this structure works correctly is case of multiple usages
     */
    @Test
    void fillingSupplyTest() throws FullSupplyException {
        ResourceSupply supply = new ResourceSupply();
        ResourceSupply.putResourceInContainer(Resource.COIN);
        ResourceSupply.putResourceInContainer(Resource.SERVANT);
        ResourceSupply.putResourceInContainer(Resource.SHIELD);
        ResourceSupply.putResourceInContainer(Resource.STONE);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.COIN);
        resources.add(Resource.SERVANT);
        resources.add(Resource.SHIELD);
        resources.add(Resource.STONE);
        assertEquals(resources,ResourceSupply.showSupply());
    }

    /*
   this Test is implemented to check if this structure works correctly is case of multiple usages
    */
    @Test
    void takingSomeResourceTest() throws FullSupplyException {
        ResourceSupply supply=new ResourceSupply();
        ResourceSupply.putResourceInContainer(Resource.COIN);
        ResourceSupply.putResourceInContainer(Resource.SERVANT);
        ResourceSupply.putResourceInContainer(Resource.SHIELD);
        ResourceSupply.putResourceInContainer(Resource.STONE);
        ResourceSupply.containers[0].takeResource();
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.SERVANT);
        resources.add(Resource.SHIELD);
        resources.add(Resource.STONE);
        assertEquals(resources,ResourceSupply.showSupply());
    }

}