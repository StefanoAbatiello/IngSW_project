package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Resource;
import java.util.ArrayList;

public class SpecialShelf {

    /**
     * this attribute stands for the type of Resources stored in this Special Shelf
     */
    private final Resource resourceType;

    /**
     * this is the structure where Resources are stored
     */
    private final ArrayList<Resource> specialSlots;

    /**
     * this boolean is true when the player can use this Special Shelf
     */
    private boolean spActive;

    public SpecialShelf(Resource resourceType) {
        this.resourceType=resourceType;
        spActive=true;
        specialSlots=new ArrayList<>();
    }

    /**
     * @return true if there is enough space in order to store resources in Special Shelf
     */
    public boolean isSpShelfAvailability() {
        return specialSlots.size() < 2;
    }

    /**
     * @return true if specialShelf is active in game
     */
    public boolean isSpActive() {
        return spActive;
    }

    /**
     * @param resource is the resource that will be added
     * @return new disposition of special slots only in case of special shelf availability and compatibility with resource indicated in the card
     */
    public ArrayList<Resource> addResources(Resource resource){
        if(isSpShelfAvailability() && resource == resourceType){
            specialSlots.add(resource);
        }
        return specialSlots;
    }

    /**
     * @return the resource type indicated in the card
     */
    public Resource getResourceType() {
        return resourceType;
    }

    /**
     * @return all resources contained in special shelves
     */
    public ArrayList<Resource> getSpecialSlots() {
        return specialSlots;
    }
}
