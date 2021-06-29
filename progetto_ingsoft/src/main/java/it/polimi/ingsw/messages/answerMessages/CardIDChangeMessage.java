package it.polimi.ingsw.messages.answerMessages;

import it.polimi.ingsw.messages.SerializedMessage;

import java.util.Map;

public class CardIDChangeMessage implements SerializedMessage {

    private final Map<Integer,Boolean> cardID;
    private final Map<Integer,Integer> cardPosition;


    public Map<Integer, Boolean> getCardID() {
        return cardID;
    }

    public Map<Integer, Integer> getCardPosition() {
        return cardPosition;
    }


    public CardIDChangeMessage(Map<Integer, Boolean> cardsId,Map<Integer, Integer> cardPosition ) {
        this.cardID= cardsId;
        this.cardPosition=cardPosition;
    }



}
