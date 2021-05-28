package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;
import java.util.Optional;

public class ProductionAction implements SerializedMessage {

    private ArrayList<Integer> cardProductions;
    private ArrayList<String> personalProduction;
    private String personalOut;
    private ArrayList<String> leadOut;

    public ProductionAction(ArrayList<Integer> cardProductions, ArrayList<String> personalProduction, String personalOut, ArrayList<String> leadOut) {
        this.cardProductions = cardProductions;
        this.personalProduction = personalProduction;
        this.personalOut = personalOut;
        this.leadOut = leadOut;
    }

    public ArrayList<Integer> getCardProductions() {
        return cardProductions;
    }

    public ArrayList<String> getPersProdIn() {
        return personalProduction;
    }

    public String getPersProdOut() {
        return personalOut;
    }


    public ArrayList<String> getLeadProdOut() {
        return leadOut;
    }
}