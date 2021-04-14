package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Resources;

import java.util.ArrayList;

public class StrongBox {
    private ArrayList<Resources> strongboxContent;
    private boolean resourceInStrongbox;

    public StrongBox() {
        this.strongboxContent = new ArrayList<>();
        this.resourceInStrongbox = false;
    }

    /**
     * @return strongbox after adding resources
     */
    public ArrayList<Resources> addInStrongbox(Resources resources) {

        strongboxContent.add(resources);
        return strongboxContent;
    }

    /**
     * @return actual state of strongbox
     */
    public ArrayList<Resources> getStrongboxContent() {
        return strongboxContent;
    }

    public Resources getResource(Resources resources) throws ResourceNotValidException {
        for(Resources resources1:strongboxContent){
            if(resources1==resources){
                strongboxContent.remove(resources1);
                return resources;
            }
        }
        throw new ResourceNotValidException();
    }
}
