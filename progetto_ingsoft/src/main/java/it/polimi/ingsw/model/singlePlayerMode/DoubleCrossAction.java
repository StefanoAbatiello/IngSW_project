package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import java.util.ArrayList;

public class DoubleCrossAction implements ActionToken {

    /**
     * @return the new position of Lorenzo's BlackCross
     */
    @Override
    public int applyEffect(ArrayList<ActionToken> tokensStack){
        return BlackCrossToken.updateBlackCross(2);
    }

}
