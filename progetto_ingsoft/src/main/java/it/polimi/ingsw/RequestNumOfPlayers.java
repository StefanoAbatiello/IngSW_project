package it.polimi.ingsw;

public class RequestNumOfPlayers implements SerializedMessage{
    private String message;
    public RequestNumOfPlayers(String s){
        message=s;
    }
}
