package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.personalboard.FaithMarker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MarketTest {

    /*
    this Test is implemented to check if method buyResources throws the correct exception in case of negative selector
     */
    @Test
    void negativeSelectorTest(){
        Market market=new Market();
        assertThrows(NotAcceptableSelectorException.class, ()->market.buyResources(-1,new Player(),new FaithMarker()));
    }

    /*
    this Test is implemented to check if method buyResources throws the correct exception in case of a too big selector
    */
    @Test
    void notValidSelectorTest(){
        Market market=new Market();
        assertThrows(NotAcceptableSelectorException.class, ()->market.buyResources(7,new Player(),new FaithMarker()));
    }

    @Test
    void buyinglineTest() throws FullSupplyException, NotAcceptableSelectorException {
        Market market=new Market();
        MarketMarble[][] marketTray= market.getMarketBoard();
        ResourceSupply supply=new ResourceSupply();
        ArrayList <MarketMarble> line=new ArrayList<>();
        ArrayList<Resource> resources=new ArrayList<>();
        FaithMarker fp=new FaithMarker();
        Player p=new Player();
        int selector = 0;
        for(int i = 0; i < 4; i++){
            MarketMarble marble=marketTray[selector][i];
            if(marble.equals(new BlueMarble()))
                resources.add(Resource.SHIELD);
            else if(marble.equals(new GreyMarble()))
                resources.add(Resource.STONE);
            else if(marble.equals(new PurpleMarble()))
                resources.add(Resource.SERVANT);
            else if(marble.equals(new YellowMarble()))
                resources.add(Resource.COIN);
        }

        /*for(Container container : supply.containers)
            container.takeResource();*/
       market.buyResources(selector, p,fp);
        assertEquals(Resource.valueOf(String.valueOf(resources)), supply.showSupply());
    }

/*    @Test
    void buyingcolumnTest() throws FullSupplyException, NotAcceptableSelectorException {
        Market market=new Market();
        MarketMarble[][] marketTray= market.getMarketBoard();
        ResourceSupply supply=new ResourceSupply();
        ArrayList <Resource> resources=new ArrayList <>();
        FaithMarker fp=new FaithMarker();
        Player p=new Player();
        int selector = ;
        for(int i = 0; i < 4; i++){
            Resource res=marketTray[selector][i].changeMarble(fp, p);
            if(res!=Resource.NONE)
                resources.add(res);
        }
        for(Container container : supply.containers)
            container.takeResource();
        market.buyResources(selector, p,fp);
        assertEquals(resources, supply.showSupply());
    }*/

    @Test
    void insertExtMarbleInLineTest()throws FullSupplyException, NotAcceptableSelectorException{
        Market market=new Market();
        MarketMarble[][] marketTray= market.getMarketBoard();
        MarketMarble extMarble= market.getExtMarble();
        ResourceSupply supply=new ResourceSupply();
        int selector = 2;
        MarketMarble[] line= new MarketMarble[4];
        MarketMarble[] newLine= new MarketMarble[4];
            for (int i = 1; i < 4; i++)
                line[i - 1] = marketTray[selector][i];
            line[3] = extMarble;
            market.buyResources(selector, new Player(), new FaithMarker());
            for (int i = 0; i < 4; i++)
                newLine[i] = marketTray[selector][i];
            assertArrayEquals(line, newLine);
    }

    @Test
    void insertExtMarbleInColumnTest()throws FullSupplyException, NotAcceptableSelectorException{
        Market market=new Market();
        MarketMarble[][] marketTray= market.getMarketBoard();
        MarketMarble extMarble= market.getExtMarble();
        ResourceSupply supply=new ResourceSupply();
        int selector = 6;
        MarketMarble[] column= new MarketMarble[3];
        MarketMarble[] newColumn= new MarketMarble[3];
        for(int i=1; i<3; i++)
            column[i-1]=marketTray[i][selector-3];
        column[2]=extMarble;
        market.buyResources(selector,new Player(),new FaithMarker());
        for(int i=0; i<3; i++)
            newColumn[i]=marketTray[i][selector-3];
        assertArrayEquals(column,newColumn);
    }

}