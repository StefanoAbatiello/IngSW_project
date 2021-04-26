package it.polimi.ingsw.model.singlePlayerMode;

public interface ActionToken {

    /**
     * @return new blackCross position or 0 if there are other card with that color
     */
    public int applyEffect();

}

