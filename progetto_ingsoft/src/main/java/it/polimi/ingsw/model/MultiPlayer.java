package it.polimi.ingsw.model;


import java.util.ArrayList;

public class MultiPlayer implements Game {
    private final  ArrayList<Player> players=new ArrayList<>();

    /**
     * This attribute represent the first vatican zone and its state
     */
    private boolean VC1active;
    private boolean VC2active;
    private boolean VC3active;
    //private static int turnOwner;


    public MultiPlayer() {
        this.VC1active=true;
        this.VC2active=true;
        this.VC3active=true;
    }

    public void createNewPlayer(Player player) {
        players.add(player);
    }

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

    public ArrayList<Player> getPlayers() {
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
    /**
     * @return the current status of indicated Vatican zone after being activated.
     * Furthermore set which slot is in pope space & increase points that will be added in the end of game
     */

    //TODO check faithtrackpoints updater in player
    public boolean activePopeSpace(Player playerInput) {
        if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==8 && isVC1active()) {

            getPlayers().stream().filter(player -> player.getPersonalBoard().getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(2));
            setVC1active(false);
            return isVC1active();
        }
        else if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==16 && isVC2active()) {
            getPlayers().stream().filter(player -> player.getPersonalBoard().getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(3));
            setVC2active(false);
            return isVC2active();
        }
        else if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==24 && isVC3active()) {
            getPlayers().stream().filter(player -> player.getPersonalBoard().getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(4));
            setVC3active(false);
            return isVC3active();
        }
        else
            return true;
    }


}
