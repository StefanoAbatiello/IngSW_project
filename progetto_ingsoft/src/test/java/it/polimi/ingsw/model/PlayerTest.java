package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getFaithtrackpoints() {
        Game game = new Game();
        Player p1= new Player();
        ArrayList<Integer> list=new ArrayList<Integer>();

        game.createNewPlayer(p1);
        for(int i=0;i<8;i++)
            p1.getFaithMarker().updatePosition();

        list.add(2);
        assertEquals(list.get(0),p1.getFaithtrackpoints());
    }
}