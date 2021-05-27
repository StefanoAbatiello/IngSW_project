package it.polimi.ingsw.messages.answerMessages;

import it.polimi.ingsw.messages.SerializedMessage;

public class StrongboxChangeMessage implements SerializedMessage {

    private final int[] strongbox;

    public StrongboxChangeMessage(int[] strongbox){
        this.strongbox=strongbox;
    }

    public int[] getStrongbox(){
        return strongbox;
    }
}
