package it.polimi.ingsw.messages.answerMessages;

import it.polimi.ingsw.messages.SerializedMessage;

public class FaithPositionChangeMessage implements SerializedMessage {

    private final int faithPosition;

    public int getFaithPosition() {
        return faithPosition;
    }

    public FaithPositionChangeMessage(int faithPosition){
        this.faithPosition=faithPosition;

    }
}
