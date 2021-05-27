package it.polimi.ingsw.messages.answerMessages;

import it.polimi.ingsw.messages.SerializedMessage;

import java.util.ArrayList;

public class WareHouseChangeMessage implements SerializedMessage {

    private final ArrayList<String>[] warehouse;

    public ArrayList<String>[] getWarehouse() {
        return warehouse;
    }

    public WareHouseChangeMessage(ArrayList<String>[] warehouse){
        this.warehouse=warehouse;
    }

}
