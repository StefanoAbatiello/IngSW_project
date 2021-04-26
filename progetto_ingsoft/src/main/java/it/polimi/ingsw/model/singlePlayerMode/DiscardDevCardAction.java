package it.polimi.ingsw.model.singlePlayerMode;

import it.polimi.ingsw.model.SinglePlayer;

public class DiscardDevCardAction implements ActionToken {



    private final String color;

    public DiscardDevCardAction(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    /**
     * @return 0
     */
    @Override
    public int applyEffect() {
        return SinglePlayer.removeTokenCard(color);
    }
}

