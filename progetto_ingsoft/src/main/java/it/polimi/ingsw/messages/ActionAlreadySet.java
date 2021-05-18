package it.polimi.ingsw.messages;

public class ActionAlreadySet implements GameMessage {
    String message;

    public ActionAlreadySet(String s){
        message=s;
    }

    public String getMessage(){
        return this.message;
    }

}
