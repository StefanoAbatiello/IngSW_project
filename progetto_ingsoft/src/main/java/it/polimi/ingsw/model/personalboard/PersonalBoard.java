package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Player;
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
        ArrayList<Resource> resources=new ArrayList<>();
        for(int i=0;i<5;i++){
            resources.add(Resource.COIN);
            resources.add(Resource.STONE);
            resources.add(Resource.SHIELD);
            resources.add(Resource.SERVANT);
        }
        strongBox.addInStrongbox(resources);
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
    public boolean removeResourcesfromBuy(ArrayList<Resource> resourceArrayList){
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
        for(Resource resource:resources){
            if(warehouseDepots.getResources().contains(resource)){
                warehouseDepots.getResources().remove(resource);
            }
            else if(!specialShelves.isEmpty()) {
                for(int i=0; i<2;i++)
                if (specialShelves.get(i).isPresent())
                    specialShelves.get(i).get().getSpecialSlots().remove(resource);
            }
            else strongBox.getStrongboxContent().remove(resource);


        }
        return resources;
    }

    public ArrayList<String>[] getSimplifiedWarehouse() {
        ArrayList<String>[] warehouse = new ArrayList[5];
        for(int i=0;i<5;i++) {
            warehouse[i] = new ArrayList<>();
        }
        getWarehouseDepots().getShelves()[0].getSlots().forEach(resource -> warehouse[0].add(String.valueOf(resource)));
        getWarehouseDepots().getShelves()[1].getSlots().forEach(resource -> warehouse[1].add(String.valueOf(resource)));
        getWarehouseDepots().getShelves()[2].getSlots().forEach(resource -> warehouse[2].add(String.valueOf(resource)));
        if(!getSpecialShelves().isEmpty()) {
            getSpecialShelves().get(0).ifPresent(specialShelf ->
                specialShelf.getSpecialSlots().forEach(resource -> warehouse[3].add(String.valueOf(resource))));
            getSpecialShelves().get(1).ifPresent(specialShelf ->
                specialShelf.getSpecialSlots().forEach(resource -> warehouse[4].add(String.valueOf(resource))));
        }
        return warehouse;
    }

    public int[] getSimplifiedStrongbox(){
        int[] strongbox=new int[4];
        ArrayList<Resource> resources=getStrongBox().getStrongboxContent();
        for (Resource resource:resources) {
            switch (resource) {
                case COIN:
                    strongbox[0]++;
                    break;
                case SERVANT:
                    strongbox[1]++;
                    break;
                case SHIELD:
                    strongbox[2]++;
                    break;
                case STONE:
                    strongbox[3]++;
                    break;
            }
        }
        return strongbox;
    }
}

