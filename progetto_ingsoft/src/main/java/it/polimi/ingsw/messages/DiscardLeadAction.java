package it.polimi.ingsw.messages;

public class DiscardLeadAction implements SerializedMessage {

    int leaderId;

    public DiscardLeadAction(int leaderId) {
        this.leaderId= leaderId;
    }

    public int getLead(){
        return this.leaderId;
    }

}
