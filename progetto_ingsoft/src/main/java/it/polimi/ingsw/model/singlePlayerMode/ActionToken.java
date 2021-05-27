package it.polimi.ingsw.model.singlePlayerMode;

import java.util.ArrayList;

public interface ActionToken {


    /**
     * @return a String which descibes the effect of specified token
     */
    String applyEffect(ArrayList<ActionToken> tokensStack);
}

