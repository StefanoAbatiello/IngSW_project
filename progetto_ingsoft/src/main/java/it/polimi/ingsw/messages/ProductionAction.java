package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;
import java.util.Optional;

public class ProductionAction implements SerializedMessage {

    private ArrayList<Integer> cardProductions;
    private ArrayList<String> personalProduction;
    private Optional<String> personalOut;
    private Optional<String> leadOut;

    public ProductionAction(ArrayList<Integer> cardProductions, ArrayList<String> personalProduction, String personalOut, String leadOut) {
        this.cardProductions = cardProductions;
        this.personalProduction = personalProduction;
        this.personalOut = Optional.ofNullable(personalOut);
        this.leadOut = Optional.ofNullable(leadOut);
    }

    public ArrayList<Integer> getCardProductions() {
        return cardProductions;
    }

    public ArrayList<String> getPersProdIn() {
        return personalProduction;
    }

    public Optional<String> getPersProdOut() {
        return personalOut;
    }


    public Optional<String> getLeadProdOut() {
        return leadOut;
    }
}