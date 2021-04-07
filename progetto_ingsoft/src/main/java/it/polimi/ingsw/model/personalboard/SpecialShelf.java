package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Resources;

import java.util.ArrayList;

public class SpecialShelf {
    private String resourceType;
    private ArrayList<Resources> specialSlots;
    private boolean spShelfAvailability;
    private boolean spActive;

    public SpecialShelf(String resourceType) {
        this.resourceType=resourceType;
        spActive=true;
    }

    /**
     * @return true if there is enough space in order to store resources in specialshelf
     */
    public boolean isSpShelfAvailability() {
        if(specialSlots.size()<2)
            spShelfAvailability=true;
        else spShelfAvailability=false;
        return spShelfAvailability;
    }

    /**
     * @return true if specialShelf is active in game
     */
    public boolean isSpActive() {
        return spActive;
    }

    /*
    Add resource only in case of special shelf availability and compatibility with resource indicated in the card
     */
    public ArrayList<Resources> addResources(Resources resources){
        if(isSpShelfAvailability()&&resources.getType()==resourceType){
            specialSlots.add(resources);
        }
        return specialSlots;
    }

    /*
    return the resource type indicated in the card
     */
    public String getResourceType() {
        return resourceType;
    }

    /*
    return all resources contained in special shelves
     */
    public ArrayList<Resources> getSpecialSlots() {
        return specialSlots;
    }
}
