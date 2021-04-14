package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Resources;

import java.util.ArrayList;

public class SpecialShelf {
    private Resources resourceType;
    private ArrayList<Resources> specialSlots;
    private boolean spShelfAvailability;
    private boolean spActive;

    public SpecialShelf(Resources resourceType) {
        this.resourceType=resourceType;
        spActive=true;
        specialSlots=new ArrayList<>();
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

    /**
     * @param resources is the resource that will be added
     * @return new dispositon of special slots only in case of special shelf availability and compatibility with resource indicated in the card
     */
    public ArrayList<Resources> addResources(Resources resources){
        if(isSpShelfAvailability()&&resources==resourceType){
            specialSlots.add(resources);
        }
        return specialSlots;
    }

    /**
     * @return the resource type indicated in the card
     */
    public Resources getResourceType() {
        return resourceType;
    }

    /**
     * @return all resources contained in special shelves
     */
    public ArrayList<Resources> getSpecialSlots() {
        return specialSlots;
    }
}
