package it.polimi.ingsw.messages.answerMessages;

import it.polimi.ingsw.messages.SerializedMessage;

public class DevMatrixChangeMessage implements SerializedMessage {

    private final int[][] devMatrix;

    public DevMatrixChangeMessage(int[][] simplifiedMarket) {
        devMatrix=simplifiedMarket;
    }

    public int[][] getDevMatrix(){
        return devMatrix;
    }
}
