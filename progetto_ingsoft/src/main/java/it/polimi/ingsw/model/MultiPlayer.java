package it.polimi.ingsw.model;


import it.polimi.ingsw.model.Market.Market;
import it.polimi.ingsw.model.cards.DevDeck;
import it.polimi.ingsw.model.cards.DevDeckMatrix;
import it.polimi.ingsw.model.cards.LeadDeck;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class MultiPlayer extends Game {

    /**
     * this is the list of players in game
     */
    private final ArrayList<Player> players;

    /**
     * this is the Resource market of this game
     */
    private final Market market;

    /**
     * this is the matrix of Development cards of this game
     */
    private final DevDeckMatrix matrix;

    /**
     * this is the deck of all Leader cards
     */
    private final LeadDeck leads;

    /**
     * this is the deck of all Development cards
     */
    private final DevDeck devDeck;

    public MultiPlayer(ArrayList<String> username) throws playerLeadsNotEmptyException, IOException, ParseException {
        setVC1active(true);
        setVC2active(true);
        setVC3active(true);
        players= new ArrayList<>();
        System.out.println("mi salvo la lista di giocatori");
        for(String user:username) {
            System.out.println("creo il giocatore " + user);
            this.players.add(new Player(user));
            System.out.println(user + "creato");
        }
        System.out.println("lista di giocatori salvata, creo le devCards");
        matrix = new DevDeckMatrix();
        devDeck = matrix.getDeck();
        System.out.println("devCards create, creo il market");
        this.market=new Market();
        System.out.println("market creato, creo le leadCards");
        leads = new LeadDeck();
        System.out.println("distribuisco le leadCards");
        leads.shuffle();
        for(Player player:players){
            leads.giveToPlayer(player);
        }
    }


    @Override
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * @param playerInput is the player who activates the pope meeting
     * @return false if the player who activates the pope meeting was the first to reach this faith position, true otherwise
     */
    public boolean activePopeSpace(Player playerInput) {
        if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==8 && isVC1active()) {
            players.stream().filter(player ->
                    player.getPersonalBoard().getFaithMarker().isVaticanZone()).forEach(player ->
                        player.increaseFaithTrackPoints(2));
            setVC1active(false);
            return isVC1active();
        }
        else if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==16 && isVC2active()) {
            players.stream().filter(player ->
                    player.getPersonalBoard().getFaithMarker().isVaticanZone()).forEach(player ->
                        player.increaseFaithTrackPoints(3));
            setVC2active(false);
            return isVC2active();
        }
        else if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==24 && isVC3active()) {
            players.stream().filter(player ->
                    player.getPersonalBoard().getFaithMarker().isVaticanZone()).forEach(player ->
                        player.increaseFaithTrackPoints(4));
            setVC3active(false);
            return isVC3active();
        }
        else
            return true;
    }


    public Market getMarket() {
        return market;
    }

    public DevDeck getDevDeck() {
        return devDeck;
    }

    @Override
    public DevDeckMatrix getDevDeckMatrix() {
        return matrix;
    }

    /**
     * @param player      is the player who give away faith points
     * @param pointsGiven is the number of faith points to give away
     */
    @Override
    public void pointsGiveAway(Player player, int pointsGiven) {
        players.forEach(p -> {
            if(p!=player)
                for (int i=0; i<pointsGiven; i++)
                    p.getPersonalBoard().getFaithMarker().updatePosition();
        });
    }

    /**
     * @param name is the name of the player searched
     * @return the player searched
     */
    @Override
    public Player getPlayerFromName(String name) {
        if (players.get(0).getName().equals(name)) {
            return players.get(0);
        } else if (players.get(1).getName().equals(name)) {
            return players.get(1);
        } else if (players.get(2).getName().equals(name)) {
            return players.get(2);
        } else
            return players.get(3);
    }

}
