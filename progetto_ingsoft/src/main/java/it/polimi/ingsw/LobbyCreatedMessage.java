package it.polimi.ingsw;

public class LobbyCreatedMessage implements SerializedMessage {
    public String getMessage() {
        return message;
    }

    public String message;

    public LobbyCreatedMessage(String s) {
        message=s;
    }
}
