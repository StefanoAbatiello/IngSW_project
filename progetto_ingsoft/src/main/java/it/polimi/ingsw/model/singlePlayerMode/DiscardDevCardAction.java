package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;
import java.util.ArrayList;

public class DiscardDevCardAction implements ActionToken {

    /*
    this String indicates the color of the card to discard
     */
    private final String color;

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
     * @return 0 if the card removal works correctly, otherwise -1, or -2 if the card removal failed
     */
    @Override
    public int applyEffect(ArrayList<ActionToken> tokensStack) {
        return SinglePlayer.removeTokenCard(color);
    }
}

