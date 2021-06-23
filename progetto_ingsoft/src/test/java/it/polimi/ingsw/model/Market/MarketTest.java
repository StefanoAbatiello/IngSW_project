package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.exceptions.NotAcceptableSelectorException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarketTest {

    //TODO controller test
    /*
    this Test is implemented to check if method buyResources throws the correct exception in case of negative selector

    @Test
    void negativeSelectorTest(){
        Market market=new Market();
        assertThrows(NotAcceptableSelectorException.class, ()->market.buyResources(-1,new Player("0")));
    }*/

    /*
    this Test is implemented to check if method buyResources throws the correct exception in case of a too big selector
    */

    //TODO controller test
    /*@Test
    void notValidSelectorTest(){
        Market market=new Market();
        assertThrows(NotAcceptableSelectorException.class, ()->market.buyResources(7,new Player("0")));
    }*/

    /*
    this Test verifies if selecting a row(0<=selector<=2) the resources put in ResourceSupply are the expected ones
     */

    @Test
    void buyingRowTest() throws FullSupplyException, NotAcceptableSelectorException {
        Market market = new Market();
        MarketMarble[][] marketTray = market.getMarketBoard();
        ArrayList<Resource> resources = new ArrayList<>();
        Player p = new Player("0");
        int selector = 0;
        for (int i=0; i<4; i++) {
            MarketMarble marble = marketTray[selector][i];
            if (marble.getColor().equals("BLUE"))
                resources.add(Resource.SHIELD);
            else if (marble.getColor().equals("GREY"))
                resources.add(Resource.STONE);
            else if (marble.getColor().equals("PURPLE"))
                resources.add(Resource.SERVANT);
            else if (marble.getColor().equals("YELLOW"))
                resources.add(Resource.COIN);
        }
        market.buyResources(selector, p);
        try {
            assertEquals(resources, p.getResourceSupply().getResources());
        } catch (it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException e) {
            e.printStackTrace();
        }
    }

    /*
   this Test verifies if selecting a column(3<=selector<=6) the resources put in ResourceSupply are the expected ones
    */
    @Test
    void buyingColumnTest() throws FullSupplyException, NotAcceptableSelectorException {
        Market market = new Market();
        MarketMarble[][] marketTray = market.getMarketBoard();
        ArrayList<Resource> resources = new ArrayList<>();
        Player p = new Player("0");
        int selector = 4;
        for (int i=0; i<3; i++) {
            MarketMarble marble = marketTray[i][selector-3];
            if (marble.getColor().equals("BLUE"))
                resources.add(Resource.SHIELD);
            else if (marble.getColor().equals("GREY"))
                resources.add(Resource.STONE);
            else if (marble.getColor().equals("PURPLE"))
                resources.add(Resource.SERVANT);
            else if (marble.getColor().equals("YELLOW"))
                resources.add(Resource.COIN);
        }
        market.buyResources(selector, p);
        try {
            assertEquals(resources, p.getResourceSupply().getResources());
        } catch (it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException e) {
            e.printStackTrace();
        }
    }

    /*
   this Test verifies if selecting a row(0<selector<=2) the marketTray is modified correctly
    */
    @Test
    void insertExtMarbleInRowTest()throws FullSupplyException, NotAcceptableSelectorException{
        Market market=new Market();
        MarketMarble[][] marketTray= market.getMarketBoard();
        MarketMarble extMarble= market.getExtMarble();
        int selector = 2;
        MarketMarble[] line= new MarketMarble[4];
        MarketMarble[] newLine= new MarketMarble[4];
            for (int i = 1; i < 4; i++)
                line[i - 1] = marketTray[selector][i];
            line[3] = extMarble;
            market.buyResources(selector, new Player("0"));
            for (int i = 0; i < 4; i++)
                newLine[i] = marketTray[selector][i];
            assertArrayEquals(line, newLine);
    }

    /*
   this Test verifies if selecting a column(3<=selector<=6) the marketTray is modified correctly
    */
    @Test
    void insertExtMarbleInColumnTest()throws FullSupplyException, NotAcceptableSelectorException{
        Market market=new Market();
        MarketMarble[][] marketTray= market.getMarketBoard();
        MarketMarble extMarble= market.getExtMarble();
        int selector = 6;
        MarketMarble[] column= new MarketMarble[3];
        MarketMarble[] newColumn= new MarketMarble[3];
        for(int i=1; i<3; i++)
            column[i-1]=marketTray[i][selector-3];
        column[2]=extMarble;
        market.buyResources(selector,new Player("0"));
        for(int i=0; i<3; i++)
            newColumn[i]=marketTray[i][selector-3];
        assertArrayEquals(column,newColumn);
    }

}