package it.polimi.ingsw.model.cards;
import it.polimi.ingsw.exceptions.WrongAbilityInCardException;
import it.polimi.ingsw.model.Points;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.ResourceCreator;
import it.polimi.ingsw.model.cards.cardExceptions.InvalidActiveParameterException;
import it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException;

import java.util.ArrayList;
import java.util.HashMap;

public class LeadCard  implements Cards, Points, ResourceCreator {
    private final int points;
    private final String ability;
    private final Resource resource;
    private final HashMap<Integer,Resource> resourceRequired;
    private final HashMap<Integer,ArrayList<String>> devCardRequired;
    private boolean active = false;
    private boolean inGame = false;


    public LeadCard(int points, String ability, Resource resource, HashMap<Integer,Resource> resourceRequired, HashMap<Integer,ArrayList<String>> devCardRequired) {
        this.points = points;
        this.ability = ability;
        this.resource = resource;
        this.resourceRequired = resourceRequired;
        this.devCardRequired = devCardRequired;

    }

    @Override
    public int getPoints(){
        return points;
    }
    public String getAbility() {
        return ability;
    }

    //TODO check exception
    @Override
    public ArrayList<Resource> getResources() throws NoSuchRequirementException {
        if(resourceRequired.isEmpty())
            throw new NoSuchRequirementException("This card does not have a resource requirement");
        ArrayList<Resource> resReq = new ArrayList<>();
        int key =resourceRequired.keySet().iterator().next();
        for(int i=0; i<key; i++)
            resReq.add(resourceRequired.get(key));
        return resReq;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    public HashMap<Integer,ArrayList<String>> getDevCardRequired() {
        return devCardRequired;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) throws InvalidActiveParameterException {
        if (!active)
            throw new InvalidActiveParameterException("Invalid Input: active card cannot be deactivated");

        this.active = true;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame){
        this.inGame = inGame;
    }

    @Override
    public Cards getCard() {
        return this;
    }

    //TODO tests
    public LeadAbility getAbilityFromCard() throws WrongAbilityInCardException {
        switch (this.getAbility()) {
            case "WHITEMARBLE":
                return new LeadAbilityWhiteMarble();
            case "PRODUCTION":
                return new LeadAbilityProduction();
            case "SHELF":
                return new LeadAbilityShelf();
            case "DISCOUNT":
                return new LeadAbilityDiscount();

        }
        //TODO stop the game? error in the card construction
        throw new WrongAbilityInCardException("Error in the card construction");
    }

}
