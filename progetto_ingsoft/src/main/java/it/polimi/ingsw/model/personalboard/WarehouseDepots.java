package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Resources;

import java.util.ArrayList;

public class WarehouseDepots {
    private ArrayList<Resources> allResources;
    private Shelf shelves[]=new Shelf[3];

    public WarehouseDepots(){
        for(int i=0;i<shelves.length;i++)
            shelves[i]=new Shelf(shelves.length-i);
    }

    /**
     * @return shelf at floor i
     */
    public Shelf chooseShelf(int i){
        return shelves[i];
    }

    /**
     * @return all resources that are in Warehouse in every shelf
     */
    public ArrayList<Resources> getAllResources() {
        for(int i=0;i< shelves.length;i++){
            allResources.addAll(shelves[i].getSlots());
            if(shelves[i]==null) {
                throw new NullPointerException("Do this operation after fill shelves");
            }
        }
        return allResources;
    }

}
