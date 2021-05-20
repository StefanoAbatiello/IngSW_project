package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;

public class ProductionAction implements GameMessage{
    ArrayList<Integer> productions;

    public ProductionAction(ArrayList<Integer> inputLine) {
        productions=inputLine;
    }

    public ArrayList<Integer> getProductions(){
        return this.productions;
    }

}
