package it.polimi.ingsw;

import it.polimi.ingsw.messages.SerializedMessage;

public interface Sender {

    public void send(SerializedMessage message);
}
