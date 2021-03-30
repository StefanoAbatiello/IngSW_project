package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FaithMarkerTest {

    @Test
    void getFaithPosition() {

        FaithMarker faithMarker=new FaithMarker();
        assertEquals(0,faithMarker.getFaithPosition());

    }


    @Test
    void setFaithMarkerID() {
    }

    @Test
    void activePopeSpace() {
        Player p1 = new Player();
        ArrayList<Integer> list=new ArrayList<Integer>();

        for(int i=0;i<8;i++)
            p1.getFaithMarker().updatePosition();

        assertFalse(Game.isVC1active());

        for(int i=0;i<8;i++)
            p1.getFaithMarker().updatePosition();
        assertFalse(Game.isVC2active());

        for(int i=0;i<8;i++)
            p1.getFaithMarker().updatePosition();
        assertFalse(Game.isVC3active());

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
    }

    @Test
    void updatePosition() {
        Player p1 = new Player();

        for(int i=0;i<8;i++)
            p1.getFaithMarker().updatePosition();
        assertEquals(8,p1.getFaithMarker());
    }
}