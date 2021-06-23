package it.polimi.ingsw.messages;

public class ErrorMessage implements SerializedMessage {
    String nickname;

    public ErrorMessage(String inputLine) {
        nickname=inputLine;
    }

    public String getMessage(){
        return nickname;
    }
}
