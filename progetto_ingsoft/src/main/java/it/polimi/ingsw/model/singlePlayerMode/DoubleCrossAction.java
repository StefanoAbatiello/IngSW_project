package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public Map<Integer, String> applyEffect(ArrayList<ActionToken> tokensStack){
        Map<Integer,String> result=new HashMap<>();
        sP.getBlackCrossToken().updateBlackCross(2);
        result.put(2,"Lorenzo receives two faith point");
        return result;
    }

}
