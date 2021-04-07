package it.polimi.ingsw.model;


import it.polimi.ingsw.model.personalboard.FaithMarker;

import java.util.Random;

public class Player {
    private int points;
    private int playerID;
    private Random random;
    private FaithMarker faithMarker;
    private int faithtrackpoints;

    public Player() {
        this.points=0;
        this.faithtrackpoints=0;
        this.faithMarker = new FaithMarker();

    }

    public int getPoints() {
        return points;
    }

    public FaithMarker getFaithMarker() {
        return faithMarker;
    }

    public int setPlayerID() {
        this.playerID= random.nextInt(3);
        return playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void increaseFaithtrackPoints(int points) {
        this.faithtrackpoints =this.faithtrackpoints + points;
    }

    public int getFaithtrackpoints() {
        return faithtrackpoints;
    }
}
