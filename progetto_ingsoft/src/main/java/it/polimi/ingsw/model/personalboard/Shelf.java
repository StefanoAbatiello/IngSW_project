package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.ResourceCreator;
import it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException;

import java.util.ArrayList;

public class Shelf implements ResourceCreator {

    /**
     * this is the structure where Resources are stored
     */
    private final ArrayList<Resource> slots;

    /**
     * this attribute indicates the type of Resource stored on this Shelf
     */
    private Resource resourceType;

    /**
     * this is the max number of Resources that can be stored on this Shelf
     */
    private final int maxSize;

    public Shelf(int maxSize) {
        this.maxSize = maxSize;
        this.slots=new ArrayList<>();
    }

    /**
     * @return return an ArrayList containing the Resources stored on this Shelf
     */
    public ArrayList<Resource> getSlots() {
        return slots;
    }

    /**
     * @return true if there is enough space in order to store resources in Shelf
     */
    public boolean isShelfAvailability() {
        return slots.size() <= maxSize;
    }

    /**
     * @return the type of Resources stored on this Shelf
     */
    public Resource getResourceType() {
        return resourceType;
    }

    /**
     * @param resource is what we want add in shelf
     * @return slot after adding resource. Adding is allowed only in case of shelf availability and compatibility with existing resources type.
     */
    public ArrayList<Resource> addResources(Resource resource){
        if(slots.isEmpty()){
            resourceType=resource;
        }
        slots.add(resource);
        return slots;
    }

    /**
     * @return all resources contained on this shelf
     */
    @Override
    public ArrayList<Resource> getResources() throws NoSuchRequirementException {
        return slots;
    }

}
