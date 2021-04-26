package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.personalboard.*;
import it.polimi.ingsw.model.singlePlayerMode.*;
import java.util.ArrayList;
import java.util.Collections;

public class SinglePlayer {

    private static ArrayList<ActionToken> tokensStack =new ArrayList<>();
    BlackCrossToken blackCross=new BlackCrossToken();
    DevDeckMatrix devDeckMatrix = new DevDeckMatrix();

    public static ArrayList<ActionToken> getTokensStack() {
        return tokensStack;
    }

    public SinglePlayer() {
        tokensStack.add(new CrossShuffleAction());
        tokensStack.add(new DoubleCrossAction());
        tokensStack.add(new DoubleCrossAction());
        tokensStack.add(new DiscardDevCardAction("YELLOW"));
        tokensStack.add(new DiscardDevCardAction("GREEN"));
        tokensStack.add(new DiscardDevCardAction("PURPLE"));
        tokensStack.add(new DiscardDevCardAction("BLUE"));
        Collections.shuffle(tokensStack);
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

    public static ArrayList<ActionToken> draw(){
        ActionToken token= tokensStack.remove(0);
        tokensStack.add(token);
        token.applyEffect();
        return tokensStack;
    }

    public static ArrayList<ActionToken> getTokenStack() {
        return tokensStack;
    }

    /**
     * @param color indicates the color of the card that lorenzo is trying to discard from DevDeckMatrix
     * @return 0 if removing a card the are other cards with same color, otherwise -1
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
        return -1;
    }

}
