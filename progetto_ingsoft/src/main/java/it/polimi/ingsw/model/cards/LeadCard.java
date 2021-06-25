package it.polimi.ingsw.model.cards;
import it.polimi.ingsw.model.Points;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.ResourceCreator;
import it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException;

import java.util.ArrayList;
import java.util.HashMap;

public class LeadCard  implements Cards, Points,ResourceCreator {

    /**
     * This attribute represents the card id
     */
    private final int id;

    /**
     * This attribute represents the card's points
     */
    private final int points;

    /**
     * This attribute represents type of LeadAbility of the card
     */
    private final LeadAbility ability;

    /**
     * This attribute represents the list of resources required to active the card
     */
    private final HashMap<Integer,Resource> resourceRequired;

    /**
     * This attribute represents the list of development card required to active the card
     */
    private final HashMap<Integer,ArrayList<String>> devCardRequired;

    /**
     * This attribute represents if the card is active or not
     */
    private boolean active = false;


    public LeadCard(int id, int points, LeadAbility ability, HashMap<Integer,Resource> resourceRequired) {
        this.id = id;
        this.points = points;
        this.ability = ability;
        this.resourceRequired = resourceRequired;
        this.devCardRequired = new HashMap<>();

    }

    public LeadCard(int id, int points, HashMap<Integer,ArrayList<String>> devCardRequired, LeadAbility ability ) {
        this.id = id;
        this.points = points;
        this.ability = ability;
        this.resourceRequired = new HashMap<>();
        this.devCardRequired = devCardRequired;
    }

    public int getId() {
        return id;
    }

    @Override
    public int getPoints(){
        return points;
    }

    public LeadAbility getAbility() {
        return ability;
    }

    /**
     * @return an arraylist of the resources required to active the card
     */
    @Override
    public ArrayList<Resource> getResources() {
        ArrayList<Resource> resReq = new ArrayList<>();
        if(!resourceRequired.isEmpty()) {
            int key = resourceRequired.keySet().iterator().next();
            for (int i = 0; i < key; i++)
                resReq.add(resourceRequired.get(key));
        }
        return resReq;

    }

    /**
     * @return an hashmap of the development card required to active the card
     */
    public HashMap<Integer,ArrayList<String>> getDevCardRequired() {
            return devCardRequired;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(){
        this.active = true;
    }


    @Override
    public Cards getCard() {
        return this;
    }




}
