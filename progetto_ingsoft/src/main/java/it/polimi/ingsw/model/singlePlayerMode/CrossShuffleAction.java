package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.personalboard.BlackCrossToken;

import java.util.Collections;

public class CrossShuffleAction implements ActionToken {


    /**
     * @return the new position of Lorenzo's BlackCross
     */
    @Override
    public int applyEffect(){
        Collections.shuffle(SinglePlayer.getTokensStack());
        return BlackCrossToken.updateBlackCross(1);
    }

}