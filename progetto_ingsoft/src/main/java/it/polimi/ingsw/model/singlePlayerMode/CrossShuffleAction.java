package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import java.util.*;

public class CrossShuffleAction implements ActionToken {

    private final String effect="Lorenzo receives one faith point";


    /**
     * @return a String which descibes the effect of specified token
     */
    @Override
    public String applyEffect(ArrayList<ActionToken> tokensStack){
        Collections.shuffle(tokensStack);
        BlackCrossToken.updateBlackCross(1);
        return effect;
    }

}