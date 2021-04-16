package it.polimi.ingsw.model;


import java.util.ArrayList;

public class Game {
    private static final  ArrayList<Player> players=new ArrayList<>();

    /**
     * This attribute represent the first vatican zone and its state
     */
    private static boolean VC1active;
    private static boolean VC2active;
    private static boolean VC3active;
    //private static int turnOwner;


    public Game(int numOfPlayers) {
        VC1active=true;
        VC2active=true;
        VC3active=true;
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



    /*setAction(Object )
        /*riceve oggetto che esce dal metodo chiamato dal giocatore come prima mossa,
        in base al tipo dell'oggetto imposta la tipologia di azione legata a quel turno
         */
        //mercato ->matrice modificata
        //carte->carta
        //produzioni->risorse*/

    /*public static int getTurnOwner() {
        return turnOwner;
    }

    public static void setTurnOwner(int playerID) {
        Game.turnOwner = playerID;
    }

    turnAction= 0;*/

    //TODO metodo if che in base mossa giocatore manda metodi per l'azione corrispondente

    //TODO per ogni azione un metodo

    public static boolean resetGame(){
        setVC1active(true);
        setVC2active(true);
        setVC3active(true);
        for(Player player:players)
            Game.getPlayers().remove(player);
        return Game.getPlayers().isEmpty();
    }


}
