package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.cardExceptions.CardNotOnTableException;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class DevDeckMatrix {

    /**
     * This attribute represents the matrix of dev cards on the table
     */
    private final LittleDevDeck[][] devDecksOnTable = new LittleDevDeck[4][3];

    /**
     *This attribute is a reference to the whole deck of development card
     */
    private DevDeck deck;

    /**
     * This constructor creates a matrix and dispose the cards on the table, divided in little decks of 4 cards each, dived by color and level
     */
    public DevDeckMatrix () throws FileNotFoundException, IOException, ParseException {
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
    public DevCard[][] getUpperDevCardsOnTable(){
        DevCard[][] cardsOnTable = new DevCard[4][3];
        for(int i=0;i<4;i++){
            for(int j=0; j<3;j++){
                if (!devDecksOnTable[i][j].littleDevDeck.isEmpty())
                    cardsOnTable[i][j]= devDecksOnTable[i][j].littleDevDeck.get(0);
            }
        }
        return cardsOnTable;
    }

    public DevDeck getDeck(){
        return deck;
    }

    /**
     *
     * @return the dev cards matrix on the game table
     */
    public LittleDevDeck[][] getDevMatrix(){
        return devDecksOnTable;
    }

    /**
     *This method gives the possibility to get a card from the matrix
     * @param cardToBuy the dev card that who calls the method wants to buy
     * @return the dev card that the caller wants to buy, if the card is present
     * @throws CardNotOnTableException the card wanted is not present on the game table
     */
    public boolean buyCard ( DevCard cardToBuy) {
        for(int i=0;i<4;i++){
            for(int j=0; j<3;j++){
                if(!devDecksOnTable[i][j].littleDevDeck.isEmpty())
                    if((devDecksOnTable[i][j].littleDevDeck.get(0)).equals(cardToBuy)){
                        devDecksOnTable[i][j].littleDevDeck.remove(0);
                        return true;
                    }
            }
        }
        return false;
    }

}

