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
        //System.out.println("mi salvo la lista di giocatori");[Debug]
        for(String user:username) {
            //System.out.println("creo il giocatore " + user);[Debug]
            this.players.add(new Player(user));
            //System.out.println(user + "creato");[Debug]
        }
        //System.out.println("lista di giocatori salvata, creo le devCards");[Debug]
        matrix = new DevDeckMatrix();
        devDeck = matrix.getDeck();
        //System.out.println("devCards create, creo il market");[Debug]
        this.market=new Market();
        //System.out.println("market creato, creo le leadCards");[Debug]
        leads = new LeadDeck();
        //System.out.println("distribuisco le leadCards");[Debug]
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
     * @return the number of the pope meeting activated(1, 2 or 3), otherwise return 0
     */
    @Override
    public int activePopeSpace(Player playerInput) {
        if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==8 && isVC1active()) {
            players.stream().filter(player ->
                    player.getPersonalBoard().getFaithMarker().isVaticanZone(1)).forEach(player ->
                        player.increasePopeMeetingPoints(2));
            setVC1active(false);
            return 1;
        }
        else if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==16 && isVC2active()) {
            players.stream().filter(player ->
                    player.getPersonalBoard().getFaithMarker().isVaticanZone(2)).forEach(player ->
                        player.increasePopeMeetingPoints(3));
            setVC2active(false);
            return 2;
        }
        else if(playerInput.getPersonalBoard().getFaithMarker().getFaithPosition()==24 && isVC3active()) {
            players.stream().filter(player ->
                    player.getPersonalBoard().getFaithMarker().isVaticanZone(3)).forEach(player ->
                        player.increasePopeMeetingPoints(4));
            setVC3active(false);
            return 3;
        }
        else
            return 0;
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
     * @return 1 if the game is multiplayer, 0 otherwise
     */
    @Override
    public int faithPointsGiveAway(Player player) {
        players.forEach(p -> {
            if (p != player) {
                System.out.println("il player "+p.getName()+" sta ricevendo il punto");
                p.getPersonalBoard().getFaithMarker().updatePosition();
            }
        });
        return 1;
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

    @Override
    public String getWinner() {
        int winnerPlayerIndex=0;
        int winnerPoints=players.get(0).getPoints();
        ArrayList<Resource> winnerResources=new ArrayList<>(players.get(winnerPlayerIndex).getWarehouseResources());
        winnerResources.addAll(players.get(winnerPlayerIndex).getSpecialShelfResources());
        winnerResources.addAll(players.get(winnerPlayerIndex).getStrongboxResources());
        for (int i=1;i<players.size();i++){
            int playerPoints=players.get(i).getPoints();
            ArrayList<Resource> playerResource=new ArrayList<>(players.get(i).getWarehouseResources());
            playerResource.addAll(players.get(i).getSpecialShelfResources());
            playerResource.addAll(players.get(i).getStrongboxResources());
            if (playerPoints>winnerPoints){
                winnerPlayerIndex=i;
                winnerPoints=playerPoints;
                winnerResources=playerResource;
            }else if (playerPoints==winnerPoints){
                if (playerResource.size()>winnerResources.size()){
                    winnerPlayerIndex=i;
                    winnerPoints=playerPoints;
                    winnerResources=playerResource;
                }
            }
        }
        return players.get(winnerPlayerIndex).getName();
    }

    @Override
    public int getBlackCrossPosition() {
        return -1;
    }

}
