package it.polimi.ingsw;

public class RequestNumOfPlayers implements SerializedMessage{
    public String getMessage() {
        return message;
    }

    private String message;
    public RequestNumOfPlayers(String s){
        message=s;
    }
}
