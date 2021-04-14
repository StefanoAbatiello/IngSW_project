package it.polimi.ingsw.model;


import java.util.ArrayList;

public class Game {
    private static final  ArrayList<Player> players=new ArrayList<>();
    private static boolean VC1active;
    private static boolean VC2active;
    private static boolean VC3active;


    public Game() {
        setVC1active(true);
        setVC2active(true);
        setVC3active(true);
    }

    public static void createNewPlayer(Player player) {
        players.add(player);
    }

    public static void setVC1active(boolean VC1active) {
        Game.VC1active = VC1active;
    }

    public static void setVC2active(boolean VC2active) {
        Game.VC2active = VC2active;
    }

    public static void setVC3active(boolean VC3active) {
        Game.VC3active = VC3active;
    }

    public static boolean isVC1active() {
        return VC1active;
    }

    public static boolean isVC2active() {
        return VC2active;
    }

    public static boolean isVC3active() {
        return VC3active;
    }

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    public static ArrayList<Player> removePlayer(Player p) {
            players.remove(p);
        return players;
    }


    public boolean canaddPlayer(){
        if (players.size()<4)
            return true;
        else
            return false;
    }
}
