package it.polimi.ingsw.messages;

public class NickNameAction implements SerializedMessage {
    String nickname;

    public NickNameAction(String inputLine) {
        nickname=inputLine;
    }

    public String getMessage(){
        return this.nickname;
    }

}
