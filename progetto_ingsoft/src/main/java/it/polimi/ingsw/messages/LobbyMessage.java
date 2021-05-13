package it.polimi.ingsw.messages;

public class LobbyMessage implements SerializedMessage {

    private String message;

    public LobbyMessage(String s) {
        message=s;
    }

    public String getMessage() {
        return message;
    }
}
