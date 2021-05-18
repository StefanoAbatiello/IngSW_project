package it.polimi.ingsw.gameActions;

import it.polimi.ingsw.messages.GameMessage;

public class DiscardLeadAction implements GameMessage {
    String lead;

    public DiscardLeadAction(String inputLine) {
        lead=inputLine;
    }

    public String getLead(){
        return this.lead;
    }

}
