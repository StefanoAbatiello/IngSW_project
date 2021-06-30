package it.polimi.ingsw.messages;

public class ShelfAbilityActiveMessage implements SerializedMessage {
    private int cardId;
    public ShelfAbilityActiveMessage(int cardId) {
        this.cardId=cardId;
    }
    public int getCardId(){
        return cardId;
    }
}
