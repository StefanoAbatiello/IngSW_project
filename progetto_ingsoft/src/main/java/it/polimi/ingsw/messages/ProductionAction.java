package it.polimi.ingsw.messages;

import java.util.ArrayList;

public class ProductionAction implements SerializedMessage {

    private ArrayList<Integer> cardProductions;
    private ArrayList<String> personalProduction;
    private String personalOut;
    private String leadOut;

    public ProductionAction(ArrayList<Integer> cardProductions, ArrayList<String> personalProduction, String personalOut, String leadOut) {
        this.cardProductions = cardProductions;
        this.personalProduction = personalProduction;
        this.personalOut = personalOut;
        this.leadOut = leadOut;
    }

    public ArrayList<Integer> getCardProductions() {
        return cardProductions;
    }

    public ArrayList<String> getPersonalProduction() {
        return personalProduction;
    }

    public String getPersonalOut() {
        return personalOut;
    }

    public String getLeadOut() {
        return leadOut;
    }
}