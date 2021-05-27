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

    public Shelf[] getShelves() {
        return shelves;
    }

    /**
     * @return shelf at floor i
     */
    public Shelf addinShelf(int i, ArrayList<Resource> resources) {
        shelves[i].getSlots().addAll(resources);
        return shelves[i];

    }

    public Shelf addinShelf(int i, Resource resource) {
        shelves[i].getSlots().add(resource);
        return shelves[i];

    }

    /**
     * return a specific resource and remove it from player's warehouse
     */
    public Resource getResource(Resource resource) {
        for (Shelf shelf : shelves) {
            for (Resource resource1 : shelf.getResources()) {
                if (resource == resource1) {
                    shelf.getResources().remove(resource1);
                    getResources().remove(resource);
                    return resource;
                }
            }
        }
        //TODO controllo eccezione, potrebbe essere inutile poichè nel controller prima vedo se ho risorse e poi rimuovo
        //throw new ResourceNotValidException();
        return null;
    }

    @Override
    /**
     * @return all resources that are in Warehouse in every shelf
     */
    public ArrayList<Resource> getResources(){
        ArrayList<Resource> allres=new ArrayList<>();
        for (Shelf shelf : shelves) {
            allres.addAll(shelf.getResources());
        }
        return allres;
    }

}
