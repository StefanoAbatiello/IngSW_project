package it.polimi.ingsw;

public class GameMessage implements SerializedMessage{

    private String message;

    public GameMessage(String s) {
        message=s;
    }

    public String getMessage() {
        return message;
    }
}
