package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Market.Market;

import it.polimi.ingsw.model.cards.DevDeck;

import java.util.ArrayList;

public abstract class  Game {
    /**
     * This attribute represent the first vatican zone and its state
     */
    private boolean VC1active;
    private boolean VC2active;
    private boolean VC3active;

    public boolean setVC1active(boolean VC1active) {
        this.VC1active = VC1active;
        return VC1active;
    }

    public boolean setVC2active(boolean VC2active) {
        this.VC2active = VC2active;
        return VC2active;
    }

    public boolean setVC3active(boolean VC3active) {
        this.VC3active = VC3active;
        return VC3active;
    }

    public boolean isVC1active() {
        return VC1active;
    }

    public boolean isVC2active() {
        return VC2active;
    }

    public boolean isVC3active() {
        return VC3active;
    }

    abstract boolean activePopeSpace(Player player);

    public abstract Market getMarket();

    public abstract ArrayList<Player> getPlayers();

    public abstract DevDeck getDevDeck();
}
