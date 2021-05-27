package it.polimi.ingsw.messages.answerMessages;

import it.polimi.ingsw.messages.SerializedMessage;

public class NumOfPlayersAnswer implements SerializedMessage {

    private int playersNum;

    public NumOfPlayersAnswer(int playersNum) {
        this.playersNum = playersNum;
    }

    public int getPlayersNum() {
        return playersNum;
    }

}
