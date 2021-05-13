package it.polimi.ingsw.messages;

import it.polimi.ingsw.PreGameMessage;

public class NickNameAction extends PreGameMessage {
    String nickname;

    public NickNameAction(String inputLine) {
        nickname=inputLine;
    }

    public String getMessage(){
        return this.nickname;
    }

}
