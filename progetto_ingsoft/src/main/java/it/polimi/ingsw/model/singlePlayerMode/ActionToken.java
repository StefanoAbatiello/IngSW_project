package it.polimi.ingsw.model.singlePlayerMode;

import java.util.ArrayList;

public interface ActionToken {

    /**
     * @return new blackCross position or 0 if there are other card with that color
     */
    int applyEffect(ArrayList<ActionToken> tokensStack);
}

