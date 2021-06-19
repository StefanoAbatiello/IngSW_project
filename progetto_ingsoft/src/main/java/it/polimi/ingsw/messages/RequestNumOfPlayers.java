package it.polimi.ingsw.messages;

import it.polimi.ingsw.messages.SerializedMessage;

import java.io.Serializable;

public class RequestNumOfPlayers implements SerializedMessage {

    private String message;

    public RequestNumOfPlayers(String s){
        message=s;
    }

    public String getMessage() {
        return message;
    }
}
