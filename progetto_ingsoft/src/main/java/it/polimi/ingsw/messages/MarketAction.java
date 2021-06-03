package it.polimi.ingsw.messages;

public class MarketAction implements SerializedMessage {
    int coordinate;

    public MarketAction(int position) {
        coordinate=position;
    }

    public int getCoordinate(){
        return this.coordinate;
    }

}
