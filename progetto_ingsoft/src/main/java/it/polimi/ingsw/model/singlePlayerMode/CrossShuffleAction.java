package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;

import java.util.*;

public class CrossShuffleAction implements ActionToken {

    private final String effect="Lorenzo receives one faith point";
    private final SinglePlayer sP;

    public CrossShuffleAction(SinglePlayer singlePlayer){
        this.sP=singlePlayer;
    }

    /**
     * @return a String which descibes the effect of specified token
     */
    @Override
    public String applyEffect(ArrayList<ActionToken> tokensStack){
        Collections.shuffle(tokensStack);
        sP.getBlackCrossToken().updateBlackCross(1);
        return effect;
    }

}