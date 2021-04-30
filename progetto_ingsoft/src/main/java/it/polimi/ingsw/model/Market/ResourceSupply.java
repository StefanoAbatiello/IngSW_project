package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.ResourceCreator;

import java.util.ArrayList;

public class ResourceSupply implements ResourceCreator {

    /*
        this is an array of containers,
        one for each resource taken from the market
        */
    final Container[] containers = new Container[4];

    /**
     * this constructor method creates containers
     */
    public ResourceSupply() {
        for (int i = 0; i < 4; i++)
            containers[i] = new Container();
    }

    /**
     * @return ResourceSupply structure
     */
    public Container[] getContainers() {
        return containers;
    }

    /**
     * this method search for an empty container
     * and when finds it try to store the Resource calling Container's method
     * @param resource is the Resource to store in container
     * @return a boolean to indicate if storing has been done or not
     */
    public Resource putResourceInContainer(Resource resource) throws FullSupplyException {
        for (Container container : containers) {
            if (container.isEmpty()) {
                container.fillContainer(resource);
                return resource;
            }
        }
        throw new FullSupplyException("Il supply è pieno, non è possibile inserire nuove risorse");
    }

    /**
     * @return an ArrayList of the Resources stored in ResourceSupply
     */
    @Override
    public ArrayList<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<>();
        for (Container container : containers) {
            if (!container.isEmpty()) {
                resources.add(container.takeResource());
            }
        }
        return resources;
    }

  //TODO implementation
    @Override
    public Resource getResource() {
        return null;
    }
}