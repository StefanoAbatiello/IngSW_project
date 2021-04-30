package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.cardExceptions.CardNotOnTableException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class DevDeckMatrix {

    /**
     * This attribute represents the matrix of dev cards on the table
     */
    private static final LittleDevDeck[][] devDecksOnTable = new LittleDevDeck[4][3];
    protected static DevDeck deck;
    /**
     * This constructor creates a matrix and dispose the cards on the table, divided in little decks of 4 cards each, dived by color and level
     */
    public DevDeckMatrix (){
        deck = new DevDeck();
        ArrayList<DevCard> greenCards = deck.createLittleDecks("GREEN");
        ArrayList<DevCard> yellowCards = deck.createLittleDecks("YELLOW");
        ArrayList<DevCard> purpleCards = deck.createLittleDecks("PURPLE");
        ArrayList<DevCard> blueCards = deck.createLittleDecks("BLUE");
        ArrayList<DevCard> devDeckInOrder = Stream.of(greenCards, yellowCards, purpleCards,blueCards).flatMap(Collection::stream).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        int k=0;
        do{
            for(int i=0;i<4;i++){
                for(int j=0;j<3;j++){
                    devDecksOnTable[i][j] = new LittleDevDeck();
                    for(int numcards=0; numcards<4;numcards++,k++)
                        devDecksOnTable[i][j].littleDevDeck.add(devDeckInOrder.get(k));
                    Collections.shuffle(devDecksOnTable[i][j].littleDevDeck);
                }
            }
        }while(k<devDeckInOrder.size());
    }

    /**
     *This method gets only the first dev card of each slot in the matrix
     * @return a matrix of dev cards which contains in each slot the first dev card contained in the game matrix
     */
    public static DevCard[][] getUpperDevCardsOnTable(){
        DevCard[][] cardsOnTable = new DevCard[4][3];
        for(int i=0;i<4;i++){
            for(int j=0; j<3;j++){
                if (!devDecksOnTable[i][j].littleDevDeck.isEmpty())
                    cardsOnTable[i][j]= devDecksOnTable[i][j].littleDevDeck.get(0);
            }
        }
        return cardsOnTable;
    }

    /**
     *
     * @return the dev cards matrix on the game table
     */
    public static LittleDevDeck[][] getDevMatrix(){
        return devDecksOnTable;
    }


    //TODO creo mazzo quando faccio order? mazzo deve essere static, anche i mini array
    //TODO gestisco eccezione

    /**
     *This method gives the possibility to get a card from the matrix
     * @param cardToBuy the dev card that who calls the method wants to buy
     * @return the dev card that the caller wants to buy, if the card is present
     * @throws CardNotOnTableException the card wanted is not present on the game table
     */
    public static DevCard buyCard ( DevCard cardToBuy) throws CardNotOnTableException {
        for(int i=0;i<4;i++){
            for(int j=0; j<3;j++){
                if(!devDecksOnTable[i][j].littleDevDeck.isEmpty())
                    if((devDecksOnTable[i][j].littleDevDeck.get(0)).equals(cardToBuy)){
                        devDecksOnTable[i][j].littleDevDeck.remove(0);
                        return cardToBuy;
                    }
            }
        }
        throw new CardNotOnTableException("Error: card not found on table");
    }



}

