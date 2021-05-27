package it.polimi.ingsw.messages.answerMessages;

import it.polimi.ingsw.messages.SerializedMessage;

import java.util.Map;

public class CardIDChangeMessage implements SerializedMessage {

    private final Map<Integer,Boolean> cardID;

    public Map<Integer, Boolean> getCardID() {
        return cardID;
    }

    public CardIDChangeMessage(Map<Integer, Boolean> cardsId) {
        this.cardID= cardsId;
    }



}
