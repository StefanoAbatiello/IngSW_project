package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import java.util.ArrayList;

public class DoubleCrossAction implements ActionToken {

    private final String effect="Lorenzo receives two faith point";


    /**
     * @return a String which descibes the effect of specified token
     */
    @Override
    public String applyEffect(ArrayList<ActionToken> tokensStack){
        BlackCrossToken.updateBlackCross(2);
        return effect;
    }

}
