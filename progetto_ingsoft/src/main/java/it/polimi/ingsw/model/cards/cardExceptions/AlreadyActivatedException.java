package it.polimi.ingsw.model.cards.cardExceptions;

public class AlreadyActivatedException extends Exception {
    public AlreadyActivatedException(String message) {
        super(message);
    }
}