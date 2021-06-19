package it.polimi.ingsw.messages;

public class WaitingRoomAction implements SerializedMessage{
    String message;

    public WaitingRoomAction(String inputLine) {
        message=inputLine;
    }

    public String getMessage(){
        return this.message;
    }
}
