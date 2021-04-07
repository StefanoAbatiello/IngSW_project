package it.polimi.ingsw.model.cards;

public class DevCard {
    private int DevCardLevel;
    private int DevCardPoint;
    private boolean Active;

    public DevCard() {
        Active=false;
    }

    public int getDevCardLevel() {
        return DevCardLevel;
    }

    public int getDevCardPoint() {
        return DevCardPoint;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }
}
