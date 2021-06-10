package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Market.Market;

import it.polimi.ingsw.model.cards.DevCard;
import it.polimi.ingsw.model.cards.DevDeck;
import it.polimi.ingsw.model.cards.DevDeckMatrix;

import java.util.ArrayList;

public abstract class  Game {
    /**
     * This attribute represent the first vatican zone and its state
     */
    private boolean VC1active;
    private boolean VC2active;
    private boolean VC3active;

    public boolean setVC1active(boolean VC1active) {
        this.VC1active = VC1active;
        return VC1active;
    }

    public boolean setVC2active(boolean VC2active) {
        this.VC2active = VC2active;
        return VC2active;
    }

    public boolean setVC3active(boolean VC3active) {
        this.VC3active = VC3active;
        return VC3active;
    }

    public boolean isVC1active() {
        return VC1active;
    }

    public boolean isVC2active() {
        return VC2active;
    }

    public boolean isVC3active() {
        return VC3active;
    }

    abstract boolean activePopeSpace(Player player);

    public abstract Market getMarket();

    public abstract ArrayList<Player> getPlayers();

    public abstract DevDeck getDevDeck();

    public abstract DevDeckMatrix getDevDeckMatrix();

    public String draw() {
        return "";
    }

    public int[][] getSimplifiedDevMatrix(){
        int[][] devMatrix = new int[4][3];
        DevCard[][] matrix = getDevDeckMatrix().getUpperDevCardsOnTable();
        System.out.println("mi sono salvato le carte acquistabili");
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 3; k++) {
                devMatrix[j][k] = matrix[j][k].getId();
            }
        }
        return devMatrix;
    }

    public String[][] getSimplifiedMarket(){
        String[][] market=new String[3][4];
        for(int i=0;i<3;i++)
            for(int j=0; j<4; j++){
                market[i][j]=getMarket().getMarketBoard()[i][j].getColor();
            }
        return market;
    }

    public abstract void pointsGiveAway(Player player, int pointsGiven);
}
