package it.polimi.ingsw.model.personalboard;

import it.polimi.ingsw.exceptions.InvalidInputException;
import it.polimi.ingsw.model.Resources;

import java.util.ArrayList;

public class StrongBox {
    private ArrayList<Resources> strongboxContent;

    /**
     * @return strongbox after adding resources
     * @exception InvalidInputException if any resources is given as input
     */
    public ArrayList<Resources> setStrongboxContent(Resources resources) throws InvalidInputException {
        if(resources==null)
            throw new InvalidInputException();
        strongboxContent.add(resources);
        return strongboxContent;
    }

    /**
     * @return actual state of strongbox
     */
    public ArrayList<Resources> getStrongboxContent() {
        return strongboxContent;
    }
}
