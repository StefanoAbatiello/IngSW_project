package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FaithMarkerTest {

    @Test
    void getFaithPositionAtCreation() {

        FaithMarker faithMarker=new FaithMarker();
        assertEquals(0,faithMarker.getFaithPosition());

    }

    @Test
    void getFaithPositionInGame() {

        FaithMarker faithMarker=new FaithMarker();
        faithMarker.updatePosition();
        assertEquals(1,faithMarker.getFaithPosition());

    }


    @Test
    void setFaithMarkerID() {
        Player player=new Player(1);
        assertEquals(player.getPlayerID(),player.getPersonalBoard().getFaithMarker().setFaithMarkerID(player));
    }

    @Test
    void CheckDifferentActivePopeSpace() {
        Player p1 = new Player(1);

        for(int i=0;i<8;i++)
            p1.getPersonalBoard().getFaithMarker().updatePosition();

        assertFalse(Game.isVC1active());

        for(int i=0;i<8;i++)
            p1.getPersonalBoard().getFaithMarker().updatePosition();
        assertFalse(Game.isVC2active());

        for(int i=0;i<8;i++)
            p1.getPersonalBoard().getFaithMarker().updatePosition();
        assertFalse(Game.isVC3active());

    }
    @Test
    void activePopeSpaceWithDifferentPlayers(){

        new Game();
        for(Player p : Game.getPlayers())
            Game.removePlayer(p);

        Game.createNewPlayer(new Player(1));
        Game.createNewPlayer(new Player(2));

        for(int i=0;i<8;i++)
            Game.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();

        assertEquals(2,Game.getPlayers().get(0).getFaithtrackpoints());
        assertEquals(0,Game.getPlayers().get(1).getFaithtrackpoints());

    }


    @Test
    void isVaticanZone() {
        FaithMarker faithMarker= new FaithMarker();
        assertFalse(faithMarker.isVaticanZone());

        for(int i=0;i<8;i++)
            faithMarker.updatePosition();
        assertTrue(faithMarker.isVaticanZone());
    }

    @Test
    void updatePoints() {
        Game.createNewPlayer(new Player(1));
        Game.createNewPlayer(new Player(2));

        for(int i=0;i<8;i++)
            Game.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();

        assertEquals(2,Game.getPlayers().get(0).getPersonalBoard().getFaithMarker().getPoints());

    }

    @Test
    void updatePosition() {
        Player p1 = new Player(1);

        for(int i=0;i<8;i++)
            p1.getPersonalBoard().getFaithMarker().updatePosition();


        assertEquals(8,p1.getPersonalBoard().getFaithMarker().getFaithPosition());
    }
}