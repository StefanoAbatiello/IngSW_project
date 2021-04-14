package it.polimi.ingsw.exceptions;

public class InvalidSlotException  extends Exception{
    public String getWarning(){
        return ("Warning: Slot must be between 0 and 2");
    }
}
