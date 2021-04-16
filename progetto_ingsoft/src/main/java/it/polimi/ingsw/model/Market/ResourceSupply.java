package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.exceptions.FullSupplyException;
import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;

public class ResourceSupply {

    /*
    this is an array of containers,
    one for each resource taken from the market
    */
    static final Container[] containers = new Container[4];

    /**
     * this constructor method creates containers
     */
    public ResourceSupply() {
        for (int i = 0; i < 4; i++)
            containers[i] = new Container();
    }

    /**
     * this method search for an empty container
     * and when finds it try to store the Resource calling Container's method
     *
     * @param resource is the Resource to store in container
     * @return a boolean to indicate if storing has been done or not
     */
    public static Resource putResourceInContainer(Resource resource) throws FullSupplyException {
        for (Container container : containers) {
            if (container.isEmpty()) {
                container.fillContainer(resource);
                return resource;
            }
        }
        throw new FullSupplyException("Il supply è pieno, non è possibile inserire nuove risorse");
    }

    /**
     * @return an Arraylist of the Resource stored in ResourceSupply
     */
    public static ArrayList<Resource> showSupply() {
        ArrayList<Resource> supply = new ArrayList<>();
        for (Container container : containers) {
            if (!container.isEmpty()) {
                supply.add(container.getResource());
            }
        }
        return supply;
    }

}