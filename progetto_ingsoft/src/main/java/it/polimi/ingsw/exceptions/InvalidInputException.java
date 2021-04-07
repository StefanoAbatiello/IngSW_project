package it.polimi.ingsw.exceptions;

public class InvalidInputException  extends Exception{
    public String getWarning(){
        return ("Warning: Input must be between 0 and 2");
    }
}
