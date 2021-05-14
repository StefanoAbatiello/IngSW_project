package it.polimi.ingsw.messages;

public class BuyCardAction implements GameMessage {
    String card;

    public BuyCardAction(String inputLine) {
        card=inputLine;
    }

    public String getCard(){
        return this.card;
    }

}
