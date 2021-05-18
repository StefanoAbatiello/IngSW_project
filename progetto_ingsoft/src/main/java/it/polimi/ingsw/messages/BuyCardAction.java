package it.polimi.ingsw.messages;

public class BuyCardAction implements GameMessage {
    int card;
    int slot;

    public BuyCardAction(int inputLine) {
        card=inputLine;
    }

    public int getCard(){
        return this.card;
    }

    public Object getSlot() {
        return this.slot;
    }
}
