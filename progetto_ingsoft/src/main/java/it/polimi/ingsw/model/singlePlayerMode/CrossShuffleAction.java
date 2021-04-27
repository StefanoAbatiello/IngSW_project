package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import java.util.*;

public class CrossShuffleAction implements ActionToken {

    /**
     * @return the new position of Lorenzo's BlackCross
     */
    @Override
    public int applyEffect(ArrayList<ActionToken> tokensStack){
        Collections.shuffle(tokensStack);
        return BlackCrossToken.updateBlackCross(1);
    }

}