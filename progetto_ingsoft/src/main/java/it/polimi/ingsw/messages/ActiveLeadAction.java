package it.polimi.ingsw.messages;

public class ActiveLeadAction implements SerializedMessage {

    int lead;

    public ActiveLeadAction(int leadId) {
        this.lead=leadId;
    }

    public int getLead(){
        return this.lead;
    }

}
