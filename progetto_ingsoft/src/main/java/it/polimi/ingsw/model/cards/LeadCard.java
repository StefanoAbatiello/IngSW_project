package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Resource;

public class LeadCard {

    public Resource resource;
    private final String ability;
    private boolean active=false;

    public LeadCard(String ability, Resource resource) {
        this.ability = ability;
        this.resource=resource;
    }

    public boolean activateCard(){
        this.active=true;
        return true;
    }

    public boolean isActive() {
        return active;
    }

    public String getAbility(){
        return this.ability;
    }
}
