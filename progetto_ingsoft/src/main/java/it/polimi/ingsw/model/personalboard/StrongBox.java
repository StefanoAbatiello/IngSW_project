package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resource;

import java.util.ArrayList;

public class StrongBox {
    private ArrayList<Resource> strongboxContent;
    private boolean resourceInStrongbox;

    public StrongBox() {
        this.strongboxContent = new ArrayList<>();
        this.resourceInStrongbox = false;
    }

    /**
     * @return strongbox after adding resources
     */
    public ArrayList<Resource> addInStrongbox(Resource resource) {

        strongboxContent.add(resource);
        return strongboxContent;
    }

    /**
     * @return actual state of strongbox
     */
    public ArrayList<Resource> getStrongboxContent() {
        return strongboxContent;
    }

    public Resource getResource(Resource resource) throws ResourceNotValidException {
        for(Resource resource1 :strongboxContent){
            if(resource1 == resource){
                strongboxContent.remove(resource1);
                return resource;
            }
        }
        throw new ResourceNotValidException();
    }
}
