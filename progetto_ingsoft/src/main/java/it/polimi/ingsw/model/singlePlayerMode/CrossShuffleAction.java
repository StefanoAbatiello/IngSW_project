package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import java.util.*;

public class CrossShuffleAction implements ActionToken {

    /**
     * this is a reference to the game mode
     */
    private final SinglePlayer singlePlayer;

    public CrossShuffleAction(SinglePlayer singlePlayer){
        this.singlePlayer =singlePlayer;
    }

    /**
     * @return a String which descibes the effect of specified token
     */
    @Override
    public Map<Integer, String> applyEffect(ArrayList<ActionToken> tokensStack){
        Map<Integer,String> result=new HashMap<>();
        Collections.shuffle(tokensStack);
        singlePlayer.getBlackCrossToken().updateBlackCross(1);
        result.put(1,"Lorenzo receives one faith point");
        return result;
    }

}