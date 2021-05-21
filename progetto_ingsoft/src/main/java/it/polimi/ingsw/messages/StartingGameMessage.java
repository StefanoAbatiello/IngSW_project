package it.polimi.ingsw.messages;

import java.util.ArrayList;

public class StartingGameMessage implements SerializedMessage{

    private ArrayList<Integer> personalCardId;
    private ArrayList<String>[] warehouse;
    private int faithPosition;
    private String[][] market;
    private int[][] devMatrix;
    private String message;

    public StartingGameMessage(ArrayList<Integer> personalCardId, ArrayList<String>[] warehouse,
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

    public ArrayList<Integer> getPersonalCardId() {
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
