package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;


public class FaithMarker {
    private int faithPosition;
    private int faithMarkerID;
    private boolean invaticanzone;
    private int points;




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
     * @throws NullPointerException
     * */
    public int setFaithMarkerID(Player player) {
        if(player==null)
            throw new NullPointerException("Insert a validate player");
        this.faithMarkerID = player.getPlayerID();
        return faithMarkerID;
    }


    /**
     * @return the current status of indicated Vatican zone after being activated.
     * Furthermore set which slot is in pope space & increase points that will be added in the end of game
     */
    public boolean activePopeSpace() {
        if(faithPosition==8 && Game.isVC1active()==true) {

            Game.getPlayers().stream().filter(player -> player.getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(2));
            Game.setVC1active(false);
            return Game.isVC1active();
        }
        else if(faithPosition==16 && Game.isVC2active()==true) {
            Game.getPlayers().stream().filter(player -> player.getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(3));
            Game.setVC2active(false);
            return Game.isVC2active();
        }
        else if(faithPosition==24 && Game.isVC3active()==true) {
            Game.getPlayers().stream().filter(player -> player.getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(4));
            Game.setVC3active(false);
            return Game.isVC3active();
        }
        else
            return true;
    }

    public boolean activePopeSpace2(){
        Game.getPlayers().stream().filter(player -> player.getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(2));
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
    public int setPoints(){
        if (faithPosition==3)
            points=1;
        else if(faithPosition==6)
            points=2;
        else if(faithPosition==9)
            points=4;
        else if(faithPosition==12)
            points=6;
        else if(faithPosition==15)
            points=9;
        else if(faithPosition==18)
            points=12;
        else if(faithPosition==21)
            points=16;
        else if(faithPosition==24)
            points=20;
        return points;
    }

    /**
     * @return points of faith track
     */
    public int getPoints() {
        return points;
    }

    /**
     * @return following position & checking if it will be in a pope space or in a vatican zone
     */
    public int updatePosition(){
        faithPosition= faithPosition +1;
        setPoints();
        isVaticanZone();
        activePopeSpace();
        return faithPosition;
    }
}