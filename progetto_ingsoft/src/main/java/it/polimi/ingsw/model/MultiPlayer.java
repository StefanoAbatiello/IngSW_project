package it.polimi.ingsw.model;


import it.polimi.ingsw.model.Market.Market;
import it.polimi.ingsw.model.cards.DevDeck;
import it.polimi.ingsw.model.cards.DevDeckMatrix;
import it.polimi.ingsw.model.cards.LeadDeck;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MultiPlayer extends Game {
    private ArrayList<Player> players;
    private Market market;
    private DevDeckMatrix matrix;
    private LeadDeck leads;
    private DevDeck devDeck;

    //TODO controlla eccezione se deve essere gestit dentro il model

    /**
     *
     * @param username is the players' list of the game
     * @throws playerLeadsNotEmptyException when a player receive a fifth lead card
     */
    public MultiPlayer(ArrayList<String> username, int numPlayer) throws playerLeadsNotEmptyException {
        setVC1active(true);
        setVC2active(true);
        setVC3active(true);
        int i=0;
        players= new ArrayList<>();
        System.out.println("mi salvo la lista di giocatori");
        for(String user:username) {
            System.out.println("creo il giocatore " + user);
            Player player= new Player(user);
            this.players.add(player);
            System.out.println(user + "creato");
            i++;
        }
        System.out.println("lista di giocatori salvata, creo le devCards");
        matrix = new DevDeckMatrix();
        devDeck = DevDeckMatrix.getDeck();
        System.out.println("devCards create, creo il market");
        this.market=new Market();
        System.out.println("market creato, creo le leadCards");
        leads = new LeadDeck();
        System.out.println("distribuisco le leadCards");
        for(Player player:players){
            leads.giveToPlayer(player);
        }
    }


    @Override
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

    public boolean activePopeSpace(Player playerInput) {
        if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==8 && isVC1active()) {

            players.stream().filter(player -> player.getPersonalBoard().getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(2));
            setVC1active(false);
            return isVC1active();
        }
        else if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==16 && isVC2active()) {
            players.stream().filter(player -> player.getPersonalBoard().getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(3));
            setVC2active(false);
            return isVC2active();
        }
        else if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==24 && isVC3active()) {
            players.stream().filter(player -> player.getPersonalBoard().getFaithMarker().isVaticanZone()).forEach(player -> player.increaseFaithtrackPoints(4));
            setVC3active(false);
            return isVC3active();
        }
        else
            return true;
    }


    public Market getMarket() {
        return market;
    }

    public DevDeckMatrix getMatrix() {
        return matrix;
    }

    public LeadDeck getLeads() {
        return leads;
    }

    @Override
    public DevDeck getDevDeck() {
        return devDeck;
    }
}
