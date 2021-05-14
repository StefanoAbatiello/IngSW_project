package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;

public class ProductionAction {
    ArrayList<Resource> productions;

    public ProductionAction(ArrayList<Resource> inputLine) {
        productions=inputLine;
    }

    public ArrayList<Resource> getProductions(){
        return this.productions;
    }

}
