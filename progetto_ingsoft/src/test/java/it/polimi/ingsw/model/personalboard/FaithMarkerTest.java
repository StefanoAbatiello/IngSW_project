package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

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
    void ResetFaithPoints(){
        FaithMarker faithMarker=new FaithMarker();
        faithMarker.updatePosition();
        faithMarker.updatePosition();
        assertEquals(2,faithMarker.getFaithPosition());
        faithMarker.reset();
        assertEquals(0,faithMarker.getFaithPosition());
    }


    @Test
    void CheckDifferentActivePopeSpace() throws playerLeadsNotEmptyException, IOException, ParseException {
        ArrayList<String> user= new ArrayList<>();
        user.add("a");
        user.add("b");
        user.add("c");
        user.add("d");
        MultiPlayer game=new MultiPlayer(user,4);

        for(int i=0;i<8;i++)
            game.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();
        game.activePopeSpace(game.getPlayers().get(0));
        assertFalse(game.isVC1active());

        for(int i=0;i<8;i++)
            game.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();
        game.activePopeSpace(game.getPlayers().get(0));
        assertFalse(game.isVC2active());

        for(int i=0;i<8;i++)
            game.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();
        game.activePopeSpace(game.getPlayers().get(0));
        assertFalse(game.isVC3active());

    }

    @Test
    void activePopeSpaceWithDifferentPlayers() throws playerLeadsNotEmptyException, IOException, ParseException {

        ArrayList<String> user= new ArrayList<>();
        user.add("a");
        user.add("b");
        user.add("c");
        user.add("d");
        MultiPlayer game=new MultiPlayer(user,4);

        for(int i=0;i<8;i++)
            game.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();

        game.activePopeSpace(game.getPlayers().get(0));
        assertEquals(2,game.getPlayers().get(0).getFaithtrackPoints());
        assertEquals(0,game.getPlayers().get(1).getFaithtrackPoints());

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
    void updatePoints() throws playerLeadsNotEmptyException, IOException, ParseException  {
        ArrayList<String> user= new ArrayList<>();
        user.add("a");
        user.add("b");
        user.add("c");
        user.add("d");
        MultiPlayer game=new MultiPlayer (user,4);

        for(int i=0;i<8;i++)
            game.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();

        assertEquals(2,game.getPlayers().get(0).getPersonalBoard().getFaithMarker().getPoints());

    }

    @Test
    void updatePosition() {
        Player p1 = new Player("1");

        for(int i=0;i<8;i++)
            p1.getPersonalBoard().getFaithMarker().updatePosition();


        assertEquals(8,p1.getPersonalBoard().getFaithMarker().getFaithPosition());
    }
}