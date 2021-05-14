package it.polimi.ingsw.server;

public class MarketAction {
    int coordinate;

    public MarketAction(int position) {
        coordinate=position;
    }

    public int getCoordinate(){
        return this.coordinate;
    }

}
