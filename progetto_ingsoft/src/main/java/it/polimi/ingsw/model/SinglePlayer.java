package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Market.Market;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import it.polimi.ingsw.model.personalboard.*;
import it.polimi.ingsw.model.singlePlayerMode.*;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SinglePlayer extends Game{

    private final ArrayList<ActionToken> tokensStack =new ArrayList<>();
    private Market market;
    private DevDeckMatrix matrix;
    private LeadDeck leads;
    private DevDeck devDeck;
    private ArrayList<Player> player= new ArrayList<>();
    private BlackCrossToken blackCrossToken;

    public  ArrayList<ActionToken> getTokensStack() {
        return tokensStack;
    }

    public SinglePlayer(String username) throws playerLeadsNotEmptyException, FileNotFoundException, IOException, ParseException {
        Player singlePlayer= new Player(username);
        this.player.add(singlePlayer);
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
        blackCrossToken=new BlackCrossToken();
        this.market=new Market();
        leads= new LeadDeck();
        leads.shuffle();
        leads.giveToPlayer(player.get(0));

    }
    public ArrayList<Player>  getPlayers() {
        return this.player;
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
        return blackCrossToken.getCrossPosition() >= 24;
    }

    /**
     * @return tokensStack after activation of first token's effect and reorganization of the stack
     */
    @Override
    public String draw(){
        ActionToken token = tokensStack.remove(0);
        tokensStack.add(token);
        String effect=token.applyEffect(tokensStack);
        if(checkBlackCrossPosition()||checkEmptyLineInMatrix())
            return "Finished";
        return effect;
    }

    /**
     * @param color indicates the color of the card that lorenzo is trying to discard from DevDeckMatrix
     * @return 0 if removing a card there are other cards with same color, otherwise -1, or -2 if the card removal failed
     */
    public int removeTokenCard(String color)  {
        for(int i=0;i<4;i++){
            for(int j=0; j<3;j++){
                if(!matrix.getDevMatrix()[i][j].getLittleDevDeck().isEmpty()){
                    if(matrix.getDevMatrix()[i][j].getLittleDevDeck().get(0).getColor().equals(color)){
                        matrix.getDevMatrix()[i][j].getLittleDevDeck().remove(0);
                        if(j==2 && matrix.getDevMatrix()[i][j].getLittleDevDeck().isEmpty())
                            return -1;
                        return 0;
                    }else
                        j=3;
                }
            }
        }
        return -2;
    }

    @Override
    public boolean setVC1active(boolean VC1active) {
        return false;
    }

    @Override
    public boolean setVC2active(boolean VC2active) {
        return false;
    }

    @Override
    public boolean setVC3active(boolean VC3active) {
        return false;
    }

    @Override
    public boolean isVC1active() {
        return false;
    }

    @Override
    public boolean isVC2active() {
        return false;
    }

    @Override
    public boolean isVC3active() {
        return false;
    }

    @Override
    public boolean activePopeSpace(Player player) {
        if((player.getPersonalBoard().getFaithMarker().getFaithPosition()==8 || blackCrossToken.getCrossPosition()==8) && isVC1active()) {

            if(player.getPersonalBoard().getFaithMarker().isVaticanZone())
                player.increaseFaithtrackPoints(2);
            setVC1active(false);
            return isVC1active();
        }
        else if((player.getPersonalBoard().getFaithMarker().getFaithPosition()==16 || blackCrossToken.getCrossPosition()==16) && isVC2active()) {
            if(player.getPersonalBoard().getFaithMarker().isVaticanZone())
                player.increaseFaithtrackPoints(3);
            setVC2active(false);
            return isVC2active();
        }
        else if((player.getPersonalBoard().getFaithMarker().getFaithPosition()==24 || blackCrossToken.getCrossPosition()==24) && isVC3active()) {
            if(player.getPersonalBoard().getFaithMarker().isVaticanZone())
                player.increaseFaithtrackPoints(4);
            setVC3active(false);
            return isVC3active();
        }
        else
            return true;
        }

    public BlackCrossToken getBlackCrossToken(){return blackCrossToken;}

    public Market getMarket() {
        return market;
    }

    public LeadDeck getLeads() {
        return leads;
    }

    public DevDeck getDevDeck() {
        return devDeck;
    }

    @Override
    public DevDeckMatrix getDevDeckMatrix() {
        return matrix;
    }
}
