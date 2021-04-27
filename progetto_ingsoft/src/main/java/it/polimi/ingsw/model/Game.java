package it.polimi.ingsw.model;

import java.util.ArrayList;

public interface Game {
    boolean setVC1active(boolean VC1active);
    boolean setVC2active(boolean VC2active);
    boolean setVC3active(boolean VC3active);
    boolean isVC1active();
    boolean isVC2active();
    boolean isVC3active();
    ArrayList<Player> getPlayers();
    boolean activePopeSpace(Player player);
    void createNewPlayer(Player player);

}
