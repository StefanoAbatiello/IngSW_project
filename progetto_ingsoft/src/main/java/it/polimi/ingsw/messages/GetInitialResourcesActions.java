package it.polimi.ingsw.messages;

import it.polimi.ingsw.messages.SerializedMessage;

public class GetInitialResourcesActions implements GameMessage {

    private final String message;

    public GetInitialResourcesActions(String s) {
        this.message=s;
    }

    public String getMessage() {
        return message;
    }
}
