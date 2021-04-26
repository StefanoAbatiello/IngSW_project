package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import it.polimi.ingsw.model.singlePlayerMode.ActionToken;

public class DoubleCrossAction implements ActionToken {


    /**
     * @return the new position of Lorenzo's BlackCross
     */
    @Override
    public int applyEffect(){
        return BlackCrossToken.updateBlackCross(2);
    }

}
