package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException;
import java.util.ArrayList;

public interface ResourceCreator {
    ArrayList<Resource> getResources() throws NoSuchRequirementException;

}
