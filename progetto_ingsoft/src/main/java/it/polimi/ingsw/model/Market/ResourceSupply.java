package it.polimi.ingsw.model.Market;

import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.ResourceCreator;

import java.util.ArrayList;
import java.util.Arrays;

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
    public boolean putResourceInContainer(Resource resource) {
        for (Container container : containers) {
            if (container.isEmpty()) {
                container.fillContainer(resource);
                return true;
            }
        }
        return false;
    }

    /**
     * @return shows the Resources stored in ResourceSupply
     */
    public ArrayList<Resource> viewResources() {
        ArrayList<Resource> resources = new ArrayList<>();
        for (Container container:containers){
            if (!container.isEmpty())
                resources.add(container.showResource());
        }
        //Arrays.stream(containers).forEach(container ->{if (!container.isEmpty()) resources.add(container.showResource());});
        return resources;
    }


    /**
     * @return an Arraylist of the Resources stored in ResourceSupply
     */
    @Override
    public ArrayList<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<>();
        Arrays.stream(containers).forEach(container ->{if (!container.isEmpty()) resources.add(container.takeResource());});
        return resources;
    }

    /**
     * This method removes all the resources left in supply
     * @param discardedResources is the list of resources to discard
     * @return the number of Faith points ti give to other player
     */
    public int discardResources(ArrayList<Resource> discardedResources) {
        for(Resource res:discardedResources)
            for (int i=0;i<4;i++){
                if (!containers[i].isEmpty()&&containers[i].showResource()==res){
                    containers[i].takeResource();
                    break;
                }
            }
        return discardedResources.size();
    }

    /**
     * @param res is the resource wanted by the Player
     * @return true if the change is done correctly
     */
    public boolean changeChoosable(Resource res) {
        for(Container c:containers){
            if (c.showResource()==Resource.CHOOSABLE){
                c.takeResource();
                c.fillContainer(res);
                return true;
            }
        }
        return false;
    }

    /**
     * this method clear the supply from old resources
     */
    public void emptySupply() {
        Arrays.stream(containers).forEach(Container::takeResource);
    }
}