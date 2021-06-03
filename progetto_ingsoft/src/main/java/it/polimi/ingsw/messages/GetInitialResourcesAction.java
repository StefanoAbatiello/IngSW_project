package it.polimi.ingsw.messages;

public class GetInitialResourcesAction implements SerializedMessage {

    private final String message;

    public GetInitialResourcesAction(String s) {
        this.message=s;
    }

    public String getMessage() {
        return message;
    }
}
