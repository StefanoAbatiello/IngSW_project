package it.polimi.ingsw.model.Market;


import it.polimi.ingsw.model.Player;

public interface MarketMarble {

    /**
     * @return marble's color
     */
    String getColor();

    /**
     * this is a strategy, in fact each subclass overrides his only method
     * @param player is a reference to the player who is taking this marble
     * @return a boolean that indicate if the method worked or not
     */
    boolean changeMarble(Player player);

}

