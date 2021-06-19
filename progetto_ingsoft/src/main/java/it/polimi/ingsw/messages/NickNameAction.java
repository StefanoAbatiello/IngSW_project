package it.polimi.ingsw.messages;

import java.io.Serializable;

public class NickNameAction implements SerializedMessage {
    String nickname;

    public NickNameAction(String inputLine) {
        nickname=inputLine;
    }

    public String getMessage(){
        return this.nickname;
    }

}
