package it.polimi.ingsw.messages;

public class StartingGameMessage implements SerializedMessage {

    private final String message = "The game is started";

    public String getMessage(){
        return message;
    }
}
