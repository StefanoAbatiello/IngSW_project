package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import java.util.ArrayList;

public class DiscardDevCardAction implements ActionToken {
    /*
        this String indicates the color of the card to discard
         */
    private final String color;

    private final String effect="Lorenzo has discarded two development card of color ";

    /*
    this constructor associates the color to this ActionToken
     */
    public DiscardDevCardAction(String color) {
        this.color = color;
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
        SinglePlayer.removeTokenCard(color);
        return effect + color;
    }
}

