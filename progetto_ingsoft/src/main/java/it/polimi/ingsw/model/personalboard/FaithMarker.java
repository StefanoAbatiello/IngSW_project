package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;


public class FaithMarker {
    private int faithPosition;
    private int faithMarkerID;
    private boolean invaticanzone;




    public int getFaithPosition() {
        return faithPosition;
    }

    public FaithMarker( ) {
        this.faithPosition = 0;
        this.invaticanzone = false;
    }

    /*
        matching faithmarkerID with playerID
         */
    public int setFaithMarkerID(Player player) {
        this.faithMarkerID = player.getPlayerID();
        return faithMarkerID;
    }


    /*
      setting which slot is in popespace & increasing points that will be added in the end of game
      */
    public boolean activePopeSpace() {
        if(faithPosition==8 && Game.isVC1active()==true) {
            for(Player player: Game.getPlayers()) {
                if (player.getFaithMarker().isVaticanZone())
                    player.increaseFaithtrackPoints(2);
            }
            Game.setVC1active(false);
            return Game.isVC1active();
        }
        else if(faithPosition==16 && Game.isVC2active()==true) {
            for(Player player: Game.getPlayers()) {
                if (player.getFaithMarker().isVaticanZone())
                    player.increaseFaithtrackPoints(3);
            }
            Game.setVC2active(false);
            return Game.isVC2active();
        }
        else if(faithPosition==24 && Game.isVC3active()==true) {
            for(Player player: Game.getPlayers()) {
                if (player.getFaithMarker().isVaticanZone())
                    player.increaseFaithtrackPoints(4);
            }
            Game.setVC3active(false);
            return Game.isVC3active();
        }
        else
            return true;
    }


    /*
    setting which slot is in vaticanzone
     */
    public boolean isVaticanZone(){
        if ((getFaithPosition()>=5 && getFaithPosition()<=8) || (getFaithPosition()>=12 && getFaithPosition()<=16)|| (getFaithPosition()>=19 && getFaithPosition()<=24))
            invaticanzone =true;
        else
            invaticanzone = false;
        return invaticanzone;
    }

/*
increasing points according to current faithposition
 */
    public int updatePoints(Player player){
        if (faithPosition==3)
            player.setPoints(1);
        else if(faithPosition==6)
            player.setPoints(2);
        else if(faithPosition==9)
            player.setPoints(4);
        else if(faithPosition==12)
            player.setPoints(6);
        else if(faithPosition==15)
            player.setPoints(9);
        else if(faithPosition==18)
            player.setPoints(12);
        else if(faithPosition==21)
            player.setPoints(16);
        else
            player.setPoints(20);
        return player.getPoints();
    }

    /*
    Flow faithMarker ahead of 1 position & checking if it will be in a popespace
     */
    public int updatePosition(){
        faithPosition= faithPosition +1;
        isVaticanZone();
        activePopeSpace();
        return faithPosition;
    }
}