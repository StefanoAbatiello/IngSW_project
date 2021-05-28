package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Market.Market;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.cardExceptions.playerLeadsNotEmptyException;
import it.polimi.ingsw.model.personalboard.*;
import it.polimi.ingsw.model.singlePlayerMode.*;
import java.util.ArrayList;
import java.util.Collections;

public class SinglePlayer extends Game{

    private final ArrayList<ActionToken> tokensStack =new ArrayList<>();
    private Market market;
    private DevDeckMatrix matrix;
    private LeadDeck leads;
    private DevDeck devDeck;
    private ArrayList<Player> player= new ArrayList<>();

    public  ArrayList<ActionToken> getTokensStack() {
        return tokensStack;
    }

    public SinglePlayer(String username) throws playerLeadsNotEmptyException {
        Player singlePlayer= new Player(username);
        this.player.add(singlePlayer);
        tokensStack.add(new CrossShuffleAction());
        tokensStack.add(new DoubleCrossAction());
        tokensStack.add(new DoubleCrossAction());
        tokensStack.add(new DiscardDevCardAction("YELLOW"));
        tokensStack.add(new DiscardDevCardAction("GREEN"));
        tokensStack.add(new DiscardDevCardAction("PURPLE"));
        tokensStack.add(new DiscardDevCardAction("BLUE"));
        Collections.shuffle(tokensStack);
        matrix= new DevDeckMatrix();
        devDeck = DevDeckMatrix.getDeck();
        new BlackCrossToken();
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
    public static boolean checkEmptyLineInMatrix() {
        for(int i=0;i<4;i++) {
            if (DevDeckMatrix.getDevMatrix()[i][0].getLittleDevDeck().isEmpty() &&
                DevDeckMatrix.getDevMatrix()[i][1].getLittleDevDeck().isEmpty() &&
                DevDeckMatrix.getDevMatrix()[i][2].getLittleDevDeck().isEmpty())
                    return true;
        }
        return false;
    }

    /**
     * @return true if Lorenzo's BlackCross has reached the end of Faith track
     */
    public static boolean checkBlackCrossPosition(){
        return BlackCrossToken.getCrossPosition() >= 24;
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
    public static int removeTokenCard(String color)  {
        for(int i=0;i<4;i++){
            for(int j=0; j<3;j++){
                if(!DevDeckMatrix.getDevMatrix()[i][j].getLittleDevDeck().isEmpty()){
                    if(DevDeckMatrix.getDevMatrix()[i][j].getLittleDevDeck().get(0).getColor().equals(color)){
                        DevDeckMatrix.getDevMatrix()[i][j].getLittleDevDeck().remove(0);
                        if(j==2 && DevDeckMatrix.getDevMatrix()[i][j].getLittleDevDeck().isEmpty())
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
        if((player.getPersonalBoard().getFaithMarker().getFaithPosition()==8 || BlackCrossToken.getCrossPosition()==8) && isVC1active()) {

            if(player.getPersonalBoard().getFaithMarker().isVaticanZone())
                player.increaseFaithtrackPoints(2);
            setVC1active(false);
            return isVC1active();
        }
        else if((player.getPersonalBoard().getFaithMarker().getFaithPosition()==16 || BlackCrossToken.getCrossPosition()==16) && isVC2active()) {
            if(player.getPersonalBoard().getFaithMarker().isVaticanZone())
                player.increaseFaithtrackPoints(3);
            setVC2active(false);
            return isVC2active();
        }
        else if((player.getPersonalBoard().getFaithMarker().getFaithPosition()==24 || BlackCrossToken.getCrossPosition()==24) && isVC3active()) {
            if(player.getPersonalBoard().getFaithMarker().isVaticanZone())
                player.increaseFaithtrackPoints(4);
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

    public DevDeck getDevDeck() {
        return devDeck;
    }
}
