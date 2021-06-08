package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.cardExceptions.CardChosenNotValidException;

public interface Decks {

    Cards getCardFromId(int id) throws CardChosenNotValidException;
}
