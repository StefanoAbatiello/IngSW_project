package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import it.polimi.ingsw.model.personalboard.BlackCrossToken;
import java.util.ArrayList;

public class DoubleCrossAction implements ActionToken {

    private final String effect="Lorenzo receives two faith point";
    private final SinglePlayer sP;


    public DoubleCrossAction(SinglePlayer singlePlayer) {
        this.sP=singlePlayer;
    }


    /**
     * @return a String which descibes the effect of specified token
     */
    @Override
    public String applyEffect(ArrayList<ActionToken> tokensStack){
        sP.getBlackCrossToken().updateBlackCross(2);
        return effect;
    }

}
