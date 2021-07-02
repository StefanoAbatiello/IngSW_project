package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Market.Market;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import it.polimi.ingsw.model.personalboard.*;
import it.polimi.ingsw.model.singlePlayerMode.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SinglePlayer extends Game{

    /**
     * this is the list of Lorenzo's tokens
     */
    private final ArrayList<ActionToken> tokensStack =new ArrayList<>();

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

    /**
     * this is the list of players in game
     */
    private final ArrayList<Player> players = new ArrayList<>();

    /**
     * this is the Lorenzo's cross
     */
    private final BlackCross blackCross;

    private String winnerName;

    public  ArrayList<ActionToken> getTokensStack() {
        return tokensStack;
    }

    public SinglePlayer(String username) throws playerLeadsNotEmptyException, IOException, ParseException {
        blackCross =new BlackCross();
        this.players.add(new Player(username));
        tokensStack.add(new CrossShuffleAction(this));
        tokensStack.add(new DoubleCrossAction(this));
        tokensStack.add(new DoubleCrossAction(this));
        tokensStack.add(new DiscardDevCardAction("YELLOW",this));
        tokensStack.add(new DiscardDevCardAction("GREEN",this));
        tokensStack.add(new DiscardDevCardAction("PURPLE",this));
        tokensStack.add(new DiscardDevCardAction("BLUE",this));
        Collections.shuffle(tokensStack);
        matrix= new DevDeckMatrix();
        devDeck = matrix.getDeck();
        this.market=new Market();
        leads= new LeadDeck();
        leads.shuffle();
        leads.giveToPlayer(players.get(0));

    }
    public ArrayList<Player>  getPlayers() {
        return this.players;
    }

    /**
     * @return true if all the Players in game have chosen their Leader cards
     */
    @Override
    public boolean checkAllPlayersChooseLeads(){
        //System.out.println("controllo se tutti hanno scelto le leads");[Debug]
        for(Player player:players) {
            if (!player.leaderCardAlreadyChosen())
                return false;
        }
        //System.out.println("tutti i giocatori hanno già scelto le leads");[Debug]
        return true;
    }

    /**
     * @return true if checking line by line an empty one is found(each line corresponds to a color)
     */
    public boolean checkEmptyLineInMatrix() {
        for(int i=0;i<4;i++) {
            if (matrix.getDevMatrix()[i][0].getLittleDevDeck().isEmpty() &&
                matrix.getDevMatrix()[i][1].getLittleDevDeck().isEmpty() &&
                matrix.getDevMatrix()[i][2].getLittleDevDeck().isEmpty())
                    return true;
        }
        return false;
    }

    /**
     * @return true if Lorenzo's BlackCross has reached the end of Faith track
     */
    public boolean checkBlackCrossPosition(){
        return blackCross.getCrossPosition() >= 24;
    }

    /**
     * @return tokensStack after activation of first token's effect and reorganization of the stack
     */
    @Override
    public String draw() {
        if (checkBlackCrossPosition() || checkEmptyLineInMatrix()) {
            System.out.println("vince lorenzo");
            winnerName="Lorenzo il Magnifico";
            return "Finished";
        }else if (players.get(0).getPersonalBoard().getFaithMarker().getFaithPosition()==24){
            System.out.println("vinco io");
            winnerName=players.get(0).getName();
            return "Finished";
        }
        ActionToken token = tokensStack.remove(0);
        tokensStack.add(token);
        String message =token.applyEffect(tokensStack);
        if (checkBlackCrossPosition() || checkEmptyLineInMatrix()) {
            winnerName = "Lorenzo il Magnifico";
            return "Finished";
        }
        return message;
    }

    /**
     * @param player      is the players who give away faith points
     * @return 1 if the game is multiplayer, 0 otherwise
     */
    @Override
    public void faithPointsGiveAway(Player player) {
        blackCross.updateBlackCross(1);
    }

    /**
     * @param color indicates the color of the card that lorenzo is trying to discard from DevDeckMatrix
     * @return 0 if the removing of the card of the color indicated is done correctly, 1 otherwise
     */
    public int removeTokenCard(String color)  {
        for(int i=0;i<4;i++){
            for(int j=0; j<3;j++){
                if(!matrix.getDevMatrix()[i][j].getLittleDevDeck().isEmpty()){
                    if(matrix.getDevMatrix()[i][j].getLittleDevDeck().get(0).getColor().equals(color)){
                        matrix.getDevMatrix()[i][j].getLittleDevDeck().remove(0);
                        return 0;
                    }else
                        j=3;
                }
            }
        }
        return 1;
    }

    /**
     * @param player is the players who activates the pope meeting
     * @return the number of the pope meeting activated(1, 2 or 3), otherwise return 0
     */
    @Override
    public int activePopeSpace(Player player) {
        if((player.getPersonalBoard().getFaithMarker().getFaithPosition()==8 || blackCross.getCrossPosition()==8) && isVC1active()) {

            if(player.getPersonalBoard().getFaithMarker().isVaticanZone(1))
                player.increasePopeMeetingPoints(2);
            setVC1active(false);
            return 1;
        }
        else if((player.getPersonalBoard().getFaithMarker().getFaithPosition()==16 || blackCross.getCrossPosition()==16) && isVC2active()) {
            if(player.getPersonalBoard().getFaithMarker().isVaticanZone(2))
                player.increasePopeMeetingPoints(3);
            setVC2active(false);
            return 2;
        }
        else if((player.getPersonalBoard().getFaithMarker().getFaithPosition()==24 || blackCross.getCrossPosition()==24) && isVC3active()) {
            if(player.getPersonalBoard().getFaithMarker().isVaticanZone(3))
                player.increasePopeMeetingPoints(4);
            setVC3active(false);
            return 3;
        }
        else
            return 0;
        }

    public BlackCross getBlackCrossToken(){return blackCross;}

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
     * @param name is the name of the players searched
     * @return the players searched
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
        return winnerName;
    }

    @Override
    public int getBlackCrossPosition() {
        return blackCross.getCrossPosition();
    }
}
