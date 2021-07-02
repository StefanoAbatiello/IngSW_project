package it.polimi.ingsw.messages;

import java.util.ArrayList;

public class WareHouseDisposition implements SerializedMessage {
    private ArrayList<String>[] warehouse;

    public WareHouseDisposition(ArrayList<String>[] newWarehouse) {
        this.warehouse=newWarehouse;
    }

    public ArrayList<String>[] getWarehouse() {
        return warehouse;
    }
}
