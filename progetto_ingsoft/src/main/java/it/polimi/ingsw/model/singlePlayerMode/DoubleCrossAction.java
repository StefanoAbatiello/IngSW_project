package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import java.util.ArrayList;

public class DoubleCrossAction implements ActionToken {

    /**
     * this is a reference to the game mode
     */
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
        return "Lorenzo receives two faith point";
    }

}
