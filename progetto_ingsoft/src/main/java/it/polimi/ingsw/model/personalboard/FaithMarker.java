package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.InvalidInputException;
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

    /**
     * @param player is the owner of faith marker
     * @return faith marker ID after having associated player ID
     * @throws InvalidInputException
     * */
    public int setFaithMarkerID(Player player) throws InvalidInputException {
        if(player==null)
            throw new InvalidInputException();
        this.faithMarkerID = player.getPlayerID();
        return faithMarkerID;
    }


    /**
     * @return the current status of indicated Vatican zone after being activated.
     * Furthermore set which slot is in pope space & increase points that will be added in the end of game
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


    /**
     * @return true if faith marker position is in vatican zone
     */
    public boolean isVaticanZone(){
        if ((getFaithPosition()>=5 && getFaithPosition()<=8) || (getFaithPosition()>=12 && getFaithPosition()<=16)|| (getFaithPosition()>=19 && getFaithPosition()<=24))
            invaticanzone =true;
        else
            invaticanzone = false;
        return invaticanzone;
    }

    /**
     * @return points according to current faith position
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

    /**
     * @return following position & checking if it will be in a pope space or in a vatican zone
     */
    public int updatePosition(){
        faithPosition= faithPosition +1;
        isVaticanZone();
        activePopeSpace();
        return faithPosition;
    }
}