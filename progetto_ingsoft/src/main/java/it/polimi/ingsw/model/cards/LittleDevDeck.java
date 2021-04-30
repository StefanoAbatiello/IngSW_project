package it.polimi.ingsw.model.cards;

import java.util.ArrayList;

public class LittleDevDeck {

    /**
     * This constructor creates an array of dev cards representing a little deck
     */
    protected ArrayList<DevCard> littleDevDeck = new ArrayList<>();

    public ArrayList<DevCard> getLittleDevDeck(){
        return littleDevDeck;
    }

}