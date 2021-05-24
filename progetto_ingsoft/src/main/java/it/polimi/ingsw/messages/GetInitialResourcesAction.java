package it.polimi.ingsw.messages;

import it.polimi.ingsw.messages.SerializedMessage;

public class GetInitialResourcesAction implements GameMessage {

    private final String message;

    public GetInitialResourcesAction(String s) {
        this.message=s;
    }

    public String getMessage() {
        return message;
    }
}
