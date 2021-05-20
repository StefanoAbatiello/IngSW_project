package it.polimi.ingsw.gameActions;

import it.polimi.ingsw.messages.GameMessage;

public class DiscardLeadAction implements GameMessage {

    int leaderId;

    public DiscardLeadAction(int leaderId) {
        this.leaderId= leaderId;
    }

    public int getLead(){
        return this.leaderId;
    }

}
