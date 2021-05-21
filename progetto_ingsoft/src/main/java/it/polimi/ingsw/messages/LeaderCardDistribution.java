package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.cards.LeadCard;

import java.util.ArrayList;

public class LeaderCardDistribution implements SerializedMessage {

    private ArrayList<Integer> leadCardsId;
    private String message;

    public LeaderCardDistribution(ArrayList<Integer> leadCards, String message) {
        this.leadCardsId=leadCards;
        this.message=message;
    }

    public ArrayList<Integer> getLeadCardsId(){
        return this.leadCardsId;
    }
}
