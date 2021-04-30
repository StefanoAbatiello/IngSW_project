package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.ResourceCreator;

import java.util.ArrayList;

public class WarehouseDepots implements ResourceCreator {

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
     * return a specific resource and remove it from player's warehouse
     */
    public Resource getResource(Resource resource) throws ResourceNotValidException {
        for(int i=0;i< shelves.length;i++){
            for(Resource resource1 :shelves[i].getResources()){
                if(resource == resource1){
                    shelves[i].getResources().remove(resource1);
                    getResources().remove(resource);
                    return resource;
                }
            }
        }
        throw new ResourceNotValidException();
    }

    @Override
    /**
     * @return all resources that are in Warehouse in every shelf
     */
    public ArrayList<Resource> getResources(){
        ArrayList<Resource> allres=new ArrayList<>();
        for(int i=0;i< shelves.length;i++) {
            for (Resource resources1 : shelves[i].getResources()) {
                    allres.add(resources1);
            }
        }
        return allres;
    }

    //TODO implement method
    @Override
    public Resource getResource() {
        return null;
    }
}
