package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resources;

public class WarehouseDepots {

    private Shelf shelves[]=new Shelf[3];


    public WarehouseDepots(){
        for(int i=0;i<shelves.length;i++)
            shelves[i]=new Shelf(shelves.length-i);
    }

    /**
     * @return shelf at floor i
     */
    public Shelf addinShelf(int i,Resources resources) throws ResourceNotValidException {
        shelves[i].addResources(resources);
        return shelves[i];

    }

    /**
     * @return all resources that are in Warehouse in every shelf
     */
    public Resources getResource(Resources resources) throws ResourceNotValidException {
        for(int i=0;i< shelves.length;i++){
            for(Resources resources1:shelves[i].getSlots()){
                if(resources==resources1){
                    shelves[i].getSlots().remove(resources1);
                    return resources;
                }
            }
        }
        throw new ResourceNotValidException();
    }

}
