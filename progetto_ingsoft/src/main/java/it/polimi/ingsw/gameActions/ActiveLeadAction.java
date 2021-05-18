package it.polimi.ingsw.gameActions;

import it.polimi.ingsw.messages.GameMessage;

public class ActiveLeadAction implements GameMessage {
    String lead;

    public ActiveLeadAction(String inputLine) {
        lead=inputLine;
    }

    public String getLead(){
        return this.lead;
    }

}
