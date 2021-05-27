package it.polimi.ingsw.messages;

import it.polimi.ingsw.messages.SerializedMessage;

import java.util.ArrayList;

public class ResourceInSupplyRequest implements SerializedMessage {

    private ArrayList<String> resources;

    public ResourceInSupplyRequest(ArrayList<String> resources) {
        this.resources=resources;
    }

    public ArrayList<String> getResources(){
        return resources;
    }
}
