package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.cardExceptions.NoSuchRequirementException;

import java.util.ArrayList;

public interface ResourceCreator {
    public ArrayList<Resource> getResources() throws NoSuchRequirementException, NoSuchRequirementException;

}
