package it.polimi.ingsw.messages;

import java.util.ArrayList;

public class ResourceInSupplyAction implements SerializedMessage {

    private ArrayList<String>[] warehouse;

    public ResourceInSupplyAction(ArrayList<String>[] warehouse) {
        this.warehouse=warehouse;
    }

    public ArrayList<String>[] getWarehouse(){
        return  warehouse;
    }

}
