package it.polimi.ingsw.messages;

public class ActionAnswer {
    String message;

    public ActionAnswer(String inputLine) {
        message=inputLine;
    }

    public String getMessage(){
        return this.message;
    }

}
