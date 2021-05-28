package it.polimi.ingsw.messages;

import java.util.ArrayList;
import java.util.Map;

public class ReconnectionMessage implements SerializedMessage {

    private Map<Integer,Boolean> cardsId;
    private ArrayList<String>[] warehouse;
    private int faithposition;
    private String [][] simplifiedMarket;
    private int [][] devMatrix;
    private int[] strongbox;

    public ReconnectionMessage(Map<Integer,Boolean> cardsId, ArrayList<String>[] warehouse, int faithPosition, String[][] simplifiedMarket, int[][] devMatrix, int[] strongbox) {
        this.cardsId=cardsId;
        this.warehouse=warehouse;
        this.faithposition=faithPosition;
        this.simplifiedMarket=simplifiedMarket;
        this.devMatrix=devMatrix;
        this.strongbox=strongbox;
    }

    public Map<Integer,Boolean> getCardsId() {
        return cardsId;
    }

    public ArrayList<String>[] getWarehouse() {
        return warehouse;
    }

    public int getFaithposition() {
        return faithposition;
    }

    public String[][] getSimplifiedMarket() {
        return simplifiedMarket;
    }

    public int[][] getDevMatrix() {
        return devMatrix;
    }

    public int[] getStrongbox() {return strongbox;
    }
}
