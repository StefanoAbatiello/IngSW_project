package it.polimi.ingsw.model.cards.cardExceptions;

public class cardOwnerAlreadySetException extends Exception {
    public cardOwnerAlreadySetException(String message){
        super(message);
    }
}
