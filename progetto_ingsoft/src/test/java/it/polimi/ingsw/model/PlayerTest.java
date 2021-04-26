package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getFaithtrackpoints() {
        MultiPlayer.resetGame();
        MultiPlayer.createNewPlayer(new Player(1));
        for(int i=0;i<8;i++)
            MultiPlayer.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();

        assertEquals(2, MultiPlayer.getPlayers().get(0).getFaithtrackPoints());
    }

    @Test
    void doBasicProduction() throws ResourceNotValidException {
        Player player=new Player(1);
        player.getPersonalBoard().getWarehouseDepots().addinShelf(0, Resource.SERVANT);
        player.getPersonalBoard().getStrongBox().addInStrongbox(Resource.SHIELD);
        player.setPotentialresource("COIN");

        assertEquals(Resource.COIN,player.doBasicProduction(Resource.SERVANT, Resource.SHIELD));
    }


    @Test
    void doNotValidBasicProduction1() throws ResourceNotValidException {
        Player player=new Player(1);
        player.getPersonalBoard().getWarehouseDepots().addinShelf(0, Resource.SERVANT);
        player.getPersonalBoard().getStrongBox().addInStrongbox(Resource.SHIELD);
        player.setPotentialresource("COIN");
        player.doBasicProduction(Resource.STONE, Resource.SHIELD);

        //assertEquals(1,player.getCoderr());
    }

    @Test
    void doNotValidBasicProduction2() throws ResourceNotValidException {
        Player player=new Player(1);
        player.getPersonalBoard().getWarehouseDepots().addinShelf(0, Resource.SERVANT);
        player.getPersonalBoard().getStrongBox().addInStrongbox(Resource.SHIELD);
        player.setPotentialresource("COIN");
        player.doBasicProduction(Resource.STONE, Resource.COIN);

        //assertEquals(2,player.getCoderr());
    }
}