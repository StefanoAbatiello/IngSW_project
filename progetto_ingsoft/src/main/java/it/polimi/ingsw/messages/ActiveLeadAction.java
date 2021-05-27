package it.polimi.ingsw.messages;

import it.polimi.ingsw.messages.GameMessage;

public class ActiveLeadAction implements GameMessage {

    int lead;

    public ActiveLeadAction(int leadId) {
        this.lead=leadId;
    }

    public int getLead(){
        return this.lead;
    }

}
