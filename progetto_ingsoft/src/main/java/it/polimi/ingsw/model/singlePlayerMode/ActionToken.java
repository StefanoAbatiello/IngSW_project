package it.polimi.ingsw.model.singlePlayerMode;

import java.util.ArrayList;
import java.util.Map;

public interface ActionToken {

    /**
     * @return a String which describes the effect of specified token
     */
    Map<Integer,String> applyEffect(ArrayList<ActionToken> tokensStack);
}

