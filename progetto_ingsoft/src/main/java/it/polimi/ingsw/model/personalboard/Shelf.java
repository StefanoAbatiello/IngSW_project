package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.ResourceCreator;

import java.util.ArrayList;

public class Shelf implements ResourceCreator {

    private ArrayList<Resource> slots;
    private Resource resourceType;
    private boolean shelfAvailability ;
    private int maxSize;

    public Shelf(int maxSize) {
        this.maxSize = maxSize;
        this.slots=new ArrayList<Resource>();
    }


    public ArrayList<Resource> getSlots() {
        return slots;
    }

    /**
     * @return true if there is enough space in order to store resources in specialshelf
     */
    public boolean isShelfAvailability() {
        if(slots.size()<=maxSize )
            shelfAvailability=true;
        else
            shelfAvailability=false;
        return shelfAvailability;
    }


    public Resource getResourceType() {
        return resourceType;
    }

    /**
     * @param resource is what we want add in shelf
     * @return slot after adding resource. Adding is allow only in case of shelf availability and compatibility with existing resources type.
     */
    public ArrayList<Resource> addResources(Resource resource){

        if(slots.isEmpty()){
            resourceType=resource;

        }
        slots.add(resource);

        return slots;
    }

    /**
     * @return all resources contained in shelf
     */
    @Override
    public ArrayList<Resource> getResources() {
        return slots;
    }


}
