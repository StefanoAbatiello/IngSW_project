package it.polimi.ingsw.messages;

public class BuyCardAction implements SerializedMessage {
    int card;
    int slot;

    public BuyCardAction(int card, int slot) {
        this.card=card;
        this.slot=slot;
    }

    public int getCard(){
        return this.card;
    }

    public int getSlot() {
        return this.slot;
    }
}
