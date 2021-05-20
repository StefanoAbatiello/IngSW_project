package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PersonalBoard {
    private final FaithMarker faithMarker;
    private final WarehouseDepots warehouseDepots;
    private final StrongBox strongBox;
    private final DevCardSlot devCardSlot;
    private ArrayList<Optional<SpecialShelf>> specialShelves;

    /**
     * create personal board with its component
     */
    public PersonalBoard(){
        //System.out.println("creo il faithmarker"); [Debug]
        faithMarker=new FaithMarker();
        //System.out.println("faithmarker creato, creo warehousedepots");[Debug]
        warehouseDepots=new WarehouseDepots();
        //System.out.println("warehousedepots creata, creo strongbox");[Debug]
        strongBox=new StrongBox();
        //System.out.println("strongbox creata, creo devslot");[Debug]
        devCardSlot=new DevCardSlot();
        //System.out.println("devslot creati, creo specialshelf");[Debug]
        specialShelves = new ArrayList<>();
    }

    public ArrayList<Optional<SpecialShelf>> getSpecialShelves() {
        return specialShelves;
    }
    public FaithMarker getFaithMarker() {
        return faithMarker;
    }

    public WarehouseDepots getWarehouseDepots() {
        return warehouseDepots;
    }

    public StrongBox getStrongBox() {
        return strongBox;
    }

    public DevCardSlot getDevCardSlot() {
        return devCardSlot;
    }

    /**
     * @param resourceArrayList are resources that we want check if they are in warehouse or in strongbox
     * @return true if resources are contains , false otherwise
     */
    public boolean checkUseProd(ArrayList<Resource> resourceArrayList){
        boolean result = false;
        List<Resource> personalResources = Stream.concat(getWarehouseDepots().getResources().stream(), getStrongBox().getStrongboxContent().stream())
                .collect(Collectors.toList());
        for(Resource resource: resourceArrayList){
            if (personalResources.contains(resource)) {
                result = true;
                personalResources.remove(resource);
            }
            else
                return result;
        }
        return result;
    }

    /**
     * @param resources are that to delete
     * @return all resources to delete
     * @throws ResourceNotValidException if one resources is not contained in warehouse or in strongbox
     */
    public ArrayList<Resource> removeResources(ArrayList<Resource> resources)  {
        for(Resource resources1:resources){
            if(strongBox.getStrongboxContent().contains(resources1)){
                strongBox.getStrongboxContent().remove(resources1);
            }
            else if(warehouseDepots.getResources().contains(resources1)){
                warehouseDepots.getResource(resources1);
                warehouseDepots.getResources().remove(resources1);
            }
        }
        return resources;
    }
}

