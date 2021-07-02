package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class WhiteMarbleTest {


    @Test
    //this test checks that without whitemarble abilities active the white marble does not generates resources
    void oneAbilityActive(){
        WhiteMarble marble=new WhiteMarble();
        Player p=new Player("0");
        p.getWhiteMarbleAbility().add(Resource.COIN);
        marble.changeMarble(p);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.COIN);

        assertEquals(resources,p.getResourceSupply().getResources());
    }

    @Test
    //this test checks if with one whitemarble ability active the white generates the correct resource
    void noAbilityActive(){
        WhiteMarble marble=new WhiteMarble();
        Player p=new Player("0");
        marble.changeMarble(p);
        ArrayList<Resource> resources=new ArrayList<>();
        assertEquals(resources,p.getResourceSupply().getResources());

    }

    @Test
    //this test checks if with two whitemarble abilities active the white marble generates a choosable resource
    void twoAbilityActive(){
        WhiteMarble marble=new WhiteMarble();
        Player p=new Player("0");
        p.getWhiteMarbleAbility().add(Resource.COIN);
        p.getWhiteMarbleAbility().add(Resource.SHIELD);
        marble.changeMarble(p);
        ArrayList<Resource> resources=new ArrayList<>();
        resources.add(Resource.CHOOSABLE);

        assertEquals(resources,p.getResourceSupply().getResources());
    }
}