package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.MultiPlayer;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FaithMarkerTest {

    //test the initial position of faithmarker
    @Test
    void getFaithPositionAtCreation() {

        FaithMarker faithMarker=new FaithMarker();
        assertEquals(0,faithMarker.getFaithPosition());

    }

    //test faith position when faithmarker is updated
    @Test
    void getFaithPositionInGame() {

        FaithMarker faithMarker=new FaithMarker();
        faithMarker.updatePosition();
        assertEquals(1,faithMarker.getFaithPosition());

    }

    //test faithpoints at the recreation of a new game
    @Test
    void ResetFaithPoints(){
        FaithMarker faithMarker=new FaithMarker();
        faithMarker.updatePosition();
        faithMarker.updatePosition();
        assertEquals(2,faithMarker.getFaithPosition());
        faithMarker.reset();
        assertEquals(0,faithMarker.getFaithPosition());
    }


    //check if all different pope space are activated in right way
    @Test
    void CheckDifferentActivePopeSpace() throws playerLeadsNotEmptyException, IOException, ParseException {
        ArrayList<String> user= new ArrayList<>();
        user.add("a");
        user.add("b");
        user.add("c");
        user.add("d");
        MultiPlayer game=new MultiPlayer(user);

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

    //check if different player that access at the same pope space does not active its twice
    @Test
    void activePopeSpaceWithDifferentPlayers() throws playerLeadsNotEmptyException, IOException, ParseException {

        ArrayList<String> user= new ArrayList<>();
        user.add("a");
        user.add("b");
        user.add("c");
        user.add("d");
        MultiPlayer game=new MultiPlayer(user);

        for(int i=0;i<8;i++)
            game.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();

        game.activePopeSpace(game.getPlayers().get(0));
        assertEquals(2,game.getPlayers().get(0).getPopeMeetingPoints());
        assertEquals(0,game.getPlayers().get(1).getPopeMeetingPoints());

    }


    //check if faithmarker active vatican zone when it is in the aspected position
    @Test
    void isVaticanZone() {
        FaithMarker faithMarker= new FaithMarker();
        assertFalse(faithMarker.isVaticanZone(1));

        for(int i=0;i<8;i++)
            faithMarker.updatePosition();
        assertTrue(faithMarker.isVaticanZone(1));
    }

    //check if all players have points incremented when they are in a vatican zone
    @Test
    void updatePoints() throws playerLeadsNotEmptyException, IOException, ParseException  {
        ArrayList<String> user= new ArrayList<>();
        user.add("a");
        user.add("b");
        user.add("c");
        user.add("d");
        MultiPlayer game=new MultiPlayer (user);

        for(int i=0;i<8;i++)
            game.getPlayers().get(0).getPersonalBoard().getFaithMarker().updatePosition();

        assertEquals(2,game.getPlayers().get(0).getPersonalBoard().getFaithMarker().getPoints());

    }

}