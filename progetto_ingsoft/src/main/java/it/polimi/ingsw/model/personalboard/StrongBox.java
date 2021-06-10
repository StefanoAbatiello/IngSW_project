package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.ResourceNotValidException;
import it.polimi.ingsw.model.Player;
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
    public ArrayList<Resource> addInStrongbox(ArrayList<Resource> resources) {
        for(Resource res: resources)
            strongboxContent.add(res);
        return strongboxContent;
    }

    /**
     * @return actual state of strongbox
     */
    public ArrayList<Resource> getStrongboxContent() {
        return strongboxContent;
    }



}
