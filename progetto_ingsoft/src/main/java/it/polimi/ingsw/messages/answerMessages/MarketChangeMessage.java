package it.polimi.ingsw.messages.answerMessages;

import it.polimi.ingsw.messages.SerializedMessage;

public class MarketChangeMessage implements SerializedMessage {

    private String[][] market;

    public MarketChangeMessage(String[][] market){
        this.market=market;
    }

    public String[][] getMarket(){
        return market;
    }
}
