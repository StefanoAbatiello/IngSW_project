package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Points;


public class FaithMarker implements Points {
    private int faithPosition;
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
    @Override
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
        return faithPosition;
    }
}