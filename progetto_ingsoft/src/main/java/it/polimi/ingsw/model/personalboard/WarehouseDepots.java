package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;

public class WarehouseDepots {

    private Shelf shelves[]=new Shelf[3];


    public WarehouseDepots(){
        for(int i=0;i<shelves.length;i++)
            shelves[i]=new Shelf(shelves.length-i);
    }

    /**
     * @return shelf at floor i
     */
    public Shelf addinShelf(int i, Resource resource) throws ResourceNotValidException {
        shelves[i].addResources(resource);
        return shelves[i];

    }

    /**
     * @return all resources that are in Warehouse in every shelf
     */
    public Resource getResource(Resource resource) throws ResourceNotValidException {
        for(int i=0;i< shelves.length;i++){
            for(Resource resource1 :shelves[i].getSlots()){
                if(resource == resource1){
                    shelves[i].getSlots().remove(resource1);
                    return resource;
                }
            }
        }
        throw new ResourceNotValidException();
    }

}
