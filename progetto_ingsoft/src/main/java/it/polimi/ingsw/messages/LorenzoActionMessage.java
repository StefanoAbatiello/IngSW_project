package it.polimi.ingsw.messages;


public class LorenzoActionMessage implements SerializedMessage {

    int position;

    public LorenzoActionMessage(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
