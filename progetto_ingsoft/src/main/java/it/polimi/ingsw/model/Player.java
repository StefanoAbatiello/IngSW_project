package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Market.Resource;
import it.polimi.ingsw.model.cards.Cards;
import it.polimi.ingsw.model.cards.LeadCard;

public class Player {
    public Resource[] WhiteMarbleAbility=new Resource[2];
    private LeadCard[] cards;

    public boolean isWhiteMarbleAbility() {
    return false;
    }

    public LeadCard[] getLeadCards() {
        return cards;
    }
}
