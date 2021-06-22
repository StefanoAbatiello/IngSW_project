package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.model.Resource;
import java.util.ArrayList;

public class StrongBox {

    /**
     * this is the structure where the Resources are stored
     */
    private final ArrayList<Resource> strongboxContent;

    public StrongBox() {
        this.strongboxContent = new ArrayList<>();
    }

    /**
     * @param resources is the List of Resources to add in Strongbox
     * @return strongbox after adding resources
     */
    public ArrayList<Resource> addInStrongbox(ArrayList<Resource> resources) {
        strongboxContent.addAll(resources);
        return strongboxContent;
    }

    /**
     * @return actual state of strongbox
     */
    public ArrayList<Resource> getStrongboxContent() {
        return strongboxContent;
    }

}
