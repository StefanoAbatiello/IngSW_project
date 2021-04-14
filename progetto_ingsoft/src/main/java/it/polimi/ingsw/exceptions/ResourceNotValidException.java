package it.polimi.ingsw.exceptions;

public class ResourceNotValidException extends Throwable {
    public String getWarning(){
        return ("Warning: resource not found");}
}
