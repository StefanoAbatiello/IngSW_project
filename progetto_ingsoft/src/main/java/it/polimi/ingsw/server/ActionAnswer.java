package it.polimi.ingsw.server;

public class ActionAnswer {
    String message;

    public ActionAnswer(String inputLine) {
        message=inputLine;
    }

    public String getMessage(){
        return this.message;
    }

}
