package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiscardDevCardAction implements ActionToken {

    /**
     * this is the color of the Development card to discard
     */
    private final String color;

    /**
     * this is a reference to the game mode
     */
    private final SinglePlayer sP;

    public DiscardDevCardAction(String color,SinglePlayer singlePlayer) {
        this.color = color;
        this.sP=singlePlayer;
    }

    /**
     * @return color of the card to discard
     */
    public String getColor() {
        return color;
    }

    /**
     * @return a String which descibes the effect of specified token
     */
    @Override
    public String applyEffect(ArrayList<ActionToken> tokensStack) {
        sP.removeTokenCard(color);
        sP.removeTokenCard(color);
        return "Lorenzo has discarded two development card of color " + color;
    }
}

