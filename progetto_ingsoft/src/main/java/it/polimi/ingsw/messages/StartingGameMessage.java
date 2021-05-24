package it.polimi.ingsw.messages;

import java.util.ArrayList;
import java.util.Map;

public class StartingGameMessage implements SerializedMessage{

    private Map<Integer,Boolean> personalCardId;
    private ArrayList<String>[] warehouse;
    private int faithPosition;
    private String[][] market;
    private int[][] devMatrix;
    private String message;

    public StartingGameMessage(Map<Integer,Boolean> personalCardId, ArrayList<String>[] warehouse,
                               int faithPosition, String[][] market, int[][] devMatrix, String message) {
        this.personalCardId = personalCardId;
        this.warehouse = warehouse;
        this.faithPosition = faithPosition;
        this.market = market;
        this.devMatrix = devMatrix;
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public Map<Integer,Boolean> getPersonalCardId() {
        return personalCardId;
    }

    public ArrayList<String>[] getWarehouse() {
        return warehouse;
    }

    public int getFaithPosition() {
        return faithPosition;
    }

    public String[][] getMarket() {
        return market;
    }

    public int[][] getDevMatrix() {
        return devMatrix;
    }
}
